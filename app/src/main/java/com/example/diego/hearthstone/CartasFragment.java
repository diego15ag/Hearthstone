package com.example.diego.hearthstone;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Diego on 13/4/15.
 */
public class CartasFragment extends Fragment implements RecyclerViewAdapterCartas.ClickListener{

    RecyclerView recyclerView;
    RecyclerViewAdapterCartas rva;
    private Callbacks mCallback;
    Carta cartaselect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_cartas,container,false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_cartas);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 1));

        JSONManager.position_clase=0;

        rva=new RecyclerViewAdapterCartas(JSONManager.filtro_clase(),getActivity().getApplicationContext());
        rva.setClickListener(this);
        recyclerView.setAdapter(rva);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_activity_collection_cartas, menu);


        //El icono para seleccionar la clase tendra una imagen distinta dependiendo de la clase seleccionada
        MenuItem item= menu.findItem(R.id.action_filter);

        int position=JSONManager.position_clase;

        if(position==0)
            item.setIcon(getResources().getDrawable(R.mipmap.druida));

        else if(position==1)
            item.setIcon(getResources().getDrawable(R.mipmap.cazador));

        else if(position==2)
            item.setIcon(getResources().getDrawable(R.mipmap.mago));

        else if(position==3)
            item.setIcon(getResources().getDrawable(R.mipmap.paladin));

        else if(position==4)
            item.setIcon(getResources().getDrawable(R.mipmap.sacerdote));

        else if(position==5)
            item.setIcon(getResources().getDrawable(R.mipmap.picaro));

        else if(position==6)
            item.setIcon(getResources().getDrawable(R.mipmap.chaman));

        else if(position==7)
            item.setIcon(getResources().getDrawable(R.mipmap.brujo));

        else if(position==8)
            item.setIcon(getResources().getDrawable(R.mipmap.guerrero));

        else
            item.setIcon(getResources().getDrawable(R.mipmap.hearthstone_logo));


        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public void itemClicked(View view, int position) {
        Carta carta=rva.get(position);
        cartaselect=carta;
        mCallback.onCardSelected(carta);
    }

    public void cambiarLista(ArrayList<Carta> cartas){
        rva.cambiaArray(cartas);
        //Fijamos el scroll al principio
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement Callbacks");
        }
    }

    public interface Callbacks {
        public void onCardSelected(Carta carta);
    }
}
