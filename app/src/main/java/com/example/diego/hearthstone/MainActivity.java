package com.example.diego.hearthstone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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


public class MainActivity extends ActionBarActivity implements  CartasFragment.Callbacks,MazosFragment.Callbacks,DetallesMazosPredefinidoFragment.OnFragmentInteractionListener{


    private DrawerLayout drawerLayout;
    private ListView lvDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout layoutDelDrawer;
    private Toolbar toolbar;
    //json actualizado sin pesos
    //public final String url_cards = "https://dl.dropboxusercontent.com/u/16678562/all-cards.json";
    // json con algunos pesos
    public final String url_cards = "https://dl.dropboxusercontent.com/u/16678562/all-cards-updated.json";
    private String DB_FULL_PATH = "/data/data/com.example.diego.hearthstone/databases/CartasManager.db";
    private JSONManager ayudabd;

    private static boolean cargado=false;

    private static boolean cargando=false;
    static ProgressDialog progressDialog;

    SharedPreferences sp;

    RellenaLista_JSON rellenaLista_json;
    JSONManager.RellenaBD_JSON bd;


    ViewPager pager;
    ViewPagerAdapter VPadapter;
    SlidingTabLayout tabs;
    CharSequence Titles[];
    int Numboftabs =2;
    private boolean landscape;

    AlertDialog.Builder dialog;

    JSONManager jsonhelp;

