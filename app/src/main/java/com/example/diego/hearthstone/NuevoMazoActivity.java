package com.example.diego.hearthstone;

import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class NuevoMazoActivity extends ActionBarActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapterCartas rva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_mazo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE&&
                (Configuration.SCREENLAYOUT_SIZE_MASK&getResources().getConfiguration().screenLayout)
                        ==Configuration.SCREENLAYOUT_SIZE_LARGE)
           recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        else
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));




        JSONManager.position_clase=0;

        rva=new RecyclerViewAdapterCartas(JSONManager.filtro_clase(),getApplicationContext());
        recyclerView.setAdapter(rva);

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
}
