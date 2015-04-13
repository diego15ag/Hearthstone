package com.example.diego.hearthstone;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;


public class ActivityMazos extends ActionBarActivity {

    //para el drawer
    private DrawerLayout drawerLayout;
    private ListView lvDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout layoutDelDrawer;

    private RecyclerView rvListaMazos;
    //private RecyclerViewAdapter rva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mazos);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }

        //Quitamos el titulo a la toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        //---------------------Este codigo era para poner el spinner el la toolbar

        //Accedemos al spinner de la toolbar y le indicamos los valores disponibles
        //Spinner spinner= (Spinner) findViewById(R.id.spinner_nav);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                //R.array.ClasesHearthstone, android.R.layout.simple_spinner_item);

        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setAdapter(adapter);

        //Habria que completar con el codigo para obtener las cartas de la clase
        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        //------------------------------------------------


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

                //switch donde se actua dependiendo de la opcion escogida

                drawerLayout.closeDrawer(layoutDelDrawer);

            }
        });

        rvListaMazos = (RecyclerView) findViewById(R.id.recycler_lista_mazos);
        rvListaMazos.setLayoutManager(new GridLayoutManager(this, 1));
        //rva=new RecyclerViewAdapter(courses);
        //rva.setClickListener(this);
        //rvListaMazos.setAdapter(rva);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_mazos, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Aqui se actuaria de acuerdo al texto introducido en la barra de buscar
                Log.i("texto",s);
                return false;
            }
        });

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
        else if(id==R.id.action_filter){
            //Mostrar dialogo
            mostrarDialogoClase(0);
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

    private void mostrarDialogoClase(int seleccion){
        final Dialog d = new Dialog(ActivityMazos.this);

        String [] contenido={};

        if(seleccion==0) {
            //Para mostrar las opciones que se corresponderian con mazos
            d.setTitle(getResources().getString(R.string.select_clase));
            d.setContentView(R.layout.dialogo_sel_clase);

            contenido=getResources().getStringArray(R.array.ClasesHearthstone);
            ListView lvSeleccion= (ListView) d.findViewById(R.id.lvSeleccionClase);
            lvSeleccion.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,contenido));
            lvSeleccion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    d.cancel();
                }
            });
        }


        d.show();

    }
}

