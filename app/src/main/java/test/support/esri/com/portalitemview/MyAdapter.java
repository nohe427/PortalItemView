package test.support.esri.com.portalitemview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by kwas7493 on 4/1/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    ArrayList<FeatureItem> portalDataset;

    public MyAdapter(ArrayList<FeatureItem> mPortalDataset){
        portalDataset = mPortalDataset;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        TextView holderTextView = (TextView) holder.viewTextView;
        final ImageView holderImageView = holder.imageView;
        final MapView holderMapView = holder.mapView;
        holderTextView.setText(portalDataset.get(position).getPortalItemName());
        holderImageView.setImageBitmap(portalDataset.get(position).getmBitmap());

      /*  holderImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArcGISVectorTiledLayer vectorTiledLayer = new ArcGISVectorTiledLayer(portalDataset.get(position)
                .getPortalItemName());
               holderMapView.getMap().getOperationalLayers().add(vectorTiledLayer);
                Toast.makeText(, portalDataset.get(position).getPortalItemName(), Toast.LENGTH_LONG).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return portalDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View viewTextView;
        ImageView imageView;
        MapView mapView;
        public ViewHolder(View view){
            super(view);
            this.viewTextView = view.findViewById(R.id.view_text);
            this.imageView = (ImageView)view.findViewById(R.id.thumbnail);
           this.mapView = (MapView) view.findViewById(R.id.map_view);
        }
    }
}
