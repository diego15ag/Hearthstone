package com.example.diego.hearthstone;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MazosPredefinidosFragment extends Fragment implements RecyclerViewAdapterMazosPredefinidos.ClickListener{

    private OnFragmentInteractionListener mListener;
    public RecyclerView recyclerView;
    private RecyclerViewAdapterMazosPredefinidos rva;

    public MazosPredefinidosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_mazos_predefinidos, container, false);

        recyclerView= (RecyclerView) v.findViewById(R.id.recycler_mazos_pred);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rva=new RecyclerViewAdapterMazosPredefinidos(JSONManager.Mazos_predefinidos_array,getActivity());
        rva.setClickListener(this);
        recyclerView.setAdapter(rva);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void itemClicked(View view, int position) {
        Mazo m= rva.get(position);
        if(mListener!=null)
            mListener.muestraMazo(m);
    }

    public interface OnFragmentInteractionListener {
        public void muestraMazo(Mazo m);
    }

    public void actualiza(){
        rva=new RecyclerViewAdapterMazosPredefinidos(JSONManager.Mazos_predefinidos_array,getActivity());
        rva.setClickListener(this);
        recyclerView.setAdapter(rva);
    }

}
