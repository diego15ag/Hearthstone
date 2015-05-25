package com.example.diego.hearthstone;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by blukstack on 01/04/2015.
 */
public class JSONManager {
    public static ArrayList<Carta> Cartas_array;
    public static ArrayList<Mazo> Mazos_array;
    public static ArrayList<Mazo> Mazos_predefinidos_array;
    public static ArrayList<Carta> Heroes_array;
    private CartasManagerDbHelper mDbHelper;
    private SQLiteDatabase dbRW;
    private SQLiteDatabase dbRO;
    //private final String url_cards = "https://dl.dropboxusercontent.com/u/16678562/all-cards.json";
    //public final String url_cards = "https://dl.dropboxusercontent.com/u/16678562/all-cards-updated.json";
    private Context contexto;
    public static int position_clase = 0;
    public static int control = 0;

    public JSONManager(Context context) {
        contexto = context;
    }

    public JSONManager() {

    }

    public void close_params() {
        dbRO.close();
        dbRW.close();
    }

    public void start() {
        mDbHelper = new CartasManagerDbHelper(contexto);
        dbRO = mDbHelper.getReadableDatabase();
        dbRW = mDbHelper.getWritableDatabase();
    }

    public void startBG() {
        new initparamsBD().execute();
    }

    public class initparamsBD extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mDbHelper = new CartasManagerDbHelper(contexto);
            dbRO = mDbHelper.getReadableDatabase();
            dbRW = mDbHelper.getWritableDatabase();
            return null;
        }
    }


    public class RellenaBD_JSON extends AsyncTask<Object, Void, Void> {

        private Handler mHandle = new Handler();

        protected Void doInBackground(Object... obj) {

            final MainActivity ma = (MainActivity) obj[1];


            InputStream is = null;
            String result = "";
            JSONObject json = null;
            JSONArray array_cards = null;
            mDbHelper = new CartasManagerDbHelper(contexto);
            dbRW = mDbHelper.getWritableDatabase();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet((String) obj[0]);
                //HttpPost httppost = new HttpPost(urls[0]);
                HttpResponse response = httpclient.execute(httpget);
                //HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-16"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    //System.out.println("línea almacenada: "+line);
                }
                is.close();
                result = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.printf("El tamaño del string es : %d \n", result.length());
                json = new JSONObject(result);
                if (json == null) {
                    System.out.println("he entrado aquí");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                array_cards = json.getJSONArray("cards");

                //Borramos una antigua bd si existiese y creamos una nueva
                mDbHelper.borrar(dbRW);

                for (int i = 0; i < array_cards.length(); i++) { // insercion en BD
                    if(isCancelled()) return null;
                    if (array_cards.getJSONObject(i).getString("category").equals("hero") == false &&
                            array_cards.getJSONObject(i).getString("category").equals("ability") == false
                            && array_cards.getJSONObject(i).getString("collectible").equals("true") == true)
                        inserta_carta(array_cards.getJSONObject(i).getString("name"), array_cards.getJSONObject(i).getString("hero"),
                                false, 0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



            mHandle.post(new Runnable() {
                @Override
                public void run() {
                    if (ma != null&&!isCancelled()) {
                        SharedPreferences sp = contexto.getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putBoolean("BDCargada", true);
                        edit.commit();
                        ma.rellena();
                    }
                    else
                        cancel(true);
                }
            });
            control = 1;

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.i("cancelada", "cancelado");
        }
    }


    public void inserta_carta(String name, String clase, boolean obtenida, int cantidad) {
        ContentValues values = new ContentValues();
        values.put(CartasManagerContract.Carta.COLUMN_NAME_NAME, name);
        values.put(CartasManagerContract.Carta.COLUMN_NAME_CLASS, clase);
        values.put(CartasManagerContract.Carta.COLUMN_NAME_OBTENIDA, obtenida);
        values.put(CartasManagerContract.Carta.COLUMN_NAME_CANTIDAD, cantidad);
        long newRowId;
        newRowId = dbRW.insert(
                CartasManagerContract.Carta.TABLE_NAME,
                null,
                values
        );
        System.out.printf("Carta %s insertada \n", name);
    }

    public void inserta_mazo(String nombre, String predefinido, String clase) {
        ContentValues values = new ContentValues();
        values.put(CartasManagerContract.Mazo.COLUMN_NAME_NAME, nombre);
        values.put(CartasManagerContract.Mazo.COLUMN_NAME_PREDEFINIDO, predefinido);
        values.put(CartasManagerContract.Mazo.COLUMN_NAME_CLASS, clase);
        long newRowId;
        newRowId = dbRW.insert(
                CartasManagerContract.Mazo.TABLE_NAME,
                null,
                values
        );

    }

    public void inserta_carta_mazo(int id_carta, int id_mazo) {
        ContentValues values = new ContentValues();
        values.put(CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDCARTA, id_carta);
        values.put(CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDMAZO, id_mazo);
        long newRowId;
        newRowId = dbRW.insert(
                CartasManagerContract.Carta_Mazo.TABLE_NAME,
                null,
                values
        );
    }

    public boolean getObtenida(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(id);
        String strI = sb.toString();
        String[] projection = {CartasManagerContract.Carta.COLUMN_NAME_OBTENIDA};
        String whereColum = CartasManagerContract.Carta._ID + "=?";
        String[] valor = {strI};

        Cursor c = dbRO.query(
                CartasManagerContract.Carta.TABLE_NAME, // Nombre de la tabla
                projection, // Columnas a devolver
                whereColum, // Columnas de la cláusula WHERE
                valor, // Valores de la cláusula WHERE
                null, // Agrupamiento
                null, // Filtro por grupos
                null);
        c.moveToFirst();
        int obtenida = c.getInt(c.getColumnIndex(CartasManagerContract.Carta.COLUMN_NAME_OBTENIDA));
        c.close();
        if (obtenida == 0)
            return false;
        else
            return true;
    }

    public int getCantidad(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(id);
        String strI = sb.toString();
        String[] projection = {CartasManagerContract.Carta.COLUMN_NAME_CANTIDAD};
        String whereColum = CartasManagerContract.Carta._ID + "=?";
        String[] valor = {strI};

        Cursor c = dbRO.query(
                CartasManagerContract.Carta.TABLE_NAME, // Nombre de la tabla
                projection, // Columnas a devolver
                whereColum, // Columnas de la cláusula WHERE
                valor, // Valores de la cláusula WHERE
                null, // Agrupamiento
                null, // Filtro por grupos
                null);
        c.moveToFirst();
        int cantidad = c.getInt(c.getColumnIndex(CartasManagerContract.Carta.COLUMN_NAME_CANTIDAD));
        c.close();
        return cantidad;
    }

    public void deleteMazo(Mazo mazo) {
        // borramos las cartas de Carta_Mazo que pertenezcan al mazo
        String selection = CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDMAZO + " LIKE ?";
        String[] selectionArgs = {String.valueOf(mazo.getId())};
        String table_name = CartasManagerContract.Carta_Mazo.TABLE_NAME;
        dbRW.delete(table_name, selection, selectionArgs);

        // borramos el mazo
        selection = CartasManagerContract.Mazo._ID + " LIKE ?";
        String[] selectionArgs2 = {String.valueOf(mazo.getId())};
        table_name = CartasManagerContract.Mazo.TABLE_NAME;
        dbRW.delete(table_name, selection, selectionArgs);
    }

    public void modificaNombreMazo(int id_mazo, String nuevo_nombre) {
        ContentValues values = new ContentValues();
        values.put(CartasManagerContract.Mazo.COLUMN_NAME_NAME, nuevo_nombre);

        String selection = CartasManagerContract.Mazo._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id_mazo)};
        int count = dbRW.update(
                CartasManagerContract.Mazo.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public void borraCartasMazo(int idmazo) {
        String selection = CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDMAZO + " LIKE ?";
        String[] selectionArgs = {String.valueOf(idmazo)};
        String table_name = CartasManagerContract.Carta_Mazo.TABLE_NAME;
        dbRW.delete(table_name, selection, selectionArgs);
    }

    public void insertaCartasMazo(ArrayList<Carta> cartas, int id_mazo) {
        ContentValues valuescartas = new ContentValues();
        for (int i = 0; i < cartas.size(); i++) {
            valuescartas.put(CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDCARTA,
                    cartas.get(i).getId() + 1);
            valuescartas.put(CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDMAZO, id_mazo);
            valuescartas.put(CartasManagerContract.Carta_Mazo.COLUMN_NAME_CANTIDAD, cartas.get(i).getCantidad());
            dbRW.insert(
                    CartasManagerContract.Carta_Mazo.TABLE_NAME,
                    null,
                    valuescartas
            );
        }
    }

    public void creaMazo(Mazo mazo) {
        ContentValues values = new ContentValues();
        values.put(CartasManagerContract.Mazo.COLUMN_NAME_NAME, mazo.getNombre());
        values.put(CartasManagerContract.Mazo.COLUMN_NAME_PREDEFINIDO, mazo.isPredefinido());
        values.put(CartasManagerContract.Mazo.COLUMN_NAME_CLASS, mazo.getClase());
        long newRowId;
        newRowId = dbRW.insert(
                CartasManagerContract.Mazo.TABLE_NAME,
                null,
                values
        );
        System.out.printf("Mazo %s insertado en BD con id %d \n", mazo.getNombre(), newRowId);
        if (JSONManager.Mazos_array.size() == 0 && mazo.isPredefinido() == false) {
            JSONManager.Mazos_array = new ArrayList<Mazo>();
            JSONManager.Mazos_array.add(mazo);
            JSONManager.Mazos_array.get(JSONManager.Mazos_array.size() - 1).setId((int) newRowId);
        } else if (mazo.isPredefinido() == false)
            JSONManager.Mazos_array.get(JSONManager.Mazos_array.size() - 1).setId((int) newRowId);
        ContentValues valuescartas = new ContentValues();
        for (int i = 0; i < mazo.getCartas().size(); i++) {
            valuescartas.put(CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDCARTA,
                    mazo.getCartas().get(i).getId() + 1);
            valuescartas.put(CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDMAZO, (int) newRowId);
            valuescartas.put(CartasManagerContract.Carta_Mazo.COLUMN_NAME_CANTIDAD, mazo.getCartas().get(i).getCantidad());
            dbRW.insert(
                    CartasManagerContract.Carta_Mazo.TABLE_NAME,
                    null,
                    valuescartas
            );
            System.out.printf("Carta %s insertada en el mazo %s \n", mazo.getCartas().get(i).getNombre(), mazo.getNombre());
        }

    }

    private static Carta getCartaByName(String name, int cantidad) {
        Carta c = new Carta();

        for (int i = 0; i < JSONManager.Cartas_array.size(); i++)
            if (JSONManager.Cartas_array.get(i).getNombre().equals(name)) {
                c = JSONManager.Cartas_array.get(i).clone();
                c.setCantidad(cantidad);
                return c;
            }
        c.setNombre("FALLO EN NOMBRE");
        c.setId(-1);
        return c;
    }

    public static ArrayList<Mazo> declaraMazosPredefinidos() {
        ArrayList<Mazo> mazos = new ArrayList<Mazo>();
        ArrayList<Carta> cartas = new ArrayList<Carta>();
        cartas.add(getCartaByName("Execute", 2));
        cartas.add(getCartaByName("Shield Slam", 2));
        cartas.add(getCartaByName("Whirlwind", 1));
        cartas.add(getCartaByName("Armorsmith", 2));
        cartas.add(getCartaByName("Cruel Taskmaster", 2));
        cartas.add(getCartaByName("Fiery War Axe", 2));
        cartas.add(getCartaByName("Shield Block", 2));
        cartas.add(getCartaByName("Death's Bite", 2));
        cartas.add(getCartaByName("Brawl", 1));
        cartas.add(getCartaByName("Grommash Hellscream", 1));
        cartas.add(getCartaByName("Ironbeak Owl", 1));
        cartas.add(getCartaByName("Big Game Hunter", 1));
        cartas.add(getCartaByName("Acolyte of Pain", 2));
        cartas.add(getCartaByName("Piloted Shredder", 1));
        cartas.add(getCartaByName("Harrison Jones", 1));
        cartas.add(getCartaByName("Sludge Belcher", 1));
        cartas.add(getCartaByName("Sylvanas Windrunner", 1));
        cartas.add(getCartaByName("Shieldmaiden", 1));
        cartas.add(getCartaByName("Baron Geddon", 1));
        cartas.add(getCartaByName("Dr. Boom", 1));
        cartas.add(getCartaByName("Ragnaros the Firelord", 1));
        cartas.add(getCartaByName("Alexstrasza", 1));
        /* el id que pongais en el mazo es irrelevante, se crea un mazo nuevo y cuando se lee de la BD se lee con el ID
         que la BD le haya puesto */
        mazos.add(new Mazo(-1, "Warrior Control", true, "warrior", cartas));
        cartas = new ArrayList<Carta>();
        // crear nuevos mazos a continuacion
        // Paladin
        cartas.add(getCartaByName("Equality", 1));
        cartas.add(getCartaByName("Annoy-o-Tron", 2));
        cartas.add(getCartaByName("Ironbeak Owl", 1));
        cartas.add(getCartaByName("Knife Juggler", 2));
        cartas.add(getCartaByName("Shielded Minibot", 2));
        cartas.add(getCartaByName("Coghammer", 1));
        cartas.add(getCartaByName("Muster for Battle", 2));
        cartas.add(getCartaByName("Aldor Peacekeeper", 2));
        cartas.add(getCartaByName("Big Game Hunter", 1));
        cartas.add(getCartaByName("Truesilver Champion", 2));
        cartas.add(getCartaByName("Consecration", 2));
        cartas.add(getCartaByName("Piloted Shredder", 2));
        cartas.add(getCartaByName("Antique Healbot", 1));
        cartas.add(getCartaByName("Harrison Jones", 1));
        cartas.add(getCartaByName("Loatheb", 1));
        cartas.add(getCartaByName("Quartermaster", 1));
        cartas.add(getCartaByName("Sludge Belcher", 2));
        cartas.add(getCartaByName("Emperor Thaurissan", 1));
        cartas.add(getCartaByName("Sylvanas Windrunner", 1));
        cartas.add(getCartaByName("Lay on Hands", 1));
        cartas.add(getCartaByName("Tirion Fordring", 1));
        mazos.add(new Mazo(-1, "Paladin Control", true, "paladin", cartas));
        cartas = new ArrayList<Carta>();
        // Mage
        cartas.add(getCartaByName("Ice Lance", 2));
        cartas.add(getCartaByName("Frostbolt", 2));
        cartas.add(getCartaByName("Bloodmage Thalnos", 1));
        cartas.add(getCartaByName("Doomsayer", 2));
        cartas.add(getCartaByName("Loot Hoarder", 2));
        cartas.add(getCartaByName("Mad Scientist", 2));
        cartas.add(getCartaByName("Arcane Intellect", 2));
        cartas.add(getCartaByName("Frost Nova", 2));
        cartas.add(getCartaByName("Ice Barrier", 2));
        cartas.add(getCartaByName("Ice Block", 2));
        cartas.add(getCartaByName("Acolyte of Pain", 2));
        cartas.add(getCartaByName("Fireball", 2));
        cartas.add(getCartaByName("Blizzard", 2));
        cartas.add(getCartaByName("Emperor Thaurissan", 1));
        cartas.add(getCartaByName("Flamestrike", 2));
        cartas.add(getCartaByName("Archmage Antonidas", 1));
        cartas.add(getCartaByName("Alexstrasza", 1));
        mazos.add(new Mazo(-1, "Freeze Mage", true, "mage", cartas));
        cartas = new ArrayList<Carta>();
        // Warlock
        cartas.add(getCartaByName("Mortal Coil", 2));
        cartas.add(getCartaByName("Zombie Chow", 1));
        cartas.add(getCartaByName("Darkbomb", 2));
        cartas.add(getCartaByName("Ancient Watcher", 2));
        cartas.add(getCartaByName("Ironbeak Owl", 1));
        cartas.add(getCartaByName("Sunfury Protector", 2));
        cartas.add(getCartaByName("Big Game Hunter", 1));
        cartas.add(getCartaByName("Imp Gang Boss", 2));
        cartas.add(getCartaByName("Hellfire", 1));
        cartas.add(getCartaByName("Shadowflame", 2));
        cartas.add(getCartaByName("Voidcaller", 2));
        cartas.add(getCartaByName("Antique Healbot", 2));
        cartas.add(getCartaByName("Twilight Drake", 2));
        cartas.add(getCartaByName("Sludge Belcher", 2));
        cartas.add(getCartaByName("Sylvanas Windrunner", 1));
        cartas.add(getCartaByName("Dr. Boom", 1));
        cartas.add(getCartaByName("Lord Jaraxxus", 1));
        cartas.add(getCartaByName("Mal'Ganis", 1));
        cartas.add(getCartaByName("Mountain Giant", 2));
        mazos.add(new Mazo(-1, "Demonlock", true, "warlock", cartas));
        cartas = new ArrayList<Carta>();
        // Druid
        cartas.add(getCartaByName("Innervate", 2));
        cartas.add(getCartaByName("Wild Growth", 2));
        cartas.add(getCartaByName("Wrath", 2));
        cartas.add(getCartaByName("Savage Roar", 2));
        cartas.add(getCartaByName("Big Game Hunter", 1));
        cartas.add(getCartaByName("Shade of Naxxramas", 2));
        cartas.add(getCartaByName("Swipe", 2));
        cartas.add(getCartaByName("Piloted Shredder", 2));
        cartas.add(getCartaByName("Keeper of the Grove", 2));
        cartas.add(getCartaByName("Kezan Mystic", 1));
        cartas.add(getCartaByName("Druid of the Claw", 2));
        cartas.add(getCartaByName("Loatheb", 1));
        cartas.add(getCartaByName("Sludge Belcher", 1));
        cartas.add(getCartaByName("Force of Nature", 2));
        cartas.add(getCartaByName("Emperor Thaurissan", 1));
        cartas.add(getCartaByName("Sylvanas Windrunner", 1));
        cartas.add(getCartaByName("Ancient of Lore", 2));
        cartas.add(getCartaByName("Dr. Boom", 1));
        cartas.add(getCartaByName("Cenarius", 1));
        mazos.add(new Mazo(-1, "2 x Combo", true, "druid", cartas));
        cartas = new ArrayList<Carta>();
        // Rogue
        cartas.add(getCartaByName("Backstab", 2));
        cartas.add(getCartaByName("Preparation", 2));
        cartas.add(getCartaByName("Deadly Poison", 2));
        cartas.add(getCartaByName("Southsea Deckhand", 1));
        cartas.add(getCartaByName("Blade Flurry", 2));
        cartas.add(getCartaByName("Eviscerate", 2));
        cartas.add(getCartaByName("Sap", 2));
        cartas.add(getCartaByName("Fan of Knives", 2));
        cartas.add(getCartaByName("SI:7 Agent", 2));
        cartas.add(getCartaByName("Bloodmage Thalnos", 1));
        cartas.add(getCartaByName("Earthen Ring Farseer", 1));
        cartas.add(getCartaByName("Azure Drake", 2));
        cartas.add(getCartaByName("Tinker's Sharpsword Oil", 2));
        cartas.add(getCartaByName("Violet Teacher", 2));
        cartas.add(getCartaByName("Antique Healbot", 1));
        cartas.add(getCartaByName("Loatheb", 1));
        cartas.add(getCartaByName("Emperor Thaurissan", 1));
        cartas.add(getCartaByName("Sprint", 2));
        mazos.add(new Mazo(-1, "Oil Rogue", true, "rogue", cartas));
        cartas = new ArrayList<Carta>();
        cartas.add(getCartaByName("Circle of Healing", 2));
        cartas.add(getCartaByName("Northshire Cleric", 2));
        cartas.add(getCartaByName("Zombie Chow", 2));
        cartas.add(getCartaByName("Power Word: Shield", 2));
        cartas.add(getCartaByName("Shadow Word: Death", 2));
        cartas.add(getCartaByName("Velen's Chosen", 1));
        cartas.add(getCartaByName("Auchenai Soulpriest", 2));
        cartas.add(getCartaByName("Thoughtsteal", 1));
        cartas.add(getCartaByName("Shadow Madness", 1));
        cartas.add(getCartaByName("Cabal Shadow Priest", 1));
        cartas.add(getCartaByName("Holy Nova", 1));
        cartas.add(getCartaByName("Mind Control", 1));
        cartas.add(getCartaByName("Wild Pyromancer", 2));
        cartas.add(getCartaByName("Deathlord", 2));
        cartas.add(getCartaByName("Piloted Shredder", 1));
        cartas.add(getCartaByName("Injured Blademaster", 2));
        cartas.add(getCartaByName("Loatheb", 1));
        cartas.add(getCartaByName("Azure Drake", 1));
        cartas.add(getCartaByName("Sylvanas Windrunner", 1));
        cartas.add(getCartaByName("Ragnaros the Firelord", 1));
        cartas.add(getCartaByName("Prophet Velen", 1));
        mazos.add(new Mazo(-1, "Zetalot chooses you", true, "priest", cartas));
        cartas = new ArrayList<Carta>();
        cartas.add(getCartaByName("Abusive Sergeant", 2));
        cartas.add(getCartaByName("Leper Gnome", 2));
        cartas.add(getCartaByName("Worgen Infiltrator", 1));
        cartas.add(getCartaByName("Haunted Creeper", 2));
        cartas.add(getCartaByName("Quick Shot", 1));
        cartas.add(getCartaByName("Ironbeak Owl", 2));
        cartas.add(getCartaByName("Knife Juggler", 2));
        cartas.add(getCartaByName("Mad Scientist", 2));
        cartas.add(getCartaByName("Arcane Golem", 2));
        cartas.add(getCartaByName("Wolfrider", 2));
        cartas.add(getCartaByName("Leeroy Jenkins", 1));
        cartas.add(getCartaByName("Explosive Trap", 2));
        cartas.add(getCartaByName("Misdirection", 1));
        cartas.add(getCartaByName("Animal Companion", 2));
        cartas.add(getCartaByName("Eaglehorn Bow", 2));
        cartas.add(getCartaByName("Kill Command", 2));
        cartas.add(getCartaByName("Unleash the Hounds", 2));
        mazos.add(new Mazo(-1, "Hunter face", true, "hunter", cartas));
        cartas = new ArrayList<Carta>();
        cartas.add(getCartaByName("Earth Shock", 1));
        cartas.add(getCartaByName("Zombie Chow", 2));
        cartas.add(getCartaByName("Haunted Creeper", 2));
        cartas.add(getCartaByName("Harvest Golem", 2));
        cartas.add(getCartaByName("Flametongue Totem", 2));
        cartas.add(getCartaByName("Hex", 2));
        cartas.add(getCartaByName("Powermace", 2));
        cartas.add(getCartaByName("Lightning Storm", 2));
        cartas.add(getCartaByName("Defender of Argus", 2));
        cartas.add(getCartaByName("Fireguard Destroyer", 2));
        cartas.add(getCartaByName("Piloted Shredder", 2));
        cartas.add(getCartaByName("Antique Healbot", 2));
        cartas.add(getCartaByName("Azure Drake", 2));
        cartas.add(getCartaByName("Fire Elemental", 2));
        cartas.add(getCartaByName("Neptulon", 1));
        cartas.add(getCartaByName("Dr. Boom", 1));
        cartas.add(getCartaByName("Ragnaros the Firelord", 1));
        mazos.add(new Mazo(-1, "Legendary Cyrak Mech Control", true, "shaman", cartas));
        cartas = new ArrayList<Carta>();
        cartas.add(getCartaByName("Mana Wyrm", 2));
        cartas.add(getCartaByName("Mirror Image", 2));
        cartas.add(getCartaByName("Arcane Missiles", 1));
        cartas.add(getCartaByName("Frostbolt", 2));
        cartas.add(getCartaByName("Flamecannon", 1));
        cartas.add(getCartaByName("Sorcerer's Apprentice", 2));
        cartas.add(getCartaByName("Mad Scientist", 2));
        cartas.add(getCartaByName("Unstable Portal", 2));
        cartas.add(getCartaByName("Arcane Intellect", 1));
        cartas.add(getCartaByName("Counterspell", 1));
        cartas.add(getCartaByName("Flamewaker", 2));
        cartas.add(getCartaByName("Mirror Entity", 2));
        cartas.add(getCartaByName("Kezan Mystic", 1));
        cartas.add(getCartaByName("Polymorph", 1));
        cartas.add(getCartaByName("Piloted Shredder", 1));
        cartas.add(getCartaByName("Fireball", 2));
        cartas.add(getCartaByName("Azure Drake", 2));
        cartas.add(getCartaByName("Flamestrike", 1));
        cartas.add(getCartaByName("Archmage Antonidas", 1));
        cartas.add(getCartaByName("Dr. Boom", 1));
        mazos.add(new Mazo(-1, "Bluky tempo mage", true, "mage", cartas));
        cartas = new ArrayList<Carta>();
        // Más mazos
        return mazos;
    }

    public ArrayList<Mazo> getMazosNoPredefinidos() {
        //System.out.printf("estoy en mazos no predefinidos \n");
        String[] projection = {CartasManagerContract.Mazo._ID, CartasManagerContract.Mazo.COLUMN_NAME_NAME
                , CartasManagerContract.Mazo.COLUMN_NAME_PREDEFINIDO, CartasManagerContract.Mazo.COLUMN_NAME_CLASS};
        String whereColum = CartasManagerContract.Mazo.COLUMN_NAME_PREDEFINIDO + "=?";
        String[] valor = {String.valueOf(0)};
        Cursor c = dbRO.query(
                CartasManagerContract.Mazo.TABLE_NAME, // Nombre de la tabla
                projection, // Columnas a devolver
                whereColum, // Columnas de la cláusula WHERE
                valor, // Valores de la cláusula WHERE
                null, // Agrupamiento
                null, // Filtro por grupos
                null);
        int id;
        String nombre;
        int predefinido;
        boolean predefinido2;
        String clase;
        ArrayList<Mazo> mazos = new ArrayList<Mazo>();
        //Mazo maux;
        while (c.moveToNext()) {
            Mazo m;
            id = c.getInt(c.getColumnIndex(CartasManagerContract.Mazo._ID));
            nombre = c.getString(c.getColumnIndex(CartasManagerContract.Mazo.COLUMN_NAME_NAME));
            predefinido = c.getInt(c.getColumnIndex(CartasManagerContract.Mazo.COLUMN_NAME_PREDEFINIDO));
            if (predefinido == 0)
                predefinido2 = false;
            else
                predefinido2 = true;
            clase = c.getString(c.getColumnIndex(CartasManagerContract.Mazo.COLUMN_NAME_CLASS));
            m = new Mazo(id, nombre, predefinido2, clase, getCartasFromMazo(id));
            mazos.add(m);
        }
        c.close();
        for (int i = 0; i < mazos.size(); i++) {
            System.out.printf("Se ha sacado el mazo %s de la clase %s con id: %d de la base de datos \n",
                    mazos.get(i).getNombre(), mazos.get(i).getClase(), mazos.get(i).getId());
        }
        return mazos;
    }

    public ArrayList<Mazo> getMazosPredefinidos() {
        //System.out.printf("estoy en mazos no predefinidos \n");
        String[] projection = {CartasManagerContract.Mazo._ID, CartasManagerContract.Mazo.COLUMN_NAME_NAME
                , CartasManagerContract.Mazo.COLUMN_NAME_PREDEFINIDO, CartasManagerContract.Mazo.COLUMN_NAME_CLASS};
        String whereColum = CartasManagerContract.Mazo.COLUMN_NAME_PREDEFINIDO + "=?";
        String[] valor = {String.valueOf(1)};
        Cursor c = dbRO.query(
                CartasManagerContract.Mazo.TABLE_NAME, // Nombre de la tabla
                projection, // Columnas a devolver
                whereColum, // Columnas de la cláusula WHERE
                valor, // Valores de la cláusula WHERE
                null, // Agrupamiento
                null, // Filtro por grupos
                null);
        int id;
        String nombre;
        int predefinido;
        boolean predefinido2;
        String clase;
        ArrayList<Mazo> mazos = new ArrayList<Mazo>();
        //Mazo maux;
        while (c.moveToNext()) {
            Mazo m;
            id = c.getInt(c.getColumnIndex(CartasManagerContract.Mazo._ID));
            nombre = c.getString(c.getColumnIndex(CartasManagerContract.Mazo.COLUMN_NAME_NAME));
            predefinido = c.getInt(c.getColumnIndex(CartasManagerContract.Mazo.COLUMN_NAME_PREDEFINIDO));
            if (predefinido == 0)
                predefinido2 = false;
            else
                predefinido2 = true;
            clase = c.getString(c.getColumnIndex(CartasManagerContract.Mazo.COLUMN_NAME_CLASS));
            m = new Mazo(id, nombre, predefinido2, clase, getCartasFromMazo(id));
            mazos.add(m);
        }
        c.close();
        for (int i = 0; i < mazos.size(); i++) {
            System.out.printf("Se ha sacado el mazo %s de la clase %s con id: %d de la base de datos \n",
                    mazos.get(i).getNombre(), mazos.get(i).getClase(), mazos.get(i).getId());
        }
        return mazos;
    }

    private ArrayList<Carta> getCartasFromMazo(int idmazo) {
        ArrayList<Carta> cartas = new ArrayList<Carta>();
        String[] projection = {CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDCARTA,
                CartasManagerContract.Carta_Mazo.COLUMN_NAME_CANTIDAD};
        String whereColum = CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDMAZO + "=?";
        String[] valor = {String.valueOf(idmazo)};

        Cursor c = dbRO.query(
                CartasManagerContract.Carta_Mazo.TABLE_NAME, // Nombre de la tabla
                projection, // Columnas a devolver
                whereColum, // Columnas de la cláusula WHERE
                valor, // Valores de la cláusula WHERE
                null, // Agrupamiento
                null, // Filtro por grupos
                null);
        int idcarta;
        Carta carta;
        while (c.moveToNext()) {
            idcarta = c.getInt(c.getColumnIndex(CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDCARTA));
            carta = getCartaById(idcarta - 1);
            carta.setCantidad(c.getInt(c.getColumnIndex(CartasManagerContract.Carta_Mazo.COLUMN_NAME_CANTIDAD)));
            cartas.add(carta);
        }
        c.close();
        return cartas;
    }

    public Carta getCartaById(int idcarta) {
        int posicion = 0;
        while (JSONManager.Cartas_array.get(posicion).getId() != idcarta)
            posicion++;
        return JSONManager.Cartas_array.get(posicion).clone();
    }

    public void setCantidad(int cantidad, int id) {
        // New value for one column
        int rowId = id + 1;
        ContentValues values = new ContentValues();
        values.put(CartasManagerContract.Carta.COLUMN_NAME_CANTIDAD, cantidad);

        // Which row to update, based on the ID
        String selection = CartasManagerContract.Carta._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(rowId)};

        int count = dbRO.update(
                CartasManagerContract.Carta.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        System.out.printf("Carta con id %d en la BD updateada con la cantidad %d \n", rowId, cantidad);
    }


    public static ArrayList<Carta> ordena_lista(ArrayList<Carta> cartas) {
        Carta temp;
        for (int i = 0; i < cartas.size() - 1; i++) // ordenacion por coste
            for (int j = i + 1; j < cartas.size(); j++)
                if (cartas.get(j).getCoste() < cartas.get(i).getCoste()) {
                    temp = cartas.get(i);
                    cartas.set(i, cartas.get(j));
                    cartas.set(j, temp);
                }
        for (int i = 0; i < cartas.size() - 1; i++) // ordenacion por orden alfabetico
            for (int j = i + 1; j < cartas.size(); j++)
                if (cartas.get(j).getCoste() == cartas.get(i).getCoste())
                    if (cartas.get(j).getNombre().compareTo(cartas.get(i).getNombre()) < 0) {
                        temp = cartas.get(i);
                        cartas.set(i, cartas.get(j));
                        cartas.set(j, temp);
                    }
        return cartas;
    }

    public static ArrayList<Carta> ordena_heroes(ArrayList<Carta> cartas) {
        Carta temp;
        for (int i = 0; i < cartas.size() - 1; i++) // ordenacion por orden alfabetico
            for (int j = i + 1; j < cartas.size(); j++)
                if (cartas.get(j).getClase().compareTo(cartas.get(i).getClase()) < 0) {
                    temp = cartas.get(i);
                    cartas.set(i, cartas.get(j));
                    cartas.set(j, temp);
                }
        /*for (int i = 0; i < cartas.size(); i++)
            System.out.printf("%s \n", cartas.get(i).getNombre());*/
        return cartas;
    }

    public static String getNameFromPositionClase(int position) {
        if (position == 0)
            return "druid";
        else if (position == 1)
            return "hunter";
        else if (position == 2)
            return "mage";
        else if (position == 3)
            return "paladin";
        else if (position == 4)
            return "priest";
        else if (position == 5)
            return "rogue";
        else if (position == 6)
            return "shaman";
        else if (position == 7)
            return "warlock";
        else
            return "warrior";
    }

    public static int getPositionFromNameClase(String name) {
        if (name.equals("druid"))
            return 0;
        else if (name.equals("hunter"))
            return 1;
        else if (name.equals("mage"))
            return 2;
        else if (name.equals("paladin"))
            return 3;
        else if (name.equals("priest"))
            return 4;
        else if (name.equals("rogue"))
            return 5;
        else if (name.equals("shaman"))
            return 6;
        else if (name.equals("warlock"))
            return 7;
        else
            return 8;
    }

    public static ArrayList<Carta> fotos_heroes() {
        String[] urls = new String[9];
        urls[0] = "http://hydra-media.cursecdn.com/hearthstone.gamepedia.com/thumb/6/63/Malfurion_Stormrage-f.png/250px-Malfurion_Stormrage-f.png?version=8febdbbb3c11afe80d7d4de4da134a99";
        urls[1] = "http://hydra-media.cursecdn.com/hearthstone.gamepedia.com/thumb/d/d8/Rexxar-f.png/250px-Rexxar-f.png?version=83245767b6820ef49dc494582c9c54a1";
        urls[2] = "http://hydra-media.cursecdn.com/hearthstone.gamepedia.com/thumb/7/73/Jaina_Proudmoore-f.png/250px-Jaina_Proudmoore-f.png?version=4e33b9d2ed9f179afa42c42abd2f13a1";
        urls[3] = "http://hydra-media.cursecdn.com/hearthstone.gamepedia.com/thumb/5/53/Uther_Lightbringer-f.png/250px-Uther_Lightbringer-f.png?version=39c5c298740540e3151cf58f52aa2a39";
        urls[4] = "http://hydra-media.cursecdn.com/hearthstone.gamepedia.com/thumb/b/b3/Anduin_Wrynn-f.png/250px-Anduin_Wrynn-f.png?version=592f40180a0885ba6c267dc16fc23f3b";
        urls[5] = "http://hydra-media.cursecdn.com/hearthstone.gamepedia.com/thumb/7/72/Valeera_Sanguinar-f.png/250px-Valeera_Sanguinar-f.png?version=c49b35b82c17424ab370f0247aae51db";
        urls[6] = "http://hydra-media.cursecdn.com/hearthstone.gamepedia.com/thumb/f/f4/Thrall-f.png/250px-Thrall-f.png?version=4fb3d012f5817083769f77069d982dab";
        urls[7] = "http://hydra-media.cursecdn.com/hearthstone.gamepedia.com/thumb/3/38/Guldan-f.png/250px-Guldan-f.png?version=ab6020ec133b75370d1243f05f37caf8";
        urls[8] = "http://hydra-media.cursecdn.com/hearthstone.gamepedia.com/thumb/5/5c/Garrosh_Hellscream-f.png/250px-Garrosh_Hellscream-f.png?version=d9afac26684d44d5bfda81278a16a819";
        ArrayList<Carta> cartas = new ArrayList<Carta>();
        Carta c;
        for (int i = 0; i < 9; i++) {
            c = new Carta();
            c.setUrl(urls[i]);
            cartas.add(c);
        }
        return cartas;
    }

    public static ArrayList<Carta> filtro_clase() {
        /*<array name="ClasesHearthstoneCartas" >
        <item>Druida</item>
        <item>Cazador</item>
        <item>Mago</item>
        <item>Paladín</item>
        <item>Sacerdote</item>
        <item>Pícaro</item>
        <item>Chaman</item>
        <item>Brujo</item>
        <item>Guerrero</item>
        <item>Común</item>
        </array>*/
        System.out.printf("Estoy en el filtro de clase, posicion : %d \n", position_clase);
        ArrayList<Carta> cartas_filtradas = new ArrayList<Carta>();

        int i;

        /*if (position_clase == 0) {
            for (i = 0; i < Cartas_array.size(); i++)
                    cartas_filtradas.add(Cartas_array.get(i));
        }*/
        if (position_clase == 0) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("druid"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 1) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("hunter"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 2) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("mage"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 3) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("paladin"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 4) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("priest"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 5) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("rogue"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 6) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("shaman"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 7) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("warlock"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 8) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("warrior"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("neutral"))
                    cartas_filtradas.add(Cartas_array.get(i));
        }
        return cartas_filtradas;

    }


    public static ArrayList<Carta> filtro_clase_param(int position_clase) {
        /*<array name="ClasesHearthstoneCartas" >
        <item>Druida</item>
        <item>Cazador</item>
        <item>Mago</item>
        <item>Paladín</item>
        <item>Sacerdote</item>
        <item>Pícaro</item>
        <item>Chaman</item>
        <item>Brujo</item>
        <item>Guerrero</item>
        <item>Común</item>
        </array>*/
        System.out.printf("Estoy en el filtro de clase, posicion : %d \n", position_clase);
        ArrayList<Carta> cartas_filtradas = new ArrayList<Carta>();

        int i;

        /*if (position_clase == 0) {
            for (i = 0; i < Cartas_array.size(); i++)
                    cartas_filtradas.add(Cartas_array.get(i));
        }*/
        if (position_clase == 0) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("druid"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 1) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("hunter"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 2) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("mage"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 3) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("paladin"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 4) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("priest"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 5) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("rogue"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 6) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("shaman"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 7) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("warlock"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else if (position_clase == 8) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("warrior"))
                    cartas_filtradas.add(Cartas_array.get(i));
        } else {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("neutral"))
                    cartas_filtradas.add(Cartas_array.get(i));
        }
        return cartas_filtradas;

    }


}

