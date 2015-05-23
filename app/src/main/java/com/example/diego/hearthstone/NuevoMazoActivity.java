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
    public int ref;
    public static ArrayList<Carta> cartas;
    public static int RESULT_OK=1;
    public EditText editTextNombre;
    public String nombreMazo;

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

        if((Configuration.SCREENLAYOUT_SIZE_MASK&getResources().getConfiguration().screenLayout)
                        ==Configuration.SCREENLAYOUT_SIZE_LARGE)
           recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        else
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));

        clase = getIntent().getExtras().getInt(mazoClase);
        ref = getIntent().getExtras().getInt(referencia);
        nombreMazo=getIntent().getExtras().getString("NombreMazo");

        ImageButton botoncartas= (ImageButton) findViewById(R.id.btAdd);

        botoncartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EleccionCartasMazoActivity.cartas_padre=null;
                Intent i = new Intent(NuevoMazoActivity.this, EleccionCartasMazoActivity.class);
                i.putExtra("clase", clase);
                startActivity(i);
            }
        });


        editTextNombre = (EditText) findViewById(R.id.editTextNombre);
        InputFilter filters[] = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(15);
        editTextNombre.setFilters(filters);
        editTextNombre.setText(nombreMazo);

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
            if (ref==-1) { // guardado de mazo
                if (JSONManager.Mazos_array == null)
                    JSONManager.Mazos_array = new ArrayList<Mazo>();

                if (rva.getNumeroCartas() > 30)
                    Toast.makeText(NuevoMazoActivity.this, R.string.more_than_30, Toast.LENGTH_SHORT).show();
                else if (rva.getNumeroCartas() == 0)
                    Toast.makeText(NuevoMazoActivity.this, R.string.Toast_no_cards_create, Toast.LENGTH_SHORT).show();
                else if (editTextNombre.getText().toString().equals(""))
                    Toast.makeText(NuevoMazoActivity.this, R.string.Toast_no_name_create, Toast.LENGTH_SHORT).show();
                else {
                    Mazo m = new Mazo(-1, editTextNombre.getText().toString(), false, JSONManager.getNameFromPositionClase(clase), cartas);
                    JSONManager.Mazos_array.add(m);
                    ayudabd.creaMazo(m);
                    Toast.makeText(NuevoMazoActivity.this, m.getNombre() + " " + getResources().getString(R.string.Toast_been_created), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                return true;
            }
            else{ // edicion de mazo
                if (rva.getNumeroCartas() > 30)
                    Toast.makeText(NuevoMazoActivity.this, R.string.more_than_30, Toast.LENGTH_SHORT).show();
                else if (rva.getNumeroCartas() == 0)
                    Toast.makeText(NuevoMazoActivity.this, R.string.Toast_no_cards_edit, Toast.LENGTH_SHORT).show();
                else if (editTextNombre.getText().toString().equals(""))
                    Toast.makeText(NuevoMazoActivity.this, R.string.Toast_no_name_edit, Toast.LENGTH_SHORT).show();
                else {
                    JSONManager ayudabd = new JSONManager(this);
                    ayudabd.start();
                    int pos = buscaPosicionMazoPorID(ref);
                    ayudabd.borraCartasMazo(ref); // borramos las cartas antiguas
                    ayudabd.insertaCartasMazo(cartas, ref); // insertamos las cartas nuevas
                    ayudabd.modificaNombreMazo(ref, editTextNombre.getText().toString());
                    JSONManager.Mazos_array.get(pos).setCartas(cartas); // modificamos las cartas del mazo en la lista
                    JSONManager.Mazos_array.get(pos).setNombre(editTextNombre.getText().toString());
                    Toast.makeText(NuevoMazoActivity.this,JSONManager.Mazos_array.get(pos).getNombre()
                            + " " + getResources().getString(R.string.Toast_been_updated), Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                }
            }
        }
        else if(id ==R.id.action_delete){
            if(ref==-1) {
                Toast.makeText(NuevoMazoActivity.this, R.string.Toast_delete_save, Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
            else {
                JSONManager ayudabd = new JSONManager(this);
                ayudabd.start();
                int pos = buscaPosicionMazoPorID(ref);
                ayudabd.deleteMazo(JSONManager.Mazos_array.get(pos)); // borramos mazo de la bd
                Toast.makeText(NuevoMazoActivity.this, JSONManager.Mazos_array.get(pos).getNombre()
                        + " " + getResources().getString(R.string.Toast_been_deleted), Toast.LENGTH_SHORT).show();
                JSONManager.Mazos_array.remove(pos); // borramos mazo de la lista
                finish();
                return true;
            }
        }
        else if(id==android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private int buscaPosicionMazoPorID(int id){
        int pos=0;
        for(int i=0;i<JSONManager.Mazos_array.size();i++)
            if(JSONManager.Mazos_array.get(i).getId()==id){
                pos=i;
                break;
            }
        return pos;
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
