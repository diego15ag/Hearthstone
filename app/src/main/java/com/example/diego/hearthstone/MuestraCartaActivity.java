package com.example.diego.hearthstone;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;


public class MuestraCartaActivity extends ActionBarActivity {

    public static final String CARTA="CARTA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muestra_carta);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }



        //Mostrar boton hacia atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //No mostrar titulo
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView ivCarta = (ImageView) findViewById(R.id.ivCarta);

        Bundle datos= getIntent().getExtras();
        Carta c= (Carta) datos.getSerializable(CARTA);

        if(c!=null)
            ImageLoader.getInstance().displayImage(c.getUrl(),ivCarta);
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
