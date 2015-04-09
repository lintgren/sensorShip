package se.sensorship;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andy on 15-04-09.
 * Klass som tar hand om routen. Det är här all logik ligger.
 * Framtiden slumpa nya routes och så.
 */
public class Route {

    private LatLng[] turns,  path;

    public Route(LatLng[] turns, LatLng[] path){

        /**
         * Skapa ny random route efter där man är och hur långt man ska springa.
         */
    }
    public Route(){
        /**
         * Skapa fördeffad route.
         */
    }

    public boolean isOnTrack(Location currentLocation){
        /**
         * retunerar om användaren befinner sig inom PATH ( på rimligt avstånd och i rätt riktning)
         */
        return false;
    }

    public double distanceToNextDirectionPoint(Location currentLocation){
        /**
         * metod som returnar olika ints, om man är rätt/ om man ska svänga?
         */

        return 0;
    }

    public int directionToNextDirectionPoint(){
        return 0;
    }


}
