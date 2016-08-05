package test.support.esri.com.portalitemview;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.AndroidLocationDataSource;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DrawerLayout routeFragmentView;
    private FloatingActionButton floatingRouteButton;
//    private final String AGO_ROUTING_SERVICE = "http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Network/USA/NAServer/Route";
    private final String AGO_ROUTING_SERVICE= "http://csc-kasante7l3.esri.com:6080/arcgis/rest/services/Routing/Routing/NAServer/Route";
//    private final String AGO_ROUTING_SERVICE= "http://192.168.1.6:6080/arcgis/rest/services/Routing/Routing/NAServer/Route";
//    private final String AGO_ROUTING_SERVICE = "http://route.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World";
    private ProgressDialog progressDialog;
    private  Route route;
    private DrawerLayout route_drawer_layout;
    private FloatingActionButton directionsFloatingButton;
    private TextToSpeech textToSpeech;
    private Switch switcher;
    private GraphicsOverlay graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);

    private MapView routeMapView;
    private LocationDisplay locationDisplay;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        routeFragmentView = (DrawerLayout) inflater.inflate(R.layout.fragment_routing, container, false);
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


        //initialize the mapview and the location display
        routeMapView = (MapView)getActivity().findViewById(R.id.nav_map_view);
        locationDisplay = routeMapView.getLocationDisplay();


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



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults){
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            locationDisplay.startAsync();
            locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
        }
    }



    /**
     * Inner class for performing geocoding and routing on background thread
     *
     */


    public class RoutingFragAsyncTask extends AsyncTask<Void, Void, Void>{
        ArrayList<Point> fromGeocodedPoints;
        ArrayList<Point> toGeocodedPoints;


        @Override
        protected Void doInBackground(Void... params) {
            performNavigation();
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


        /**
         * Perform geocoding of the entered origin/destination and then
         * use the stops for routing
         * @return fromGeocodedPoints
         */
        private ArrayList<Point> performGeocoding() {
            fromGeocodedPoints = new ArrayList<>();
            toGeocodedPoints = new ArrayList<>();
            EditText fromRouteLocation = (EditText) routeFragmentView.findViewById(R.id.from_route_location);
            EditText toRouteLocation = (EditText) routeFragmentView.findViewById(R.id.to_route_location);
            final String fromPoint = fromRouteLocation.getText().toString().trim();
            final String toPoint = toRouteLocation.getText().toString().trim();
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
                                    progressDialog = ProgressDialog.show(getContext(), "Geocoding...",
                                            "Locating origin and destination points. \nPlease wait...", true);
                                }
                            });
                            SuggestParameters suggestParams = new SuggestParameters();
                            suggestParams.setCountryCode("USA");
                            //perform for from location
                            List<SuggestResult> fromSuggestResults = locatorTask.suggestAsync(fromPoint, suggestParams).get();

                            List<GeocodeResult> fromSuggestGeocodedResult;
                            //check to see if suggestion returned some values and geocode first 3 point
                            if(fromSuggestResults.size() > 0){
                             if(fromSuggestResults.size() < 3){
                                 for(int i=0; i < fromSuggestResults.size(); i++){
                                     fromSuggestGeocodedResult = locatorTask.geocodeAsync(fromSuggestResults.get(i)).get();
                                     if(fromSuggestGeocodedResult.size() > 0){
                                         fromGeocodedPoints.add(new Point(fromSuggestGeocodedResult.get(0).getRouteLocation().getX(),
                                                 fromSuggestGeocodedResult.get(0).getRouteLocation().getY(), SpatialReferences.getWgs84()
                                         ));
                                     }
                                 }
                             }
                                if(fromSuggestResults.size() > 3){
                                    for(int i=0; i < 3; i++){
                                        fromSuggestGeocodedResult = locatorTask.geocodeAsync(fromSuggestResults.get(i)).get();
                                       if(fromSuggestGeocodedResult.size() > 0){
                                           fromGeocodedPoints.add(new Point(fromSuggestGeocodedResult.get(0).getRouteLocation().getX(),
                                                   fromSuggestGeocodedResult.get(0).getRouteLocation().getY(), SpatialReferences.getWgs84()

                                           ));
                                       }
                                    }
                                }
                            }else {
                                showMessage("Unable to geocode the specified origin.");
                            }

                            //do likewise for to location
                            List<SuggestResult> toSuggestResults = locatorTask.suggestAsync(toPoint, suggestParams).get();
                            List<GeocodeResult> toSuggestGeocodedResult;
                            if(toSuggestResults.size() > 0){
                                if(toSuggestResults.size() < 3){
                                    for(int i=0; i < toSuggestResults.size(); i++){
                                        toSuggestGeocodedResult = locatorTask.geocodeAsync(toSuggestResults.get(i)).get();
                                        if(toSuggestGeocodedResult.size() >0){
                                            toGeocodedPoints.add(new Point(toSuggestGeocodedResult.get(0).getRouteLocation().getX(),
                                                    toSuggestGeocodedResult.get(0).getRouteLocation().getY(),
                                                    SpatialReferences.getWgs84()));
                                        }
                                    }
                                }

                                if(toSuggestResults.size() > 3){
                                    for(int i=0; i < 3; i++){
                                        toSuggestGeocodedResult = locatorTask.geocodeAsync(toSuggestResults.get(i)).get();
                                       if(toSuggestGeocodedResult.size() >0){
                                           toGeocodedPoints.add(new Point(toSuggestGeocodedResult.get(0).getRouteLocation().getX(),
                                                   toSuggestGeocodedResult.get(0).getRouteLocation().getY(),
                                                   SpatialReferences.getWgs84()));
                                       }
                                    }
                                }
                                progressDialog.dismiss();
                                performRouting(fromGeocodedPoints, toGeocodedPoints);
                            }else{
                                showMessage("Unable to geocode the specified destination.");
                            }
                        }catch(ExecutionException|InterruptedException exIn){

                        }
                    }
                }
            });
            return fromGeocodedPoints;
        }

        /**
         * Perform the routing process by using results from performGeocoding as the stops entered by the user.
         * @param fromArrayList
         */

        private synchronized void performRouting(final ArrayList<Point> fromArrayList, final ArrayList<Point> toArrayList) {
                    //begin routing
                    final RouteTask routeTask = new RouteTask(AGO_ROUTING_SERVICE);
                    routeTask.loadAsync();
                    routeTask.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(routeTask.getLoadStatus() == LoadStatus.LOADED){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog = ProgressDialog.show(getContext(), "Routing...",
                                                    "Starting routing calculation. \nPlease wait...", true);
                                        }
                                    });
                                    RouteParameters routeParams = routeTask.generateDefaultParametersAsync().get();
                                    routeParams.setLocalStartTime(Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles")));
                                    routeParams.setReturnDirections(true);
                                    routeParams.setOutputSpatialReference(SpatialReferences.getWgs84());
                                    routeParams.setReturnStops(true);
                                    List<Stop> routeStops = routeParams.getStops();
                                    if(fromArrayList.size() != 0 && toArrayList.size() !=0){
                                        routeStops.add(new Stop(fromArrayList.get(0)));
                                        routeStops.add(new Stop(toArrayList.get(0)));
                                    }else{
                                        showMessage("The entered stops could not be goecoded.");
                                        return;
                                    }

                                    //solve and retrieve the first route
                                    ListenableFuture<RouteResult> listenableRouteResult = routeTask.solveAsync(routeParams);
                                    RouteResult routeResult = listenableRouteResult.get();
                                    route = routeResult.getRoutes().get(0);
                                    final Polyline routeLines = route.getRouteGeometry();


                                    //construct route line and add to graphicsoverlay
                                    Graphic routeGraphic = new Graphic(routeLines);
                                    Symbol routeSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 4);
                                    routeGraphic.setSymbol(routeSymbol);



                                    //construct origin and destination and add to layer
                                    PictureMarkerSymbol originMarkerSymbol = new PictureMarkerSymbol("http://static.arcgis.com/images/Symbols/Shapes/GreenPin2LargeB.png");
                                    originMarkerSymbol.setWidth(30);
                                    originMarkerSymbol.setHeight(35);
                                    originMarkerSymbol.loadAsync();
                                    Graphic originGraphic = new Graphic(fromGeocodedPoints.get(0), originMarkerSymbol);


                                    PictureMarkerSymbol destinationMarkerSym = new PictureMarkerSymbol("http://static.arcgis.com/images/Symbols/Basic/CheckeredFlag.png");
                                    destinationMarkerSym.setHeight(30);
                                    destinationMarkerSym.setWidth(20);
                                    destinationMarkerSym.loadAsync();
                                    Graphic destinationGraphic = new Graphic(toGeocodedPoints.get(0), destinationMarkerSym);


                                    //check for the presence of older graphics, clear them and add new ones
                                    if(graphicsOverlay.getGraphics() != null){
                                        graphicsOverlay.getGraphics().clear();
                                    }
                                    graphicsOverlay.getGraphics().add(routeGraphic);
                                    graphicsOverlay.getGraphics().add(destinationGraphic);
                                    graphicsOverlay.getGraphics().add(originGraphic);


                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
