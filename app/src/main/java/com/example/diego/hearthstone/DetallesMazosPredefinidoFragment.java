package com.example.diego.hearthstone;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetallesMazosPredefinidoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetallesMazosPredefinidoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetallesMazosPredefinidoFragment extends Fragment implements RecyclerViewAdapterCartasMazoPredefinido.ClickListener {

    private static final String MAZO = "mazo";

    private Mazo mazo;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterCartasMazoPredefinido rva;

    private OnFragmentInteractionListener mListener;

    public static DetallesMazosPredefinidoFragment newInstance(Mazo mazo) {
        DetallesMazosPredefinidoFragment fragment = new DetallesMazosPredefinidoFragment();
        Bundle args = new Bundle();
        args.putSerializable(MAZO, mazo);
        fragment.setArguments(args);
        return fragment;
    }

    public DetallesMazosPredefinidoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mazo= (Mazo) getArguments().getSerializable(MAZO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_detalles_mazos_predefinido, container, false);

        recyclerView= (RecyclerView) view.findViewById(R.id.recycler_cartas_pred);
        rva=new RecyclerViewAdapterCartasMazoPredefinido(mazo.getCartas(),getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rva.setClickListener(this);
        recyclerView.setAdapter(rva);

        return view;
    }

    public int getIdMazo(){
        return mazo.getId();
    }

    public void actualiza(Mazo m){
        mazo=m;
        rva=new RecyclerViewAdapterCartasMazoPredefinido(mazo.getCartas(),getActivity());
        rva.setClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(rva);
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
        if(mListener!=null)
            mListener.detallesCarta(mazo.getCartas().get(position));
    }

    public interface OnFragmentInteractionListener {
        public void detallesCarta(Carta c);
    }

}
