package test.support.esri.com.portalitemview;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Point;

/**
 * Created by kwas7493 on 6/8/2016.
 */
public class GeocodeAdapter extends RecyclerView.Adapter<GeocodeAdapter.ViewHolder>{
    View recyclerView;
    GeocodeData dataGeocoded;
    public GeocodeAdapter(GeocodeData geocodeData){
        this.dataGeocoded = geocodeData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        recyclerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.geocodeshow, parent, false);
        ViewHolder viewHolder = new ViewHolder(recyclerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TextView geocodeText = holder.innnerText;
        geocodeText.setText(dataGeocoded.getLabelName().get(position));
        geocodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Point point = dataGeocoded.getLocationPoint().get(position);
                Intent intent = new Intent(v.getContext(), PortalViewMain.class);
                double[] coordinates = {point.getX(), point.getY(),
                        new Integer(point.getSpatialReference().getWkid()).doubleValue()};
                intent.putExtra("point", coordinates);
                    v.getContext().startActivity(intent);
                /*mapView.setMap(new Map(Basemap.createLightGrayCanvas()));
                mapView.setViewpointCenterAsync(point);
                Toast.makeText(v.getContext(), "Map point is: "+point.getX(), Toast.LENGTH_LONG).show();*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataGeocoded.getSize();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView innnerText;
        public ViewHolder(View itemView) {
            super(itemView);
            this.innnerText = (TextView) itemView.findViewById(R.id.testing_text);
        }
    }
}
