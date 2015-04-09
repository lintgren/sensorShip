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
import android.location.Location;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

public class LocationActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    Location lastLocation;
    GoogleApiClient googleApiClient;
    TextView tvLongitude, tvLatitude;
    boolean updateLocation = true;
    LocationRequest locationRequest;
    String lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_activity);
        buildGoogleApiClient();
        googleApiClient.connect();
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        createLocationRequest();
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    protected void createLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle hint) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(lastLocation != null){
            tvLongitude.setText(String.valueOf(lastLocation.getLongitude()));
            tvLatitude.setText(String.valueOf(lastLocation.getLatitude()));
        }
        if(updateLocation){
            startLocationUpdates();
        }
    }
    protected void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, this);
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
        lastLocation =location;
        lastUpdate = DateFormat.getTimeInstance().format(new Date());
        updateUI();

    }

    private void updateUI() {
        tvLatitude.setText(String.valueOf(lastLocation.getLatitude()));
        tvLongitude.setText(String.valueOf(lastLocation.getLongitude()));
    }
    @Override
    protected void onPause(){
        super.onPause();
        stopLocationUpdate();
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

}
