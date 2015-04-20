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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Jonatan on 16/4/15.
 */
public class RecyclerViewAdapterMazos extends RecyclerView.Adapter<RecyclerViewAdapterMazos.ViewHolder> {

    public ClickListener clickListener;
    private ArrayList<Mazo>  mazos;
    private Context context;

    public RecyclerViewAdapterMazos(ArrayList<Mazo> mazos, Context context) {
        this.mazos=mazos;
        this.context=context;
    }


    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerViewAdapterMazos.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_deck_item,viewGroup,false);

        return new RecyclerViewAdapterMazos.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterMazos.ViewHolder viewHolder, final int i) {

        viewHolder.tvNombre.setText(mazos.get(i).getNombre());
        viewHolder.tvHeroe.setText(mazos.get(i).getClase());

        //Aqui habria que mostrar una imagen dependiendo del heroe
        int id;

        if(mazos.get(i).getClase().equals("druid"))
            id=R.mipmap.druida;
        else if (mazos.get(i).getClase().equals("hunter"))
            id=R.mipmap.cazador;
        else if (mazos.get(i).getClase().equals("mage"))
            id=R.mipmap.mago;
        else if (mazos.get(i).getClase().equals("paladin"))
            id=R.mipmap.paladin;
        else if (mazos.get(i).getClase().equals("priest"))
            id=R.mipmap.sacerdote;
        else if (mazos.get(i).getClase().equals("rogue"))
            id=R.mipmap.picaro;
        else if (mazos.get(i).getClase().equals("shaman"))
            id=R.mipmap.chaman;
        else if (mazos.get(i).getClase().equals("warlock"))
            id=R.mipmap.brujo;
        else
            id=R.mipmap.guerrero;

        viewHolder.ivMazo.setImageDrawable(context.getResources().getDrawable(id));


    }


    @Override
    public int getItemCount() {
        return mazos.size();
    }

    public void cambiaArray(ArrayList<Mazo> mazos){
        this.mazos=mazos;
        notifyDataSetChanged();
    }

    public Mazo get(int position){
        return mazos.get(position);
    }



    protected class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        public ImageView ivMazo;
        public TextView tvNombre;
        public TextView tvHeroe;


        public ViewHolder(View itemView) {
            super(itemView);

            this.ivMazo= (ImageView) itemView.findViewById(R.id.ivMazo);
            this.tvNombre= (TextView) itemView.findViewById(R.id.tvMazo);
            this.tvHeroe= (TextView) itemView.findViewById(R.id.tvHeroe);

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
        public void itemClicked(View view, int position);
    }

}
