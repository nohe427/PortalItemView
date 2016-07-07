package test.support.esri.com.portalitemview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by kwas7493 on 7/5/2016.
 */
public class RoutingManouvAdapter extends RecyclerView.Adapter<RoutingManouvAdapter.ViewHolder> {
        View mainView;
    RoutingManouversData routingData;
    public RoutingManouvAdapter(RoutingManouversData routingManouversData){
     this.routingData = routingManouversData;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mainView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_recycler_content, parent, false);

        ViewHolder viewHolder = new ViewHolder(mainView);
        return viewHolder;
    }


    public double convertToMile(double length){
        DecimalFormat decimalFormat = new DecimalFormat("##");
        double mile = Double.parseDouble(decimalFormat.format(length * 0.0006214));
        if(mile < 1){
            return length;
        }else {
            return mile;
        }
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
                String directionsText =  routingData.getDirectionManeuvers().get(position).getDirectionText();
                holder.routeDirections.setText(directionsText);
                double meters = convertToMile(routingData.getDirectionManeuvers().get(position).getLength());
                if(meters < 1){
                    holder.manouverMessage.setText(String.valueOf(meters)+ " meters");
                }else{
                    holder.manouverMessage.setText(String.valueOf(meters)+ " mi");
                }
    }


    @Override
    public int getItemCount() {
        return routingData.getDirectionManeuvers().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView routeDirections;
        TextView manouverMessage;

        public ViewHolder(View view){
            super(view);
                this.routeDirections = (TextView) view.findViewById(R.id.manouvers);
                this.manouverMessage = (TextView)view.findViewById(R.id.direction_text);
            }

    }
}
