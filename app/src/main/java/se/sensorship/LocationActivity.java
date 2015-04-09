package se.sensorship;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

public class LocationActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener, OnMapReadyCallback {
    Location deltaLocation,lastLocation;
    GoogleApiClient googleApiClient;
    TextView tvLongitude, tvLatitude;
    boolean updateLocation = true;
    LocationRequest locationRequest;
    Date lastUpdate;
    private GoogleMap mMap;

    private LatLng[] rutt2 = {new LatLng(55.7135923,13.2111561), new LatLng(55.7153994,13.2122505), new LatLng(55.7155081, 13.2143104), new LatLng(55.7130785,13.214052900000002), new LatLng(55.7128791,13.2130229),new LatLng(55.7135923,13.2111561)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_activity);
        buildGoogleApiClient();
        googleApiClient.connect();
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        createLocationRequest();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.setMyLocationEnabled(true);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(rutt2[0], 13));
        PolylineOptions polylineOptions = new PolylineOptions().geodesic(true);
        for(LatLng location : rutt2){
            polylineOptions.add(location);
        }
        mMap.addPolyline(polylineOptions);
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
            tvLongitude.setText(String.valueOf(lastLocation.getLongitude()));
            tvLatitude.setText(String.valueOf(lastLocation.getLatitude()));
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
        updateUI();

    }

    private void updateUI() {
        tvLatitude.setText(String.valueOf(lastLocation.getLatitude()));
        tvLongitude.setText(String.valueOf(lastLocation.getLongitude()));
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

}
