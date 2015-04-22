package se.sensorship;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andy on 15-04-16.
 */
public class Direction {
    private com.google.android.gms.maps.model.LatLng location;
    private String direction;
    public final static String LEFT = "Left";
    public final static String RIGHT = "Right";
    public final static String STRAIGHT = "Straight";
    public final static String GOAL = "Finished";
    private boolean longNotified = false;

    public boolean isShortnotified() {
        return shortnotified;
    }

    public void setShortnotified() {
        shortnotified = true;
    }

    public boolean isLongNotified() {
        return longNotified;
    }

    public void setLongNotified() {
        longNotified = true;
    }

    private boolean shortnotified = false;

    public Direction(LatLng location, String direction){
        this.location = location;
        this.direction = direction;
    }
    public Direction(double lat, double lon, String direction){
       this(new LatLng(lat,lon),direction);
    }
    public String getDirection() {
        return direction;
    }

    public LatLng getLocation() {

        return location;
    }
    public void setLocation(LatLng location) {
        this.location = location;
    }
}
