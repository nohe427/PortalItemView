package test.support.esri.com.portalitemview;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.portal.PortalQueryParams;
import com.esri.arcgisruntime.portal.PortalQueryResultSet;
import com.esri.arcgisruntime.security.UserCredential;

import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AnonymousPortalFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAnalytics mFirebaseAnalytics;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View anonymousPortalLayoutView;
    private ProgressDialog progressDialog;
    public ArrayList<CardViewData> mCardViewData;

    public AnonymousPortalFragment() {
        // Required empty public constructor
    }

    public static AnonymousPortalFragment newInstance(String param1, String param2) {
        AnonymousPortalFragment fragment = new AnonymousPortalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mFirebaseAnalytics.logEvent("anonportalviewopened", null);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
        new AnonPortalAsyncTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        anonymousPortalLayoutView = inflater.from(getContext()).inflate(R.layout.fragment_anonymous_portal, container, false);
        return anonymousPortalLayoutView;
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

    public class AnonPortalAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            final Portal portal = new Portal("http://www.arcgis.com");
            portal.setCredential(new UserCredential("mrasante", "applegoe"));
            portal.loadAsync();
            portal.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {

                    if (portal.getLoadStatus() == LoadStatus.LOADED) {
                        mCardViewData = new ArrayList<>();
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {


                                    Snackbar.make(getActivity().findViewById(R.id.nav_map_view),
                                            portal.getUri() + " loaded for anonymous public user.", Snackbar.LENGTH_LONG
                                    ).show();
                                    TextView screen_name = (TextView) getActivity().findViewById(R.id.screen_name);
                                    screen_name.setText("Anonymous User");


                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                            progressDialog = ProgressDialog.show(getContext(), "Loading...", "Loading portal contents for anonymous user", true);
                                            PortalQueryParams portalQueryparams = new PortalQueryParams();
                                            portalQueryparams.setQuery("group: c755678be14e4a0984af36a15f5b643e OR group: b8787a74b4d74f7fb9b8fac918735153 and " +
                                                    "type: web map");
                                            portalQueryparams.setLimit(100);
                                            PortalQueryResultSet<PortalItem> portalQueryResults = portal.findItemsAsync(portalQueryparams).get();
                                            List<PortalItem> portalItems = portalQueryResults.getResults();
                                            for(PortalItem portalItem : portalItems){
                                                byte[] byteData = portalItem.fetchThumbnailAsync().get();
                                                if(byteData != null) {
                                                    mCardViewData.add(new CardViewData(portalItem,
                                                            BitmapFactory.decodeByteArray(byteData, 0,
                                                                    byteData.length), null));
                                                }
                                            }
                                        } catch (InterruptedException | ExecutionException inexe) {

                                        }
                                            CardViewAdapter anonAdapter = new CardViewAdapter(mCardViewData, getActivity().getSupportFragmentManager());
                                            RecyclerView anonRecyclerView = (RecyclerView)getActivity().findViewById(R.id.anonymous_recycler_view);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                            anonRecyclerView.setAdapter(anonAdapter);
                                            anonRecyclerView.setLayoutManager(linearLayoutManager);
                                           progressDialog.dismiss();
                                        }
                                    });


                            }
                        });
                    }

                }
            });

            return null;
        }
    }
}
