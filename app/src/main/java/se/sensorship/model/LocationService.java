package se.sensorship.model;

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
import android.text.Layout;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

import se.sensorship.R;

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
        LatLng[] longPath = new LatLng[]{new LatLng(55.7148675, 13.2119232),
                new LatLng(55.7147799, 13.2118857), new LatLng(55.7147497, 13.2118642),
                new LatLng(55.7147043, 13.2118428), new LatLng(55.7146650, 13.2118160),
                new LatLng(55.7146227, 13.2117999), new LatLng(55.7145955, 13.2117999),
                new LatLng(55.7145563, 13.2117569), new LatLng(55.7145140, 13.2117194),
                new LatLng(55.7144777, 13.2117140), new LatLng(55.7144505, 13.2116979),
                new LatLng(55.7144324, 13.2116765), new LatLng(55.7143961, 13.2116497),
                new LatLng(55.7143417, 13.2116121), new LatLng(55.7142934, 13.2115853),
                new LatLng(55.7142511, 13.2115638), new LatLng(55.7142208, 13.2115424),
                new LatLng(55.7141483, 13.2114995), new LatLng(55.7140909, 13.2114619),
                new LatLng(55.7140123, 13.2114404), new LatLng(55.7139609, 13.2113975),
                new LatLng(55.7139247, 13.2113922), new LatLng(55.7138975, 13.2115209),
                new LatLng(55.7138794, 13.2116175), new LatLng(55.7138673, 13.2116818),
                new LatLng(55.7138552, 13.2117409), new LatLng(55.7138491, 13.2117945),
                new LatLng(55.7138401, 13.2118320), new LatLng(55.7138038, 13.2118267),
                new LatLng(55.7137736, 13.2118106), new LatLng(55.7137464, 13.2117730),
                new LatLng(55.7137252, 13.2117569), new LatLng(55.7136860, 13.2117301),
                new LatLng(55.7136739, 13.2116443), new LatLng(55.7136588, 13.2115263),
                new LatLng(55.7136588, 13.2114619), new LatLng(55.7136527, 13.2114190),
                new LatLng(55.7136467, 13.2113653), new LatLng(55.7136436, 13.2113063),
                new LatLng(55.7136316, 13.2112527), new LatLng(55.7136134, 13.2111669),
                new LatLng(55.7136104, 13.2111293), new LatLng(55.7136104, 13.2110810),
                new LatLng(55.7136104, 13.2110435), new LatLng(55.7136316, 13.2110220),
                new LatLng(55.7136618, 13.2110327), new LatLng(55.7137071, 13.2110327),
                new LatLng(55.7137343, 13.2110327), new LatLng(55.7137736, 13.2110274),
                new LatLng(55.7138099, 13.2110006), new LatLng(55.7138431, 13.2109845),
                new LatLng(55.7138703, 13.2109684), new LatLng(55.7139398, 13.2109416),
                new LatLng(55.7139730, 13.2109040), new LatLng(55.7140033, 13.2108986),
                new LatLng(55.7140728, 13.2108450), new LatLng(55.7141332, 13.2107967),
                new LatLng(55.7141815, 13.2107645), new LatLng(55.7142511, 13.2107216),
                new LatLng(55.7143115, 13.2106841), new LatLng(55.7143598, 13.2106465),
                new LatLng(55.7143931, 13.2106143), new LatLng(55.7144414, 13.2105660),
                new LatLng(55.7144898, 13.2105178), new LatLng(55.7145260, 13.2104802),
                new LatLng(55.7145955, 13.2104212), new LatLng(55.7146379, 13.2103944),
                new LatLng(55.7146681, 13.2104319), new LatLng(55.7147043, 13.2104856),
                new LatLng(55.7147466, 13.2105446), new LatLng(55.7147769, 13.2105982),
                new LatLng(55.7148071, 13.2106358), new LatLng(55.7148584, 13.2106894),
                new LatLng(55.7148856, 13.2107431), new LatLng(55.7149310, 13.2108235),
                new LatLng(55.7149733, 13.2108933), new LatLng(55.7150126, 13.2109416),
                new LatLng(55.7150609, 13.2109952), new LatLng(55.7150790, 13.2110542),
                new LatLng(55.7150609, 13.2111293), new LatLng(55.7150458, 13.2112312),
                new LatLng(55.7150307, 13.2113171), new LatLng(55.7149975, 13.2114351),
                new LatLng(55.7149944, 13.2115102), new LatLng(55.7149854, 13.2115799),
                new LatLng(55.7149672, 13.2116765), new LatLng(55.7149521, 13.2117516),
                new LatLng(55.7149461, 13.2118267)};
        Direction[] longDirections = new Direction[]{new Direction(55.7139247, 13.2113975,
                Direction.LEFT), new Direction(55.7138371, 13.2118428, Direction.RIGHT),
                new Direction(55.7136829, 13.2117677, Direction.RIGHT), new Direction(55.713630,
                13.211080, Direction.SLIGHT_RIGHT), new Direction(55.7146288, 13.2103890,
                Direction.RIGHT), new Direction(55.7149461, 13.2118267, Direction.GOAL)};
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
        route = new Route(longDirections, longPath);
        timeToTurnThread = new TimeToTurnThread(this, route);
        startTime = new Date().getTime();
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
        Log.d(TAG, "accuracy: " + location.getAccuracy());
        if (location.getAccuracy() < 15){
            onLocationChangedSynchronized(location);
            GPSWorking = true;
        }else if (GPSWorking){
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
        notificationBuilder.setContentText("Distance: " + new DecimalFormat("##.#").format
                (distanceInKm) +
                "km");
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
        if (direction.getDirection().equals(Direction.LEFT) || direction.getDirection().equals
                (Direction.SLIGHT_LEFT)){
            vibrate(LEFT_VIBRATE);
        }else if (direction.getDirection().equals(Direction.RIGHT) || direction.getDirection()
                .equals(Direction.SLIGHT_RIGHT)){
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

    public void notifyUser(Direction direction) {
        Log.d(TAG, "notifyUser: " + direction.toString());
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
        long elapsedTime = Math.round((new Date().getTime() - startTime) / 1000);
        String distance = new DecimalFormat("##.#").format(route.getElapsedDistance() / 1000);
        speak("Well done! You ran " + distance + " kilometers in " + elapsedTime + "seconds");
    }
}
