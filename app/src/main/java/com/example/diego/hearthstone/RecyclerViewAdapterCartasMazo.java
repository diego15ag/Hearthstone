package com.example.diego.hearthstone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by blukstack on 30/04/2015.
 */
public class RecyclerViewAdapterCartasMazo extends RecyclerView.Adapter<RecyclerViewAdapterCartasMazo.ViewHolder> {

    private ArrayList<Carta> cartas;
    private Context context;
    public static ArrayList<Carta> cartas_elegidas;
    public ClickListener clickListener;
    public static int BIT_COMUNES=0;

    public RecyclerViewAdapterCartasMazo(ArrayList<Carta> cartas, ArrayList<Carta> cartasPadre, Context context) {
        this.cartas = cartas;
        this.context = context;
        this.cartas_elegidas = cartasPadre;

        /*if(cartas_elegidas==null) {
            cartas_elegidas = new ArrayList<Carta>();
            asigna_elegidas();
        }*/
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    private void asigna_elegidas() {
        for (int i = 0; i < cartas.size(); i++) {
            cartas_elegidas.add(cartas.get(i).clone());
            cartas_elegidas.get(i).setCantidad(0);
        }
    }

    public int getNumeroCartas(){
            int cantidad = 0;
            for (int i = 0; i < cartas_elegidas.size(); i++)
                cantidad = cantidad + cartas_elegidas.get(i).getCantidad();
            return cantidad;
    }

    public void datasetchanged(){
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewAdapterCartasMazo.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view =
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_card_item, viewGroup, false);

        return new RecyclerViewAdapterCartasMazo.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterCartasMazo.ViewHolder viewHolder, final int i) {
        viewHolder.tvNombre.setText(cartas_elegidas.get(i).getNombre());

        int cantidad = cartas_elegidas.get(i).getCantidad();


        String[] valores_cantidad = {};

        if (cartas.get(i).getCantidad() == 1)
            valores_cantidad = context.getResources().getStringArray(R.array.CantidadLegendaria);
        else // solo puede ser 2 en este caso
            valores_cantidad = context.getResources().getStringArray(R.array.CantidadNormales);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner_item, valores_cantidad);

        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);

        viewHolder.spCantidad.setAdapter(adapter);
        viewHolder.spCantidad.setSelection(cantidad); // al entrar siempre hay 0 cartas seleccionadas


        viewHolder.spCantidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                cartas_elegidas.get(i).setCantidad(position);
                clickListener.cambiadoNumero();
                /*Toast.makeText(context, "Modificando cantidad de: " + cartas_elegidas.get(i).getNombre()
                        + " en " + Integer.toString(position), Toast.LENGTH_SHORT).show();*/
                /*int j = 0;
                if (cartas_elegidas.size() != 0) {
                    System.out.printf("El tamaño de cartas elegidas es: %d \n", cartas_elegidas.size());
                    while (cartas_elegidas.get(j).getId() != cartas.get(i).getId() && j < cartas_elegidas.size()-1)
                        j++;
                    if (position != 0) {
                        if (j == cartas_elegidas.size()) { // la carta no estaba en el array
                            cartas_elegidas.add(cartas.get(i).clone());
                            Toast.makeText(context, "Insertando carta: " + cartas.get(i).getNombre(), Toast.LENGTH_SHORT).show();
                        }
                        else {// la carta esta en la posicion j del array
                            cartas_elegidas.get(j).setCantidad(position);
                            Toast.makeText(context, "Modificando cantidad de: " + cartas.get(i).getNombre(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (j != cartas_elegidas.size()) { // la carta esta en la posicion j del array y se eligen 0

                        Toast.makeText(context, "Eliminando carta: " + cartas_elegidas.get(j).getNombre(), Toast.LENGTH_SHORT).show();
                        cartas_elegidas.remove(j);
                    }
                }
                else {
                    cartas_elegidas.add(cartas.get(i).clone());
                    //System.out.printf("Carta %s insertada en cartas elegidas \n", cartas.get(i).getNombre());
                    //Log.d("Eleccion", "Estoy insertando en cartas elegidas la carta: " + cartas.get(i).getNombre());
                    Toast.makeText(context, "Insertando carta: " + cartas.get(i).getNombre(), Toast.LENGTH_SHORT).show();
                }*/

                /*if(cartas.get(i).getCantidad()!=position) {
                    String carta = cartas.get(i).getNombre();
                    cartas.get(i).setCantidad(position);
                    jsonhelp.setCantidad(position, cartas.get(i).getId());

                    Toast.makeText(context, "Se acaban de seleccionar " + position + " " + carta, Toast.LENGTH_SHORT).show();
                }*/


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Obtenemos la url de la imagen mas pequeña
        String url = cartas_elegidas.get(i).getUrl();

        ImageLoader.getInstance().displayImage(url,viewHolder.ivCarta);

        /*JSONManager ayudabd = new JSONManager(context);
        JSONManager.DownloadImageTask im = ayudabd.new DownloadImageTask(viewHolder.ivCarta);
        im.execute(url);*/
    }

    @Override
    public int getItemCount() {
        return cartas.size();
    }

    protected class ViewHolder
            extends RecyclerView.ViewHolder {

        public ImageView ivCarta;
        public TextView tvNombre;
        public Spinner spCantidad;


        public ViewHolder(View itemView) {
            super(itemView);

            this.ivCarta = (ImageView) itemView.findViewById(R.id.imageViewCarta);
            this.tvNombre = (TextView) itemView.findViewById(R.id.textViewCarta);
            this.spCantidad = (Spinner) itemView.findViewById(R.id.spCantidad);

        }

    }

    public interface ClickListener {
        public void itemClicked(View view, int position);
        public void cambiadoNumero();
    }
}
