package test.support.esri.com.portalitemview;

import android.graphics.Bitmap;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.portal.PortalItem;

/**
 * Created by kwas7493 on 4/5/2016.
 */
public class CardViewData {
    PortalItem mPortalItem;
    Bitmap mBitmap;
    ArcGISMap map;

    public CardViewData(PortalItem portalItem, Bitmap bitmap, ArcGISMap portalMap){
        this.mBitmap = bitmap;
        this.mPortalItem = portalItem;
        this.map = portalMap;
    }

    public String getPortalItemName(){
        assert mPortalItem != null;
        return mPortalItem.getName();
    }

    public String getPortalItemTitle(){
        assert  mPortalItem !=null;
        return mPortalItem.getTitle();

    }




    public Bitmap getmBitmap(){
        assert mBitmap != null;
        return mBitmap;
    }

    public ArcGISMap getPortalMap(){
        assert map !=null;
              return map;
    }

    public PortalItem getPortalItem(){
      return mPortalItem;
    }
}
