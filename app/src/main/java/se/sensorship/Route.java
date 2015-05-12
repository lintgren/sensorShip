package se.sensorship;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy on 15-04-09.
 * Klass som tar hand om routen. Det är här all logik ligger.
 * Framtiden slumpa nya routes och så.
 */
public class Route implements Serializable {
    //TODO remove the fist two attributes, only used for the map
    static List<LatLng> pointsToTurn = new ArrayList<LatLng>();
    static LatLng[] static_path;
    private final String TAG = "Route";
    private LatLng[] path;
    static Direction[] directions;
    private List<Integer> turnDirectionOnPathIndex = new ArrayList<>();
    private float startTime = 0;
    private Location currentLocation;
    private int closestPointOnPathIndex = 0;

    private List<Location> prevLocations = new ArrayList<>();

    public Route(Direction[] directions, LatLng[] path) {
        this.directions = directions;
        this.path = path;
        static_path = path;
        int closestPointOnPathIndex = 0;
        for (int turnIndex = 0; turnIndex < directions.length; turnIndex++){
            float closestPointOnPathToTurnDistance = Float.MAX_VALUE;

            for (int pathIndex = closestPointOnPathIndex; pathIndex < path.length; pathIndex++){
                if (closestPointOnPathToTurnDistance != Float.MAX_VALUE &&
                        closestPointOnPathIndex + 10 < pathIndex){
                    break;
                }
                Location pathPoint = convertLatLngToLocation(path[pathIndex]);
                Location turnPoint = convertLatLngToLocation(directions[turnIndex].getLocation());
                float distance = pathPoint.distanceTo(turnPoint);
                if (distance <= closestPointOnPathToTurnDistance){
                    closestPointOnPathToTurnDistance = distance;
                    closestPointOnPathIndex = pathIndex;
                }
            }
            turnDirectionOnPathIndex.add(closestPointOnPathIndex);
            pointsToTurn.add(path[closestPointOnPathIndex]);
        }
        Log.d(TAG, "point to turn: " + turnDirectionOnPathIndex.toString());

    }

    public static List<LatLng> getLocationOnTurns() {
        return pointsToTurn;
    }

    public void updateLocation(Location location) {
        if(startTime==0)
            startTime=location.getTime();
        currentLocation = location;
        prevLocations.add(location);
        updateClosestPointOnPathIndex();
    }

    private void updateClosestPointOnPathIndex() {
        closestPointOnPathIndex = 0;
        float distanceToTrack = Float.MAX_VALUE;
        for (int pathIndex = 0; pathIndex < path.length; pathIndex++){
            LatLng pointOnPath = path[pathIndex];
            float distance = currentLocation.distanceTo(convertLatLngToLocation(pointOnPath));
            if (distance < distanceToTrack){
                distanceToTrack = distance;
                closestPointOnPathIndex = pathIndex;
            }
        }
    }

    /**
     * retunerar om användaren befinner sig inom PATH ( på rimligt avstånd och i rätt riktning)
     */
    public boolean isOnTrack() {
        int closestPointIndex = getClosestPointOnPathIndex();
        float distanceToTrack = currentLocation.distanceTo(convertLatLngToLocation
                (path[closestPointIndex]));
        Log.d(TAG, "Distance: " + distanceToTrack);
        return distanceToTrack < 20;
    }


    public double distanceToNextDirectionPoint() {
        int currentIndexOnPath = getClosestPointOnPathIndex();
        int directionPointOnPathIndex = 0;
        for (Integer i : turnDirectionOnPathIndex){
            if (i > currentIndexOnPath){
                directionPointOnPathIndex = i;
                break;
            }
        }
        float distance = 0;
        for (int i = currentIndexOnPath; i < directionPointOnPathIndex; i++){
            distance += convertLatLngToLocation(path[i]).distanceTo(convertLatLngToLocation
                    (path[i + 1]));
        }
        return distance;
    }


    /**
     * metod som returnar olika ints, om man är rätt/ om man ska svänga?
     * <p/>
     * Servicen kanske ska få en notifiering när denna behöver anropas? isOnTrack kan hålla reda
     * på om förra punkten är passerad eller ej
     */
    public Direction nextDirection() {
        int currentIndexOnPath = getClosestPointOnPathIndex();
        int directionPointIndex = 0;
        for (; directionPointIndex < turnDirectionOnPathIndex.size(); directionPointIndex++){
            if (turnDirectionOnPathIndex.get(directionPointIndex) >= currentIndexOnPath){
                break;
            }
        }
        return directions[directionPointIndex];
    }

    public int getPathSize() {
        return path.length;
    }

    public int getClosestPointOnPathIndex() {
        return closestPointOnPathIndex;
    }

    private Location convertLatLngToLocation(LatLng source) {
        Location ret = new Location("Converted");
        ret.setLatitude(source.latitude);
        ret.setLongitude(source.longitude);
        return ret;
    }

    public float getElapsedDistanceOnPath() {
        int currentIndexOnPath = getClosestPointOnPathIndex();
        float distance = 0;
        for (int i = 0; i < currentIndexOnPath; i++){
            distance += convertLatLngToLocation(path[i]).distanceTo(convertLatLngToLocation
                    (path[i + 1]));
        }
        return distance;
    }
    public float getElapsedTime(){
        float currentTime = currentLocation.getTime();
        return currentTime - startTime;
    }

    public double getElapsedDistance() {
        double distance = 0;
        for (int i = 0; i < prevLocations.size() - 1; i++){
            distance += prevLocations.get(i).distanceTo(prevLocations.get(i + 1));
        }
        return distance;
    }
}
