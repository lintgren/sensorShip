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
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int NOTIFICATION_ID = 1;

    private final String TAG = "LocationService";
    private final Object lock = new Object();
    private GoogleApiClient googleApiClient;
    private Route route;
    private boolean loadedTts;
    private TextToSpeech tts;
    private Vibrator vibrator;
    private Location prevLocation;
    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;
    private boolean longNotified = false, shortNotified = false;
    private boolean isAllDone = true;


    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        setupTTS();
        setupVibrator();

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();

        route = new Route(new Direction[]{new Direction(55.714916, 13.211974, Direction.RIGHT), new Direction(55.715079, 13.211094, Direction.RIGHT), new Direction(55.715396, 13.212231, Direction.LEFT)}, new LatLng[]{new LatLng(55.714879, 13.211993), new LatLng(55.714879, 13.211993), new LatLng(55.714927, 13.211742), new LatLng(55.714972, 13.21146), new LatLng(55.715019, 13.211201), new LatLng(55.715036, 13.211121), new LatLng(55.715196, 13.211357), new LatLng(55.715299, 13.211585), new LatLng(55.71538, 13.21185), new LatLng(55.715421, 13.212169), new LatLng(55.715288, 13.212151), new LatLng(55.715035, 13.212179), new LatLng(55.714914, 13.212089), new LatLng(55.714914, 13.212089), new LatLng(55.714914, 13.212089)});
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setContentTitle("Rundomizer");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setProgress(route.getPathSize(), 0, false);

        Intent notificationIntent = new Intent(this, LocationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        startForeground(NOTIFICATION_ID, notificationBuilder.build());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        closeTTS();
        Log.d("tts Destroy", "tts Destroy");
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
        onLocationChangedSynchronized(location);
    }

    private synchronized void onLocationChangedSynchronized(Location location) {
        long time = System.currentTimeMillis();
        route.updateLocation(location);
        boolean isOnTrack = route.isOnTrack();
        if (!isOnTrack) {
            Log.e(TAG, "NOT ON TRACK!");
            //TODO lämna meddelande till användaren?
        }
        int progress = route.getClosestPointOnPathIndex();
        float elapsedDistance = route.getElapsedDistance();
        updateNotification(progress, elapsedDistance);

        Log.d(TAG, "distanceToNextDirection: " + route.distanceToNextDirectionPoint());
        prevLocation = location;

        if (timeToTurn() < 12) { //slightly higher than 10 seconds to get some time to say the message
            Direction direction = route.directionOnNextDirectionPoint();
            String turn = direction.getDirection();
            if (!direction.isLongNotified()) {
                longVibrate();
                speak("turn " + turn + " in 10 seconds. Beep");
                direction.setLongNotified();
            }
            if (timeToTurn() < 3 && !direction.isShortnotified()) {
                direction.setShortnotified();
                speak("turn" + turn);
                if (turn.equals(Direction.LEFT)) {
                    vibrate(2);
                } else if (turn.equals(Direction.RIGHT)) {
                    vibrate(1);
                }

                Log.d(TAG, "TURN " + turn);
            }
        } else {
            longNotified = false;
            shortNotified = false;
        }
    }


    private void updateNotification(int currentPositionInPathIndex, float distanceInMeter) {
        float distanceInKm = distanceInMeter / 1000;
        notificationBuilder.setProgress(route.getPathSize(), currentPositionInPathIndex, false);
        notificationBuilder.setContentText("Distance: " + String.format("%.1f", distanceInKm) +
                "km");
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    private void speak(String text) {
        if (!loadedTts) {
            return;
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            //noinspection deprecation
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    private void vibrate(int numberOfVibrations) {
        detailedVibrate(numberOfVibrations, 150);
    }

    private void longVibrate() {
        detailedVibrate(1, 500);
    }

    private void detailedVibrate(int numberOfVibrations, int vibrateLength) {
        long[] pattern = new long[numberOfVibrations * 2];
        for (int i = 0; i < pattern.length; i++) {
            if (i % 2 == 0) { // vibration
                pattern[i] = vibrateLength;
            } else {
                pattern[i] = 300;
            }

        }
        vibrator.vibrate(pattern, -1);
    }

    private void setupTTS() {
        if (tts != null) {
            Log.d("tts not null", "tts not null");
            return;
        }

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    loadedTts = true;
                    tts.setLanguage(Locale.CANADA);
                } else {
                    loadedTts = false;
                }
            }
        });

    }

    private void setupVibrator() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void closeTTS() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    private double timeToTurn() {
        double distance = route.distanceToNextDirectionPoint();
        if (distance < 100) {
            return distance / prevLocation.getSpeed();
        }
        return 1000;
    }
}
