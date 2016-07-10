package test.support.esri.com.portalitemview;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.PortalItemType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by kwas7493 on 4/1/2016.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
    ArrayList<CardViewData> portalDataset;
    MapView mapView;
    View view;
    FragmentManager fragmentManager;

    public CardViewAdapter(ArrayList<CardViewData> mPortalDataset, FragmentManager appFragmentManager) {
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
        TextView holderOwnerView = (TextView)holder.itemOwner;
        TextView holderDateModified = (TextView)holder.dateModified;
        TextView holderNumOfView = (TextView)holder.numOfViews;
        TextView holderNumOfRating = (TextView)holder.numOfRating;

        final ImageView holderImageView = holder.imageView;
        holderNameView.setText(" Item Title: "+portalDataset.get(position).getPortalItemTitle());
        holderImageView.setImageBitmap(portalDataset.get(position).getmBitmap());
        //holderDescView.setText(portalDataset.get(position).getPortalItem().getDescription());
        holderOwnerView.setText(" Owner: "+portalDataset.get(position).getPortalItem().getOwner());
        Date date = new Date(portalDataset.get(position).getPortalItem().getModified());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        holderDateModified.setText(" Date modified: "+ cal.get(Calendar.MONTH) +"/"+cal.get(Calendar.DAY_OF_MONTH) +"/"+
        cal.get(Calendar.YEAR));
        holderNumOfRating.setText(" Item rated as a "+portalDataset.get(position).getPortalItem().getNumRatings());
        holderNumOfView.setText(" "+portalDataset.get(position).getPortalItem().getNumViews()+" views");
        holderImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (portalDataset.get(position).getPortalItem().getType() == PortalItemType.WEBMAP) {
                    final ArcGISMap portalMap = new ArcGISMap(portalDataset.get(position).getPortalItem());
                    portalMap.loadAsync();
                    portalMap.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            if (portalMap.getLoadStatus() == LoadStatus.LOADED) {
                                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("recycler_view_fragment"))
                                        .commit();
                                MapView realMapView = (MapView) fragmentManager.findFragmentByTag("recycler_view_fragment").getActivity()
                                        .findViewById(R.id.nav_map_view);
                                realMapView.setMap(portalMap);


                                Toast.makeText(v.getContext(), "Loaded portal item "+portalDataset.get(position)
                                        .getPortalItemName(), Toast.LENGTH_SHORT).show();
                            } else if (portalMap.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                                Snackbar.make(v, "Yo man the map did not load because \n" +
                                                " " + portalMap.getLoadError().getMessage(),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });

                }

                //trying to demonstrated a reported issue on early adopter
                /*final ArcGISVectorTiledLayer vectorTiledLayer = new ArcGISVectorTiledLayer(portalDataset.get(position)
                .getPortalItem());
                vectorTiledLayer.loadAsync();
                vectorTiledLayer.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("recycler_view_fragment"))
                                .commit();
                        MapView realMapView = (MapView) fragmentManager.findFragmentByTag("recycler_view_fragment").getActivity()
                                .findViewById(R.id.nav_map_view);
                        realMapView.getMap().getOperationalLayers().add(vectorTiledLayer);
                       Log.d("KwasiIDTest", realMapView.getMap().getOperationalLayers().get(0).getName());
                    }
                });*/
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
        View itemOwner;
        ImageView imageView;
        MapView map_view;
        View numOfViews;
        View numOfRating;
        View dateModified;

        public ViewHolder(View view) {
            super(view);
            this.viewTextView = view.findViewById(R.id.item_title);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.viewDescView = view.findViewById(R.id.map_desc);
            this.map_view = (MapView) view.findViewById(R.id.nav_map_view);
            this.itemOwner = view.findViewById(R.id.owner);
            this.dateModified = view.findViewById(R.id.date_modified);
            this.numOfViews = view.findViewById(R.id.num_views);
            this.numOfRating = view.findViewById(R.id.num_ratings);
        }
    }
}
