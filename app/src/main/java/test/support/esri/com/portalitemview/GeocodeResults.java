package test.support.esri.com.portalitemview;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GeocodeResults extends Activity {


    private GeocodeAdapter geocodeAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GeocodeData geocodeAdapterData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocode_results);
       new GeocodePlaceAsyncTask().execute();
    }

    @Override
    protected void onNewIntent(Intent intent){
        new GeocodePlaceAsyncTask().execute();
    }



    public class GeocodePlaceAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            handleIntent(getIntent());
            return null;
        }

        private void handleIntent(Intent intent) {
            if(Intent.ACTION_SEARCH.equals(intent.getAction())){
                final String query = intent.getStringExtra(SearchManager.QUERY);
                final LocatorTask locatorTask = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
                locatorTask.loadAsync();
                final GeocodeParameters geocodeParameters = new GeocodeParameters();
               /* geocodeParameters.setSearchArea(new Envelope(-9555926.059, 5614076.887,-12707563.406,3054635.375,
                        SpatialReference.create(3857)));*/
                locatorTask.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        if(locatorTask.getLoadStatus() == LoadStatus.LOADED) {
                            try {
                                ListenableFuture<List<GeocodeResult>> geocodeResults = locatorTask.geocodeAsync(query, geocodeParameters);
                                List<GeocodeResult> list_geocoded = geocodeResults.get();

                                ArrayList<String> labelData = new ArrayList<>();
                                ArrayList<Point> pointData = new ArrayList<>();
                                int counter =0;
                                for(GeocodeResult geocodeResult : list_geocoded){
                                    labelData.add(counter, geocodeResult.getLabel());
                                    pointData.add(counter, geocodeResult.getDisplayLocation());
                                    counter++;
                                }
                                geocodeAdapterData = new GeocodeData(labelData, pointData);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        geocodeAdapter = new GeocodeAdapter(geocodeAdapterData);
                                        recyclerView = (RecyclerView)findViewById(R.id.geocode_recycler);
                                        linearLayoutManager = new LinearLayoutManager(GeocodeResults.this);
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        recyclerView.setAdapter(geocodeAdapter);
                                    }
                                });
                                Log.d("KwasiD", "Just checking");
                            } catch (InterruptedException | ExecutionException ine) {
                                Log.d("KwasiD", ine.getMessage());
                            }

                        }
                    }
                });

            }

        }
    }
}
