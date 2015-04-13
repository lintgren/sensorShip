package se.sensorship;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

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
    TextView tvLongitude, tvLatitude;
    boolean updateLocation = true;
    LocationRequest locationRequest;
    Date lastUpdate;
    public static GoogleMap mMap;
    private LatLng[] turnPoints = {new LatLng(55.703503700000006, 13.2191062), new LatLng(55.7090651,
            13.2399845)};
    private LatLng[] path = {new LatLng(55.705788, 13.211124), new LatLng(55.705788, 13.211124),
            new LatLng(55.705679, 13.211462), new LatLng(55.705466, 13.211608),
            new LatLng(55.705288, 13.21184), new LatLng(55.705123, 13.211989),
            new LatLng(55.704991, 13.212413), new LatLng(55.704893, 13.212749),
            new LatLng(55.704783, 13.213084), new LatLng(55.704715, 13.213445),
            new LatLng(55.704575, 13.21382), new LatLng(55.704437, 13.214153),
            new LatLng(55.704378, 13.214535), new LatLng(55.704334, 13.214774),
            new LatLng(55.704221, 13.215181), new LatLng(55.704219, 13.215598),
            new LatLng(55.704241, 13.21601), new LatLng(55.704258, 13.216279),
            new LatLng(55.704273, 13.216639), new LatLng(55.704291, 13.216922),
            new LatLng(55.704342, 13.21719), new LatLng(55.704395, 13.217446),
            new LatLng(55.704409, 13.217712), new LatLng(55.704341, 13.218133),
            new LatLng(55.70426, 13.218402), new LatLng(55.704086, 13.218649),
            new LatLng(55.703961, 13.218778), new LatLng(55.703833, 13.218862),
            new LatLng(55.703641, 13.219126), new LatLng(55.703627, 13.219362),
            new LatLng(55.70368, 13.219628), new LatLng(55.70373, 13.219957),
            new LatLng(55.703852, 13.220195), new LatLng(55.703937, 13.220519),
            new LatLng(55.703978, 13.22074), new LatLng(55.704069, 13.221149),
            new LatLng(55.704143, 13.221587), new LatLng(55.704185, 13.221851),
            new LatLng(55.704289, 13.222056), new LatLng(55.704399, 13.222326),
            new LatLng(55.704509, 13.222612), new LatLng(55.704599, 13.222806),
            new LatLng(55.704722, 13.223263), new LatLng(55.704846, 13.22336),
            new LatLng(55.704934, 13.223691), new LatLng(55.705011, 13.224032),
            new LatLng(55.705096, 13.224384), new LatLng(55.705119, 13.224947),
            new LatLng(55.705215, 13.22519), new LatLng(55.705329, 13.225496),
            new LatLng(55.70546, 13.225877), new LatLng(55.705532, 13.226284),
            new LatLng(55.705571, 13.226658), new LatLng(55.705651, 13.226996),
            new LatLng(55.705784, 13.227289), new LatLng(55.70591, 13.227652),
            new LatLng(55.706016, 13.227955), new LatLng(55.706092, 13.228379),
            new LatLng(55.706158, 13.228731), new LatLng(55.706174, 13.229157),
            new LatLng(55.706336, 13.229532), new LatLng(55.706356, 13.229785),
            new LatLng(55.706547, 13.230181), new LatLng(55.706619, 13.230568),
            new LatLng(55.706731, 13.230854), new LatLng(55.706795, 13.231359),
            new LatLng(55.706893, 13.231629), new LatLng(55.706959, 13.231931),
            new LatLng(55.707098, 13.232376), new LatLng(55.707183, 13.232651),
            new LatLng(55.70735, 13.233251), new LatLng(55.707385, 13.233546),
            new LatLng(55.707472, 13.233694), new LatLng(55.707603, 13.234096),
            new LatLng(55.70779, 13.234592), new LatLng(55.707835, 13.235088),
            new LatLng(55.707906, 13.235469), new LatLng(55.708031, 13.235781),
            new LatLng(55.708095, 13.236098), new LatLng(55.708188, 13.236433),
            new LatLng(55.708239, 13.236838), new LatLng(55.70835, 13.237085),
            new LatLng(55.708448, 13.237371), new LatLng(55.708549, 13.237776),
            new LatLng(55.708622, 13.238071), new LatLng(55.708715, 13.238496),
            new LatLng(55.708771, 13.238829), new LatLng(55.708849, 13.239173),
            new LatLng(55.708932, 13.239557), new LatLng(55.709007, 13.239691),
            new LatLng(55.709218, 13.239811), new LatLng(55.709371, 13.239708),
            new LatLng(55.709526, 13.239562), new LatLng(55.709788, 13.239275),
            new LatLng(55.710094, 13.238888), new LatLng(55.710235, 13.23864),
            new LatLng(55.710617, 13.238549), new LatLng(55.710859, 13.238448),
            new LatLng(55.710869, 13.238166), new LatLng(55.711114, 13.237861),
            new LatLng(55.711242, 13.237738), new LatLng(55.711408, 13.237572),
            new LatLng(55.711587, 13.237424), new LatLng(55.711794, 13.237189),
            new LatLng(55.711889, 13.237005), new LatLng(55.712058, 13.236721),
            new LatLng(55.712236, 13.236377), new LatLng(55.712369, 13.235995),
            new LatLng(55.712565, 13.235724), new LatLng(55.712672, 13.235532),
            new LatLng(55.712819, 13.235252), new LatLng(55.713019, 13.235161),
            new LatLng(55.713161, 13.235056), new LatLng(55.713345, 13.234791),
            new LatLng(55.713256, 13.234294), new LatLng(55.713224, 13.233925),
            new LatLng(55.713226, 13.233523), new LatLng(55.713241, 13.233237),
            new LatLng(55.713237, 13.23293), new LatLng(55.713223, 13.232666),
            new LatLng(55.713234, 13.232325), new LatLng(55.713243, 13.231962),
            new LatLng(55.713219, 13.231723), new LatLng(55.713225, 13.231285),
            new LatLng(55.713237, 13.230952), new LatLng(55.713199, 13.230688),
            new LatLng(55.713106, 13.230329), new LatLng(55.713006, 13.229824),
            new LatLng(55.712886, 13.229555), new LatLng(55.712754, 13.229155),
            new LatLng(55.712608, 13.228772), new LatLng(55.712444, 13.228426),
            new LatLng(55.712372, 13.227942), new LatLng(55.712308, 13.227656),
            new LatLng(55.712232, 13.227445), new LatLng(55.712235, 13.226982),
            new LatLng(55.712202, 13.226764), new LatLng(55.712174, 13.226474),
            new LatLng(55.712147, 13.226177), new LatLng(55.712296, 13.22587),
            new LatLng(55.712181, 13.225473), new LatLng(55.712025, 13.225008),
            new LatLng(55.711942, 13.224789), new LatLng(55.711782, 13.22436),
            new LatLng(55.711622, 13.224006), new LatLng(55.711494, 13.223859),
            new LatLng(55.711354, 13.223697), new LatLng(55.711129, 13.223601),
            new LatLng(55.710929, 13.223568), new LatLng(55.71075, 13.223284),
            new LatLng(55.710613, 13.22295), new LatLng(55.710373, 13.222856),
            new LatLng(55.710127, 13.222794), new LatLng(55.709823, 13.222705),
            new LatLng(55.709666, 13.222658), new LatLng(55.709436, 13.222723),
            new LatLng(55.709107, 13.222773), new LatLng(55.708894, 13.222708),
            new LatLng(55.708739, 13.222617), new LatLng(55.708668, 13.222094),
            new LatLng(55.708687, 13.221694), new LatLng(55.708548, 13.222048),
            new LatLng(55.708323, 13.22229), new LatLng(55.708081, 13.222283),
            new LatLng(55.707914, 13.222213), new LatLng(55.707697, 13.222138),
            new LatLng(55.70755, 13.221866), new LatLng(55.707468, 13.221538),
            new LatLng(55.707347, 13.221384), new LatLng(55.70723, 13.221262),
            new LatLng(55.707293, 13.221596), new LatLng(55.707393, 13.221859),
            new LatLng(55.707399, 13.222173), new LatLng(55.707209, 13.222232),
            new LatLng(55.707017, 13.22214), new LatLng(55.706851, 13.221838),
            new LatLng(55.70679, 13.22153), new LatLng(55.706613, 13.221246),
            new LatLng(55.706415, 13.221052), new LatLng(55.706082, 13.220946),
            new LatLng(55.705923, 13.220842), new LatLng(55.705737, 13.220786),
            new LatLng(55.705467, 13.220745), new LatLng(55.7053, 13.220683),
            new LatLng(55.705041, 13.220517), new LatLng(55.704788, 13.220468),
            new LatLng(55.704659, 13.22044), new LatLng(55.704498, 13.220426),
            new LatLng(55.704316, 13.220365), new LatLng(55.703957, 13.220337),
            new LatLng(55.703846, 13.220044), new LatLng(55.703674, 13.2197),
            new LatLng(55.703579, 13.21933), new LatLng(55.703635, 13.218957),
            new LatLng(55.703751, 13.218754), new LatLng(55.703936, 13.218487),
            new LatLng(55.704124, 13.218324), new LatLng(55.704228, 13.218146),
            new LatLng(55.704325, 13.217849), new LatLng(55.70427, 13.217566),
            new LatLng(55.704217, 13.217282), new LatLng(55.704269, 13.216974),
            new LatLng(55.704278, 13.216693), new LatLng(55.704194, 13.216264),
            new LatLng(55.704145, 13.216007), new LatLng(55.704122, 13.21565),
            new LatLng(55.704101, 13.215343), new LatLng(55.70418, 13.215059),
            new LatLng(55.704248, 13.214775), new LatLng(55.704364, 13.214351),
            new LatLng(55.704446, 13.214137), new LatLng(55.704446, 13.214137),
            new LatLng(55.704446, 13.214137)};

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

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(path[0], 13));
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
        LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());

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
