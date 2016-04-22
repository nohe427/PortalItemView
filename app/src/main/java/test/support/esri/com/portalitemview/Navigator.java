package test.support.esri.com.portalitemview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalInfo;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.portal.PortalQueryParams;
import com.esri.arcgisruntime.portal.PortalQueryResultSet;
import com.esri.arcgisruntime.security.UserCredential;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Navigator extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecyclerFragment.OnFragmentInteractionListener{
private MapView navMapView;
    private Map nap_map;
    private NavigationView navigationView;
    private static String USERNAME;
    private static String PASSWORD;
    private Portal portal;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayout;
    private ArrayList<FeatureItem> mFeatureItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
            String menuTitle;
        for(int i=1; i < navigationView.getMenu().size(); i++){
            menuTitle = navigationView.getMenu().getItem(i).getTitle().toString();
            if(!menuTitle.equalsIgnoreCase("3D Maps")){
               navigationView.getMenu().getItem(i).setEnabled(false);
            }
           if(menuTitle.equalsIgnoreCase("ArcGIS Community")){
               portal = new Portal("http://www.arcgis.com");//"http://www.arcgis.com", new UserCredential(USERNAME, PASSWORD));
               if(portal.getLoadStatus() != LoadStatus.LOADED){
               for(int c=0; c < navigationView.getMenu().getItem(i).getSubMenu().size(); c++) {
                       navigationView.getMenu().getItem(i).getSubMenu().getItem(c).setEnabled(false);
                   }
                       }
        }
        }
        navigationView.setNavigationItemSelectedListener(this);
        navMapView = (MapView)findViewById(R.id.nav_map_view);
        nap_map = new Map(Basemap.createStreets());
        navMapView.setMap(nap_map);
        LocationDisplay locationDisplay = navMapView.getLocationDisplay();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
        locationDisplay.startAsync();
    }

    @Override
    protected void onResume(){
            super.onResume();
        Intent portalIntent = getIntent();
        USERNAME = portalIntent.getStringExtra("username");
        PASSWORD = portalIntent.getStringExtra("password");
        if(USERNAME == null && PASSWORD==null){
            return;
        }else{
            new PortalViewAsyncTask().execute();
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigator, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
                Intent logInIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(logInIntent);
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


        } else if (id==R.id.nav_routing) {
            //Handle navigating activity here
            Intent portalIntent = new Intent(getApplicationContext(), Navigator.class);
            startActivity(portalIntent);
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

    public class PortalViewAsyncTask extends AsyncTask<Void, Void, Void> {
        private Exception mException;

        private PortalInfo portalInfo;
        public PortalViewAsyncTask(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            mException= null;
            portal.setCredential(new UserCredential(USERNAME, PASSWORD));
            portal.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (portal.getLoadStatus() == LoadStatus.LOADED) {

                            //handle image thumbnail display and user name setting

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(getApplicationContext(), "Portal loaded for " + portal.getPortalUser().getFullName(),
                                                Toast.LENGTH_LONG).show();
                                        TextView screen_name = (TextView) findViewById(R.id.screen_name);
                                        screen_name.setText("Welcome " + portal.getPortalUser().getFullName());
                                        ImageView screenImage = (ImageView)findViewById(R.id.screen_image);

                                        byte[] imgByte = portal.getPortalUser().fetchThumbnailAsync().get();
                                        if(imgByte != null) {
                                            screenImage.setImageBitmap(BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length));
                                        }
                                    }catch(ExecutionException |InterruptedException ex){

                                    }

                                }
                            });
                            portalInfo = portal.getPortalInfo();
                            //initialize ArrayList
                            mFeatureItem = new ArrayList<>();
                            //return if cancelled
                            if (isCancelled()) {
                                return;
                            }

                            //provide your queries
                            ListenableFuture<PortalQueryResultSet<PortalItem>> portalListItems =
                                    portal.findItemsAsync(new PortalQueryParams("owner: "+USERNAME));
                            List<PortalItem> portalItems = portalListItems.get().getResults();
                            for (PortalItem portalItem : portalItems) {
                                byte[] data = portalItem.fetchThumbnailAsync().get();
                                if (isCancelled()) {
                                    return;
                                }

                                if (data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    mFeatureItem.add(new FeatureItem(portalItem, bitmap));
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter = new MyAdapter(mFeatureItem);
                                    recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
                                    mLinearLayout = new LinearLayoutManager(getApplicationContext());
                                    if(portal.getLoadStatus() == LoadStatus.LOADED){
                                        for(int k=0; k< navigationView.getMenu().size(); k++){
                                            navigationView.getMenu().getItem(k).setEnabled(true);
                                        }
                                        for(int c=0; c < navigationView.getMenu().getItem(4).getSubMenu().size(); c++) {
                                            navigationView.getMenu().getItem(4).getSubMenu().getItem(c).setEnabled(true);
                                        }
                                        navigationView.getMenu().getItem(0).setTitle("Log out");
                                    }
                                   /* recyclerView.setLayoutManager(mLinearLayout);
                                    recyclerView.setAdapter(mAdapter);
*/
                                }
                            });
                        }
                    } catch (ExecutionException | InterruptedException exception) {
                        Log.d("Exception", exception.getMessage());
                    }
                }
            });
            portal.loadAsync();
            return null;
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected void onPostExecute(Void result){

        }
    }


}
