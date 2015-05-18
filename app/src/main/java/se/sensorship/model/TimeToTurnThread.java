package se.sensorship.model;

import android.location.Location;
import android.util.Log;

/**
 * Created by Wycheproof on 15-04-23.
 */
public class TimeToTurnThread extends Thread {
    private static final String TAG = "Thread";

    private LocationService locationService;
    private Route route;
    private int locationHead;
    private Location[] oldLocations;
    private boolean oldLocationsIsFilled = false;
    private double calculatedSpeed;

    private double timeToTurn;

    public TimeToTurnThread(LocationService locationService, Route route) {
        this.locationService = locationService;
        oldLocations = new Location[3];
        this.route = route;
    }

    @Override
    public void run() {
        Direction direction = route.nextDirection();
        while (!direction.getDirection().equals(Direction.GOAL)){
            direction = route.nextDirection();
            Direction prevDirection = route.prevDirection();
            if(prevDirection == null) Log.d(TAG, "PrevDir is null");
            if(prevDirection!= null && !prevDirection.isShortnotified()){
                Log.d(TAG, "prevDir is a " + prevDirection.getDirection());
                locationService.notifyUser(prevDirection);
            }
            Log.d(TAG, "TimeToTurn: " + timeToTurn);
            try{
                long sleepTime;
                if(!direction.isLongNotified()){
                    Log.d("Time to turn","Waiting for long notification");
                    sleepTime = (long) ((timeToTurn - Direction.LONG_ALERT_TIME) * 1000);
                }
                else if (!direction.isShortnotified()){
                    Log.d("Time to turn","Waiting for short notification");
                    sleepTime = (long) ((timeToTurn - Direction.SHORT_ALERT_TIME) * 1000);
                }
                else
                    sleepTime = Long.MAX_VALUE;

                if (sleepTime > 0){
                    Log.d(TAG, "sleeping for " + sleepTime);
                    sleep(sleepTime);
                }
                locationService.notifyUser(direction);
            }catch (InterruptedException e){
            }
        }
        while (direction.getDirection().equals(Direction.GOAL)) {
            try {
                long sleepTime;
                sleepTime = (long) (timeToTurn * 1000);

                if (sleepTime > 0) {
                    sleep(sleepTime);
                }
                if(direction.isShortnotified()){
                    return;
                }
                locationService.notifyUserFinishedRound();
                direction.setShortnotified();
            } catch (InterruptedException e) {
            }
        }
        Log.d(TAG, "Route finished!");
    }

    public void updateLocation(Location location) {
        oldLocations[locationHead] = location;
        locationHead = (locationHead + 1) % oldLocations.length;
        calculateSpeed();
        calculateTimeToTurn();
        if(!route.nextDirection().isLongNotified()){
            this.interrupt();
        }
    }


    private void calculateSpeed() {
        if (!oldLocationsIsFilled && locationHead == 0) oldLocationsIsFilled = true;
        //Calculates mean speed
        if (oldLocationsIsFilled){
            calculatedSpeed = 0;
            for (int i = 0; i < oldLocations.length - 1; i++){
                long firstTime = oldLocations[(i + locationHead) % oldLocations.length]
                        .getElapsedRealtimeNanos();
                long secondTime = oldLocations[(i + locationHead + 1) % oldLocations.length]
                        .getElapsedRealtimeNanos();
                long deltaTime = secondTime - firstTime;
                float distance = oldLocations[(i + locationHead) % oldLocations.length]
                        .distanceTo(oldLocations[(i + locationHead + 1) % oldLocations.length]);
                calculatedSpeed += distance / deltaTime * 1000000000;
            }
            Log.d(TAG, "SPEED:" + calculatedSpeed);
            calculatedSpeed /= oldLocations.length - 1;
        }else{
            int i = locationHead - 1 + oldLocations.length;
            calculatedSpeed = oldLocations[i % oldLocations.length].getSpeed();
        }
    }

    private void calculateTimeToTurn() {
        timeToTurn = route.distanceToNextDirectionPoint() / calculatedSpeed;
    }

}
