package test.support.esri.com.portalitemview;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwas7493 on 6/14/2016.
 */
public class BasemapAdapter {
    private View cardView;
    private static Map<String, String> basemaps = new HashMap<>();

    public BasemapAdapter(){


    }

    public BasemapAdapter(Map<String, String> basemap){
    this.basemaps = basemap;
    }

        public Map<String, String> getBasemaps(){
            basemaps.put("delorme", "http://server.arcgisonline.com/ArcGIS/rest/services/Specialty/DeLorme_World_Base_Map/MapServer");
            basemaps.put("imagery", "http://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer");
            basemaps.put("natgeo", "http://services.arcgisonline.com/ArcGIS/rest/services/NatGeo_World_Map/MapServer");
            basemaps.put("ocean", "http://services.arcgisonline.com/ArcGIS/rest/services/Ocean/World_Ocean_Base/MapServer");
            basemaps.put("physical", "http://services.arcgisonline.com/ArcGIS/rest/services/World_Physical_Map/MapServer");
            basemaps.put("relief", "http://services.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer");
            basemaps.put("street", "http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer");
            basemaps.put("transportation", "http://services.arcgisonline.com/ArcGIS/rest/services/Reference/World_Transportation/MapServer");
            basemaps.put("ustopo", "http://services.arcgisonline.com/ArcGIS/rest/services/USA_Topo_Maps/MapServer");
            basemaps.put("world_business", "http://services.arcgisonline.com/ArcGIS/rest/services/Reference/World_Boundaries_and_Places/MapServer");
            basemaps.put("world_dark", "http://services.arcgisonline.com/arcgis/rest/services/Canvas/World_Dark_Gray_Base/MapServer");
            basemaps.put("world_gray", "http://services.arcgisonline.com/ArcGIS/rest/services/Canvas/World_Light_Gray_Base/MapServer");

            return basemaps;
        }


}
