package test.support.esri.com.portalitemview;

import android.graphics.Bitmap;

import com.esri.arcgisruntime.portal.PortalItem;

/**
 * Created by kwas7493 on 4/5/2016.
 */
public class FeatureItem {
    PortalItem mPortalItem;
    Bitmap mBitmap;

    public FeatureItem(PortalItem portalItem, Bitmap bitmap){
        this.mBitmap = bitmap;
        this.mPortalItem = portalItem;
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

    public PortalItem getPortalItem(){
      return mPortalItem;
    }
}
