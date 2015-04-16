package se.sensorship;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
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

    private final String TAG = "LocationService";
    private GoogleApiClient googleApiClient;
    private Route route;
    private boolean loadedTts;
    private TextToSpeech tts;
    private Vibrator vibrator;
    private Location prevLocation;
    private NotificationManager notificationManager;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        route = new Route();
        setupTTS();
        setupVibrator();

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(this).setContentTitle
                ("LocationActivity").setContentText(new Date().toString()).setSmallIcon(R
                .drawable.powered_by_google_light).build();
        Intent notificationIntent = new Intent(this, LocationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,
                this);
        closeTTS();
        Log.d("tts Destroy", "tts Destroy");
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
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
        if (prevLocation != null) {
            float bearing = prevLocation.bearingTo(location);
            location.setBearing(bearing);
        }
        if (!route.isOnTrack(location)) {
            Log.e(TAG, "NOT ON TRACK!");
        }
        Log.d(TAG, "distanceToNextDirection: " + route.distanceToNextDirectionPoint(location));
        prevLocation = location;
        int progress = route.getClosestPointOnPathIndex(location);
        updateNotification(progress);


        if(route.distanceToNextDirectionPoint(location)>50){
            return;
        }
        int direction = route.directionOnNextDirectionPoint(location);
        if (direction == Direction.LEFT) {
            speak("Left");
            vibrate(2);
            Log.d(TAG, "TURN LEFT!");
        } else if (direction == Direction.RIGHT) {
            speak("Right");
            vibrate(1);
            Log.d(TAG, "TURN RIGHT!");
        } else if (direction == Direction.GOAL) {
            Log.d(TAG, "GOAL");
        }
    }


    private void updateNotification(int currentPositionInPathIndex){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setProgress(route.getPathLengthInNbr(), currentPositionInPathIndex, false);
        notificationManager.notify(NOTIFICATION_ID,mBuilder.build());

    }


    private void speak(String text) {
        if (!loadedTts){
            return;
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP){
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        }else{
            //noinspection deprecation
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }

    }

    private void vibrate(int numberOfVibrations) {
        long[] pattern = new long[numberOfVibrations * 2];
        for (int i = 0; i < pattern.length; i++){
            if (i % 2 == 0){ // vibration
                pattern[i] = 150;
            }else{
                pattern[i] = 300;
            }

        }
        vibrator.vibrate(pattern, -1);
    }

    private void setupTTS() {
        if (tts != null){
            Log.d("tts not null", "tts not null");
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
}
