package se.sensorship.view;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import se.sensorship.R;
import se.sensorship.model.LocationService;

public class LocationActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {
    GoogleApiClient googleApiClient;
    boolean updateLocation = true;
    LocationRequest locationRequest;
    private Intent locationServiceIntent;
    private int distance, duration;
    private Boolean audio, vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_vibration_help,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        setContentView(R.layout.activity_location_activity);
        getActionBar().hide();
        Intent intent = getIntent();
        distance = intent.getIntExtra("distance", -1);
        duration = intent.getIntExtra("duration", -1);
        vibration = intent.getBooleanExtra("vibration", true);
        audio = intent.getBooleanExtra("audio", true);
        startLocationService();

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
        buildGoogleApiClient();
        googleApiClient.connect();
        createLocationRequest();

    }

    private void startLocationService() {
        locationServiceIntent = new Intent(this, LocationService.class);
        Bundle extras = new Bundle();
        extras.putInt("distance", distance);
        extras.putInt("duration", duration);
        extras.putBoolean("audio", audio);
        extras.putBoolean("vibration", vibration);
        locationServiceIntent.putExtras(extras);
        startService(locationServiceIntent);
    }


    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle hint) {
        if (updateLocation){
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
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
