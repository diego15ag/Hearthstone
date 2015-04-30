package com.example.diego.hearthstone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

/**
 * Created by Diego on 13/4/15.
 */
public class MazosFragment extends Fragment implements RecyclerViewAdapterMazos.ClickListener{

    RecyclerView recyclerView;
    RecyclerViewAdapterMazos rva;
    private Callbacks mCallback;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.tab_mazos,container,false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_mazos);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 1));

        rva=new RecyclerViewAdapterMazos(JSONManager.Mazos_array,getActivity().getApplicationContext());
        recyclerView.setAdapter(rva);

        FloatingActionButton fab= (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onNewDeck();
            }
        });


        return v;
    }

    @Override
    public void itemClicked(View view, int position) {
        //Abrir los detalles del mazo


    }


    public interface Callbacks {
        public void onNewDeck();
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

    @Override
    public void onResume() {
        super.onResume();
        if (!isOnline()){
            Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
