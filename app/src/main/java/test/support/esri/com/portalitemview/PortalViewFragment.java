package test.support.esri.com.portalitemview;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.portal.PortalItemType;
import com.esri.arcgisruntime.portal.PortalQueryParams;
import com.esri.arcgisruntime.portal.PortalQueryResultSet;
import com.esri.arcgisruntime.security.UserCredential;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PortalViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PortalViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PortalViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private ArrayList<CardViewData> mCardViewData;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayout;
    private String username;
    private String password;
    private ProgressDialog progressDialog;
    private boolean isPortalChecked;
    private String portalURL;
    private OnFragmentInteractionListener mListener;
    public static Portal portal;
    private NavigationView navigationView;
    public PortalViewFragment() {
        // Required empty public constructor
    }

    public static PortalViewFragment newInstance(String param1, String param2) {
        PortalViewFragment fragment = new PortalViewFragment();
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
            new PortalViewAsyncTask().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        view = inflater.inflate(R.layout.portal_view_fragment, container, false);
        return view;
    }

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
            username = getArguments().getString("USERNAME");
            password = getArguments().getString("PASSWORD");
            isPortalChecked = getArguments().getBoolean("isPortalChecked");
            portalURL = getArguments().getString("portalURL");
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class PortalViewAsyncTask extends AsyncTask<Void, Void, Void> {


        public PortalViewAsyncTask() {

        }

        private boolean activateItems(boolean activation) {
            navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
            MenuItem loginMenuItem = navigationView.getMenu().getItem(1);
            if (activation)
                loginMenuItem.setTitle("Log Out");
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setEnabled(activation);
                if (navigationView.getMenu().getItem(i).getTitle().toString().equalsIgnoreCase("ArcGIS Community")) {
                    for (int c = 0; c < navigationView.getMenu().getItem(i).getSubMenu().size(); c++) {
                        navigationView.getMenu().getItem(i).getSubMenu().getItem(c).setEnabled(true);
                    }
                }
            }
            return activation;
        }


        @Override
        protected Void doInBackground(Void... params) {
            if (username == null && password == null) {
                return null;
            }
            if (isPortalChecked) {
                portal = new Portal(portalURL);
            } else {
                portal = new Portal("http://www.arcgis.com");
            }
            portal.setCredential(new UserCredential(username, password));
            portal.loadAsync();
            portal.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (portal.getLoadStatus() == LoadStatus.LOADED) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        progressDialog = ProgressDialog.show(getContext(), "Loading... ",
                                                "Loading content from " + portal.getUri().toString(), true);
                                        Snackbar.make(getActivity().findViewById(R.id.nav_map_view), "Portal loaded for " +
                                                portal.getPortalUser().getFullName(), Snackbar.LENGTH_LONG).show();
                                        TextView screen_name = (TextView) getActivity().findViewById(R.id.screen_name);
                                        screen_name.setText("Welcome " + portal.getPortalUser().getFullName());
                                        ImageView screenImage = (ImageView) getActivity().findViewById(R.id.screen_image);
                                        byte[] imgByte = portal.getPortalUser().fetchThumbnailAsync().get();

                                        if (imgByte == null) {
                                            Snackbar.make(getActivity().findViewById(R.id.nav_view), "No thumbnail set for "
                                                    + portal.getPortalUser().getFullName(), Snackbar.LENGTH_LONG).show();
                                        } else {
                                            screenImage.setImageBitmap(BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length));
                                        }
                                        activateItems(true);
                                    } catch (ExecutionException | InterruptedException ex) {

                                    }
                                }
                            });

                            //initialize ArrayList
                            mCardViewData = new ArrayList<>();
                            //return if cancelled
                            if (isCancelled()) {
                                return;
                            }

                            //provide your queries
                            PortalQueryParams portalQueryParams = new PortalQueryParams();
                            portalQueryParams.setQuery("owner: " + portal.getPortalUser().getUserName());
                            portalQueryParams.setLimit(100);
                            ListenableFuture<PortalQueryResultSet<PortalItem>> portalListItems =
                                    portal.findItemsAsync(portalQueryParams);
                            List<PortalItem> portalItems = portalListItems.get().getResults();
                            for (PortalItem portalItem : portalItems) {
                                byte[] data = portalItem.fetchThumbnailAsync().get();
                                if (isCancelled()) {
                                    return;
                                }

                                if (data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    if (portalItem.getType() == PortalItemType.WEBMAP)
                                        mCardViewData.add(new CardViewData(portalItem, bitmap, new ArcGISMap(portalItem)));

                                }
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter = new CardViewAdapter(mCardViewData, getActivity().getSupportFragmentManager());
                                    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
                                    mLinearLayout = new LinearLayoutManager(getActivity());
                                    recyclerView.setLayoutManager(mLinearLayout);
                                    recyclerView.setAdapter(mAdapter);
                                    progressDialog.dismiss();
                                    
                                }
                            });


                        } else {
                            if (portal.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                                Snackbar.make(getActivity().findViewById(R.id.nav_view), "The provided credentials not valid for " +
                                        portal.getUri(), Snackbar.LENGTH_LONG).show();
                                   NavigationView navView = (NavigationView)getActivity().findViewById(R.id.nav_view);
                                    navView.getMenu().getItem(1).setEnabled(true);
                            }
                        }
                    } catch (ExecutionException | InterruptedException exception) {
                        Log.d("Exception", exception.getMessage());
                    }
                }
            });


            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

}
