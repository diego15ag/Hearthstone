package com.example.diego.hearthstone;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.diego.hearthstone.CartasManagerContract;
import com.example.diego.hearthstone.CartasManagerDbHelper;
import com.example.diego.hearthstone.LoadActivity;
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
    private CartasManagerDbHelper mDbHelper;
    private SQLiteDatabase dbRW;
    private SQLiteDatabase dbRO;
    private final String url_cards = "https://dl.dropboxusercontent.com/u/16678562/all-cards.json";
    private Context contexto;

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
    }


    public class RellenaBD_JSON extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... urls) {
            InputStream is = null;
            String result = "";
            JSONObject json = null;
            JSONArray array_cards= null;
            mDbHelper = new CartasManagerDbHelper(contexto);
            dbRW = mDbHelper.getWritableDatabase();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(urls[0]);
                //HttpPost httppost = new HttpPost(urls[0]);
                HttpResponse response = httpclient.execute(httpget);
                //HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (Exception e) {
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
            return array_cards;
        }
        protected void onPostExecute(JSONArray objeto_cartas){
            Intent intent2;
            intent2 = new Intent(contexto, LoadActivity.class);
            contexto.startActivity(intent2);
        }
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
        return cantidad;
    }


}
