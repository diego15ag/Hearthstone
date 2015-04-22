package com.example.diego.hearthstone;

import android.content.ContentValues;
import android.content.Context;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Created by blukstack on 01/04/2015.
 */
public class JSONManager {
    public static ArrayList<Carta> Cartas_array;
    public static ArrayList<Mazo> Mazos_array;
    public static ArrayList<Carta> Heroes_array;
    private CartasManagerDbHelper mDbHelper;
    private SQLiteDatabase dbRW;
    private SQLiteDatabase dbRO;
    private final String url_cards = "https://dl.dropboxusercontent.com/u/16678562/all-cards.json";
    private Context contexto;
    public static int position_clase=0;

    public JSONManager(Context context){
        contexto = context;
        //new RellenaBD_JSON().execute(url_cards);
        //new RellenaLista_JSON().execute(url_cards);
    }

    public JSONManager(){
        //new RellenaLista_JSON().execute(url_cards);

    }

    public void start(){
        mDbHelper = new CartasManagerDbHelper(contexto);
        dbRO = mDbHelper.getReadableDatabase();
        dbRW = mDbHelper.getWritableDatabase();
    }
    public void startBG(){
        new initparamsBD().execute();
    }

    public class initparamsBD extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            mDbHelper = new CartasManagerDbHelper(contexto);
            dbRO = mDbHelper.getReadableDatabase();
            return null;
        }
    }


    public class RellenaBD_JSON extends AsyncTask<Object, Void, Void> {

        private Handler mHandle=new Handler();

        protected Void doInBackground(Object... obj) {

            final MainActivity ma= (MainActivity) obj[1];


            InputStream is = null;
            String result = "";
            JSONObject json = null;
            JSONArray array_cards= null;
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
                for (int i =0; i< array_cards.length() ; i++)  // insercion en BD
                    if(array_cards.getJSONObject(i).getString("category").equals("hero")==false &&
                            array_cards.getJSONObject(i).getString("category").equals("ability") == false
                            && array_cards.getJSONObject(i).getString("collectible").equals("true")==true)
                        inserta_carta(array_cards.getJSONObject(i).getString("name"), array_cards.getJSONObject(i).getString("hero"),
                                false, 0 );
            } catch (JSONException e) {
                e.printStackTrace();
            }


            mHandle.post(new Runnable() {
                @Override
                public void run() {
                    ma.rellena();
                }
            });

            return null;
        }

        /*protected void onPostExecute(JSONArray objeto_cartas){
            Intent intent2;
            intent2 = new Intent(contexto, LoadActivity.class);
            contexto.startActivity(intent2);
        }*/
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String texto_content;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            //texto_content=urls[1];
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            //texto.setText(texto_content);
            bmImage.setImageBitmap(result);

        }
    }


    public void inserta_carta(String name, String clase, boolean obtenida, int cantidad){
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
    public void inserta_mazo(String nombre, String predefinido, String clase){
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
    public void inserta_carta_mazo(int id_carta, int id_mazo){
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

    public  boolean getObtenida (int id){
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(id);
        String strI = sb.toString();
        String[] projection = { CartasManagerContract.Carta.COLUMN_NAME_OBTENIDA };
        String whereColum = CartasManagerContract.Carta._ID+ "=?";
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

    public int getCantidad (int id){
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(id);
        String strI = sb.toString();
        String[] projection = { CartasManagerContract.Carta.COLUMN_NAME_CANTIDAD };
        String whereColum = CartasManagerContract.Carta._ID+ "=?";
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

    public void creaMazo(Mazo mazo){
        ContentValues values = new ContentValues();
        values.put(CartasManagerContract.Mazo.COLUMN_NAME_NAME, mazo.getNombre());
        values.put(CartasManagerContract.Mazo.COLUMN_NAME_PREDEFINIDO, 0);
        values.put(CartasManagerContract.Mazo.COLUMN_NAME_CLASS, mazo.getClase());
        long newRowId;
        newRowId = dbRW.insert(
                CartasManagerContract.Mazo.TABLE_NAME,
                null,
                values
        );
        System.out.printf("La nueva fila es: %d \n", newRowId);
        System.out.printf("Mazo %s insertado \n", mazo.getNombre());
        ContentValues valuescartas = new ContentValues();
        for (int i=0; i< mazo.getCartas().size();i++) {
            valuescartas.put(CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDCARTA,
                    mazo.getCartas().get(i).getId());
            valuescartas.put(CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDMAZO, (int)newRowId);
            dbRW.insert(
                    CartasManagerContract.Carta_Mazo.TABLE_NAME,
                    null,
                    valuescartas
            );
            System.out.printf("Carta %s insertada en el mazo %s \n",  mazo.getCartas().get(i).getNombre(), mazo.getNombre());
        }

    }

    public ArrayList<Mazo> getMazosNoPredefinidos(){
        //System.out.printf("estoy en mazos no predefinidos \n");
        String[] projection = { CartasManagerContract.Mazo._ID, CartasManagerContract.Mazo.COLUMN_NAME_NAME
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
        while (c.moveToNext()){
            Mazo m;
            id=c.getInt(c.getColumnIndex(CartasManagerContract.Mazo._ID));
            nombre=c.getString(c.getColumnIndex(CartasManagerContract.Mazo.COLUMN_NAME_NAME));
            predefinido=c.getInt(c.getColumnIndex(CartasManagerContract.Mazo.COLUMN_NAME_PREDEFINIDO));
            if (predefinido==0)
                predefinido2=false;
            else
                predefinido2=true;
            clase=c.getString(c.getColumnIndex(CartasManagerContract.Mazo.COLUMN_NAME_CLASS));
            m = new Mazo(id, nombre, predefinido2 , clase, getCartasFromMazo(id));
            mazos.add(m);
        }
        c.close();
        for (int i=0; i < mazos.size(); i++) {
            System.out.printf("El mazo %s de la clase %s tiene id: %d \n",
                    mazos.get(i).getNombre(), mazos.get(i).getClase(), mazos.get(i).getId());
            for (int j=0; j<mazos.get(i).getCartas().size(); j++)
                System.out.printf("Carta: %s \n", mazos.get(i).getCartas().get(j).getNombre());
        }


        return mazos;
    }

    private ArrayList<Carta> getCartasFromMazo(int idmazo){
        ArrayList<Carta> cartas = new ArrayList<Carta>();
        String[] projection = { CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDCARTA };
        String whereColum = CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDMAZO+ "=?";
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
        while (c.moveToNext()) {
            idcarta = c.getInt(c.getColumnIndex(CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDCARTA));
            cartas.add(getCartaById(idcarta-1));
        }
        c.close();
        return cartas;
    }

    private Carta getCartaById(int idcarta){
        int posicion = 0;
        while (JSONManager.Cartas_array.get(posicion).getId() != idcarta)
            posicion++;
        return JSONManager.Cartas_array.get(posicion);
    }

    public void setCantidad(int cantidad, int id){
        // New value for one column
        int rowId = id+1;
        ContentValues values = new ContentValues();
        values.put(CartasManagerContract.Carta.COLUMN_NAME_CANTIDAD, cantidad);

       // Which row to update, based on the ID
        String selection = CartasManagerContract.Carta._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(rowId) };

        int count = dbRO.update(
                CartasManagerContract.Carta.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        System.out.printf("Carta con id %d en la BD updateada con la cantidad %d \n", rowId, cantidad);
    }


    public static ArrayList<Carta> ordena_lista(ArrayList<Carta> cartas){
        Carta temp;
        for (int i=0; i < cartas.size() -1 ; i++) // ordenacion por coste
            for (int j=i+1; j < cartas.size(); j++)
                if(cartas.get(j).getCoste()< cartas.get(i).getCoste()){
                    temp = cartas.get(i);
                    cartas.set(i,cartas.get(j) );
                    cartas.set(j, temp);
                             }
        for (int i=0; i < cartas.size() -1 ; i++) // ordenacion por orden alfabetico
            for (int j=i+1; j < cartas.size(); j++)
                if(cartas.get(j).getCoste() == cartas.get(i).getCoste())
                    if(cartas.get(j).getNombre().compareTo(cartas.get(i).getNombre())<0 ) {
                        temp = cartas.get(i);
                        cartas.set(i, cartas.get(j));
                        cartas.set(j, temp);
                    }
        return cartas;
    }

    public static ArrayList<Carta> ordena_heroes(ArrayList<Carta> cartas) {
        Carta temp;
        for (int i=0; i < cartas.size() -1 ; i++) // ordenacion por orden alfabetico
            for (int j=i+1; j < cartas.size(); j++)
                    if(cartas.get(j).getClase().compareTo(cartas.get(i).getClase())<0 ) {
                        temp = cartas.get(i);
                        cartas.set(i, cartas.get(j));
                        cartas.set(j, temp);
                    }
        /*for (int i = 0; i < cartas.size(); i++)
            System.out.printf("%s \n", cartas.get(i).getNombre());*/
        return cartas;
    }

    public static ArrayList<Carta> filtro_clase(){
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
        }
        else if (position_clase == 1) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("hunter"))
                    cartas_filtradas.add(Cartas_array.get(i));
        }
        else if (position_clase == 2) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("mage"))
                    cartas_filtradas.add(Cartas_array.get(i));
        }
        else if (position_clase == 3) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("paladin"))
                    cartas_filtradas.add(Cartas_array.get(i));
        }
        else if (position_clase == 4) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("priest"))
                    cartas_filtradas.add(Cartas_array.get(i));
        }
        else if (position_clase == 5) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("rogue"))
                    cartas_filtradas.add(Cartas_array.get(i));
        }
        else if (position_clase == 6) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("shaman"))
                    cartas_filtradas.add(Cartas_array.get(i));
        }
        else if (position_clase == 7) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("warlock"))
                    cartas_filtradas.add(Cartas_array.get(i));
        }
        else if (position_clase == 8) {
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("warrior"))
                    cartas_filtradas.add(Cartas_array.get(i));
        }
        else{
            for (i = 0; i < Cartas_array.size(); i++)
                if (Cartas_array.get(i).getClase().equals("neutral"))
                    cartas_filtradas.add(Cartas_array.get(i));
        }
        return cartas_filtradas;

    }


}

