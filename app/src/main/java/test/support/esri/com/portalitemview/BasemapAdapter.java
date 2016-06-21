package test.support.esri.com.portalitemview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.ArrayList;

/**
 * Created by kwas7493 on 6/14/2016.
 */
public class BasemapAdapter extends RecyclerView.Adapter<BasemapAdapter.ViewHolder>{
    private View cardView;
    ArrayList<Basemap> basemaps;
    public BasemapAdapter(ArrayList<Basemap> basemap){
    this.basemaps = basemap;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       cardView= LayoutInflater.from(parent.getContext()).inflate(R.layout.basemap_card_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(cardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
           final MapView mapView = holder.basemap_mapView;
            final ArcGISMap map = new ArcGISMap(basemaps.get(position));
            map.loadAsync();
        Log.d("KwasiError", map.getLoadStatus().toString());

            map.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    mapView.setMap(map);
                }
            });

    }

    @Override
    public int getItemCount() {
        return basemaps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        MapView basemap_mapView;
        public ViewHolder(View viewer){
            super(viewer);
            this.basemap_mapView = (MapView)viewer.findViewById(R.id.base_map_map);

        }

    }
}
