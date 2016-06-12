package test.support.esri.com.portalitemview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kwas7493 on 6/8/2016.
 */
public class GeocodeAdapter extends RecyclerView.Adapter<GeocodeAdapter.ViewHolder> {
    RecyclerView recyclerView;
    ArrayList<String> geocodeResults;
    public GeocodeAdapter(ArrayList<String> geocodePossibles){
        this.geocodeResults = geocodePossibles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        recyclerView = (RecyclerView) LayoutInflater.from(parent.getContext()).inflate(R.layout.geocodeshow, parent, false);
        ViewHolder viewHolder = new ViewHolder(recyclerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView geocodeText = holder.innnerText;
        geocodeText.setText(geocodeResults.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView innnerText;
        public ViewHolder(View itemView) {
            super(itemView);
            this.innnerText = (TextView) itemView.findViewById(R.id.testing_text);
        }
    }
}
