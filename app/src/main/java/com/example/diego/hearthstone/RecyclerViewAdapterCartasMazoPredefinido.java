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

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


public class RecyclerViewAdapterCartasMazoPredefinido extends RecyclerView.Adapter<RecyclerViewAdapterCartasMazoPredefinido.ViewHolder> {

    private ArrayList<Carta> cartas;
    private Context context;

    public ClickListener clickListener;


    public RecyclerViewAdapterCartasMazoPredefinido(ArrayList<Carta> cartas, Context context) {
        this.cartas = cartas;
        this.context = context;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


    @Override
    public RecyclerViewAdapterCartasMazoPredefinido.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view =
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_card_item_preset, viewGroup, false);

        return new RecyclerViewAdapterCartasMazoPredefinido.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterCartasMazoPredefinido.ViewHolder viewHolder, final int i) {

        viewHolder.tvNombre.setText(cartas.get(i).getNombre());

        viewHolder.tvCantidad.setText(String.valueOf(getNumeroCartasObtenidas(cartas.get(i))+"/"+String.valueOf(cartas.get(i).getCantidad())));

        int arcano=0;

        if(!(cartas.get(i).getConjunto().equals("naxxramas")||
                cartas.get(i).getConjunto().equals("brm"))) {
            if (cartas.get(i).getTipo().equals("common"))
                arcano = 40;
            else if (cartas.get(i).getTipo().equals("rare"))
                arcano = 100;
            else if (cartas.get(i).getTipo().equals("epic"))
                arcano = 400;
            else if (cartas.get(i).getTipo().equals("legendary"))
                arcano = 1600;
        }

        viewHolder.tvArcano.setText(String.valueOf(arcano*cartas.get(i).getCantidad()));

        //Obtenemos la url de la imagen
        String url = cartas.get(i).getUrl();
        ImageLoader.getInstance().displayImage(url,viewHolder.ivCarta);

    }

    @Override
    public int getItemCount() {
        return cartas.size();
    }

    protected class ViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivCarta;
        public TextView tvNombre;
        public TextView tvCantidad;
        public TextView tvArcano;


        public ViewHolder(View itemView) {
            super(itemView);

            this.ivCarta = (ImageView) itemView.findViewById(R.id.imageViewCarta);
            this.tvNombre = (TextView) itemView.findViewById(R.id.textViewCarta);
            this.tvCantidad = (TextView) itemView.findViewById(R.id.tvNumero);
            this.tvArcano = (TextView) itemView.findViewById(R.id.tvArcano);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                clickListener.itemClicked(v, getPosition());
            }

        }

    }

    public interface ClickListener {
        public void itemClicked(View view, int position);
    }

    private int getNumeroCartasObtenidas(Carta c) {
        int cartas = 0;

            Carta necesitada= c;
            for (int j = 0; j < JSONManager.Cartas_array.size(); j++) {
                if (necesitada.getId() == JSONManager.Cartas_array.get(j).getId()) {
                    Carta obtenida = JSONManager.Cartas_array.get(j);

                    if (necesitada.getCantidad() > obtenida.getCantidad()) {
                        cartas += obtenida.getCantidad();
                    } else {
                        cartas += necesitada.getCantidad();
                    }
                    break;
                }
            }

        return cartas;
    }


}
