package com.example.diego.hearthstone;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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


public class MainActivity extends ActionBarActivity {


    private DrawerLayout drawerLayout;
    private ListView lvDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout layoutDelDrawer;
    //json actualizado sin pesos
    //public final String url_cards = "https://dl.dropboxusercontent.com/u/16678562/all-cards.json";
    // json con algunos pesos
    public final String url_cards = "https://dl.dropboxusercontent.com/u/16678562/all-cards-updated.json";
    private String DB_FULL_PATH = "/data/data/com.example.diego.hearthstone/databases/CartasManager.db";
    private JSONManager ayudabd;

    private static boolean cargado=false;

    static ProgressDialog progressDialog;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

        if (!isOnline() || getIntent().getBooleanExtra("EXIT", false)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.disconnected_title);
            dialog.setMessage(R.string.disconnected_message);
            dialog.setCancelable(false);
            dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    System.exit(0);
                }
            });
            dialog.show();

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        sp=getSharedPreferences("Preferencias",MODE_PRIVATE);

        //Para la funcion que carga las imagenes
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .diskCacheSize(1024 * 420 * 44)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        if (isOnline()) {
            ayudabd = new JSONManager(MainActivity.this);
            if (!sp.getBoolean("BDCargada",false)) {
                progressDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.DialogLoading_title),
                        getResources().getString(R.string.DialogLoading_description));
                JSONManager.RellenaBD_JSON bd = ayudabd.new RellenaBD_JSON();
                bd.execute(url_cards, this);
            }
            else if(!cargado){
                progressDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.DialogLoading_title),
                        getResources().getString(R.string.DialogLoading_description));
                new RellenaLista_JSON().execute(url_cards);
            }
        }


        layoutDelDrawer = (LinearLayout) findViewById(R.id.layoutDelDrawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lvDrawerLayout = (ListView) findViewById(R.id.left_drawer);
        lvDrawerLayout.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.NavigationDrawerValues)));


        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);

        lvDrawerLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Intent i = new Intent(MainActivity.this, ActivityCollection.class);
                        startActivity(i);
                        break;
                    case 1:
                        Intent i1 = new Intent(MainActivity.this, CartaPersonalizadaActivity.class);
                        startActivity(i1);
                        break;
                    case 2:
                        Intent i2 = new Intent(MainActivity.this, HeroSelectionActivity.class);
                        startActivity(i2);
                        break;
                    case 3:
                        Intent i3=new Intent(MainActivity.this,MazosPredefinidosActivity.class);
                        startActivity(i3);
                        break;
                }

                drawerLayout.closeDrawer(layoutDelDrawer);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null ? true : false;
    }

    public void rellena() {

        new RellenaLista_JSON().execute(url_cards);
    }

    public class RellenaLista_JSON extends AsyncTask<String, Void, ArrayList<Carta>> {
        protected ArrayList<Carta> doInBackground(String... urls) {
            InputStream is = null;
            String result = "";
            JSONObject json = null;
            JSONArray array_cards = null;
            //JSONManager.Cartas_array.
            JSONManager ayudabd = new JSONManager(MainActivity.this);
            ayudabd.start();
            ArrayList<Carta> cartas_array = new ArrayList<Carta>();
            ArrayList<Carta> heroes_array = new ArrayList<Carta>();
            ArrayList<Mazo> mazos_nopredefinidos = new ArrayList<Mazo>();

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
                JSONObject jsonaux;
                int[] pesos = new int [9];
                int j = 0;
                //int contador_heroes = 0;
                for (int i = 0; i < array_cards.length(); i++) {
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
                        c.setObtenida(ayudabd.getObtenida(j + 1)); // la bd empieza en 1, la lista en 0
                        c.setCantidad(ayudabd.getCantidad(j + 1));
                        c.setConjunto(array_cards.getJSONObject(i).getString("set"));
                        if(array_cards.getJSONObject(i).isNull("values")==false){
                            jsonaux=array_cards.getJSONObject(i).getJSONObject("values");
                            for (int k = 0; k < jsonaux.length(); k++)
                                pesos[k] = jsonaux.getInt(String.valueOf(k));
                            c.setPesos(pesos);
                            System.out.printf("Pesos guardados en carta: %s \n",
                                    array_cards.getJSONObject(i).getString("name") );
                        }
                        cAux = c;
                        cartas_array.add(cAux);
                        //System.out.printf("la carta %s esta obtenida %d veces: \n", c.getNombre(), c.getCantidad());
                        j++;
                    } /*else if (array_cards.getJSONObject(i).getString("category").equals("hero") &&
                            array_cards.getJSONObject(i).getString("health").equals("30") && contador_heroes < 9) {
                        Carta c = new Carta();
                        c.setId(i);
                        c.setClase(array_cards.getJSONObject(i).getString("hero"));
                        c.setNombre(array_cards.getJSONObject(i).getString("name"));
                        c.setUrl(array_cards.getJSONObject(i).getString("image_url"));
                        cAux = c;
                        heroes_array.add(cAux);
                        //System.out.printf("la carta %s es un heroe con id %d \n", c.getNombre(), c.getId());
                        contador_heroes++;
                    }*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.printf("La lista tiene tamaño: %d \n", cartas_array.size());
            JSONManager.Cartas_array = JSONManager.ordena_lista(cartas_array);
            //JSONManager.Heroes_array = JSONManager.ordena_heroes(heroes_array);
            JSONManager.Heroes_array = JSONManager.fotos_heroes();
            JSONManager.Mazos_array = ayudabd.getMazosNoPredefinidos();

            if(JSONManager.control==1)
                for(int i=0; i< JSONManager.declaraMazosPredefinidos().size();i++)
                    ayudabd.creaMazo(JSONManager.declaraMazosPredefinidos().get(i));
            JSONManager.Mazos_predefinidos_array = ayudabd.getMazosPredefinidos();
            return cartas_array;
        }

        protected void onPostExecute(ArrayList<Carta> objeto_cartas) {

            System.out.printf("Lista cargada \n");
            cargado=true;
            progressDialog.dismiss();
            //finish();
        }

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


}
