package test.support.esri.com.portalitemview;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.security.UserCredential;

import java.util.ArrayList;


public class PortalViewMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LogInFragment.OnFragmentInteractionListener, PortalViewFragment.OnFragmentInteractionListener,
BasemapFragment.OnFragmentInteractionListener, ControlsFragment.OnFragmentInteractionListener, ShowLayersFragment.OnFragmentInteractionListener{
    private MapView navMapView;
    public static ArcGISMap navigationMap;
    private NavigationView navigationView;
    private Portal portal;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private  String menuTitle;
    public static Menu globalMenu;
    private android.graphics.Point whereClicked;
    private Callout callout;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navMapView = (MapView)findViewById(R.id.nav_map_view);
        navigationMap = new ArcGISMap(Basemap.createLightGrayCanvas());
        navMapView.setMap(navigationMap);
        navMapView.setMagnifierEnabled(true);
        navMapView.setCanMagnifierPanMap(true);


        //implement logic for callout
         callout = navMapView.getCallout();
        TextView textView = new TextView(getApplicationContext());
        textView.setText("Testing callout");
        callout.setContent(textView);
        Callout.ShowOptions showOptions = new Callout.ShowOptions();
        showOptions.setAnimateCallout(true);
        showOptions.setRecenterMap(true);
        Callout.Style style = new Callout.Style(getApplicationContext());
        style.setBackgroundColor(Color.DKGRAY);
        callout.setShowOptions(showOptions);
        callout.setStyle(style);

        MapViewSingleClick singleClick = new MapViewSingleClick(getApplicationContext(), navMapView);
        navMapView.setOnTouchListener(singleClick);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        for(int i=1; i < navigationView.getMenu().size(); i++){
            menuTitle = navigationView.getMenu().getItem(i).getTitle().toString();
            if(!menuTitle.equalsIgnoreCase("3D Maps")){
               navigationView.getMenu().getItem(i).setEnabled(false);
            }
           if(menuTitle.equalsIgnoreCase("ArcGIS Community")){
               portal = new Portal("https://www.arcgis.com");

               if(portal.getLoadStatus() != LoadStatus.LOADED){
               for(int c=0; c < navigationView.getMenu().getItem(i).getSubMenu().size(); c++) {
                       navigationView.getMenu().getItem(i).getSubMenu().getItem(c).setEnabled(false);
                   }
                       }
        }

        }
        navigationView.setNavigationItemSelectedListener(this);

        //Location display implementation
        FloatingActionButton actionButton = (FloatingActionButton)findViewById(R.id.location_floater);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
        LocationDisplay locationDisplay = navMapView.getLocationDisplay();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
        locationDisplay.startAsync();
        }else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
            }
        });
            performArcGISOnlineQuery();
    }



    //inner class for extending single tap
    private class MapViewSingleClick extends DefaultMapViewOnTouchListener{

        public MapViewSingleClick(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e){
            whereClicked = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
            Point calloutPoint = navMapView.screenToLocation(whereClicked);
            callout.setLocation(calloutPoint);
            callout.show();
            return true;
        }
    }

    public void performArcGISOnlineQuery(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        PortalViewFragment portalViewFragment = new PortalViewFragment();
        Bundle argBundle = new Bundle();
        argBundle.putString("USERNAME",getIntent().getStringExtra("USERNAME"));
        argBundle.putString("PASSWORD", getIntent().getStringExtra("PASSWORD"));
        portalViewFragment.setArguments(argBundle);
        fragmentTransaction.add(R.id.nav_map_view, portalViewFragment, "recycler_view_fragment")
                .addToBackStack("recycler_transaction")
                .commit();

        //if the activity is started by the geocode activity
        double[] doubleArray = getIntent().getDoubleArrayExtra("point");
        if(doubleArray !=null){
            Point point = new Point(doubleArray[0], doubleArray[1],
                    SpatialReference.create(new Double(doubleArray[2]).intValue()));
            navMapView.setViewpointCenterWithScaleAsync(point, 100);
        }
    }

    @Override
    protected void onResume(){
            super.onResume();
       /* Intent portalIntent = getIntent();
        USERNAME = portalIntent.getStringExtra("username");
        PASSWORD = portalIntent.getStringExtra("password");
        if(USERNAME == null && PASSWORD==null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "No user credentials provided", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }else{

        }*/

        if(menuTitle.equalsIgnoreCase("Log out")){
            for(int n=0; n < navigationView.getMenu().getItem(n).getSubMenu().size(); n++){
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
        //super.onCreateOptionsMenu(menu);
        globalMenu = menu;
        getMenuInflater().inflate(R.menu.navigator, globalMenu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.searchable).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setSubmitButtonEnabled(true);
        //check for the visibility on the basemap floating button and perform logic

        return true;
    }


    private void showMessage(String message){
        Toast.makeText(getApplicationContext(),message , Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.portal_button) {
          if(item.getTitle().toString().equalsIgnoreCase("Show Action Buttons")){
              ControlsFragment controlsFragment = new ControlsFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.nav_map_view, controlsFragment , "ControlsFrag").commit();

                globalMenu.findItem(R.id.portal_button).setTitle("Hide Action Buttons");
            }
           else if(item.getTitle().toString().equalsIgnoreCase("Hide Action Buttons")){
              getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("ControlsFrag")).commit();
                globalMenu.findItem(R.id.portal_button).setTitle("Show Action Buttons");
            }

        }else if(id == R.id.base_map_changer){
            if(globalMenu.findItem(R.id.base_map_changer).getTitle().toString().equalsIgnoreCase("Change Basemap")){
                getSupportFragmentManager().beginTransaction().add(R.id.nav_map_view, new BasemapFragment(), "BasemapFrag").commit();
                globalMenu.findItem(R.id.base_map_changer).setTitle("Hide Basemap Selector");
            }else if(globalMenu.findItem(R.id.base_map_changer).getTitle().toString().equalsIgnoreCase("Hide Basemap Selector")){
                getSupportFragmentManager().beginTransaction().remove(
                        getSupportFragmentManager().findFragmentByTag("BasemapFrag")
                ).commit();
                globalMenu.findItem(R.id.base_map_changer).setTitle("Change Basemap");
            }

        }else if(id == R.id.show_layers_option) {
           // try {
                LayerList layerList = navigationMap.getOperationalLayers();
                String layerName;
                ArrayList<String> listOfLayerNames = new ArrayList<>();
                ShowLayersAdapter showLayersAdapter = new ShowLayersAdapter(listOfLayerNames);
                for (int i = 0; i < layerList.size(); i++) {
                    layerName = layerList.get(i).getName();
                    listOfLayerNames.add(layerName);
                    Log.d("LayerShowDebug", Integer.toString(showLayersAdapter.getItemCount()));
                    /*if (layer instanceof FeatureLayer) {
                        featureLayer = (FeatureLayer) layer;
                        listOfLegends = featureLayer.fetchLegendInfosAsync().get();
                        showLayersAdapter = new ShowLayersAdapter(listOfLegends);
                    }*/
                }
            Bundle showFragBundle = new Bundle();
            showFragBundle.putStringArrayList("layersArrayList", listOfLayerNames);
            ShowLayersFragment showLayersFragment = new ShowLayersFragment();
            showLayersFragment.setArguments(showFragBundle);
            getSupportFragmentManager().beginTransaction().add(R.id.nav_map_view, showLayersFragment, "Show_Layers_Frag")
                    .commit();

          //  }catch(InterruptedException|ExecutionException ieException){

           // }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id==R.id.nav_login) {
            // Handle the log in action
            if(item.getTitle().toString().equalsIgnoreCase("Log in to Portal")){

                if(findViewById(R.id.nav_map_view) != null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_map_view, new LogInFragment(), "LOG_IN_FRAGMENT");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            }

            //implement logic to log out of portal
            if(item.getTitle().toString().equalsIgnoreCase("Log out")){
                portal.setCredential(new UserCredential("SDFADDF", "sdadfdsa"));
                portal.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        if(portal.getLoadStatus() != LoadStatus.LOADED){
                            Toast.makeText(getApplicationContext(), "Logged out of Portal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                portal.loadAsync();
            }


        } else if (id == R.id.nav_3d) {

        } else if (id == R.id.nav_address) {

        } else if (id== R.id.nav_share) {

        } else if (id==R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }




}
