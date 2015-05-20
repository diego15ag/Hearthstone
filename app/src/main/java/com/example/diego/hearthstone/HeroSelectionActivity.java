package com.example.diego.hearthstone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


public class HeroSelectionActivity extends ActionBarActivity implements RecyclerViewAdapterHeroes.ClickListener {

    RecyclerView recyclerView;
    RecyclerViewAdapterHeroes rvh;
    private DrawerLayout drawerLayout;
    private ListView lvDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout layoutDelDrawer;

    public static String HeroKey = "HeroKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_selection);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }

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
                        Intent i=new Intent(HeroSelectionActivity.this,ActivityCollection.class);
                        startActivity(i);
                        break;
                    case 1:
                        Intent i1=new Intent(HeroSelectionActivity.this,CartaPersonalizadaActivity.class);
                        startActivity(i1);
                        break;
                    case 2:
                        break;
                    case 3:
                        Intent i3=new Intent(HeroSelectionActivity.this,MazosPredefinidosActivity.class);
                        startActivity(i3);
                        break;
                }

                drawerLayout.closeDrawer(layoutDelDrawer);

            }
        });


        //Recycler view con los heroes
        recyclerView = (RecyclerView) findViewById(R.id.recycler_heroes);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

        rvh = new RecyclerViewAdapterHeroes(JSONManager.Heroes_array, getApplicationContext());
        rvh.setClickListener(this);
        recyclerView.setAdapter(rvh);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hero_selection, menu);
        return true;
    }

    @Override
    public void itemClicked(View view, int position) {
        Carta heroe=rvh.get(position);
        //Toast.makeText(getApplicationContext(), heroe.getClase() + " seleccionado", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(HeroSelectionActivity.this, ActivityArena.class);
        i.putExtra(HeroKey,position);
        //Reproduce el emote
        playEmote(position);
        startActivity(i);
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
    protected void onResume() {
        super.onResume();
        if (!isOnline()){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void playEmote(int hero) {

        MediaPlayer mPlayer = null;
        int delay = 0;

        switch(hero){
            case 0:
                mPlayer=MediaPlayer.create(this,R.raw.druid);
                delay=2000;
                break;
            case 1:
                mPlayer=MediaPlayer.create(this,R.raw.hunter);
                delay=2000;
                break;
            case 2:
                mPlayer=MediaPlayer.create(this,R.raw.mage);
                delay=1500;
                break;
            case 3:
                mPlayer=MediaPlayer.create(this,R.raw.paladin);
                delay=2000;
                break;
            case 4:
                mPlayer=MediaPlayer.create(this,R.raw.priest);
                delay=2000;
                break;
            case 5:
                mPlayer=MediaPlayer.create(this,R.raw.rogue);
                delay=3000;
                break;
            case 6:
                mPlayer=MediaPlayer.create(this,R.raw.shaman);
                delay=1500;
                break;
            case 7:
                mPlayer=MediaPlayer.create(this,R.raw.warlock);
                delay=2500;
                break;
            case 8:
                mPlayer=MediaPlayer.create(this,R.raw.warrior);
                delay=2000;
                break;

        }

        if (mPlayer != null)
            mPlayer.start();

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPlayer.release();
    }
}
