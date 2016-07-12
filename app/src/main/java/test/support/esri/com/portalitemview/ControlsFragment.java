package test.support.esri.com.portalitemview;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.datasource.arcgis.Geodatabase;
import com.esri.arcgisruntime.datasource.arcgis.SyncModel;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.Item;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.portal.PortalItemType;
import com.esri.arcgisruntime.portal.PortalQueryParams;
import com.esri.arcgisruntime.portal.PortalUser;
import com.esri.arcgisruntime.security.UserCredential;
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseJob;
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseParameters;
import com.esri.arcgisruntime.tasks.geodatabase.GenerateLayerOption;
import com.esri.arcgisruntime.tasks.geodatabase.GeodatabaseSyncTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


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
    private PortalUser username;
    private Portal portal;
    private String JSONFileLocation = "\\res\\raw\\airport.json";//getContext().getResources().getResourceTypeName(R.raw.airport);

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
                searchForPortalItemType(PortalItemType.WEBMAP);
            }
        });
        //logic for add data button
        FloatingActionButton addDataButton = (FloatingActionButton)controlsView.findViewById(R.id.add_data);
        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            createWebMap(PortalItemType.WEBMAP);
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


    public class ControlsFragAsync extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... params) {
            //ask for system permission
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }else {
                //get or create an offline geodatabase
                GeodatabaseSyncTask geodatabaseSyncTask = new GeodatabaseSyncTask(getContext(), "http://csc-kasante7l3.esri.com:6080/arcgis/rest/services/RuntimeServices/OfflineFeatureService/FeatureServer");
                geodatabaseSyncTask.setCredential(new UserCredential("kwasi", "applegoe"));
                GenerateGeodatabaseParameters generateGeodatabaseParameters = new GenerateGeodatabaseParameters();
                generateGeodatabaseParameters.setExtent(new Envelope(-127.250, 35.724, -112.394, 43.138, SpatialReference.create(4269)));
                generateGeodatabaseParameters.setOutSpatialReference(SpatialReference.create(4269));
                generateGeodatabaseParameters.setSyncModel(SyncModel.PER_GEODATABASE);
                generateGeodatabaseParameters.getLayerOptions().add(new GenerateLayerOption(0));

                String geodatabaseFile = Environment.getExternalStorageDirectory().getPath() + "/test.geodatabase";
                File file = new File(geodatabaseFile);
                if (file.exists()) {
                    file.delete();
                }
                final GenerateGeodatabaseJob generateGeodatabaseJob =
                        geodatabaseSyncTask.generateGeodatabaseAsync(generateGeodatabaseParameters, geodatabaseFile);
                generateGeodatabaseJob.setCredential(new UserCredential("kwasi", "applegoe"));
                boolean status = generateGeodatabaseJob.start();
                final List<Job.Message> messages = generateGeodatabaseJob.getMessages();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        if (generateGeodatabaseJob.getStatus() == Job.Status.STARTED) {
                            progressDialog.setTitle("Generating geodatabase...");
                            progressDialog.show();
                            /*for(int i=0; i < messages.size(); i++){
                                progressDialog.setMessage(messages.get(i).getMessage());
                            }*/
                            progressDialog.setMessage(generateGeodatabaseJob.getStatus().toString());
                            if (generateGeodatabaseJob.getStatus() == Job.Status.SUCCEEDED) {
                                progressDialog.dismiss();
                            }
                        } else if (generateGeodatabaseJob.getStatus() == Job.Status.FAILED) {
                            showMessage(generateGeodatabaseJob.getStatus().toString());
                            showMessage(generateGeodatabaseJob.getError().getAdditionalMessage());
                        }


                    }
                });
            }



        /*SyncGeodatabaseParameters syncGeodatabaseParameters = new SyncGeodatabaseParameters();
        syncGeodatabaseParameters.setRollbackOnFailure(true);
        syncGeodatabaseParameters.setSyncDirection(SyncGeodatabaseParameters.SyncDirection.BIDIRECTIONAL);
        SyncLayerOption syncLayerOption = new SyncLayerOption(0);
        syncLayerOption.setSyncDirection(SyncGeodatabaseParameters.SyncDirection.BIDIRECTIONAL);
        syncGeodatabaseParameters.getLayerOptions().add(syncLayerOption);
        geodatabaseSyncTask.syncGeodatabaseAsync(syncGeodatabaseParameters)*/
            //read it into a json
            //read the json into a

            Geodatabase geodatabase = new Geodatabase(Environment.getExternalStorageDirectory().getPath() +"//test.geodatabase");

            Gson gson = new GsonBuilder().create();
            String jsonString =
                    gson.toJson(geodatabase, Geodatabase.class);

            return null;
        }
    }

    private LinearLayout createControlsRecyclerView(){
        LayoutInflater inflater=  (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return  (LinearLayout) inflater.inflate(R.layout.constrols_recycler_view, (ViewGroup) getActivity().findViewById(R.id.nav_map_view));
    }


    private void createWebMap(PortalItemType portalItemType){
        new ControlsFragAsync().execute();


    }


    /**
     * method to show toast messages
     *@param message
     */

    public void showMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

    }


    /**
     * check for the presence of a feature service in user's portal
     * and create a recycler view to populate with user's feature service
     * @param itemType
     * @return boolean isFeatureservice
     */

    public boolean searchForPortalItemType(PortalItemType itemType){
        boolean isFeatureService = false;
        //check to see if there is any web map already loaded in the map view
        // then give user the option to select that one or edit a different one

        MapView mapview = (MapView)getActivity().findViewById(R.id.nav_map_view);

        Item item = mapview.getMap().getItem();
        if(item instanceof PortalItem){
            if(((PortalItem) item).getType() != itemType){
                showMessage("No web map added to the map. \nPlease select one from your portal.");
                return false;
            }
        }
            isFeatureService = true;
        final List<PortalItem> portalItem;
        CardViewData cardViewData;
        final CardViewAdapter cardViewAdapter;
        portal = PortalViewFragment.portal;
        username = portal.getPortalUser();

        ArrayList<CardViewData> cardViewArrayList = new ArrayList<>();


        try{
        if(portal == null){
            return false;
        }else{

            if(username != null) {
                PortalQueryParams portalQueryParams = new PortalQueryParams("owner: " + portal.getPortalUser().getUserName());
                portalQueryParams.setLimit(100);
                portalItem = portal.findItemsAsync(portalQueryParams).get().getResults();

                if(portalItem.size() ==0){
                    showMessage("No item of type "+ itemType.toString() +" is present in the user "+
                    portal.getPortalUser().getFullName() +"'s portal");
                    controlsView.findViewById(R.id.controls_recycler).setVisibility(View.GONE);
                    return false;
                }

                for (PortalItem portalItems : portalItem) {
                        byte[] cardByteArray = portalItems.fetchThumbnailAsync().get();
                        if (cardByteArray != null) {
                            cardViewData = new CardViewData(portalItems,
                                    BitmapFactory.decodeByteArray(cardByteArray,
                                            0, cardByteArray.length), null);
                            cardViewArrayList.add(cardViewData);
                        }
                }
                cardViewAdapter = new CardViewAdapter(cardViewArrayList, getActivity().getSupportFragmentManager());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialog progressDialog = ProgressDialog.show(controlsView.getContext(),
                                "Loading...", "Please wait while items loads");
                        LinearLayout linearLayout = (LinearLayout) controlsView.findViewById(R.id.recycler_linear_layout);
                        RecyclerView recyclerView = (RecyclerView) controlsView.findViewById(R.id.controls_recycler);
                        recyclerView.setAdapter(cardViewAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        linearLayout.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                });
            }else{
                Toast.makeText(getContext(), "No username associated with this portal", Toast.LENGTH_LONG).show();
            }
        }
        }catch (ExecutionException|InterruptedException inexe){

        }

        return isFeatureService;

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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
