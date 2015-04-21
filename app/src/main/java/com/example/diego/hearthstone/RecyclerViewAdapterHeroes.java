package com.example.diego.hearthstone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Diego on 20/4/15.
 */
public class RecyclerViewAdapterHeroes extends RecyclerView.Adapter<RecyclerViewAdapterHeroes.ViewHolder> {

    public ClickListener clickListener;
    private ArrayList<Carta> heroes;
    private Context context;
    private JSONManager jsonhelp;

    public RecyclerViewAdapterHeroes(ArrayList<Carta> heroes,Context context) {
        this.heroes=heroes;
        this.context=context;
        jsonhelp = new JSONManager(context);
        jsonhelp.startBG();
    }


    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerViewAdapterHeroes.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_hero_item,viewGroup,false);

        return new RecyclerViewAdapterHeroes.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterHeroes.ViewHolder viewHolder, final int i) {

        String classes[] = context.getResources().getStringArray(R.array.ClasesHearthstoneCartas);
        viewHolder.tvClase.setText(classes[i]);

        //Obtenemos la url de la imagen mas pequeña
        String url = heroes.get(i).getUrl().replaceAll("medium","small");

        JSONManager ayudabd = new JSONManager(context);
        JSONManager.DownloadImageTask im = ayudabd.new DownloadImageTask(viewHolder.ivHeroe);
        im.execute(url);
    }


    @Override
    public int getItemCount() {
        return heroes.size();
    }

    public Carta get(int position){
        return heroes.get(position);
    }

    protected class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        public ImageView ivHeroe;
        public TextView tvClase;

        public ViewHolder(View itemView) {
            super(itemView);

            this.ivHeroe= (ImageView) itemView.findViewById(R.id.imageViewHeroe);
            this.tvClase= (TextView) itemView.findViewById(R.id.textViewHeroe);

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
