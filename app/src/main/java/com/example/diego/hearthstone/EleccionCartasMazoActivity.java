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
    static ProgressDialog pd;
    private static boolean cargando = false;
    private ArrayList<Carta> cartas_eleccion;
    public static ArrayList<Carta> cartas_padre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eleccion_cartas_mazo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (cargando)
            pd = ProgressDialog.show(EleccionCartasMazoActivity.this, getResources().getString(R.string.DialogLoading_description),
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

        cartas_eleccion = new ArrayList<Carta>();
        if (cartas_padre == null)
            cartas_padre = new ArrayList<Carta>();
        pd = ProgressDialog.show(EleccionCartasMazoActivity.this, getResources().getString(R.string.DialogLoading_description),
                getResources().getString(R.string.DialogLoading_description));
        cargando = true;
        new Manipula_Array().execute(cartas_eleccion);


        ImageButton botonAddCartas = (ImageButton) findViewById(R.id.ivOk);

        botonAddCartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cargando = true;
                pd = ProgressDialog.show(EleccionCartasMazoActivity.this, getResources().getString(R.string.DialogLoading_description),
                        getResources().getString(R.string.DialogLoading_description));
                new ResuelveConflicto().execute();

            }
        });
    }

    private void ResuelveConflicto() {
        //ArrayList<Carta> c = new ArrayList<Carta>();
        boolean flag;
        if (NuevoMazoActivity.cartas.size() != 0) {
            for (int i = 0; i < cartas_padre.size(); i++) {
                flag = false;
                for (int j = 0; j < NuevoMazoActivity.cartas.size(); j++) {
                    if (cartas_padre.get(i).getCantidad() != 0) {
                        if (cartas_padre.get(i).getId() == NuevoMazoActivity.cartas.get(j).getId()) {
                            //c.add(RecyclerViewAdapterCartasMazo.cartas_elegidas.get(i).clone());
                            NuevoMazoActivity.cartas.get(j).setCantidad(NuevoMazoActivity.cartas.get(j).getCantidad() +
                                    cartas_padre.get(i).getCantidad());
                            flag = true;
                        } else if (j == NuevoMazoActivity.cartas.size() - 1 && flag == false) { // la carta i elegida no esta en el array de cartas del mazo
                            NuevoMazoActivity.cartas.add(cartas_padre.get(i).clone());
                            j++;
                            flag = true;
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < cartas_padre.size(); i++)
                if (cartas_padre.get(i).getCantidad() != 0)
                    NuevoMazoActivity.cartas.add(cartas_padre.get(i).clone());
        }
        OrdenaNuevoMazo(NuevoMazoActivity.cartas);
    }

    private void OrdenaNuevoMazo(ArrayList<Carta> cartas) {
        Carta temp;
        for (int i = 0; i < cartas.size() - 1; i++) // ordenacion por coste
            for (int j = i + 1; j < cartas.size(); j++)
                if (cartas.get(j).getCoste() < cartas.get(i).getCoste()) {
                    temp = cartas.get(i);
                    cartas.set(i, cartas.get(j));
                    cartas.set(j, temp);
                }
        for (int i = 0; i < cartas.size() - 1; i++) // ordenacion por orden alfabetico
            for (int j = i + 1; j < cartas.size(); j++)
                if (cartas.get(j).getCoste() == cartas.get(i).getCoste())
                    if (cartas.get(j).getNombre().compareTo(cartas.get(i).getNombre()) < 0) {
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

        //Aqui habria que mostrar una imagen dependiendo del heroe
        int id;

        if (clase == 0)
            id = R.mipmap.druida;
        else if (clase == 1)
            id = R.mipmap.cazador;
        else if (clase == 2)
            id = R.mipmap.mago;
        else if (clase == 3)
            id = R.mipmap.paladin;
        else if (clase == 4)
            id = R.mipmap.sacerdote;
        else if (clase == 5)
            id = R.mipmap.picaro;
        else if (clase == 6)
            id = R.mipmap.chaman;
        else if (clase == 7)
            id = R.mipmap.brujo;
        else
            id = R.mipmap.guerrero;


        getMenuInflater().inflate(R.menu.menu_eleccion_cartas_mazo, menu);
        menu.findItem(R.id.action_select_class).setIcon(getResources().getDrawable(id));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Mostrar todas
        if (id == R.id.action_select_all) {
            rva = new RecyclerViewAdapterCartasMazo(cartas_eleccion, cartas_padre, getApplicationContext());
            rva.setClickListener(EleccionCartasMazoActivity.this);
            recyclerView.setAdapter(rva);
            return true;
        }

        //Mostrar las comunes
        else if (id == R.id.action_select_common) {
            //RecyclerViewAdapterCartasMazo.cartas_elegidas=null;
            rva = new RecyclerViewAdapterCartasMazo(giveFiltro(9), giveFiltroPadre(9), getApplicationContext());
            rva.setClickListener(EleccionCartasMazoActivity.this);
            recyclerView.setAdapter(rva);
            return true;
        }
        //Mostrar las de la clase
        else if (id == R.id.action_select_class) {
            //RecyclerViewAdapterCartasMazo.cartas_elegidas=null;
            rva = new RecyclerViewAdapterCartasMazo(giveFiltro(clase), giveFiltroPadre(clase), getApplicationContext());
            rva.setClickListener(EleccionCartasMazoActivity.this);
            recyclerView.setAdapter(rva);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private ArrayList<Carta> giveFiltro(int clase) {
        ArrayList<Carta> cartas = new ArrayList<Carta>();
        String Clase = JSONManager.getNameFromPositionClase(clase);
        if (clase == 9) {
            for (int i = 0; i < cartas_eleccion.size(); i++)
                if (cartas_eleccion.get(i).getClase().equals("neutral"))
                    cartas.add(cartas_eleccion.get(i));
            return cartas;
        } else {
            for (int i = 0; i < cartas_eleccion.size(); i++)
                if (cartas_eleccion.get(i).getClase().equals(Clase))
                    cartas.add(cartas_eleccion.get(i));
            return cartas;
        }
    }

    private ArrayList<Carta> giveFiltroPadre(int clase) {
        ArrayList<Carta> cartas = new ArrayList<Carta>();
        String Clase = JSONManager.getNameFromPositionClase(clase);
        if (clase == 9) {
            for (int i = 0; i < cartas_padre.size(); i++)
                if (cartas_padre.get(i).getClase().equals("neutral"))
                    cartas.add(cartas_padre.get(i));
            return cartas;
        } else {
            for (int i = 0; i < cartas_padre.size(); i++)
                if (cartas_padre.get(i).getClase().equals(Clase))
                    cartas.add(cartas_padre.get(i));
            return cartas;
        }
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
        textcantidad.setText(getNumeroCartasPadre() + "/" + cantidad_nuevomazo);
    }

    public int getNumeroCartasPadre() {
        int cantidad = 0;
        for (int i = 0; i < cartas_padre.size(); i++)
            cantidad = cantidad + cartas_padre.get(i).getCantidad();
        return cantidad;
    }

    public class ResuelveConflicto extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ResuelveConflicto();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            pd.dismiss();
            cargando = false;
            finish();
        }

    }

    public class Manipula_Array extends AsyncTask<ArrayList<Carta>, Void, ArrayList<Carta>> {

        @Override
        protected ArrayList<Carta> doInBackground(ArrayList<Carta>... params) {
            ManipulaArray(params[0], clase);
            if (cartas_padre.size()==0) {
                for (int i = 0; i < cartas_eleccion.size(); i++) {
                    cartas_padre.add(cartas_eleccion.get(i).clone());
                    cartas_padre.get(i).setCantidad(0);
                }
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(ArrayList<Carta> cartas_eleccion) {
            pd.dismiss();
            cargando = false;
            rva = new RecyclerViewAdapterCartasMazo(cartas_eleccion, cartas_padre, getApplicationContext());
            rva.setClickListener(EleccionCartasMazoActivity.this);
            recyclerView.setAdapter(rva);
            textcantidad = (TextView) findViewById(R.id.tvCuenta);
            cantidad_nuevomazo = 0;
            for (int i = 0; i < NuevoMazoActivity.cartas.size(); i++)
                cantidad_nuevomazo = cantidad_nuevomazo + NuevoMazoActivity.cartas.get(i).getCantidad();
            cantidad_nuevomazo = 30 - cantidad_nuevomazo;
            textcantidad.setText(getNumeroCartasPadre() + "/" + cantidad_nuevomazo);
        }
    }


}
