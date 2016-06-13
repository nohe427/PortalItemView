package test.support.esri.com.portalitemview;

import com.esri.arcgisruntime.geometry.Point;

import java.util.ArrayList;

/**
 * Created by kwas7493 on 6/13/2016.
 */
public class GeocodeData {
    private ArrayList<Point> locationPoint;
    private ArrayList<String> locationName;


    public GeocodeData(ArrayList<String> labelName, ArrayList<Point> labelLocation){
        this.locationName = labelName;
        this.locationPoint = labelLocation;
    }

    public ArrayList<String> getLabelName(){
        assert locationName != null;
        return locationName;
    }

    public ArrayList<Point> getLocationPoint(){
        assert  locationPoint != null;
        return locationPoint;
    }

    public int getSize(){
        assert locationName != null;
        return locationName.size();
    }


}
