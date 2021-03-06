package test.support.esri.com.portalitemview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogInFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogInFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    private String username;
    private String password;
    private String portalURL;
    private SharedPreferences sharedPreferences;
    Button loginButton;
    CheckBox remember_me;
    static EditText txtUsername;
    static EditText txtPassword;
    private View mainView;
    private RadioButton radio_portal;
    private RadioButton radio_arcgis;
    private EditText txtPortalURL;
    private Bundle argBundle;
    private SharedPreferences.Editor sharedPreferences_edit;
    private static boolean sharePreferencesState = false;


    public LogInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogInFragment newInstance(String param1, String param2) {
        LogInFragment fragment = new LogInFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME, txtUsername.getText().toString());
        args.putString(PASSWORD, txtPassword.getText().toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
            password = getArguments().getString(PASSWORD);
        }




    }

    /**
     * method to enable logging in to portal
     */

    private void loginToPortal() {
        //check for empty content
       boolean isPortalChecked = false;
        if(radio_portal.isChecked() && txtPortalURL.getText().toString().length() == 0){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainView.getContext(), "Please provide a portal url", Toast.LENGTH_LONG).show();
                    return;
                }
            });
        }

        if(txtUsername.getText().toString().length() == 0 && txtPassword.getText().toString().length() == 0){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainView.getContext(), "Please provide a password and username.", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        if(radio_portal.isChecked() && txtPortalURL.getText().toString().length() != 0){
                isPortalChecked = true;
        }


        if(sharedPreferences != null){
            username = sharedPreferences.getString(USERNAME, null);
            password = sharedPreferences.getString(PASSWORD, null);
            portalURL = txtPortalURL.getText().toString().trim();
            argBundle = new Bundle();
            argBundle.putString("USERNAME", username);
            argBundle.putString("PASSWORD", password);
        }else {
            username = txtUsername.getText().toString().trim();
            password = txtPassword.getText().toString().trim();
            portalURL = txtPortalURL.getText().toString().trim();
            argBundle = new Bundle();
            argBundle.putString("USERNAME", username);
            argBundle.putString("PASSWORD", password);
        }
        argBundle.putString("FRAGMENTTAG", "portal_view_fragment");
        argBundle.putBoolean("isPortalChecked", isPortalChecked);
        argBundle.putString("portalURL", portalURL);
        Intent navigatorIntent = new Intent(getContext(), PortalViewMain.class);
        navigatorIntent.putExtra("USERNAME", username);
        navigatorIntent.putExtra("PASSWORD", password);

        PortalViewFragment portalViewFragment = new PortalViewFragment();
        portalViewFragment.setArguments(argBundle);
        getActivity().startActivityFromFragment(this, navigatorIntent, 1);
}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.log_in_fragment, container, false);
        loginButton = (Button)mainView.findViewById(R.id.loginbutton);
        txtUsername = (EditText)mainView.findViewById(R.id.portal_username);
        txtPassword = (EditText)mainView.findViewById(R.id.portal_password);
        radio_arcgis = (RadioButton)mainView.findViewById(R.id.radio_arcgis);
        radio_portal = (RadioButton)mainView.findViewById(R.id.radio_portal);
        txtPortalURL = (EditText)mainView.findViewById(R.id.portal_url);
        remember_me = (CheckBox)mainView.findViewById(R.id.remember_me);

        remember_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if(remember_me.isChecked()){
            sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            sharedPreferences_edit = sharedPreferences.edit();
            sharedPreferences_edit.putString(PASSWORD, txtPassword.getText().toString());
            sharedPreferences_edit.putString(USERNAME, txtUsername.getText().toString());
            sharedPreferences_edit.commit();
            sharePreferencesState = true;
        }


        if(sharePreferencesState){
            remember_me.setChecked(true);
        }
        txtPortalURL.setVisibility(View.GONE);
        radio_arcgis.setChecked(true);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToPortal();
            }
        });

       radio_portal.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(radio_portal.isChecked()){
                   txtPortalURL.setVisibility(View.VISIBLE);
                   ((TextInputLayout)getActivity().findViewById(R.id.input_layout_password)).setHint("portal password");
                   ((TextInputLayout)getActivity().findViewById(R.id.input_layout_username)).setHint("portal username");
               }else{
                   txtPortalURL.setVisibility(View.GONE);
               }
           }
       });

        radio_arcgis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radio_arcgis.isChecked()){
                    txtPortalURL.setVisibility(View.GONE);
                    ((TextInputLayout)getActivity().findViewById(R.id.input_layout_password)).setHint("arcgis online password");
                    ((TextInputLayout)getActivity().findViewById(R.id.input_layout_username)).setHint("arcgis online username");
                }else{
                    txtPortalURL.setVisibility(View.VISIBLE);
                }
            }
        });

        return  mainView;
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


}
