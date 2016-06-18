package test.support.esri.com.portalitemview;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esri.arcgisruntime.mapping.Basemap;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BasemapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BasemapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BasemapFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View base_map_view;
    private OnFragmentInteractionListener mListener;

    public BasemapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BasemapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BasemapFragment newInstance(String param1, String param2) {
        BasemapFragment fragment = new BasemapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        new BasemapAsyncTask().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        base_map_view = inflater.inflate(R.layout.fragment_basemap, container, false);

        return base_map_view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class BasemapAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            final ArrayList<Basemap> baseLayers = new ArrayList<>();
            baseLayers.add(0, Basemap.createImagery());
            baseLayers.add(1, Basemap.createLightGrayCanvas());
            baseLayers.add(2, Basemap.createStreets());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BasemapAdapter basemapAdapter = new BasemapAdapter(baseLayers);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    RecyclerView recyclerView = (RecyclerView) base_map_view.findViewById(R.id.base_map_recycler);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(basemapAdapter);
                }
            });
            return null;
        }
    }

}
