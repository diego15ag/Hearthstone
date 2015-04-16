package com.example.diego.hearthstone;

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
public class CartasFragment extends Fragment implements RecyclerViewAdapter.ClickListener{

    RecyclerView recyclerView;
    RecyclerViewAdapter rva;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_cartas,container,false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 1));
        rva=new RecyclerViewAdapter(JSONManager.filtro_clase(),getActivity().getApplicationContext());
        rva.setClickListener(this);
        recyclerView.setAdapter(rva);

        return v;
    }

    @Override
    public void itemClicked(View view, int position) {

    }

    public void setAdapter(ArrayList<Carta> cartas, Context contexto){
        rva=new RecyclerViewAdapter(cartas, contexto);
        rva.setClickListener(this);
        recyclerView.setAdapter(rva);
    }
}
