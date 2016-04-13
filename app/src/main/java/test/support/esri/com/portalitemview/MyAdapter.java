package test.support.esri.com.portalitemview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.PortalItemType;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by kwas7493 on 4/1/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    ArrayList<FeatureItem> portalDataset;
    MapView mapView;
    public MyAdapter(ArrayList<FeatureItem> mPortalDataset){
        portalDataset = mPortalDataset;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view, parent, false);
        View underViewer = LayoutInflater.from(parent.getContext()).inflate(R.layout.portalview, parent, false);
        mapView = (MapView)underViewer.findViewById(R.id.map_view);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        TextView holderTextView = (TextView) holder.viewTextView;
        final ImageView holderImageView = holder.imageView;
        holderTextView.setText(portalDataset.get(position).getPortalItemName());
        holderImageView.setImageBitmap(portalDataset.get(position).getmBitmap());

        holderImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Map:  "+portalDataset.get(position).getPortalItemName(),
                        Toast.LENGTH_SHORT).show();
                if(portalDataset.get(position).getPortalItem().getType() == PortalItemType.WEBMAP) {
                    final Map portalMap = new Map(portalDataset.get(position).getPortalItem());
                    portalMap.loadAsync();
                   new Thread(new Runnable(){
                       @Override
                       public void run(){

                           if(portalMap.getLoadStatus() == LoadStatus.LOADED){
                               mapView.setMap(portalMap);
                               mapView.setViewpointAsync(portalMap.getInitialViewpoint());
                           }else {
                               Log.d("KwasiD", portalMap.getLoadStatus().toString());
                           }
                       }

                   }).start();

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return portalDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View viewTextView;
        ImageView imageView;
        public ViewHolder(View view){
            super(view);
            this.viewTextView = view.findViewById(R.id.view_text);
            this.imageView = (ImageView)view.findViewById(R.id.thumbnail);
        }
    }
}
