package test.support.esri.com.portalitemview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ControlsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ControlsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControlsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View controlsView;

    public ControlsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ControlsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ControlsFragment newInstance(String param1, String param2) {
        ControlsFragment fragment = new ControlsFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                     Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        controlsView = inflater.inflate(R.layout.fragment_controls, container, false);

        //Close action buttons view
        Button closeFloatButton = (Button)controlsView.findViewById(R.id.float_close_button);
        closeFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().hide(
                        getFragmentManager().findFragmentByTag("ControlsFrag")
                ).commit();
            }
        });

        //logic for edit button
        FloatingActionButton editFloatingButton = (FloatingActionButton)controlsView.findViewById(R.id.edit_data);
        editFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "action not implemented", Toast.LENGTH_LONG).show();
            }
        });
        //logic for add data button
        FloatingActionButton addDataButton = (FloatingActionButton)controlsView.findViewById(R.id.add_data);
        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "action not implemented", Toast.LENGTH_LONG).show();
            }
        });
        //logic for show layer button
        FloatingActionButton showLayerButton = (FloatingActionButton)controlsView.findViewById(R.id.show_layers);
        showLayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(getActivity().getSupportFragmentManager().findFragmentByTag("recycler_view_fragment").isHidden()){
                  getActivity().getSupportFragmentManager().beginTransaction().show(
                          getActivity().getSupportFragmentManager().findFragmentByTag("recycler_view_fragment")
                  ).commit();
              }else{
                  getActivity().getSupportFragmentManager().beginTransaction().hide(
                          getActivity().getSupportFragmentManager().findFragmentByTag("recycler_view_fragment")
                  ).commit();
              }
            }
        });
        return controlsView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden){
        if(hidden){
            PortalViewMain.globalMenu.findItem(R.id.portal_button).setTitle("Show Action Buttons");
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
}
