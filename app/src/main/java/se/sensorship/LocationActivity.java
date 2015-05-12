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
    private LatLng[] path;

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
