package test.support.esri.com.portalitemview;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.esri.arcgisruntime.tasks.geocode.SuggestParameters;
import com.esri.arcgisruntime.tasks.geocode.SuggestResult;
import com.esri.arcgisruntime.tasks.route.DirectionManeuver;
import com.esri.arcgisruntime.tasks.route.Route;
import com.esri.arcgisruntime.tasks.route.RouteParameters;
import com.esri.arcgisruntime.tasks.route.RouteResult;
import com.esri.arcgisruntime.tasks.route.RouteTask;
import com.esri.arcgisruntime.tasks.route.Stop;

import com.google.firebase.analytics.FirebaseAnalytics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoutingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoutingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String AGO_GEOCODE_URL = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer";
    private FirebaseAnalytics mFirebaseAnalytics;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View routeFragmentView;
    private FloatingActionButton floatingRouteButton;
//    private final String AGO_ROUTING_SERVICE= "http://csc-kasante7l3.esri.com:6080/arcgis/rest/services/Routing/Routing/NAServer/Route";
    private final String AGO_ROUTING_SERVICE= "http://192.168.1.6:6080/arcgis/rest/services/Routing/Routing/NAServer/Route";
    private ProgressDialog progressDialog;
    private  Route route;
    private DrawerLayout route_drawer_layout;
    private FloatingActionButton directionsFloatingButton;
    private TextToSpeech textToSpeech;
    private Switch switcher;
    //private final String AGO_ROUTING_SERVICE= "http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Network/USA/NAServer/Route";


    public RoutingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoutingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoutingFragment newInstance(String param1, String param2) {
        RoutingFragment fragment = new RoutingFragment();
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
        mFirebaseAnalytics.logEvent("routingfragmentopened", null);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        routeFragmentView = inflater.inflate(R.layout.fragment_routing, container, false);

        floatingRouteButton = (FloatingActionButton)routeFragmentView.findViewById(R.id.router);
        floatingRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new RoutingFragAsyncTask().execute();
            }

        });

        route_drawer_layout = (DrawerLayout)routeFragmentView.findViewById(R.id.route_drawer_layout);
        directionsFloatingButton = (FloatingActionButton)routeFragmentView.findViewById(R.id.directions);
/*ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), route_drawer_layout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        route_drawer_layout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();*/




        //implement logic for the initialization of speech
        switcher = (Switch)routeFragmentView.findViewById(R.id.voice_switcher);
        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switcher.isChecked()){

                    textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status == TextToSpeech.SUCCESS)
                                showMessage("Speech engine initialized successfully");
                        }
                    });
                    textToSpeech.setSpeechRate(0.8f);

                }
                if(!switcher.isChecked()){
                    textToSpeech.shutdown();
                }
            }
        });
