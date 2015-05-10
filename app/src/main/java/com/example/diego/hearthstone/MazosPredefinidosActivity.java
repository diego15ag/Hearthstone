package com.example.diego.hearthstone;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


public class MazosPredefinidosActivity extends ActionBarActivity implements MazosPredefinidosFragment.OnFragmentInteractionListener,DetallesMazosPredefinidoFragment.OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    private ListView lvDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout layoutDelDrawer;
    private boolean landscape=false;

    private static int DETALLES=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mazos_predefinidos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }



        //Codigo para el drawer
        layoutDelDrawer = (LinearLayout) findViewById(R.id.layoutDelDrawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lvDrawerLayout = (ListView) findViewById(R.id.left_drawer);
        lvDrawerLayout.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.NavigationDrawerValues)));


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
                        Intent i = new Intent(MazosPredefinidosActivity.this, ActivityCollection.class);
                        startActivity(i);
                        break;
                    case 1:
                        Intent i1 = new Intent(MazosPredefinidosActivity.this, CartaPersonalizadaActivity.class);
                        startActivity(i1);
                        break;
                    case 2:
                        Intent i2 = new Intent(MazosPredefinidosActivity.this, HeroSelectionActivity.class);
                        startActivity(i2);
                        break;
                }
                drawerLayout.closeDrawer(layoutDelDrawer);
            }
        });

        FrameLayout fragmentDetallesContainer= (FrameLayout) findViewById(R.id.containerDetallesMazosPred);
        if(fragmentDetallesContainer!=null) {
            landscape = true;
            if(JSONManager.Mazos_predefinidos_array.size()>0) {
                DetallesMazosPredefinidoFragment detallesMazosPredefinidoFragment = DetallesMazosPredefinidoFragment.newInstance(JSONManager.Mazos_predefinidos_array.get(0));
                getSupportFragmentManager().beginTransaction().replace(R.id.containerDetallesMazosPred, detallesMazosPredefinidoFragment).commit();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mazos_predefinidos, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        MazosPredefinidosFragment mazosPredefinidosFragment= (MazosPredefinidosFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        mazosPredefinidosFragment.actualiza();

        if(landscape) {
            DetallesMazosPredefinidoFragment detallesMazosPredefinidoFragment = (DetallesMazosPredefinidoFragment) getSupportFragmentManager().findFragmentById(R.id.containerDetallesMazosPred);
            int id_mazo = detallesMazosPredefinidoFragment.getIdMazo();
            detallesMazosPredefinidoFragment.actualiza(JSONManager.Mazos_predefinidos_array.get(id_mazo-1));

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

    @Override
    public void muestraMazo(Mazo m) {
        if(landscape){
            DetallesMazosPredefinidoFragment detallesMazosPredefinidoFragment=DetallesMazosPredefinidoFragment.newInstance(m);
            getSupportFragmentManager().beginTransaction().replace(R.id.containerDetallesMazosPred,detallesMazosPredefinidoFragment).commit();
        }

        else {
            Intent i = new Intent(MazosPredefinidosActivity.this, DetallesMazoActivity.class);
            i.putExtra(DetallesMazoActivity.MAZO, m);
            startActivityForResult(i, DETALLES);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==DETALLES&&resultCode==DetallesMazoActivity.DETALLES_MAZO_OK&&landscape){
            Mazo m= (Mazo) data.getSerializableExtra(DetallesMazoActivity.MAZO);
            DetallesMazosPredefinidoFragment detallesMazosPredefinidoFragment=DetallesMazosPredefinidoFragment.newInstance(m);
            getSupportFragmentManager().beginTransaction().replace(R.id.containerDetallesMazosPred, detallesMazosPredefinidoFragment).commit();

            MazosPredefinidosFragment mazosPredefinidosFragment= (MazosPredefinidosFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            mazosPredefinidosFragment.recyclerView.scrollToPosition(0);
        }

    }

    @Override
    public void detallesCarta(Carta c) {
        Intent i= new Intent(MazosPredefinidosActivity.this,MuestraCartaActivity.class);
        i.putExtra(MuestraCartaActivity.CARTA,c);
        startActivity(i);
    }
}
