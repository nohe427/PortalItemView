package test.support.esri.com.portalitemview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;

public class GeocodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocode);

        final MapView geocodeMapView = (MapView)findViewById(R.id.geocode_map_view);
        geocodeMapView.setMap(new ArcGISMap(Basemap.createLightGrayCanvas()));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PortalViewMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        double[] doubleArray = getIntent().getDoubleArrayExtra("point");
        if (doubleArray != null) {
            final Point point = new Point(doubleArray[0], doubleArray[1],
                    SpatialReference.create(new Double(doubleArray[2]).intValue()));
           // BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.geolocation);
//           final PictureMarkerSymbol geocodeMarkerSym = new PictureMarkerSymbol(new BitmapDrawable(getResources(),
//                    BitmapFactory.decodeResource(getResources(), R.drawable.geolocation)));

            //final PictureMarkerSymbol geocodeMarkerSym = new PictureMarkerSymbol(bitmapDrawable);
            final PictureMarkerSymbol geocodeMarkerSym = new PictureMarkerSymbol(
                    "http://static.arcgis.com/images/Symbols/Basic/BlueStickpin.png"
            );
            geocodeMarkerSym.setHeight(60);
            geocodeMarkerSym.setWidth(60);
            geocodeMarkerSym.loadAsync();
            geocodeMarkerSym.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    Graphic geocodedPointGraphic = new Graphic(point, geocodeMarkerSym);
                    GraphicsOverlay graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
                    graphicsOverlay.getGraphics().add(geocodedPointGraphic);
                    geocodeMapView.getGraphicsOverlays().add(graphicsOverlay);
                    geocodeMapView.setViewpointCenterWithScaleAsync(point, 0.5);
                }
            });

        }
    }

}
