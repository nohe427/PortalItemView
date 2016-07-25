package test.support.esri.com.portalitemview;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.security.CredentialChangedEvent;
import com.esri.arcgisruntime.security.CredentialChangedListener;
import com.esri.arcgisruntime.security.UserCredential;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class PortalViewMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LogInFragment.OnFragmentInteractionListener, PortalViewFragment.OnFragmentInteractionListener,
        BasemapFragment.OnFragmentInteractionListener, ControlsFragment.OnFragmentInteractionListener, ShowLayersFragment.OnFragmentInteractionListener,
        RoutingFragment.OnFragmentInteractionListener, AnonymousPortalFragment.OnFragmentInteractionListener {

    private static MapView navMapView;
    public ArcGISMap navigationMap;
    private NavigationView navigationView;
    public static Portal portal;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private String menuTitle;
    public static Menu globalMenu;
    private android.graphics.Point whereClicked;
    private Callout callout;
    private SearchView searchView;
    private RelativeLayout relativeLayout;
    public static MapViewSingleClick singleClick;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navMapView = (MapView) findViewById(R.id.nav_map_view);
        navigationMap = new ArcGISMap(Basemap.createLightGrayCanvas());
        navMapView.setMap(navigationMap);
        navMapView.setMagnifierEnabled(true);
        navMapView.setCanMagnifierPanMap(true);


        //implement logic for callout
        callout = navMapView.getCallout();
         relativeLayout = (RelativeLayout) LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.callout_layout, null

        );

        callout.setContent(relativeLayout);
        Callout.ShowOptions showOptions = new Callout.ShowOptions();
        showOptions.setAnimateCallout(true);
        showOptions.setRecenterMap(true);
        Callout.Style style = new Callout.Style(getApplicationContext());
        style.setBorderWidth(0);
        style.setCornerRadius(2);
        style.setMinWidth(200);
        style.setLeaderPosition(Callout.Style.LeaderPosition.LOWER_MIDDLE);

        callout.setShowOptions(showOptions);
        callout.setStyle(style);

        singleClick = new MapViewSingleClick(getApplicationContext(), navMapView);
        navMapView.setOnTouchListener(singleClick);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        for (int i = 1; i < navigationView.getMenu().size(); i++) {
            menuTitle = navigationView.getMenu().getItem(i).getTitle().toString();

            if (menuTitle.equalsIgnoreCase("ArcGIS Community")) {
                portal = new Portal("https://www.arcgis.com");

                if (portal.getLoadStatus() != LoadStatus.LOADED) {
                    for (int c = 0; c < navigationView.getMenu().getItem(i).getSubMenu().size(); c++) {
                        navigationView.getMenu().getItem(i).getSubMenu().getItem(c).setEnabled(false);
                    }
                }
            }

        }
        navigationView.setNavigationItemSelectedListener(this);

        //Location display implementation
        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.location_floater);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    LocationDisplay locationDisplay = navMapView.getLocationDisplay();
                    locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
                    locationDisplay.startAsync();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                        LocationDisplay locationDisplay = navMapView.getLocationDisplay();
                        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
                        locationDisplay.startAsync();
                    }
                }
            }
        });


        //implement event handler for map content changes
        navMapView.addDrawStatusChangedListener(new DrawStatusChangedListener() {
            @Override
            public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
                if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
                    if (navigationMap.getItem() != null) {
                        PortalViewMain.this.setTitle(navigationMap.getItem().getTitle());
                    }
                }
            }
        });

        if (getIntent().getStringExtra("AnonymousAccess") != null) {
            if (getIntent().getStringExtra("AnonymousAccess").equalsIgnoreCase("AnonAccess")) {
                getSupportFragmentManager().beginTransaction().add(R.id.nav_map_view, new AnonymousPortalFragment(),
                        "recycler_view_fragment").commit();
            }
        } else {
            performArcGISOnlineQuery();
        }



    }


  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                double[] pointArray = data.getDoubleArrayExtra("point");
                SimpleMarkerSymbol geocodeMarkerSym = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS,
                        Color.RED, 16);
                int spInt = new Double(pointArray[3]).intValue();
                Point point = new Point(pointArray[0], pointArray[1],
                        SpatialReference.create(spInt));
                Graphic geocodedPointGraphic = new Graphic(point, geocodeMarkerSym);
                GraphicsOverlay graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
                graphicsOverlay.getGraphics().add(geocodedPointGraphic);
                navMapView.getGraphicsOverlays().add(graphicsOverlay);
                navMapView.setViewpointCenterWithScaleAsync(point, 100);
            }
        }
    }*/

    //public static method to return map
    public static ArcGISMap getCurrentMap() {
        return navMapView.getMap();
    }


    //inner class for extending single tap
    public class MapViewSingleClick extends DefaultMapViewOnTouchListener {

        public MapViewSingleClick(Context context, MapView mapView) {
            super(context, mapView);
        }
        DecimalFormat deciformatter = new DecimalFormat(".##");
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            whereClicked = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
            Point calloutPoint = navMapView.screenToLocation(whereClicked);
            TextView locationIndicatorValues = (TextView)relativeLayout.findViewById(R.id.location_callout);

            locationIndicatorValues.setText("Lat: "+ deciformatter.format(calloutPoint.getX())+ "" +
                    ", Long: "+ deciformatter.format(calloutPoint.getY()));
            callout.setLocation(calloutPoint);
            callout.show();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            callout.dismiss();
            return true;
        }
    }





    public void performArcGISOnlineQuery() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        PortalViewFragment portalViewFragment = new PortalViewFragment();
        Bundle argBundle = new Bundle();
        argBundle.putString("USERNAME", getIntent().getStringExtra("USERNAME"));
        argBundle.putString("PASSWORD", getIntent().getStringExtra("PASSWORD"));
        portalViewFragment.setArguments(argBundle);
        fragmentTransaction.add(R.id.nav_map_view, portalViewFragment, "recycler_view_fragment")
                .addToBackStack("recycler_transaction")
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (menuTitle.equalsIgnoreCase("Log out")) {
            for (int n = 0; n < navigationView.getMenu().getItem(n).getSubMenu().size(); n++) {
                navigationView.getMenu().getItem(n).getSubMenu().getItem(n).setEnabled(true);
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        globalMenu = menu;
        getMenuInflater().inflate(R.menu.navigator, globalMenu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.searchable).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        //check for the visibility on the basemap floating button and perform logic

        return true;
    }


    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.portal_button) {
            if (item.getTitle().toString().equalsIgnoreCase("Show Action Buttons")) {
                ControlsFragment controlsFragment = new ControlsFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.nav_map_view, controlsFragment, "ControlsFrag").commit();

                globalMenu.findItem(R.id.portal_button).setTitle("Hide Action Buttons");
            } else if (item.getTitle().toString().equalsIgnoreCase("Hide Action Buttons")) {
                getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("ControlsFrag")).commit();
                globalMenu.findItem(R.id.portal_button).setTitle("Show Action Buttons");
            }

        } else if (id == R.id.base_map_changer) {
            if (globalMenu.findItem(R.id.base_map_changer).getTitle().toString().equalsIgnoreCase("Change Basemap")) {
                getSupportFragmentManager().beginTransaction().add(R.id.nav_map_view, new BasemapFragment(), "BasemapFrag").commit();
                globalMenu.findItem(R.id.base_map_changer).setTitle("Hide Basemap Selector");
            } else if (globalMenu.findItem(R.id.base_map_changer).getTitle().toString().equalsIgnoreCase("Hide Basemap Selector")) {
                getSupportFragmentManager().beginTransaction().remove(
                        getSupportFragmentManager().findFragmentByTag("BasemapFrag")
                ).commit();
                globalMenu.findItem(R.id.base_map_changer).setTitle("Change Basemap");
            }

        } else if (id == R.id.show_layers_option) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("Show_Layers_Frag");
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
            LayerList layerList = navigationMap.getOperationalLayers();
            String layerName;
            ArrayList<String> listOfLayerNames = new ArrayList<>();
            for (int i = 0; i < layerList.size(); i++) {
                layerName = layerList.get(i).getName();
                listOfLayerNames.add(i, layerName);
            }
            Bundle showFragBundle = new Bundle();
            showFragBundle.putStringArrayList("layersArrayList", listOfLayerNames);
            ShowLayersFragment showLayersFragment = new ShowLayersFragment();
            showLayersFragment.setArguments(showFragBundle);
            getSupportFragmentManager().beginTransaction().add(R.id.nav_map_view, showLayersFragment, "Show_Layers_Frag")
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            // Handle the log in action
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (item.getTitle().toString().equalsIgnoreCase("Log in to Portal")) {
                for(int i=0; i < fragments.size(); i++){
                    getSupportFragmentManager().beginTransaction().remove(fragments.get(i)).commit();
                }
                finish();
               startActivity(new Intent(getApplicationContext(), Launcher.class));

            }

            //implement logic to log out of portal
            if (item.getTitle().toString().equalsIgnoreCase("Log out")) {
                int i =0;
                while(i < fragments.size()){
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragments.get(i)).commit();
                    i++;
                }

                final Intent logoutIntent = new Intent(getApplicationContext(), Launcher.class);
                portal.setCredential(new UserCredential("null", "null"));
                portal.loadAsync();
                portal.addCredentialChangedListener(new CredentialChangedListener() {
                    @Override
                    public void credentialChanged(CredentialChangedEvent credentialChangedEvent) {
                            showMessage("Logging out of portal.");
                    }
                });
                portal.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(logoutIntent);
                    }
                });

            }


        } else if (id == R.id.nav_3d) {

        } else if (id == R.id.nav_address) {
            searchView.setIconified(false);
            globalMenu.findItem(R.id.searchable).expandActionView();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_routing) {
            enterNavigationMode();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void enterNavigationMode() {
        LocationDisplay locationDisplay = navMapView.getLocationDisplay();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
        locationDisplay.startAsync();

        getSupportFragmentManager().beginTransaction().add(R.id.nav_map_view, new RoutingFragment(), "RoutingFrag").commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
