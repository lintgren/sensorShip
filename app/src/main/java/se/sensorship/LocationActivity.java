package se.sensorship;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;

public class LocationActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener, OnMapReadyCallback {
    Location deltaLocation,lastLocation;
    GoogleApiClient googleApiClient;
    boolean updateLocation = true;
    LocationRequest locationRequest;
    Date lastUpdate;
    public static GoogleMap mMap;
    private LatLng[] path, longPath;

    private Intent locationServiceIntent;
    private int distance, duration;
    private Boolean audio, vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_activity);

        Intent intent = getIntent();
        distance = intent.getIntExtra("distance", -1);
        duration = intent.getIntExtra("duration", -1);
        vibration = intent.getBooleanExtra("vibration", true);
        audio = intent.getBooleanExtra("audio", true);
        startLocationService();
        if (audio) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (!am.isWiredHeadsetOn()) {
                Toast.makeText(this, "Plug your headphones in your face", Toast.LENGTH_LONG).show();
            } else {
                //favourite code line
                Toast.makeText(this, "Fuck you, you well prepared biatch", Toast.LENGTH_LONG).cancel();
            }
        }

        path = Route.static_path;
        buildGoogleApiClient();
        googleApiClient.connect();
        createLocationRequest();


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void startLocationService() {
        longPath = new LatLng[]{new LatLng(55.714875, 13.211989),
                new LatLng(55.714875, 13.211989),
                new LatLng(55.714732, 13.211869),
                new LatLng(55.714542, 13.211739),
                new LatLng(55.714388, 13.2117),
                new LatLng(55.714225, 13.211577),
                new LatLng(55.714022, 13.211438),
                new LatLng(55.713896, 13.21149),
                new LatLng(55.713857, 13.211782),
                new LatLng(55.713834, 13.21197),
                new LatLng(55.713702, 13.211783),
                new LatLng(55.713634, 13.211431),
                new LatLng(55.713631, 13.211127),
                new LatLng(55.7138, 13.21103),
                new LatLng(55.71403, 13.210915),
                new LatLng(55.714119, 13.210897),
                new LatLng(55.714276, 13.210761),
                new LatLng(55.714453, 13.210563),
                new LatLng(55.714658, 13.210407),
                new LatLng(55.714832, 13.210585),
                new LatLng(55.7149, 13.210711),
                new LatLng(55.715019, 13.210901),
                new LatLng(55.715084, 13.211128),
                new LatLng(55.715027, 13.211471),
                new LatLng(55.714971, 13.211732),
                new LatLng(55.714971, 13.211732)};
        Direction[] longDirections = new Direction[]{new Direction(55.71391, 13.21140,Direction.LEFT),
        new Direction(55.71377, 13.21205, Direction.RIGHT),
        new Direction(55.71371, 13.21205, Direction.RIGHT),
        new Direction(55.713600, 13.2111, Direction.SLIGHT_RIGHT),
        new Direction(55.71464, 13.21040, Direction.SLIGHT_RIGHT),
        new Direction(55.715084, 13.21107, Direction.RIGHT),
        new Direction(55.714971, 13.211732, Direction.GOAL)};

        new Route(longDirections,longPath);

        new Route(new Direction[]{new Direction(55.715079, 13.211094, Direction.RIGHT),
                new Direction(55.715396, 13.212231, Direction.GOAL),},
                new LatLng[]{new LatLng(55.714879, 13.211993), new LatLng(55.714879, 13.211993),
                        new LatLng(55.714927, 13.211742), new LatLng(55.714972, 13.21146),
                        new LatLng(55.715019, 13.211201), new LatLng(55.715036, 13.211121),
                        new LatLng(55.715196, 13.211357), new LatLng(55.715299, 13.211585),
                        new LatLng(55.71538, 13.21185), new LatLng(55.715421, 13.212169)});

        locationServiceIntent = new Intent(this, LocationService.class);
        Bundle extras = new Bundle();
        extras.putInt("distance", distance);
        extras.putInt("duration", duration);
        extras.putBoolean("audio", audio);
        extras.putBoolean("vibration", vibration);
        locationServiceIntent.putExtras(extras);
        startService(locationServiceIntent);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.setMyLocationEnabled(true);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(path[0], 16));
        PolylineOptions polylineOptions = new PolylineOptions().geodesic(true);
        for (LatLng location : path){
            polylineOptions.add(location);
        }
        mMap.addPolyline(polylineOptions);
        for(LatLng location : Route.getLocationOnTurns()) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location);
            if(LocationActivity.mMap != null)
                LocationActivity.mMap.addMarker(markerOptions);
        }

    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle hint) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null){

        }
        if (updateLocation){
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println(connectionResult);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        lastUpdate = new Date();
        LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());

        updateUI();

    }

    private void updateUI() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected() && !updateLocation){
            startLocationUpdates();
        }
    }

    private void stopLocationUpdate() {
        updateLocation = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    public void killService(View v) {
        stopService(locationServiceIntent);
        finish();
    }

}
