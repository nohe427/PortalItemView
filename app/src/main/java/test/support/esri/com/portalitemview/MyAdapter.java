package test.support.esri.com.portalitemview;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.PortalItemType;

import java.util.ArrayList;

/**
 * Created by kwas7493 on 4/1/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    ArrayList<FeatureItem> portalDataset;
    MapView mapView;
    View view;
    FragmentManager fragmentManager;

    public MyAdapter(ArrayList<FeatureItem> mPortalDataset, FragmentManager appFragmentManager) {
        portalDataset = mPortalDataset;
        fragmentManager = appFragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        View underViewer = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_navigator, parent, false);
        mapView = (MapView) underViewer.findViewById(R.id.nav_map_view);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        TextView holderNameView = (TextView) holder.viewTextView;
        TextView holderDescView = (TextView) holder.viewDescView;

        final ImageView holderImageView = holder.imageView;
        holderNameView.setText(portalDataset.get(position).getPortalItemTitle());
        holderImageView.setImageBitmap(portalDataset.get(position).getmBitmap());
        holderDescView.setText(portalDataset.get(position).getPortalItem().getDescription());
        holderImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (portalDataset.get(position).getPortalItem().getType() == PortalItemType.WEBMAP) {
                    final Map portalMap = new Map(portalDataset.get(position).getPortalItem());
                    portalMap.loadAsync();
                    portalMap.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            if (portalMap.getLoadStatus() == LoadStatus.LOADED) {
                                fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag("recycler_view_fragment"))
                                        .commit();
                                MapView realMapView = (MapView) fragmentManager.findFragmentByTag("recycler_view_fragment").getActivity()
                                        .findViewById(R.id.nav_map_view);
                                realMapView.setMap(portalMap);
                                Log.d("Kwasi", portalMap.getOperationalLayers().get(0).getName());
                                Toast.makeText(v.getContext(), "Loaded", Toast.LENGTH_SHORT).show();
                            } else if (portalMap.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                                Snackbar.make(v, "Yo man the map did not load \n" +
                                                "because; " + portalMap.getLoadError().getMessage(),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return portalDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View viewTextView;
        View viewDescView;
        ImageView imageView;
        MapView map_view;

        public ViewHolder(View view) {
            super(view);
            this.viewTextView = view.findViewById(R.id.view_text);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.viewDescView = view.findViewById(R.id.map_desc);
            this.map_view = (MapView) view.findViewById(R.id.nav_map_view);
        }
    }
}
