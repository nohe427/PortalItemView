package test.support.esri.com.portalitemview;

import com.esri.arcgisruntime.tasks.route.DirectionManeuver;
import com.esri.arcgisruntime.tasks.route.Route;

import java.util.ArrayList;

/**
 * Created by kwas7493 on 7/5/2016.
 */
public class RoutingManouversData {

    ArrayList<DirectionManeuver> directionManeuvers;
    Route route;

    public RoutingManouversData(ArrayList<DirectionManeuver> directionManCon,Route arrayRoute){
     this.directionManeuvers = directionManCon;
        this.route = arrayRoute;
    }


    public ArrayList<DirectionManeuver> getDirectionManeuvers(){
        return directionManeuvers;
    }

    public Route getRoute(){
        return route;
    }

}
