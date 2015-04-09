package se.sensorship;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.location.LocationServices;
import android.location.Location;

public class LocationActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
    Location lastLocation;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(Bundle hint) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
