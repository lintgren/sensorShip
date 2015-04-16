package se.sensorship;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andy on 15-04-16.
 */
public class Direction {
    private com.google.android.gms.maps.model.LatLng location;
    private int direction;
    public final static int LEFT = -1;
    public final static int RIGHT = 1;
    public final static int STRAIGHT = 0;
    public final static int GOAL = 1337;

    public Direction(LatLng location, int direction){
        this.location = location;
        this.direction = direction;
    }
    public Direction(double lat, double lon, int direction){
       this(new LatLng(lat,lon),direction);
    }
    public int getDirection() {
        return direction;
    }

    public LatLng getLocation() {

        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