    public final int CARTA_RESULTADO=1;
    public final int MAZO_RESULTADO=2;



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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_activity_mazos));
        }

        if(cargando)
            progressDialog= ProgressDialog.show(MainActivity.this, getResources().getString(R.string.DialogLoading_description),
                    getResources().getString(R.string.DialogLoading_description));


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

        if(findViewById(R.id.fragmentContainer)!=null)
            landscape=true;
        else landscape=false;

        if (isOnline()) {
            ayudabd = new JSONManager(MainActivity.this);

            if (!sp.getBoolean("BDCargada",false)) {
                progressDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.DialogLoading_title),
                        getResources().getString(R.string.DialogLoading_description));
                bd = ayudabd.new RellenaBD_JSON();
                bd.execute(url_cards, this);

                //Bloqueamos la orientacion
                int currentOrientation = getResources().getConfiguration().orientation;
                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                }
                else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }
            }
            else if(!cargado){
                progressDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.DialogLoading_title),
                        getResources().getString(R.string.DialogLoading_description));
                rellenaLista_json= (RellenaLista_JSON) new RellenaLista_JSON().execute(url_cards);
            }
            else
                cargarVistaActividad();
        }



        layoutDelDrawer = (LinearLayout) findViewById(R.id.layoutDelDrawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lvDrawerLayout = (ListView) findViewById(R.id.left_drawer);
        lvDrawerLayout.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.NavigationDrawerValues)));


        if(toolbar!=null) {
            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

                /**
                 * Called when a drawer has settled in a completely closed state.
                 */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);

                }

                /**
                 * Called when a drawer has settled in a completely open state.
                 */
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
                            Intent i3 = new Intent(MainActivity.this, MazosPredefinidosActivity.class);
                            startActivity(i3);
                            break;
                    }

                    drawerLayout.closeDrawer(layoutDelDrawer);

                }
            });
        }

    }

    public void cargarVistaActividad(){

        jsonhelp= new JSONManager(getApplicationContext());
        jsonhelp.startBG();

        Titles = getResources().getStringArray(R.array.TabTitles);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        VPadapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        CartasFragment saver = (CartasFragment) getLastCustomNonConfigurationInstance();
        if (saver != null) {
            VPadapter.cartasFragment=saver;
        }

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(VPadapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);


        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 1) {
                    //Pestaña mazos

                    if (landscape) {
                        if (JSONManager.Mazos_array.size() > 0) {
                            DetallesMazosPredefinidoFragment detallesMazosPredefinidoFragment = DetallesMazosPredefinidoFragment.newInstance(JSONManager.Mazos_array.get(0));
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, detallesMazosPredefinidoFragment).commit();
                        }
                        else
                            getSupportFragmentManager().beginTransaction()
                                    .remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
                    }

                } else if (i == 0) {
                    //Pestaña cartas
                    if (landscape) {
                        Carta carta;


                        if (VPadapter.cartasFragment.cartaselect == null)
                            carta = JSONManager.filtro_clase().get(0);

                        else carta = VPadapter.cartasFragment.cartaselect;

                        DetallesCartaFragment detallesCartaFragment = DetallesCartaFragment.
                                newInstance(carta);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, detallesCartaFragment).commit();


                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });


        if(findViewById(R.id.fragmentContainer)!=null)
            landscape=true;
        else landscape=false;

        if(landscape) {
            //Para que se muestre en el fragmento detalles al iniciarse la primera carta
            if (pager.getCurrentItem() == 0) {
                //JSONManager.position_clase=0;

                DetallesCartaFragment detallesCartaFragment = DetallesCartaFragment.
                        newInstance(JSONManager.filtro_clase().get(0));
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, detallesCartaFragment).commit();
            } else if (pager.getCurrentItem() == 1) {
                if (JSONManager.Mazos_array.size() > 0) {
                    DetallesMazosPredefinidoFragment detallesMazosPredefinidoFragment = DetallesMazosPredefinidoFragment.newInstance(JSONManager.Mazos_array.get(0));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, detallesMazosPredefinidoFragment).commit();
                } else
                    getSupportFragmentManager().beginTransaction()
                            .remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();

            }
        }

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {

        if (cargado&&VPadapter.cartasFragment!=null)
            return VPadapter.cartasFragment;

        else return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.action_max_cards){
            mostrarDialogoCartasAlMaxMin(true);
            return true;
        }
        else if(id==R.id.action_min_cards){
            mostrarDialogoCartasAlMaxMin(false);
            return true;
        }

        else if(id==R.id.action_filter){
            mostrarDialogoClase(pager.getCurrentItem());
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

    /*private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null ? true : false;
    }*/

    private void mostrarDialogoCartasAlMaxMin(final boolean opcion) {
        //false minimo
        //true maximo

        dialog = new AlertDialog.Builder(this);

        dialog.setTitle(R.string.Dialog_max_cards_title);

        if (opcion){
            dialog.setMessage(R.string.Dialog_max_cards_description);
        }
        else{
            dialog.setMessage(R.string.Dialog_min_cards_description);
        }
        dialog.setCancelable(false);

        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                progressDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.DialogLoading_description),
                        getResources().getString(R.string.DialogLoading_description));
                cargando = true;

                //Operacion
                new FijarMaxMin().execute(opcion);


                dialog.dismiss();


            }
        });

        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    //metodo que muestra el dialogo de seleccion de clase
    private void mostrarDialogoClase(int seleccion){
        final Dialog d = new Dialog(MainActivity.this);

        String [] contenido={};

        //Si estamos en la pestaña de cartas
        if(seleccion==0) {
            //Para mostrar las opciones que se corresponderian con carta
            d.setTitle(getResources().getString(R.string.select_clase));
            d.setContentView(R.layout.dialogo_sel_clase);

            contenido=getResources().getStringArray(R.array.ClasesHearthstoneCartas);
            ListView lvSeleccion= (ListView) d.findViewById(R.id.lvSeleccionClase);
            lvSeleccion.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,contenido));
            lvSeleccion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //Cambiar el icono en la toolbar de la clase
                    ActionMenuItemView item= (ActionMenuItemView) findViewById(R.id.action_filter);

                    if(position==0)
                        item.setIcon(getResources().getDrawable(R.mipmap.druida));

                    else if(position==1)
                        item.setIcon(getResources().getDrawable(R.mipmap.cazador));

                    else if(position==2)
                        item.setIcon(getResources().getDrawable(R.mipmap.mago));

                    else if(position==3)
                        item.setIcon(getResources().getDrawable(R.mipmap.paladin));

                    else if(position==4)
                        item.setIcon(getResources().getDrawable(R.mipmap.sacerdote));

                    else if(position==5)
                        item.setIcon(getResources().getDrawable(R.mipmap.picaro));

                    else if(position==6)
                        item.setIcon(getResources().getDrawable(R.mipmap.chaman));

                    else if(position==7)
                        item.setIcon(getResources().getDrawable(R.mipmap.brujo));

                    else if(position==8)
                        item.setIcon(getResources().getDrawable(R.mipmap.guerrero));

                    else
                        item.setIcon(getResources().getDrawable(R.mipmap.hearthstone_logo));

                    //Filtrar las cartas por su clase
                    JSONManager.position_clase=position;
                    new FiltraLista().execute();

                    //Para mostrar la primera carta en el fragento detalles
                    if(landscape){
                        DetallesCartaFragment detallesCartaFragment=DetallesCartaFragment.
                                newInstance(JSONManager.filtro_clase().get(0));
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, detallesCartaFragment).commit();
                    }

                    //Se cierra el dialogo
                    d.dismiss();
                }
            });

            d.show();
        }

    }

    //Si se selecciona una carta en la pestaña de cartas
    @Override
    public void onCardSelected(Carta carta) {
        //Habria que ver si se inicia un fragmento o una actividad
        Log.i("carta", carta.getNombre());

        //Si no estamos en landscape abrimos una actividad
        if(!landscape) {
            Intent i = new Intent(MainActivity.this, DetallesCartaActivity.class);
            i.putExtra(DetallesCartaActivity.imagen, carta);
            startActivityForResult(i, CARTA_RESULTADO);
        }
        //Si si lo estamos modificamos el fragmento
        else{
            DetallesCartaFragment detallesCartaFragment=DetallesCartaFragment.newInstance(carta);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, detallesCartaFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (pager != null)
            if (pager.getCurrentItem() == 1 && landscape) {
                if (JSONManager.Mazos_array.size() > 0) {
                    DetallesMazosPredefinidoFragment detallesMazosPredefinidoFragment = DetallesMazosPredefinidoFragment.newInstance(JSONManager.Mazos_array.get(0));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, detallesMazosPredefinidoFragment).commit();
                } else {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                    if (fragment != null)
                        getSupportFragmentManager().beginTransaction()
                                .remove(fragment).commit();
                }
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CARTA_RESULTADO){
            if(landscape){
                Carta c= (Carta) data.getSerializableExtra(DetallesCartaActivity.CARTA);

                DetallesCartaFragment detallesCartaFragment=DetallesCartaFragment.newInstance(c);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, detallesCartaFragment).commit();

                VPadapter.cartasFragment.recyclerView.scrollToPosition(0);
            }
        }else if (requestCode==MAZO_RESULTADO){
            if(pager!=null) {
                if (landscape) {
                    if (JSONManager.Mazos_array.size() > 0) {
                        DetallesMazosPredefinidoFragment detallesMazosPredefinidoFragment = DetallesMazosPredefinidoFragment.newInstance(JSONManager.Mazos_array.get(0));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, detallesMazosPredefinidoFragment).commit();
                    } else {
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                        if(fragment!=null)
                            getSupportFragmentManager().beginTransaction()
                                .remove(fragment).commit();
                    }
                }

                pager.setCurrentItem(1);
            }

        }

    }

    @Override
    public void onNewDeck() {
        final Dialog d = new Dialog(MainActivity.this);

        String[] contenido = {};

        //Si estamos en la pestaña de cartas
        //Para mostrar las opciones que se corresponderian con carta
        d.setTitle(getResources().getString(R.string.select_clase));
        d.setContentView(R.layout.dialogo_sel_clase);

        contenido = getResources().getStringArray(R.array.ClasesHearthstone);
        ListView lvSeleccion = (ListView) d.findViewById(R.id.lvSeleccionClase);
        lvSeleccion.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contenido));
        lvSeleccion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Cambiar el icono en la toolbar de la clase
                ActionMenuItemView item = (ActionMenuItemView) findViewById(R.id.action_filter);


                //Se cierra el dialogo
                if(NuevoMazoActivity.cartas!=null)
                    NuevoMazoActivity.cartas = new ArrayList<Carta>();
                Intent i = new Intent(MainActivity.this, NuevoMazoActivity.class);
                i.putExtra(NuevoMazoActivity.mazoClase, position);
                i.putExtra(NuevoMazoActivity.referencia, -1);
                i.putExtra("NombreMazo", "");
                startActivityForResult(i, MAZO_RESULTADO);
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    public void onMazoSelected(Mazo m) { // detalles mazo
        if(landscape){

            DetallesMazosPredefinidoFragment detallesMazosPredefinidoFragment = DetallesMazosPredefinidoFragment.newInstance(m);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, detallesMazosPredefinidoFragment).commit();

        }
    }

    @Override
    public void editaMazo(Mazo m) {
        Intent i = new Intent(this, NuevoMazoActivity.class);
        i.putExtra(NuevoMazoActivity.mazoClase, JSONManager.getPositionFromNameClase(m.getClase()));
        i.putExtra(NuevoMazoActivity.referencia, m.getId());
        i.putExtra("NombreMazo", m.getNombre());
        NuevoMazoActivity.cartas= m.getCartas();
        startActivity(i);
    }

    @Override
    public void detallesCarta(Carta c) {
        Intent i= new Intent(MainActivity.this,MuestraCartaActivity.class);
        i.putExtra(MuestraCartaActivity.CARTA,c);
        startActivity(i);
    }

    public class FiltraLista extends AsyncTask<Void, Void, ArrayList<Carta>> {

        @Override
        protected ArrayList<Carta> doInBackground(Void... params) {
            return JSONManager.filtro_clase();
        }

        protected void onPostExecute(ArrayList<Carta> cartas_filtradas){

            VPadapter.cartasFragment.cambiarLista(cartas_filtradas);

        }
    }

    public class FijarMaxMin extends AsyncTask<Boolean, Void,ArrayList<Carta>> {

        @Override
        protected ArrayList<Carta> doInBackground(Boolean... params) {
            boolean opcion= params[0];

            for(int i=0;i<JSONManager.Cartas_array.size();i++){

                int cantidad;

                if(!opcion)
                    cantidad=0;

                else if(JSONManager.Cartas_array.get(i).getTipo().equals("legendary"))
                    cantidad=1;
                else
                    cantidad=2;

                JSONManager.Cartas_array.get(i).setCantidad(cantidad);
                jsonhelp.setCantidad(cantidad, JSONManager.Cartas_array.get(i).getId());

            }

            return JSONManager.filtro_clase();
        }

        protected void onPostExecute(ArrayList<Carta> cartas_filtradas){

            VPadapter.cartasFragment.cambiarLista(cartas_filtradas);
            progressDialog.dismiss();
            cargando=false;
        }
    }

    public void rellena() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
                            for (int k = 0; k < jsonaux.length(); k++) {
                                pesos[k] = jsonaux.getInt(String.valueOf(k));
                                /*System.out.printf("Peso %d con valor %d guardado en carta: %s \n", k,
                                        jsonaux.getInt(String.valueOf(k)) ,
                                        array_cards.getJSONObject(i).getString("name"));*/
                            }
                            c.setPesos(pesos);
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

            cargarVistaActividad();
            cargado=true;
            progressDialog.dismiss();
        }

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(progressDialog!=null)
            progressDialog.dismiss();

        if(rellenaLista_json!=null)
            rellenaLista_json.cancel(true);
        if(bd!=null)
            bd.cancel(true);

    }

    }


