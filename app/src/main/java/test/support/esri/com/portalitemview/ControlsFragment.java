package test.support.esri.com.portalitemview;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.arcgis.Geodatabase;
import com.esri.arcgisruntime.datasource.arcgis.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.datasource.arcgis.SyncModel;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Item;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.portal.PortalItemType;
import com.esri.arcgisruntime.portal.PortalQueryParams;
import com.esri.arcgisruntime.portal.PortalUser;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseJob;
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseParameters;
import com.esri.arcgisruntime.tasks.geodatabase.GenerateLayerOption;
import com.esri.arcgisruntime.tasks.geodatabase.GeodatabaseSyncTask;
import com.esri.arcgisruntime.tasks.geodatabase.SyncGeodatabaseJob;
import com.esri.arcgisruntime.tasks.geodatabase.SyncGeodatabaseParameters;
import com.esri.arcgisruntime.tasks.geodatabase.SyncLayerOption;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Controls fragments for offline editing functionality and
 * toggle functionality for showing loaded portal items
 *
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
    private Geodatabase geodatabase;

    private MapView mapView;
    private Envelope extent;
    private Feature feature;
    private GraphicsOverlay graphicsOverlay;
    private Graphic graphic;
    private android.graphics.Point whereTapped;
    private Point whereTappedPoint;
    private SimpleMarkerSymbol simpleMarkerSym;
    private GeodatabaseFeatureTable geodatabaseFeatureTable = null;
    private  ControlsViewOnTouchListener controlModeListener;
    private GeodatabaseSyncTask geodatabaseSyncTask;
    private PortalViewMain.MapViewSingleClick mapViewSingleClick  = PortalViewMain.singleClick;
    
    private SyncGeodatabaseJob syncGdbJob;
    private ProgressDialog progressD;
    private FeatureLayer featureLayer;



    public ControlsFragment() {
        //Required empty public constructor
    }


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
        
        //instantiate progress dialog with fragment's context
        progressD = new ProgressDialog(getContext());

        //create and register event
        mapView = (MapView) getActivity().findViewById(R.id.nav_map_view);
        controlModeListener = new ControlsViewOnTouchListener(getContext(), mapView);
        mapView.setOnTouchListener(controlModeListener);


        //Close action buttons view
        Button closeFloatButton = (Button) controlsView.findViewById(R.id.float_close_button);
        closeFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().hide(
                        getFragmentManager().findFragmentByTag("ControlsFrag")
                ).commit();
                mapView.setOnTouchListener(mapViewSingleClick);
            }
        });

        //logic for edit button
        FloatingActionButton editFloatingButton = (FloatingActionButton) controlsView.findViewById(R.id.edit_data);
        editFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                searchForPortalItemType(PortalItemType.WEBMAP);
                performEdits();
            }
        });
        //logic for add data button
        FloatingActionButton addDataButton = (FloatingActionButton) controlsView.findViewById(R.id.add_data);
        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWebMap(PortalItemType.WEBMAP);
            }
        });

        //wire click to the sync button
        FloatingActionButton syncDataButton = (FloatingActionButton)controlsView.findViewById(R.id.sync_data);
        syncDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ControlsSyncAsync().execute();
            }
        });

        //logic for show layer button
        FloatingActionButton showLayerButton = (FloatingActionButton) controlsView.findViewById(R.id.show_layers);
        showLayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().findFragmentByTag("recycler_view_fragment").isHidden()) {
                    getActivity().getSupportFragmentManager().beginTransaction().show(
                            getActivity().getSupportFragmentManager().findFragmentByTag("recycler_view_fragment")
                    ).commit();
                } else {
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


    public class ControlsAddDataAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //ask for system permission
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMessage("Write access granted. Re-initialize gdb generation.");
                    }
                });
            } else {
                //get or create an offline geodatabase

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        featureLayer = new FeatureLayer(new ServiceFeatureTable(
                                "http://sampleserver5.arcgisonline.com/arcgis/rest/services/Sync/WildfireSync/FeatureServer/0"
                        ));
                        featureLayer.loadAsync();
                        featureLayer.addDoneLoadingListener(new Runnable() {
                            @Override
                            public void run() {
                                if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
                                    mapView.getMap().getOperationalLayers().add(featureLayer);
                                    extent = featureLayer.getFullExtent();
//                                    Envelope envelope = new Envelope(-150.654, -45.157, 100.58, 75.46, SpatialReference.create(4269));
                                    Viewpoint viewPoint = new Viewpoint(extent);
                                    mapView.setViewpointAsync(viewPoint);
                                    showMessage("Feature layer " + featureLayer.getName() + " successfully loaded and added to the map.");
                                    controlsView.findViewById(R.id.edit_data).setVisibility(View.VISIBLE);
                                    controlsView.findViewById(R.id.sync_data).setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                });

            }
            return null;
        }
    }

    private class ControlsSyncAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
                SyncGeodatabaseParameters syncGeodatabaseParameters = new SyncGeodatabaseParameters();
                syncGeodatabaseParameters.setRollbackOnFailure(true);
                syncGeodatabaseParameters.setSyncDirection(SyncGeodatabaseParameters.SyncDirection.UPLOAD);
                SyncLayerOption syncLayerOption = new SyncLayerOption(0);
                syncLayerOption.setSyncDirection(SyncGeodatabaseParameters.SyncDirection.UPLOAD);
                syncGeodatabaseParameters.getLayerOptions().add(syncLayerOption);
                syncGdbJob = geodatabaseSyncTask.syncGeodatabaseAsync(syncGeodatabaseParameters, geodatabase);
                syncGdbJob.start();
                final List<Job.Message> messages = syncGdbJob.getMessages();
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       progressD.setTitle("Synchronizing geodatabase...");
                       progressD.show();
                       syncGdbJob.addJobChangedListener(new Runnable() {
                           @Override
                           public void run() {
                               for(Job.Message message : messages){
                                   progressD.setMessage(message.getMessage());
                               }
                           }
                       });

                       syncGdbJob.addJobDoneListener(new Runnable() {
                           @Override
                           public void run() {
                               if(syncGdbJob.getStatus() == Job.Status.SUCCEEDED){
                                   showMessage("Offline edits synchronized successfully");
                                   progressD.dismiss();
                               }
                               if(syncGdbJob.getStatus() == Job.Status.FAILED){
                                   showMessage("Sync failure message: "+ syncGdbJob.getError().getAdditionalMessage());
                                   progressD.dismiss();
                               }
                           }
                       });
                   }
               });


            return  null;

        }
    }


    private void performEdits(){
        geodatabaseSyncTask = new GeodatabaseSyncTask(getContext(), "http://sampleserver5.arcgisonline.com/arcgis/rest/services/Sync/WildfireSync/FeatureServer");
        GenerateGeodatabaseParameters generateGeodatabaseParameters = new GenerateGeodatabaseParameters();
        generateGeodatabaseParameters.setExtent(mapView.getVisibleArea());
        generateGeodatabaseParameters.setOutSpatialReference(featureLayer.getSpatialReference());
        generateGeodatabaseParameters.setSyncModel(SyncModel.PER_LAYER);
        generateGeodatabaseParameters.getLayerOptions().add(new GenerateLayerOption(0));

        String geodatabaseFile = Environment.getExternalStorageDirectory().getPath() + "/test.geodatabase";
        final File file = new File(geodatabaseFile);
        if (file.exists()) {
            file.delete();
            showMessage("Geodatabase file deleted from " + file.getAbsolutePath());
        }
        final GenerateGeodatabaseJob generateGeodatabaseJob =
                geodatabaseSyncTask.generateGeodatabaseAsync(generateGeodatabaseParameters, geodatabaseFile);
        boolean status = generateGeodatabaseJob.start();
//                                   final List<Job.Message> messages = generateGeodatabaseJob.getMessages();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (generateGeodatabaseJob.getStatus() == Job.Status.STARTED) {
                    progressD.setTitle("Generating geodatabase...");
                    progressD.show();
                    final List<Job.Message> messages = generateGeodatabaseJob.getMessages();
                    generateGeodatabaseJob.addJobChangedListener(new Runnable() {
                        @Override
                        public void run() {
                            for (Job.Message message : messages) {
                                progressD.setMessage(message.getMessage());
                            }
                        }
                    });

                    generateGeodatabaseJob.addJobDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            if (generateGeodatabaseJob.getStatus() == Job.Status.SUCCEEDED) {
                                progressD.dismiss();
                                geodatabase = generateGeodatabaseJob.getResult();
                                showMessage("Offline geodatabase successfully downloaded.");
                                controlsView.findViewById(R.id.edit_data).setVisibility(View.VISIBLE);
                            } else if (generateGeodatabaseJob.getStatus() == Job.Status.FAILED) {
                                progressD.setMessage("Offline geodatabase generation failed with error: \n" +
                                        generateGeodatabaseJob.getError().getAdditionalMessage());
                                progressD.dismiss();
                            }
                        }
                    });
                }
            }
        });


        if(geodatabase != null){
            geodatabase.loadAsync();
           geodatabaseFeatureTable = geodatabase.getGeodatabaseFeatureTableByServiceLayerId(0);
           if(geodatabaseFeatureTable != null)
               geodatabaseFeatureTable.loadAsync();
            if(geodatabaseFeatureTable == null){
                showMessage("Could not detect the table with the specified id. \n Please try again.");
                return;
            }
            geodatabaseFeatureTable.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                   LoadStatus status = geodatabaseFeatureTable.getLoadStatus();
                    if(status == LoadStatus.LOADED){
                        showMessage("offline gdb successfully loaded for editing!\n Click anywhere in current map view to begin editing.");
                    }
                }
            });
        }
    }


    public class ControlsViewOnTouchListener extends DefaultMapViewOnTouchListener {

        public ControlsViewOnTouchListener(Context context, MapView mapView){
            super(context, mapView);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent){
            whereTapped = new android.graphics.Point(Math.round(motionEvent.getX()), Math.round(motionEvent.getY()));
            whereTappedPoint = mapView.screenToLocation(whereTapped);

            graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
            simpleMarkerSym = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 16);
            graphic = new Graphic(whereTappedPoint);

            graphic.setSymbol(simpleMarkerSym);
            graphicsOverlay.getGraphics().add(graphic);
            mapView.getGraphicsOverlays().add(graphicsOverlay);

            return true;
        }

    }
    private void createWebMap(PortalItemType portalItemType) {
        new ControlsAddDataAsync().execute();
    }


    /**
     * method to show toast messages
     *
     * @param message
     */

    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

    }


    /**
     * check for the presence of a feature service in user's portal
     * and create a recycler view to populate with user's feature service
     *
     * @param itemType
     * @return boolean isFeatureservice
     */

    public PortalItem searchForPortalItemType(PortalItemType itemType) {
        boolean isFeatureService = false;
        //check to see if there is any web map already loaded in the map view
        // then give user the option to select that one or edit a different one

        MapView mapview = (MapView) getActivity().findViewById(R.id.nav_map_view);

        Item item = mapview.getMap().getItem();
        if (item instanceof PortalItem) {
            if (((PortalItem) item).getType() != itemType) {
                showMessage("No web map added to the map. \nPlease select one from your portal.");
                return null;
            }
        }
        isFeatureService = true;
        final List<PortalItem> portalItem;
        CardViewData cardViewData = null;
        final CardViewAdapter cardViewAdapter;
        portal = PortalViewFragment.portal;
        username = portal.getPortalUser();

        ArrayList<CardViewData> cardViewArrayList = new ArrayList<>();


        try {
            if (portal == null) {
                return null;
            } else {

                if (username != null) {
                    PortalQueryParams portalQueryParams = new PortalQueryParams("owner: " + portal.getPortalUser().getUserName());
                    portalQueryParams.setLimit(100);
                    portalItem = portal.findItemsAsync(portalQueryParams).get().getResults();

                    if (portalItem.size() == 0) {
                        showMessage("No item of type " + itemType.toString() + " is present in the user " +
                                portal.getPortalUser().getFullName() + "'s portal");
                        controlsView.findViewById(R.id.controls_recycler).setVisibility(View.GONE);
                        return null;
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
                        ProgressDialog progressDialog = new ProgressDialog(getContext().getApplicationContext());

                        @Override
                        public void run() {
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setMessage("Please wait while items loads");
                            progressDialog.setTitle("Loading...");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LinearLayout linearLayout = (LinearLayout) controlsView.findViewById(R.id.recycler_linear_layout);
                                    RecyclerView recyclerView = (RecyclerView) controlsView.findViewById(R.id.controls_recycler);
                                    recyclerView.setAdapter(cardViewAdapter);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    linearLayout.setVisibility(View.VISIBLE);
                                    //progressDialog.dismiss();
                                }
                            });

                        }
                    });
                } else {
                    Toast.makeText(getContext(), "No username associated with this portal", Toast.LENGTH_LONG).show();
                }
            }
        } catch (ExecutionException | InterruptedException inexe) {

        }

        return cardViewData.getPortalItem();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
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
