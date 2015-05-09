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
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class NuevoMazoActivity extends ActionBarActivity implements RecyclerViewAdapterNewMazo.ClickListener{

    RecyclerView recyclerView;
    RecyclerViewAdapterNewMazo rva;
    public static String referencia="referencia";
    public static String mazoClase = "mazoClase";
    public int clase;
    public static ArrayList<Carta> cartas;
    public static int RESULT_OK=1;
    public EditText editTextNombre;

    private TextView tvNumeroCartas;
    JSONManager ayudabd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_mazo);

        ayudabd = new JSONManager(NuevoMazoActivity.this);
        ayudabd.startBG();

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

        ImageButton botoncartas= (ImageButton) findViewById(R.id.btAdd);

        botoncartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*for (int i = 0; i < cartas.size(); i++)
                    if (cartas.get(i).getCantidad() == 0) {
                        cartas.remove(i);
                        i=i-1;
                    }*/
                Intent i = new Intent(NuevoMazoActivity.this, EleccionCartasMazoActivity.class);
                i.putExtra("clase", clase);
                startActivity(i);
            }
        });


        editTextNombre = (EditText) findViewById(R.id.editTextNombre);
        InputFilter filters[] = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(15);
        editTextNombre.setFilters(filters);

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
        if (id == R.id.action_save) {

            if(JSONManager.Mazos_array==null)
                JSONManager.Mazos_array= new ArrayList<Mazo>();

            if(rva.getNumeroCartas()>30)
                Toast.makeText(NuevoMazoActivity.this, "No se pueden crear mazos de más de 30 cartas", Toast.LENGTH_SHORT).show();
            else if (rva.getNumeroCartas()==0)
                Toast.makeText(NuevoMazoActivity.this, "No se pueden crear mazos sin cartas", Toast.LENGTH_SHORT).show();
            else if (editTextNombre.getText().toString().equals(""))
                Toast.makeText(NuevoMazoActivity.this, "No se puede crear un mazo sin nombre", Toast.LENGTH_SHORT).show();
            else{
                Mazo m= new Mazo(-1, editTextNombre.getText().toString(), false, JSONManager.getNameFromPositionClase(clase), cartas );
                JSONManager.Mazos_array.add(m);
                ayudabd.creaMazo(m);
                Toast.makeText(NuevoMazoActivity.this, "Mazo " + m.getNombre() + " creado!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            return true;
        }
        else if(id ==R.id.action_delete){
            Toast.makeText(NuevoMazoActivity.this, "El mazo no se ha guardado", Toast.LENGTH_SHORT).show();
            finish();
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

            if(cartas==null)
                cartas = new ArrayList<Carta>();

            rva=new RecyclerViewAdapterNewMazo(cartas, getApplicationContext());

            rva.setClickListener(this);
            recyclerView.setAdapter(rva);

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
