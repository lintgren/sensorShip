package se.sensorship;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.Locale;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int NOTIFICATION_ID = 1;
    private static final int LEFT_VIBRATE = 1, RIGHT_VIBRATE = 2;
    private final String TAG = "LocationService";
    private GoogleApiClient googleApiClient;
    private Route route;
    private boolean loadedTts;
    private TextToSpeech tts;
    private Vibrator vibrator;
    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;
    private TimeToTurnThread timeToTurnThread;
    private boolean threadIsStarted = false;
    private boolean useAudio, useVibration;
    private boolean GPSWorking = true;
    private long startTime;


    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
        route = new Route(Route.directions, Route.static_path);
        timeToTurnThread = new TimeToTurnThread(this, route);
        startTime = new Date().getTime();
    }

    public Route getRoute() {
        return route;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        useAudio = extras.getBoolean("audio");
        useVibration = extras.getBoolean("vibration");
        if (useAudio){
            setupTTS();
        }
        if (useVibration){
            setupVibrator();
        }
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setContentTitle("Rundomizer");
        notificationBuilder.setContentText("Distance: 0.0km");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setProgress(route.getPathSize(), 0, false);

        startForeground(NOTIFICATION_ID, notificationBuilder.build());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        closeTTS();
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1500);
        locationRequest.setFastestInterval(1500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "Accuracy: " + location.getAccuracy());
        if (location.getAccuracy() < 15
                ){
            onLocationChangedSynchronized(location);
            GPSWorking = true;
        }else if(GPSWorking){
            GPSWorking = false;
         //   speak("We have lost GPS location, please hold");
        }
    }

    private synchronized void onLocationChangedSynchronized(Location location) {
        route.updateLocation(location);
        boolean isOnTrack = route.isOnTrack();
        if (!isOnTrack){
            Log.e(TAG, "NOT ON TRACK!");
            boolean oldValue = useAudio;
            useAudio = true;
            speak("You are not on track");
            useAudio = oldValue;
        }
        updateNotification(route.getClosestPointOnPathIndex());

        Log.d(TAG, "distanceToNextDirection: " + route.distanceToNextDirectionPoint());
        timeToTurnThread.updateLocation(location);
        if (!threadIsStarted){
            timeToTurnThread.start();
            threadIsStarted = true;
        }
    }


    private void updateNotification(int currentPositionInPathIndex) {
        double distanceInKm = route.getElapsedDistance() / 1000;
        notificationBuilder.setProgress(route.getPathSize(), currentPositionInPathIndex, false);
     //   notificationBuilder.setContentText("Distance: " + String.format("%.2d", distanceInKm) +
     //           "km");
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void speak(String text) {
        if (!loadedTts || !useAudio){
            return;
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP){
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            //noinspection deprecation
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    private void vibrate(int numberOfVibrations) {
        detailedVibrate(numberOfVibrations, 150);
    }

    private void vibrate(Direction direction) {
        if (direction.getDirection().equals(Direction.LEFT)){
            vibrate(LEFT_VIBRATE);
        }else if (direction.getDirection().equals(Direction.RIGHT)){
            vibrate(RIGHT_VIBRATE);
        }

    }

    private void longVibrate() {
        detailedVibrate(1, 1000);
    }

    private void detailedVibrate(int numberOfVibrations, int vibrateLength) {
        if (!useVibration){
            return;
        }
        long[] pattern = new long[numberOfVibrations * 2];
        for (int i = 0; i < pattern.length; i++){
            if (i % 2 == 0){ // useVibration
                pattern[i] = vibrateLength;
            }else{
                pattern[i] = 300;
            }

        }
        vibrator.vibrate(pattern, -1);
    }

    private void setupTTS() {
        if (tts != null){
            Log.e(TAG, "TTS already setup");
            return;
        }

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    loadedTts = true;
                    tts.setLanguage(Locale.CANADA);
                }else{
                    loadedTts = false;
                }
            }
        });

    }

    private void setupVibrator() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void closeTTS() {
        if (tts != null){
            tts.stop();
            tts.shutdown();
        }
    }

    public void notifyUser() {
        Direction direction = route.nextDirection();
        if (!direction.isLongNotified()){
            longVibrate();
            speak("turn " + direction.getDirection() + " in " + Direction.LONG_ALERT_TIME + " " +
                    "seconds.");
            direction.setLongNotified();
        }else{
            vibrate(direction);
            speak("Turn " + direction.getDirection());
            direction.setShortnotified();
        }
    }

    public void notifyUserFinishedRound() {
        longVibrate();
        long elapsedTime = Math.round((new Date().getTime() - startTime) * 1000);
        double elapsedDistance = Math.round(route.getElapsedDistance()/10)/100;
        speak("Well done! You ran " + elapsedDistance + " meters in " + elapsedTime + "seconds");
    }
}
