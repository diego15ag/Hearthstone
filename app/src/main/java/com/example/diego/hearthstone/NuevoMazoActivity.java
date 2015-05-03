package com.example.diego.hearthstone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;


public class NuevoMazoActivity extends ActionBarActivity implements RecyclerViewAdapterNewMazo.ClickListener{

    RecyclerView recyclerView;
    RecyclerViewAdapterNewMazo rva;
    public static String referencia="referencia";
    public static String mazoClase = "mazoClase";
    public int clase;
    public static ArrayList<Carta> cartas;
    public static ArrayList<Integer> cantidades_elegidas;

    private TextView tvNumeroCartas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_mazo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNumeroCartas= (TextView) findViewById(R.id.tvCuenta);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE&&
                (Configuration.SCREENLAYOUT_SIZE_MASK&getResources().getConfiguration().screenLayout)
                        ==Configuration.SCREENLAYOUT_SIZE_LARGE)
           recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        else
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));

        clase = getIntent().getExtras().getInt(mazoClase);
        int ref = getIntent().getExtras().getInt(referencia);
        //JSONManager.position_clase=clase;
        if(cartas==null)
            cartas = new ArrayList<Carta>();

        cantidades_elegidas = new ArrayList<Integer>();
        /*for(int i=0;i<JSONManager.filtro_clase().size();i++){
            cartas.add(JSONManager.filtro_clase().get(i).clone());
            Log.i("cantidad",String.valueOf( cartas.get(i).getCantidad()));
        }*/
        rva=new RecyclerViewAdapterNewMazo(cartas, getApplicationContext());

        rva.setClickListener(this);
        recyclerView.setAdapter(rva);

        ImageButton botoncartas= (ImageButton) findViewById(R.id.btAdd);

        botoncartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < cartas.size(); i++)
                    if (cartas.get(i).getCantidad() == 0) {
                        cartas.remove(i);
                        i=i-1;
                    }
                Intent i = new Intent(NuevoMazoActivity.this, EleccionCartasMazoActivity.class);
                i.putExtra("clase", clase);
                startActivity(i);
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nuevo_mazo, menu);
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
        else if(id==android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        else{
            tvNumeroCartas.setText(rva.getNumeroCartas()+"/30");
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void itemClicked(View view, int position) {

    }

    @Override
    public void cambiadoNumero() {
        tvNumeroCartas.setText(rva.getNumeroCartas()+"/30");

    }
}
