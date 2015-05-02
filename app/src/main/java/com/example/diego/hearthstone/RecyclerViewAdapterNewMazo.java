package com.example.diego.hearthstone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Diego on 30/4/15.
 */
public class RecyclerViewAdapterNewMazo extends RecyclerView.Adapter<RecyclerViewAdapterNewMazo.ViewHolder> {

    private ArrayList<Carta> cartas;
    private Context context;

    public RecyclerViewAdapterNewMazo(ArrayList<Carta> cartas,Context context) {
        this.cartas=cartas;
        this.context=context;
    }
    @Override
    public RecyclerViewAdapterNewMazo.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view =
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_card_item_in_deck,viewGroup,false);
        return new RecyclerViewAdapterNewMazo.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterNewMazo.ViewHolder viewHolder, final int i) {
        viewHolder.tvNombre.setText(cartas.get(i).getNombre());

        int cantidad= cartas.get(i).getCantidad();


        viewHolder.spCantidad.setText(new Integer(cantidad).toString());


        viewHolder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.spCantidad.getText().toString().equals("0")==false){
                    int c =new Integer(viewHolder.spCantidad.getText().toString());
                    viewHolder.spCantidad.setText(new Integer(c-1).toString());
                    cartas.get(i).setCantidad(c-1);
                    System.out.printf("Se ha disminuido la cantidad seleccionada de la carta %s",
                            cartas.get(i).getNombre());
                }
                /*else
                    cartas.remove(i);*/
            }
        });

        //Obtenemos la url de la imagen mas pequeña
        String url=cartas.get(i).getUrl().replaceAll("medium","small");

        JSONManager ayudabd = new JSONManager(context);
        JSONManager.DownloadImageTask im = ayudabd.new DownloadImageTask(viewHolder.ivCarta);
        im.execute(url);
    }


    @Override
    public int getItemCount() {
        return cartas.size();
    }

    protected class ViewHolder
            extends RecyclerView.ViewHolder
            {

        public ImageView ivCarta;
        public TextView tvNombre;
        public TextView spCantidad;
        public ImageButton btDelete;



        public ViewHolder(View itemView) {
            super(itemView);

            this.ivCarta= (ImageView) itemView.findViewById(R.id.imageViewCarta);
            this.tvNombre= (TextView) itemView.findViewById(R.id.textViewCarta);
            this.spCantidad= (TextView) itemView.findViewById(R.id.textViewCantidad);
            this.btDelete = (ImageButton) itemView.findViewById(R.id.imageButtonDelete);
        }
    }

    public interface ClickListener {
        public void itemClicked(View view,int position);
    }

}


