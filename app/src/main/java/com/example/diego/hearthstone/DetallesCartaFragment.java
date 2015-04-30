package com.example.diego.hearthstone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class DetallesCartaFragment extends Fragment {

    private static final String IMAGEN = "imagen";

    private String url_imagen;

    public static DetallesCartaFragment newInstance(Carta c) {
        DetallesCartaFragment fragment = new DetallesCartaFragment();
        Bundle args = new Bundle();
        args.putString(IMAGEN, c.getUrl());
        fragment.setArguments(args);
        return fragment;
    }

    public DetallesCartaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url_imagen = getArguments().getString(IMAGEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_detalles_carta, container, false);
        ImageView ivCarta= (ImageView) view.findViewById(R.id.ivCarta);

        JSONManager ayudabd = new JSONManager(getActivity());
        JSONManager.DownloadImageTask im = ayudabd.new DownloadImageTask(ivCarta);
        im.execute(url_imagen);

        return view;
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
