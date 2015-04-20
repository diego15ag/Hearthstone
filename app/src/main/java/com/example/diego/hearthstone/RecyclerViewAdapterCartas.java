package com.example.diego.hearthstone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonatan on 16/4/15.
 */
public class RecyclerViewAdapterCartas extends RecyclerView.Adapter<RecyclerViewAdapterCartas.ViewHolder> {

    public ClickListener clickListener;
    private ArrayList<Carta> cartas;
    private Context context;

    public RecyclerViewAdapterCartas(ArrayList<Carta> cartas,Context context) {
        this.cartas=cartas;
        this.context=context;
    }


    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerViewAdapterCartas.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_card_item,viewGroup,false);

        return new RecyclerViewAdapterCartas.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterCartas.ViewHolder viewHolder, int i) {

        viewHolder.tvNombre.setText(cartas.get(i).getNombre());

        int cantidad= cartas.get(i).getCantidad();


        String [] valores_cantidad={};

        if(cartas.get(i).getTipo().equals("legendary"))
            valores_cantidad=context.getResources().getStringArray(R.array.CantidadLegendaria);
        else
            valores_cantidad=context.getResources().getStringArray(R.array.CantidadNormales);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner_item,valores_cantidad);

        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);

        viewHolder.spCantidad.setAdapter(adapter);
        viewHolder.spCantidad.setSelection(cantidad);

        JSONManager ayudabd = new JSONManager(context);
        JSONManager.DownloadImageTask im = ayudabd.new DownloadImageTask(viewHolder.ivCarta);
        im.execute(cartas.get(i).getUrl());
    }


    @Override
    public int getItemCount() {
        return cartas.size();
    }

    public void cambiaArray(ArrayList<Carta> cartas){
        this.cartas=cartas;
        notifyDataSetChanged();
    }

    public Carta get(int position){
        return cartas.get(position);
    }



    protected class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        public ImageView ivCarta;
        public TextView tvNombre;
        public Spinner spCantidad;


        public ViewHolder(View itemView) {
            super(itemView);

            this.ivCarta= (ImageView) itemView.findViewById(R.id.imageViewCarta);
            this.tvNombre= (TextView) itemView.findViewById(R.id.textViewCarta);
            this.spCantidad= (Spinner) itemView.findViewById(R.id.spCantidad);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if ( clickListener != null){
                clickListener.itemClicked(v,getPosition());
            }

        }
    }


    public interface ClickListener {
        public void itemClicked(View view,int position);
    }

}
