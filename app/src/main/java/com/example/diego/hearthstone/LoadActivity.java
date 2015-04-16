package com.example.diego.hearthstone;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.diego.hearthstone.R;
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


public class LoadActivity extends ActionBarActivity {
    public final String url_cards = "https://dl.dropboxusercontent.com/u/16678562/all-cards.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        new RellenaLista_JSON().execute(url_cards);
    }


    public class RellenaLista_JSON extends AsyncTask<String, Void, ArrayList<Carta> > {
        protected ArrayList<Carta> doInBackground(String... urls) {
            InputStream is = null;
            String result = "";
            JSONObject json = null;
            JSONArray array_cards= null;
            //JSONManager.Cartas_array.
            JSONManager ayudabd= new JSONManager(LoadActivity.this);
            ayudabd.start();
            ArrayList<Carta> cartas_array = new ArrayList<Carta>();

            //CopyOnWriteArrayList<Carta> Cartas_array= new CopyOnWriteArrayList<Carta>();
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
                Carta cAux;

                int j=0;
                for (int i =0; i< array_cards.length() ; i++) {
                    if (array_cards.getJSONObject(i).getString("category").equals("hero") == false &&
                            array_cards.getJSONObject(i).getString("category").equals("ability") == false
                            && array_cards.getJSONObject(i).getString("collectible").equals("true") == true) {
                        Carta c = new Carta();
                        c.setId(j);
                        c.setClase(array_cards.getJSONObject(i).getString("hero"));
                        c.setNombre(array_cards.getJSONObject(i).getString("name"));
                        //System.out.printf("Carta %s entrando iteracion %d \n", array_cards.getJSONObject(i).getString("name"), i);
                        c.setUrl(array_cards.getJSONObject(i).getString("image_url"));
                        c.setTipo(array_cards.getJSONObject(i).getString("quality"));
                        c.setCoste(array_cards.getJSONObject(i).getInt("mana"));
                        c.setObtenida(ayudabd.getObtenida(j+1)); // la bd empieza en 1, la lista en 0
                        c.setCantidad(ayudabd.getCantidad(j+1));
                        cAux=c;
                        cartas_array.add(cAux);
                        //System.out.printf("la carta %s esta obtenida %d veces: \n", c.getNombre(), c.getCantidad());
                        j++;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.printf("La lista tiene tamaño: %d \n", cartas_array.size());
            JSONManager.Cartas_array=cartas_array;
            return cartas_array;
        }
        protected void onPostExecute(ArrayList<Carta> objeto_cartas){
            /*for (int i =0; i< objeto_cartas.size() ; i++)
                System.out.printf("Carta %s en posicion %d en memoria \n", objeto_cartas.get(i).getNombre(), i);*/
            /*for (int i=0; i< objeto_cartas.size(); i++) {
                new DownloadImageTask(imagen)
                        .execute(objeto_cartas.get(i).getUrl(), objeto_cartas.get(i).getNombre());
            }*/

            System.out.printf("Hola estoy antes del finish \n");
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
