package test.support.esri.com.portalitemview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Map;
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

/**
 * Created by kwas7493 on 4/1/2016. AppCompatActivity implements LogInFragment.OnFragmentInteractionListener
 */
public class PortalView extends AppCompatActivity{
    private MapView mapView;
    private Map map;
    private ImageButton portalimagebutton;
    private Portal portal;
    private PortalQueryParams portalQueryParams;
    private ArrayList<FeatureItem> portalDataset;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayout;
    private static String USERNAME;
    private static String PASSWORD;
    private ArrayList<FeatureItem> mFeatureItem;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portalview);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        /*mapView = (MapView)findViewById(R.id.nav_map_view);
        map = new Map(Basemap.createLightGrayCanvas());
        mapView.setMap(map);
        Point point = new Point(-9217274.228, 4257236.677, SpatialReference.create(102100));
        mapView.setViewpointCenterAsync(point);/*//**/

        Intent portalViewIntent = getIntent();
        USERNAME= portalViewIntent.getStringExtra("username");
        PASSWORD = portalViewIntent.getStringExtra("password");
        new PortalViewAsyncTask().execute();

        /*FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        LogInFragment recyclerFragment = new LogInFragment();*/
        //fragmentTransaction.add(R.id.map_view, recyclerFragment).commit();


    }

    /*@Override
    public void onFragmentInteraction(Uri uri) {

    }*/

   public class PortalViewAsyncTask extends AsyncTask<Void, Void, Void> {
    private Exception mException;
        private Portal portal;
        private PortalInfo portalInfo;
        public PortalViewAsyncTask(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            mException= null;
            portal = new Portal("http://www.arcgis.com", new UserCredential(USERNAME, PASSWORD));
            portal.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (portal.getLoadStatus() == LoadStatus.LOADED) {
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   Toast.makeText(getApplicationContext(), "Portal loaded for: " + portal.getPortalUser().getFullName(),
                                           Toast.LENGTH_LONG).show();
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
                                    recyclerView = (RecyclerView)findViewById(R.id.card_view);
                                    mLinearLayout = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(mLinearLayout);
                                    recyclerView.setAdapter(mAdapter);
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