//                                            routeMapView = (MapView)getActivity().findViewById(R.id.nav_map_view);
                                            //clear the map
                                            if(routeMapView.getGraphicsOverlays().contains(graphicsOverlay)){
                                                routeMapView.getGraphicsOverlays().remove(graphicsOverlay);
                                            }
                                            routeMapView.getGraphicsOverlays().add(graphicsOverlay);
//                                            routeFragmentView.findViewById(R.id.from_to_layout).setVisibility(View.GONE);

                                            routeMapView.setViewpointGeometryWithPaddingAsync(routeLines, 150);
                                            progressDialog.dismiss();
                                            RecyclerView routeRecycler = (RecyclerView) routeFragmentView.findViewById(R.id.route_recycler_view);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                            routeRecycler.setLayoutManager(linearLayoutManager);
                                            final ArrayList<DirectionManeuver> directionManeuvers = new ArrayList<>(route.getDirectionManeuvers());

                                            RoutingManouversData routingManouversData = new RoutingManouversData(directionManeuvers, route);
                                            RoutingManouvAdapter routeInfoAdapter = new RoutingManouvAdapter(routingManouversData);
                                            routeRecycler.setAdapter(routeInfoAdapter);
                                            TextView arrivalTime = (TextView) routeFragmentView.findViewById(R.id.arrival_time);

                                           /* arrivalTime.setText(" Arrive: "+route.getLocalEndTime().get(Calendar.HOUR)+":"+
                                                    route.getLocalEndTime().get(Calendar.MINUTE) + "\n "
                                                    +"Duration: "+convertMinutesToHoursMins(route.getTotalTime()));*/
                                            int lastStopIndex = route.getStops().size() -1;
                                            arrivalTime.setText(" Arrive: "+route.getStops().get(lastStopIndex).getLocalArrivalTime().get(Calendar.HOUR)+":"+
                                                    route.getStops().get(lastStopIndex).getLocalArrivalTime().get(Calendar.MINUTE) + "\n "
                                                    +"Duration: "+convertMinutesToHoursMins(route.getTotalTime()));
                                            TextView totalDistance = (TextView)routeFragmentView.findViewById(R.id.time_of_travel);
                                            totalDistance.setText("Distance: "+convertMetersToMiles(route.getTotalLength())+ " mi");
                                            route_drawer_layout.openDrawer(GravityCompat.END);
                                            route_drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
                                                @Override
                                                public void onDrawerSlide(View drawerView, float slideOffset) {
                                                //route_drawer_layout.setVisibility(View.VISIBLE);
                                                }

                                                @Override
                                                public void onDrawerOpened(View drawerView) {

                                                }

                                                @Override
                                                public void onDrawerClosed(View drawerView) {
                                                    routeFragmentView.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onDrawerStateChanged(int newState) {
                                                    if(route_drawer_layout.getVisibility() == View.INVISIBLE){

                                                        route_drawer_layout.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });

                                            directionsFloatingButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    route_drawer_layout.closeDrawer(GravityCompat.END);
                                                    beginNavigation();
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
                                }else if(routeTask.getLoadStatus() == LoadStatus.FAILED_TO_LOAD ||
                                        routeTask.getLoadStatus()==LoadStatus.NOT_LOADED||routeTask.getLoadStatus() == LoadStatus.LOADING){
                                    showMessage("Routing service status is "+routeTask.getLoadStatus().toString()+ "\nPlease wait...");
                                    routeTask.retryLoadAsync();
                                }
                            }catch(ExecutionException|InterruptedException inex){
                                Log.d("KwasiRouteException ", inex.getMessage());
                            }

                        }
                    });


        }


        public void performNavigation() {
            performGeocoding();
        }

        void beginNavigation(){
            //obtain location display manager
           if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                   PackageManager.PERMISSION_GRANTED){
               locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
               locationDisplay.startAsync();
           }else {
               requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
           }
            //zoom to the location displayed on the map
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setBearingRequired(true);
            criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
            criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
            criteria.setSpeedRequired(true);
            AndroidLocationDataSource androidLocationDataSource =
                    new AndroidLocationDataSource(getContext(), criteria, 2000, 1);
            androidLocationDataSource.startAsync();
            androidLocationDataSource.addStartedListener(new Runnable() {
                @Override
                public void run() {
                    showMessage("Beginning navigation...");
                }
            });
            locationDisplay.setLocationDataSource(androidLocationDataSource);
            locationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
                @Override
                public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                    Point locationPoint = locationChangedEvent.getLocation().getPosition();
                    routeMapView.setViewpointCenterWithScaleAsync(locationPoint, 10000);
                    //set the route indicators
                    DecimalFormat decimalFormat = new DecimalFormat("0.##");
                    ((TextView)getActivity().findViewById(R.id.mph)).setText(String.valueOf(decimalFormat.format(locationChangedEvent.getLocation().getVelocity()))+ "\nm/s");
                    if(route != null){
                        int counter = 0;
                        //check to make sure the location is the same at the maneuver point
                        //and change the arrival time
                        if(route.getDirectionManeuvers().get(counter).getDirectionEvents().size() >0) {
                            ((TextView) getActivity().findViewById(R.id.arrival)).setText(
                                    route.getDirectionManeuvers().get(counter).getDirectionEvents().get(0).getEstimatedLocalArrivalTime().get(Calendar.HOUR) + " : " +
                                            route.getDirectionManeuvers().get(counter).getDirectionEvents().get(0).getEstimatedLocalArrivalTime().get(Calendar.MINUTE) + "\narrival");
                            if (!route.getDirectionManeuvers().get(counter).getDirectionEvents().get(0).getGeometry().equals(locationPoint)) {
                                counter = counter;
                            } else {
                                counter++;
                            }
                        }else {
                            int lastStopIndex = route.getStops().size() -1;

                            ((TextView) getActivity().findViewById(R.id.arrival)).setText(route.getStops().get(lastStopIndex).getLocalArrivalTime().get(Calendar.HOUR)+":"+
                                    route.getStops().get(lastStopIndex).getLocalArrivalTime().get(Calendar.MINUTE) + "\n ETA");
                        }
                        ((TextView)getActivity().findViewById(R.id.travel_time)).setText(String.valueOf(convertMinutesToHoursMins(route.getTravelTime())) +"\n travel time");
                        getActivity().findViewById(R.id.location_floater).setVisibility(View.INVISIBLE);
                    }

                    ((TextView)getActivity().findViewById(R.id.distance_dir)).setText(String.valueOf(locationChangedEvent.getLocation().getCourse()));
                    ((TextView)getActivity().findViewById(R.id.upcoming_turn)).setText("Heading: "+String.valueOf(locationChangedEvent.getSource().getHeading()));

                }
            });

            //show the directions and routes indicator on the main activity
            getActivity().findViewById(R.id.routes_indicator).setVisibility(View.VISIBLE);


            getActivity().findViewById(R.id.directions_indicator).setVisibility(View.VISIBLE);
        }


    }



    //extend LocationDatasource to implement onStart method and onStop methods

    class PortalViewLocationDatasource extends LocationDataSource {

        public PortalViewLocationDatasource(){
            super();
        }

        @Override
        protected void onStart() {

        }

        @Override
        protected void onStop() {

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
