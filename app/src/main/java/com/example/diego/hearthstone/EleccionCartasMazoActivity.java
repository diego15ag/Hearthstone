package com.example.diego.hearthstone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;


public class EleccionCartasMazoActivity extends ActionBarActivity implements RecyclerViewAdapterCartasMazo.ClickListener {

    RecyclerView recyclerView;
    RecyclerViewAdapterCartasMazo rva;
    int clase;
    int cantidad_nuevomazo;
    TextView textcantidad;
    ProgressDialog pd;
    private static boolean cargando=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eleccion_cartas_mazo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if(cargando)
            pd= ProgressDialog.show(EleccionCartasMazoActivity.this, getResources().getString(R.string.DialogLoading_description),
                    getResources().getString(R.string.DialogLoading_description));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE &&
                (Configuration.SCREENLAYOUT_SIZE_MASK & getResources().getConfiguration().screenLayout)
                        == Configuration.SCREENLAYOUT_SIZE_LARGE)
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        else
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));

        clase = getIntent().getExtras().getInt("clase");

        ArrayList<Carta> cartas_eleccion = new ArrayList<Carta>();
        pd = ProgressDialog.show(EleccionCartasMazoActivity.this, getResources().getString(R.string.DialogLoading_description),
                getResources().getString(R.string.DialogLoading_description));
        cargando=true;
        new Manipula_Array().execute(cartas_eleccion);


        ImageButton botonAddCartas = (ImageButton) findViewById(R.id.ivOk);

        botonAddCartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cargando=true;
                pd = ProgressDialog.show(EleccionCartasMazoActivity.this, getResources().getString(R.string.DialogLoading_description),
                        getResources().getString(R.string.DialogLoading_description));
                new ResuelveConflicto().execute();

               /* Intent i = new Intent(EleccionCartasMazoActivity.this, NuevoMazoActivity.class);
                i.putExtra(NuevoMazoActivity.mazoClase, clase);
                i.putExtra(NuevoMazoActivity.referencia, -1);
                startActivity(i);*/
            }
        });
    }

    private void ResuelveConflicto() {
        //ArrayList<Carta> c = new ArrayList<Carta>();
        boolean flag;
        if (NuevoMazoActivity.cartas.size() != 0) {
            for (int i = 0; i < RecyclerViewAdapterCartasMazo.cartas_elegidas.size(); i++) {
                flag = false;
                for (int j = 0; j < NuevoMazoActivity.cartas.size(); j++) {
                    if (RecyclerViewAdapterCartasMazo.cartas_elegidas.get(i).getCantidad() != 0) {
                        if (RecyclerViewAdapterCartasMazo.cartas_elegidas.get(i).getId() == NuevoMazoActivity.cartas.get(j).getId()) {
                            //c.add(RecyclerViewAdapterCartasMazo.cartas_elegidas.get(i).clone());
                            NuevoMazoActivity.cartas.get(j).setCantidad(NuevoMazoActivity.cartas.get(j).getCantidad() +
                                    RecyclerViewAdapterCartasMazo.cartas_elegidas.get(i).getCantidad());
                            flag = true;
                        }
                        else if (j == NuevoMazoActivity.cartas.size() - 1 && flag == false) { // la carta i elegida no esta en el array de cartas del mazo
                            NuevoMazoActivity.cartas.add(RecyclerViewAdapterCartasMazo.cartas_elegidas.get(i).clone());
                            j++;
                            flag=true;
                        }
                    }
                }
            }
        }
        else {
            for (int i = 0; i < RecyclerViewAdapterCartasMazo.cartas_elegidas.size(); i++)
                if (RecyclerViewAdapterCartasMazo.cartas_elegidas.get(i).getCantidad() != 0)
                    NuevoMazoActivity.cartas.add(RecyclerViewAdapterCartasMazo.cartas_elegidas.get(i).clone());
        }
        OrdenaNuevoMazo(NuevoMazoActivity.cartas);
    }

    private void OrdenaNuevoMazo(ArrayList<Carta> cartas){
        Carta temp;
        for (int i=0; i < cartas.size() -1 ; i++) // ordenacion por coste
            for (int j=i+1; j < cartas.size(); j++)
                if(cartas.get(j).getCoste()< cartas.get(i).getCoste()){
                    temp = cartas.get(i);
                    cartas.set(i,cartas.get(j) );
                    cartas.set(j, temp);
                }
        for (int i=0; i < cartas.size() -1 ; i++) // ordenacion por orden alfabetico
            for (int j=i+1; j < cartas.size(); j++)
                if(cartas.get(j).getCoste() == cartas.get(i).getCoste())
                    if(cartas.get(j).getNombre().compareTo(cartas.get(i).getNombre())<0 ) {
                        temp = cartas.get(i);
                        cartas.set(i, cartas.get(j));
                        cartas.set(j, temp);
                    }
    }

    private void ManipulaArray(ArrayList<Carta> cartas_eleccion, int clase) {
        ArrayList<Carta> cartas_clase = JSONManager.filtro_clase_param(clase);
        for (int i = 0; i < cartas_clase.size(); i++) // añadimos al array de eleccion las cartas de clase
            if (cartas_clase.get(i).getCantidad() > 0)
                cartas_eleccion.add(cartas_clase.get(i).clone());

        ArrayList<Carta> cartas_comunes = JSONManager.filtro_clase_param(9);
        for (int i = 0; i < cartas_comunes.size(); i++) // añadimos al array de eleccion las cartas comunes
            if (cartas_comunes.get(i).getCantidad() > 0)
                cartas_eleccion.add(cartas_comunes.get(i).clone());

        // Si se habia elegido previamente alguna carta comprobamos que el maximo seleccionable sea la cantidad - la cantidad seleccionada
        System.out.printf("Tamaño de cartas para eleccion: %d \n", cartas_eleccion.size());
        System.out.printf("Tamaño de cartas de NuevoMazoActivity: %d \n", NuevoMazoActivity.cartas.size());
        for (int i = 0; i < NuevoMazoActivity.cartas.size(); i++)
            for (int j = 0; j < cartas_eleccion.size(); j++) {
                if (NuevoMazoActivity.cartas.get(i).getId() == cartas_eleccion.get(j).getId()) {
                    cartas_eleccion.get(j).setCantidad(cartas_eleccion.get(j).getCantidad() -
                            NuevoMazoActivity.cartas.get(i).getCantidad()); // usamos cantidad para checkear en el spinner del adapter
                    if (cartas_eleccion.get(j).getCantidad() == 0) {
                        System.out.printf("Eliminada de eleccion la carta: %s \n", cartas_eleccion.get(j).getNombre());
                        cartas_eleccion.remove(j);
                        j = j - 1;
                    }
                }
            }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eleccion_cartas_mazo, menu);
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
    public void itemClicked(View view, int position) {

    }

    @Override
    public void cambiadoNumero() {
        textcantidad.setText(rva.getNumeroCartas() + "/" + cantidad_nuevomazo);
    }

    public class ResuelveConflicto extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ResuelveConflicto();
            return null;
        }
        @Override
        protected void onPostExecute(Void v){
            pd.dismiss();
            cargando=false;
            finish();
        }

    }

    public class Manipula_Array extends AsyncTask<ArrayList<Carta>, Void, ArrayList<Carta>>{

        @Override
        protected ArrayList<Carta> doInBackground(ArrayList<Carta>... params) {
            ManipulaArray(params[0], clase);
            return params[0];
        }

        @Override
        protected void onPostExecute(ArrayList<Carta> cartas_eleccion){
            pd.dismiss();
            cargando=false;
            rva = new RecyclerViewAdapterCartasMazo(cartas_eleccion, getApplicationContext());
            rva.setClickListener(EleccionCartasMazoActivity.this);
            recyclerView.setAdapter(rva);
            textcantidad = (TextView ) findViewById(R.id.tvCuenta);
            cantidad_nuevomazo=0;
            for(int i=0; i<NuevoMazoActivity.cartas.size();i++)
                cantidad_nuevomazo= cantidad_nuevomazo + NuevoMazoActivity.cartas.get(i).getCantidad();
            cantidad_nuevomazo= 30 - cantidad_nuevomazo;
            textcantidad.setText(rva.getNumeroCartas() + "/" + cantidad_nuevomazo);
        }
    }


}
