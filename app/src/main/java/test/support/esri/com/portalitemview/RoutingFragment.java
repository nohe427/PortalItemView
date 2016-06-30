package test.support.esri.com.portalitemview;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.esri.arcgisruntime.tasks.geocode.SuggestParameters;
import com.esri.arcgisruntime.tasks.geocode.SuggestResult;
import com.esri.arcgisruntime.tasks.route.Route;
import com.esri.arcgisruntime.tasks.route.RouteParameters;
import com.esri.arcgisruntime.tasks.route.RouteResult;
import com.esri.arcgisruntime.tasks.route.RouteTask;
import com.esri.arcgisruntime.tasks.route.Stop;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View routeFragmentView;
    private FloatingActionButton floatingRouteButton;
    private RouteTask routeTask;
    private final String AGO_ROUTING_SERVICE= "http://route.arcgis.com/arcgis/rest/services/World/Route";


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
        routeFragmentView = inflater.inflate(R.layout.fragment_routing, container, false);

        floatingRouteButton = (FloatingActionButton)routeFragmentView.findViewById(R.id.router);
        floatingRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              GraphicsOverlay graphicsOverlay =  performRouting();
                MapView routeMapView = (MapView)getActivity().findViewById(R.id.nav_map_view);
                routeMapView.getGraphicsOverlays().add(graphicsOverlay);
                getActivity().getSupportFragmentManager().beginTransaction().hide(
                        getActivity().getSupportFragmentManager().findFragmentByTag("RoutingFrag")
                ).commit();
            }

        });
        return routeFragmentView;
    }

    private GraphicsOverlay performRouting() {

        final GraphicsOverlay graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
           final List<Point> fromToPoints = performGeocode();
            routeTask = new RouteTask(AGO_ROUTING_SERVICE);
            routeTask.loadAsync();
            routeTask.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(routeTask.getLoadStatus() == LoadStatus.LOADED){
                        final RouteParameters routeParams = routeTask.generateDefaultParametersAsync().get();
                        List<Stop> routeStops = routeParams.getStops();
                        if(fromToPoints.size() != 0){
                            routeStops.add(new Stop(fromToPoints.get(0)));
                            routeStops.add(new Stop(fromToPoints.get(1)));
                        }
                            showMessage("invalid destinations");
                        RouteResult routeResult = routeTask.solveAsync(routeParams).get();
                        Route route = routeResult.getRoutes().get(0);
                        Polyline routeLines = route.getRouteGeometry();

                            Graphic routeGraphic = new Graphic(routeLines);
                            Symbol routeSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.BLUE, 4);
                            routeGraphic.setSymbol(routeSymbol);
                            graphicsOverlay.getGraphics().add(routeGraphic);

                        }
                    }catch(ExecutionException|InterruptedException inex){

                    }
                }
            });
        Log.d("KwasiRoutingTest", routeTask.getLoadStatus().toString());
        return graphicsOverlay;
    }

    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private List<Point> performGeocode() {
            final List<Point> geocodedPoints = new ArrayList<>();
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
                            SuggestParameters suggestParams = new SuggestParameters();
                            suggestParams.setCountryCode("USA");
                            //perform for from location
                            List<SuggestResult> fromSuggestResults = locatorTask.suggestAsync(fromPoint, suggestParams).get();
                            List<GeocodeResult> fromSuggestGeocodedResult = locatorTask.geocodeAsync(fromSuggestResults.get(0)).get();
                            geocodedPoints.add(fromSuggestGeocodedResult.get(0).getRouteLocation());

                            //do likewise for to location
                            List<SuggestResult> toSuggestResults = locatorTask.suggestAsync(toPoint, suggestParams).get();
                            List<GeocodeResult> toSuggestGeocodedResult = locatorTask.geocodeAsync(toSuggestResults.get(0)).get();
                            geocodedPoints.add(toSuggestGeocodedResult.get(0).getRouteLocation());

                }catch(ExecutionException|InterruptedException exIn){

                }
                        }
                }
            });

        Log.d("KwasiRouteTest", locatorTask.getLoadStatus().toString());
            return geocodedPoints;
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
