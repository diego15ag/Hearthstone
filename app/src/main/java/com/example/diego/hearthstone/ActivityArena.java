package com.example.diego.hearthstone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by Tupepo on 02/05/2015.
 */


public class ActivityArena extends ActionBarActivity {


    private static String FPOS ="pos1";
    private static String SPOS ="pos2";
    private static String TPOS ="pos3";

    public int heroClass;
    ImageView iv1;
    ImageView iv2;
    ImageView iv3;
    ImageView ivW;
    Spinner sp1;
    Spinner sp2;
    Spinner sp3;
    ArrayList<Carta> listaCartas;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Recuperacion del heroe elegido
        heroClass = getIntent().getExtras().getInt(HeroSelectionActivity.HeroKey);

        if(savedInstanceState==null){
            playEmote(heroClass);
        }

        sp1 = (Spinner) findViewById(R.id.sp1Pick);
        sp2 = (Spinner) findViewById(R.id.sp2Pick);
        sp3 = (Spinner) findViewById(R.id.sp3Pick);
        iv1 = (ImageView) findViewById(R.id.iv1Pick);
        iv2 = (ImageView) findViewById(R.id.iv2Pick);
        iv3 = (ImageView) findViewById(R.id.iv3Pick);
        ivW = (ImageView) findViewById(R.id.ivWinner);

        //Obtencion de la lista de cartas filtrada
        listaCartas = JSONManager.filtro_clase_param(heroClass);
        ArrayList<Carta> listaComunes = JSONManager.filtro_clase_param(9);
        for (int i = 0; i < listaComunes.size(); i++) {
            listaCartas.add(listaComunes.get(i));
        }

        Carta temp;
        for (int i=0; i < listaCartas.size() -1 ; i++) // ordenacion por orden alfabetico
            for (int j=i+1; j < listaCartas.size(); j++)
                    if(listaCartas.get(j).getNombre().compareTo(listaCartas.get(i).getNombre())<0 ) {
                        temp = listaCartas.get(i);
                        listaCartas.set(i, listaCartas.get(j));
                        listaCartas.set(j, temp);
                    }

        //Obtencion de la lista de nombres de cartas
        int index1=0;
        int index2=0;
        int index3=0;
        ArrayList<String> nombres = new ArrayList<String>();
        for (int i = 0; i < listaCartas.size(); i++) {
            nombres.add(listaCartas.get(i).getNombre());
            if(listaCartas.get(i).getNombre().equals("Ragnaros the Firelord"))
                index1=i;
            else if(listaCartas.get(i).getNombre().equals("Deathwing"))
                index2=i;
            else if(listaCartas.get(i).getNombre().equals("Nefarian"))
                index3=i;
        }
        ArrayAdapter<String> nombresAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, nombres);

        //Cambio del adaptador
        sp1.setAdapter(nombresAdapter);
        sp2.setAdapter(nombresAdapter);
        sp3.setAdapter(nombresAdapter);
        sp1.setSelection(index1);
        sp2.setSelection(index2);
        sp3.setSelection(index3);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String url;
                url = listaCartas.get(position).getUrl();
                ImageLoader.getInstance().displayImage(url, iv1);
                UpdateWinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String url;
                url = listaCartas.get(position).getUrl();
                ImageLoader.getInstance().displayImage(url, iv2);
                UpdateWinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String url;
                url = listaCartas.get(position).getUrl();
                ImageLoader.getInstance().displayImage(url, iv3);
                UpdateWinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (savedInstanceState != null) {
            int pos1 =savedInstanceState.getInt(FPOS);
            int pos2 =savedInstanceState.getInt(SPOS);
            int pos3 =savedInstanceState.getInt(TPOS);
            sp1.setSelection(pos1);
            sp2.setSelection(pos2);
            sp3.setSelection(pos3);
            String url1,url2,url3;
            url1 = listaCartas.get(pos1).getUrl();
            ImageLoader.getInstance().displayImage(url1, iv1);
            url2 = listaCartas.get(pos2).getUrl();
            ImageLoader.getInstance().displayImage(url2, iv2);
            url3 = listaCartas.get(pos3).getUrl();
            ImageLoader.getInstance().displayImage(url3, iv3);
            UpdateWinner();
        }
    }

    public void UpdateWinner() {
        int value1 = listaCartas.get(sp1.getSelectedItemPosition()).getPeso(heroClass);
        int value2 = listaCartas.get(sp2.getSelectedItemPosition()).getPeso(heroClass);
        int value3 = listaCartas.get(sp3.getSelectedItemPosition()).getPeso(heroClass);
        String url="";
        int ganador=1;

        if (value1 >= value2 && value1 >= value3) {
            if(value1==value2)
                ganador=RandomiceBetweenTwo(1,2);
            else if(value1==value3)
                ganador=RandomiceBetweenTwo(1,3);
            else
                ganador = 1;
        } else if (value2 >= value3) {
            if(value2==value3)
                ganador=RandomiceBetweenTwo(2,3);
            else
                ganador=2;
        } else {
            ganador=3;
        }
        switch(ganador){
            case 1:
                url = listaCartas.get(sp1.getSelectedItemPosition()).getUrl();
                break;
            case 2:
                url = listaCartas.get(sp2.getSelectedItemPosition()).getUrl();
                break;
            case 3:
                url = listaCartas.get(sp3.getSelectedItemPosition()).getUrl();
                break;
        }
        ImageLoader.getInstance().displayImage(url, ivW);
    }

    public int RandomiceBetweenTwo(int a,int b){
        Random rn = new Random();
        int n = 10 - 0 + 2;
        int i = rn.nextInt() % n;
        if(i<=5){
            return a;
        }
        else
            return b;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnline()) {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FPOS, sp1.getSelectedItemPosition());
        outState.putInt(SPOS, sp2.getSelectedItemPosition());
        outState.putInt(TPOS, sp3.getSelectedItemPosition());
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