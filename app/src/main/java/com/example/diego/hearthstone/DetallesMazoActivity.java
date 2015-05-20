package com.example.diego.hearthstone;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class DetallesMazoActivity extends ActionBarActivity implements RecyclerViewAdapterCartasMazoPredefinido.ClickListener {

    public static final String MAZO="mazo";
    public static final int DETALLES_MAZO_OK=2;
    private Mazo mazo;

    private RecyclerView recyclerView;
    private RecyclerViewAdapterCartasMazoPredefinido rva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_mazo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mazo = (Mazo) getIntent().getExtras().getSerializable(MAZO);

        recyclerView= (RecyclerView) findViewById(R.id.recycler_cartas_pred);
        rva=new RecyclerViewAdapterCartasMazoPredefinido(mazo.getCartas(),this);
        rva.setClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerView.setAdapter(rva);

        if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE&&
                (Configuration.SCREENLAYOUT_SIZE_MASK&getResources().getConfiguration().screenLayout)
                        ==Configuration.SCREENLAYOUT_SIZE_LARGE) {

            Intent i = new Intent();
            i.putExtra(MAZO, mazo);
            setResult(DETALLES_MAZO_OK, i);
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(View view, int position) {
        Intent i= new Intent(DetallesMazoActivity.this,MuestraCartaActivity.class);
        i.putExtra(MuestraCartaActivity.CARTA,mazo.getCartas().get(position));
        startActivity(i);
    }
}
