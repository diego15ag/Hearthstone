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
 * Created by Jonatan on 16/4/15.
 */
public class RecyclerViewAdapterMazosPredefinidos extends RecyclerView.Adapter<RecyclerViewAdapterMazosPredefinidos.ViewHolder> {

    public ClickListener clickListener;

    private ArrayList<Mazo> mazos;
    private Context context;

    public RecyclerViewAdapterMazosPredefinidos(ArrayList<Mazo> mazos, Context context) {
        this.mazos = mazos;
        this.context = context;
    }


    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerViewAdapterMazosPredefinidos.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_preset_deck_item, viewGroup, false);


        return new RecyclerViewAdapterMazosPredefinidos.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterMazosPredefinidos.ViewHolder viewHolder, final int i) {

        viewHolder.tvNombre.setText(mazos.get(i).getNombre());

        //Aqui habria que mostrar una imagen dependiendo del heroe
        int id;

        if (mazos.get(i).getClase().equals("druid"))
            id = R.mipmap.druida;
        else if (mazos.get(i).getClase().equals("hunter"))
            id = R.mipmap.cazador;
        else if (mazos.get(i).getClase().equals("mage"))
            id = R.mipmap.mago;
        else if (mazos.get(i).getClase().equals("paladin"))
            id = R.mipmap.paladin;
        else if (mazos.get(i).getClase().equals("priest"))
            id = R.mipmap.sacerdote;
        else if (mazos.get(i).getClase().equals("rogue"))
            id = R.mipmap.picaro;
        else if (mazos.get(i).getClase().equals("shaman"))
            id = R.mipmap.chaman;
        else if (mazos.get(i).getClase().equals("warlock"))
            id = R.mipmap.brujo;
        else
            id = R.mipmap.guerrero;

        viewHolder.ivMazo.setImageDrawable(context.getResources().getDrawable(id));

        viewHolder.tvNumeroCartas.setText(String.valueOf(getNumeroCartas(mazos.get(i))) + "/30");

        viewHolder.tvArcano.setText(String.valueOf(getArcanoObtenido(mazos.get(i)))+"/"+String.valueOf(getArcano(mazos.get(i))));
    }


    @Override
    public int getItemCount() {
        return mazos.size();
    }

    public void cambiaArray(ArrayList<Mazo> mazos) {
        this.mazos = mazos;
        notifyDataSetChanged();
    }

    public Mazo get(int position) {
        return mazos.get(position);
    }


    protected class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public ImageView ivMazo;
        public TextView tvNombre;
        public TextView tvArcano;
        public TextView tvNumeroCartas;


        public ViewHolder(View itemView) {
            super(itemView);

            this.ivMazo = (ImageView) itemView.findViewById(R.id.ivMazo);
            this.tvNombre = (TextView) itemView.findViewById(R.id.tvMazo);
            this.tvArcano = (TextView) itemView.findViewById(R.id.tvArcano);
            this.tvNumeroCartas = (TextView) itemView.findViewById(R.id.tvNCartas);

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

    private int getArcano(Mazo m) {
        int arcano = 0;

        for (int i = 0; i < m.getCartas().size(); i++) {

            Carta c = m.getCartas().get(i);

            if(!(m.getCartas().get(i).getConjunto().equals("naxxramas")||
                    m.getCartas().get(i).getConjunto().equals("brm"))) {
                if (c.getTipo().equals("common"))
                    arcano += 40*c.getCantidad();
                else if (c.getTipo().equals("rare"))
                    arcano += 100*c.getCantidad();
                else if (c.getTipo().equals("epic"))
                    arcano += 400*c.getCantidad();
                else if (c.getTipo().equals("legendary"))
                    arcano += 1600*c.getCantidad();
            }
        }

        return arcano;
    }


    private int getNumeroCartas(Mazo m) {
        int cartas = 0;

        for(int i=0;i<m.getCartas().size();i++){

            Carta necesitada= m.getCartas().get(i);
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

        }

        return cartas;
    }

    private int getArcanoObtenido(Mazo m) {
        int arcano = 0;

        for(int i=0;i<m.getCartas().size();i++){

            Carta necesitada= m.getCartas().get(i);
            for (int j = 0; j < JSONManager.Cartas_array.size(); j++) {
                if (necesitada.getId() == JSONManager.Cartas_array.get(j).getId()) {
                    Carta obtenida = JSONManager.Cartas_array.get(j);
                    int cantidad;

                    if (necesitada.getCantidad() > obtenida.getCantidad()) {
                        cantidad= obtenida.getCantidad();
                    } else {
                        cantidad= necesitada.getCantidad();
                    }

                    if(!(obtenida.getConjunto().equals("naxxramas")||
                            obtenida.getConjunto().equals("brm"))) {
                        if (obtenida.getTipo().equals("common"))
                            arcano += 40*cantidad;
                        else if (obtenida.getTipo().equals("rare"))
                            arcano += 100*cantidad;
                        else if (obtenida.getTipo().equals("epic"))
                            arcano += 400*cantidad;
                        else if (obtenida.getTipo().equals("legendary"))
                            arcano += 1600*cantidad;
                    }

                    break;
                }
            }

        }

        return arcano;
    }

}
