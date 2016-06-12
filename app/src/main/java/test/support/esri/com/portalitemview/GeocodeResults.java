package test.support.esri.com.portalitemview;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GeocodeResults extends AppCompatActivity {


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
//                final EditText editText = (EditText)findViewById(R.id.test_search);
                //final ListView listView = (ListView)findViewById(R.id.list_view);
                final LocatorTask locatorTask = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
                locatorTask.loadAsync();
                final GeocodeParameters geocodeParameters = new GeocodeParameters();
                geocodeParameters.setSearchArea(new Envelope(-9555926.059, 5614076.887,-12707563.406,3054635.375,
                        SpatialReference.create(3857)));
                if(locatorTask.getLoadStatus() == LoadStatus.LOADED) {
                    try {
                        ListenableFuture<List<GeocodeResult>> geocodeResults = locatorTask.geocodeAsync(query, geocodeParameters);
                        final List<GeocodeResult> list_geocoded = geocodeResults.get();
                        for (final GeocodeResult result : list_geocoded) {
                                /*runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {*/
                                        ArrayList<String> testArray = new ArrayList<>();
                                        for(int i=0; i <list_geocoded.size(); i++){
                                            testArray.add(i, result.getLabel());
                                        }
                                        GeocodeAdapter adapter = new GeocodeAdapter(testArray);
                                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.geocode_recycler);
                                        LinearLayoutManager linearLayoutManager =
                                                new LinearLayoutManager(GeocodeResults.this);
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        recyclerView.setAdapter(adapter);
                                       /* GeocodeAdapter geocodeAdapter = new GeocodeAdapter(testArray);
                                        Log.d("KwasiD", "Number of returns: "+geocodeAdapter.getCount());
                                        listView.setAdapter(geocodeAdapter);*/
                                  /*  }
                                });*/
                        }
                    } catch (InterruptedException | ExecutionException ine) {
                            Log.d("KwasiD", ine.getMessage());
                    }

                }
            }

        }
    }
}
