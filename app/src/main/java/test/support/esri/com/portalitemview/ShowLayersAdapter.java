package test.support.esri.com.portalitemview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kwas7493 on 6/25/2016.
 */
public class ShowLayersAdapter extends RecyclerView.Adapter<ShowLayersAdapter.ViewHolder> {
    List<String> legendInfosList;
    View showLayersLayout;
    public ShowLayersAdapter(List<String> legendInfo){
        this.legendInfosList = legendInfo;
    }

    @Override
    public ShowLayersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        showLayersLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_layers_elements,parent, false);
        ShowLayersAdapter.ViewHolder viewHolder = new ShowLayersAdapter.ViewHolder(showLayersLayout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShowLayersAdapter.ViewHolder holder, int position) {
                TextView layerName = holder.testText;
        layerName.setText(legendInfosList.get(position));
    }

    @Override
    public int getItemCount() {
        return legendInfosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView testText;
        public ViewHolder(View itemView) {
            super(itemView);
            this.testText = (TextView)itemView.findViewById(R.id.show_layers_text_element);

        }
    }
}
