package com.example.diego.hearthstone;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_cartas,container,false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 1));
        rva=new RecyclerViewAdapterCartas(JSONManager.filtro_clase(),getActivity().getApplicationContext());
        rva.setClickListener(this);
        recyclerView.setAdapter(rva);

        return v;
    }

    @Override
    public void itemClicked(View view, int position) {
        Carta carta=rva.get(position);
        mCallback.onCardSelected(carta);
    }

    public void cambiarLista(ArrayList<Carta> cartas){
        rva.cambiaArray(cartas);
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