//        route_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        //route_drawer_layout.closeDrawer(GravityCompat.END);
        return routeFragmentView;
    }

       private void showMessage(final String message) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }





    public class RoutingFragAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            mFirebaseAnalytics.logEvent("routingstarted", null);
            performRouting();
            return null;
        }



        @Override
        protected void onPreExecute(){
        }

        @Override
        protected void onPostExecute(Void value){
        }


        private double convertMetersToMiles(double metersValue){
            DecimalFormat decimalFormat = new DecimalFormat("##");
            double milesValue =  Double.parseDouble(decimalFormat.format(metersValue * 0.0006214));
            if(milesValue < 1){
                return metersValue;
            }
            return milesValue;
        }

        private String convertMinutesToHoursMins(double mins){
            double hours = mins/60;
            DecimalFormat decimalFormat = new DecimalFormat("##");
            if(hours < 1){
                double minutes = mins;

                return String.valueOf(decimalFormat.format(minutes)) + " min";
            }else {
                double minutes = mins % 60;
                return String.valueOf(decimalFormat.format(hours)) + " h" + String.valueOf(decimalFormat.format(minutes)) + " min";
            }
        }


        private List<Point> performRouting() {
            final List<Point> geocodedPoints = new ArrayList<>();
            EditText fromRouteLocation = (EditText) routeFragmentView.findViewById(R.id.from_route_location);
            EditText toRouteLocation = (EditText) routeFragmentView.findViewById(R.id.to_route_location);
            final String fromPoint = fromRouteLocation.getText().toString().trim();
            final String toPoint = toRouteLocation.getText().toString().trim();
            Bundle bundle = new Bundle();
            bundle.putString("toaddress", toPoint);
            bundle.putString("fromaddress", fromPoint);
            mFirebaseAnalytics.logEvent("addressestoroute", bundle);
            final LocatorTask locatorTask = new LocatorTask(AGO_GEOCODE_URL);
            locatorTask.loadAsync();
            locatorTask.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {

                    if(locatorTask.getLoadStatus() == LoadStatus.LOADED) {
                        try {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   /* routingProgressBar.setVisibility(View.VISIBLE);
                                    routingProgressBar.setIndeterminate(true);*/
                                    progressDialog = ProgressDialog.show(getContext(),
                                            "Calculating Route...", "Please waiting... calculating route",
                                            true);
                                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                }
                            });
                            Log.d("KwasiLocatorLoaded: ", locatorTask.getLoadStatus().toString());
                            SuggestParameters suggestParams = new SuggestParameters();
                            suggestParams.setCountryCode("USA");
                            //perform for from location
                            List<SuggestResult> fromSuggestResults = locatorTask.suggestAsync(fromPoint, suggestParams).get();
                            List<GeocodeResult> fromSuggestGeocodedResult = locatorTask.geocodeAsync(fromSuggestResults.get(0)).get();
                            geocodedPoints.add(new Point(fromSuggestGeocodedResult.get(0).getRouteLocation().getX(),
                                    fromSuggestGeocodedResult.get(0).getRouteLocation().getY(),
                                    SpatialReference.create(4326)));

                            //do likewise for to location
                            List<SuggestResult> toSuggestResults = locatorTask.suggestAsync(toPoint, suggestParams).get();
                            List<GeocodeResult> toSuggestGeocodedResult = locatorTask.geocodeAsync(toSuggestResults.get(0)).get();
                            geocodedPoints.add(new Point(toSuggestGeocodedResult.get(0).getRouteLocation().getX(),
                                    toSuggestGeocodedResult.get(0).getRouteLocation().getY(),
                                    SpatialReference.create(4326)));

                            //start the routing proper
                            final GraphicsOverlay graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
                            final RouteTask routeTask = new RouteTask(AGO_ROUTING_SERVICE);

                            routeTask.loadAsync();

                            routeTask.addDoneLoadingListener(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if(routeTask.getLoadStatus() == LoadStatus.LOADED){
                                            final RouteParameters routeParams = routeTask.generateDefaultParametersAsync().get();
                                            routeParams.setReturnDirections(true);
                                            List<Stop> routeStops = routeParams.getStops();
                                            if(geocodedPoints.size() != 0){
                                                routeStops.add(new Stop(geocodedPoints.get(0)));
                                                routeStops.add(new Stop(geocodedPoints.get(1)));
                                            }

                                            RouteResult routeResult = routeTask.solveAsync(routeParams).get();
                                            route = routeResult.getRoutes().get(0);
                                            final Polyline routeLines = route.getRouteGeometry();

                                            //construct route lines and add to graphicsoverlay
                                            final Graphic routeGraphic = new Graphic(routeLines);
                                            Symbol routeSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 4);
                                            routeGraphic.setSymbol(routeSymbol);
                                            graphicsOverlay.getGraphics().add(routeGraphic);


                                            //construct origin and destination and add to layer
                                            Uri uri =Uri.parse("android.resource://test.support.esri.com/"+R.drawable.geolocation);
                                            PictureMarkerSymbol originMarkerSymbol = new PictureMarkerSymbol(
                                                 new BitmapDrawable(getResources(), BitmapFactory.decodeFile(uri.getPath())));
                                            originMarkerSymbol.loadAsync();
                                            SimpleMarkerSymbol originMarkerSym = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND,
                                                    Color.GREEN, 15);
                                            Graphic originGraphic = new Graphic(geocodedPoints.get(0), originMarkerSym);
                                            graphicsOverlay.getGraphics().add(originGraphic);
                                            /*Point originPoint = new Point(geocodedPoints.get(0).getX(),geocodedPoints.get(0).getY(),
                                                    SpatialReference.create(4326));
                                            Graphic originGraphic = new Graphic(geocodedPoints.get(0), originMarkerSymbol);
                                            graphicsOverlay.getGraphics().add(originGraphic);

                                            Point destinationPoint = new Point(geocodedPoints.get(1).getX(),geocodedPoints.get(1).getY(),
                                                    SpatialReference.create(4326));
                                            PictureMarkerSymbol destinationMarkerSymbol = new PictureMarkerSymbol(new BitmapDrawable(getResources(), BitmapFactory.decodeFile("/res/drawable/geolocation.xml")));
                                            destinationMarkerSymbol.loadAsync();*/

                                            SimpleMarkerSymbol destinationMarkerSym = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE,
                                                    Color.RED, 20);
                                            Graphic destinationGraphic = new Graphic(geocodedPoints.get(1), destinationMarkerSym);
                                            graphicsOverlay.getGraphics().add(destinationGraphic);
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MapView routeMapView = (MapView)getActivity().findViewById(R.id.nav_map_view);
                                                    routeMapView.getGraphicsOverlays().add(graphicsOverlay);
                                                   /* getActivity().getSupportFragmentManager().beginTransaction().hide(
                                                            getActivity().getSupportFragmentManager().findFragmentByTag("RoutingFrag")
                                                    ).commit()*/;
                                                    /*getActivity().getSupportFragmentManager().findFragmentByTag("RoutingFrag")
                                                            .getView().findViewById(R.id.from_to_layout).setVisibility(View.GONE);*/
                                                    routeFragmentView.findViewById(R.id.from_to_layout).setVisibility(View.GONE);

                                                    routeMapView.setViewpointGeometryWithPaddingAsync(routeLines, 250);
                                                    /*routingProgressBar.setIndeterminate(false);
                                                    routingProgressBar.setVisibility(View.INVISIBLE);*/
                                                    progressDialog.dismiss();
                                                    RecyclerView routeRecycler = (RecyclerView) routeFragmentView.findViewById(R.id.route_recycler_view);
                                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                                    routeRecycler.setLayoutManager(linearLayoutManager);
                                                    final ArrayList<DirectionManeuver> directionManeuvers = new ArrayList<>(route.getDirectionManeuvers());

                                                    RoutingManouversData routingManouversData = new RoutingManouversData(directionManeuvers, route);
                                                    RoutingManouvAdapter routeInfoAdapter = new RoutingManouvAdapter(routingManouversData);
                                                    routeRecycler.setAdapter(routeInfoAdapter);
                                                    routeFragmentView.findViewById(R.id.route_information).setVisibility(View.VISIBLE);
                                                    routeFragmentView.findViewById(R.id.route_results_layout).setVisibility(View.VISIBLE);
                                                    TextView arrivalTime = (TextView) routeFragmentView.findViewById(R.id.arrival_time);

                                                    arrivalTime.setText("Arrive: "+route.getLocalEndTime().HOUR +":"+
                                                    route.getLocalEndTime().MINUTE + "\n "
                                                    +"Duration: "+convertMinutesToHoursMins(route.getTotalTime()));
                                                    TextView totalDistance = (TextView)routeFragmentView.findViewById(R.id.time_of_travel);

                                                    totalDistance.setText("Distance: "+convertMetersToMiles(route.getTotalLength())+ " mi");

                                                    route_drawer_layout.openDrawer(GravityCompat.END);
                                                    directionsFloatingButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
/*
                                                            getActivity().getSupportFragmentManager().beginTransaction().hide(
                                                                   getActivity().getSupportFragmentManager().findFragmentByTag("RoutingFrag")
                                                            ).commit();*/
                                                            route_drawer_layout.closeDrawer(GravityCompat.END);
                                                            routeFragmentView.findViewById(R.id.route_results_layout).setVisibility(View.VISIBLE);
                                                            if(textToSpeech != null) {
                                                                for (int i = 0; i < directionManeuvers.size(); i++) {

                                                                    textToSpeech.speak(directionManeuvers.get(i).getDirectionText(), TextToSpeech.QUEUE_ADD,
                                                                            null, "stringUtterID");
                                                                }
                                                            }
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    }catch(ExecutionException|InterruptedException inex){
                                        Log.d("KwasiRouteException ", inex.getMessage());
                                    }
                                }
                            });

                        }catch(ExecutionException|InterruptedException exIn){

                        }
                    }
                }
            });

            Log.d("KwasiRouteTest", locatorTask.getLoadStatus().toString());
            return geocodedPoints;
        }



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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
