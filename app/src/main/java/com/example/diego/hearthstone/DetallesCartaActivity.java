package com.example.diego.hearthstone;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class DetallesCartaActivity extends ActionBarActivity {

    public static final String imagen="IMAGEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_carta);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }

        //Mostrar boton hacia atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //No mostrar titulo
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle datos= getIntent().getExtras();

        ImageView ivCarta = (ImageView) findViewById(R.id.ivCarta);

        JSONManager ayudabd = new JSONManager(this);
        JSONManager.DownloadImageTask im = ayudabd.new DownloadImageTask(ivCarta);
        im.execute(datos.getString(imagen,""));
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
