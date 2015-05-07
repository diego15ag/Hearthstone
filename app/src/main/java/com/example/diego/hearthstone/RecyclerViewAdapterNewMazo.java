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

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Diego on 30/4/15.
 */
public class RecyclerViewAdapterNewMazo extends RecyclerView.Adapter<RecyclerViewAdapterNewMazo.ViewHolder> {

    private ArrayList<Carta> cartas;
    private Context context;
    public ClickListener clickListener;

    public RecyclerViewAdapterNewMazo(ArrayList<Carta> cartas,Context context) {
        this.cartas=cartas;
        this.context=context;
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
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
                    cartas.get(i).setCantidad(c - 1);

                    System.out.printf("Se ha disminuido la cantidad seleccionada de la carta %s",
                            cartas.get(i).getNombre());

                    if(cartas.get(i).getCantidad()==0){
                        cartas.remove(i);
                        notifyDataSetChanged();
                    }


                    clickListener.cambiadoNumero();
                }
                /*else
                    cartas.remove(i);*/
            }
        });

        //Obtenemos la url de la imagen
        String url=cartas.get(i).getUrl();

        ImageLoader.getInstance().displayImage(url,viewHolder.ivCarta);
    }


    @Override
    public int getItemCount() {
        return cartas.size();
    }

    public int getNumeroCartas(){
        int ret=0;

        for(int i=0;i<cartas.size();i++){
            ret+=cartas.get(i).getCantidad();
        }
        return ret;
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
        public void cambiadoNumero();

    }

}


