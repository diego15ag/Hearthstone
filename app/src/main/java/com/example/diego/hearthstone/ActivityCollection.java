package com.example.diego.hearthstone;

import android.app.Dialog;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;


public class ActivityCollection extends ActionBarActivity implements  CartasFragment.Callbacks,MazosFragment.Callbacks{

    //para el drawer
    private DrawerLayout drawerLayout;
    private ListView lvDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout layoutDelDrawer;

    ViewPager pager;
    ViewPagerAdapter VPadapter;
    SlidingTabLayout tabs;
    CharSequence Titles[];
    int Numboftabs =2;
    private boolean landscape;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }

        if(findViewById(R.id.fragmentContainer)!=null)
            landscape=true;
        else landscape=false;


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
                    if(landscape){
                        DetallesCartaFragment detallesCartaFragment=DetallesCartaFragment.
                                newInstance(JSONManager.filtro_clase().get(0));
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.fragmentContainer, detallesCartaFragment).commit();
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
                                .add(R.id.fragmentContainer, detallesCartaFragment).commit();


                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });


        //Codigo para el drawer
        layoutDelDrawer = (LinearLayout) findViewById(R.id.layoutDelDrawer);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        lvDrawerLayout= (ListView) findViewById(R.id.left_drawer);
        lvDrawerLayout.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.NavigationDrawerValues)));


        mDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close) {

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

                switch (position){
                    case 0:
                        break;
                    case 1:
                        Intent i1=new Intent(ActivityCollection.this,CartaPersonalizadaActivity.class);
                        startActivity(i1);
                        break;
                    case 2:
                        Intent i2=new Intent(ActivityCollection.this, HeroSelectionActivity.class);
                        startActivity(i2);
                        break;
                }

                drawerLayout.closeDrawer(layoutDelDrawer);

            }
        });


        //Para que se muestre en el fragmento detalles al iniciarse la primera carta
        if(pager.getCurrentItem()==0&&landscape){
            //JSONManager.position_clase=0;

            DetallesCartaFragment detallesCartaFragment=DetallesCartaFragment.
                    newInstance(JSONManager.filtro_clase().get(0));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, detallesCartaFragment).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_collection, menu);

        return true;
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return VPadapter.cartasFragment;
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

        else if(id==R.id.action_filter){
            mostrarDialogoClase(pager.getCurrentItem());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Estos dos metodos son para el drawer
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

    //metodo que muestra el dialogo de seleccion de clase
    private void mostrarDialogoClase(int seleccion){
        final Dialog d = new Dialog(ActivityCollection.this);

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
                                .add(R.id.fragmentContainer, detallesCartaFragment).commit();
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
        Log.i("carta",carta.getNombre());

        //Si no estamos en landscape abrimos una actividad
        if(!landscape) {
            Intent i = new Intent(ActivityCollection.this, DetallesCartaActivity.class);
            i.putExtra(DetallesCartaActivity.imagen, carta.getUrl());
            startActivity(i);
        }
        //Si si lo estamos modificamos el fragmento
        else{
            DetallesCartaFragment detallesCartaFragment=DetallesCartaFragment.newInstance(carta);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, detallesCartaFragment).commit();
        }
    }

    @Override
    public void onNewDeck() {
        Intent i = new Intent(ActivityCollection.this,NuevoMazoActivity.class);
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
}
