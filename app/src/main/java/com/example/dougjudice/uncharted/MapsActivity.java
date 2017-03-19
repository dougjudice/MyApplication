package com.example.dougjudice.uncharted;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import android.Manifest;


//import com.example.dougjudice.uncharted.R;
import com.example.dougjudice.uncharted.DataProcessing.*;
import com.example.dougjudice.uncharted.GameElements.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // For testing purposes only:
    ArrayList<String> polyFields = new ArrayList<>();


    // Hashmap that maps every polygon's name to its respective polygon for a quick  reference and lookup
    private HashMap pairPolyMap = new HashMap<>();

    // Hashmap that maps every polygon's name to the actual Node Polygon object
    private HashMap pairNodeMap = new HashMap<>();

    private GoogleApiClient mGoogleApiClient;
    private Timer timer = new Timer();

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;

    Location mLastLocation;  // Contains user last location, updated via listener or otherwise ... analogous to current location most of the time
    String mLastUpdateTime;       // Contains last time of user location update

    boolean mRequestingLocationUpdates = true;


    // probably will need to be fixed later on ?
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == MY_LOCATION_REQUEST_CODE){
// TODO
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        timer.schedule(new MyTimerTask(), 1000, 2000); // Timer set to 2-second interval (?)

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    // HANDLES LOCATION AUTO-UPDATE LOGIC
    private class MyTimerTask extends TimerTask implements Runnable {

        @Override
        public void run(){
            runOnUiThread(new Runnable() {
                @Override
                public void run(){
                    forceLocationUpdate();
                }
            });
        }
    }

    private Handler timerHandler = new Handler(){
        public void cycle(){

        }
    };

    protected void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
    }
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final Context context = getApplicationContext();
        mMap = googleMap;

        // for testing only
        polyFields.add("hanselgriddle");
        polyFields.add("olivebranch");
        polyFields.add("oldequeens");

        // Add a marker in NB and move the camera
        LatLng NewBrunswick = new LatLng(40.5031574, -74.451819);
        mMap.addMarker(new MarkerOptions().position(NewBrunswick).title("Marker in New Brunswick"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NewBrunswick, 16.0f)); // max zoom is 21.0f

        // Establish Permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }

        for(int i = 0; i < polyFields.size(); i++) {
            JSONObject obj = GeoJsonUtil.bootJSON(getApplicationContext(), polyFields.get(i));
            NodePolygon np = GeoJsonUtil.generatePolygon(obj,mMap);
            np.getPolygon().setClickable(true);
            pairPolyMap.put(np.getPolygon(),np.getName());
            pairNodeMap.put(np.getName(),np);
        }

        //NodePolygon np = GeoJsonUtil.generatePolygon(obj, mMap);

        System.out.println("Mapping...");
        //Polygon polygon = mMap.addPolygon(np.getPolygonOptions());

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {

            public void onPolygonClick(Polygon pg){

                // Determine which polygon was clicked
                String key = (String) pairPolyMap.get(pg);
                NodePolygon clickedNode = (NodePolygon) pairNodeMap.get(key);

                // Perform action on NodePolygon
                System.out.println("Last Location: lat: " + mLastLocation.getLatitude() + " long : " + mLastLocation.getLongitude() +" timestamp: " + mLastUpdateTime);
                pg.setFillColor(Color.YELLOW);
                clickedNode.setPolygon(pg);
                forceLocationUpdate();
                Toast t = Toast.makeText(context, "Opening Olive Branch Location...", Toast.LENGTH_SHORT );
                t.show();
            }
        });
    }

    // Utility Function for updating camera
    public void updateUI(){
        System.out.println("Correcting location... ");
        double lat = mLastLocation.getLatitude();
        double longi = mLastLocation.getLongitude();
        LatLng currLoc = new LatLng(lat,longi);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 16.5f));
    }

    // *****                                            *****
    // **                                                  **
    // *                                                    *
    // GOOGLE PLAY SERVICES LOCATION INFORMATION & METHODS  *
    // *                                                    *
    // **                                                  **
    // *****                                            *****

    // Initializes automatic location services
    protected void startLocationUpdates(){
        System.out.println("# Starting Location Updates #");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.create();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission Granted Immediately");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            System.out.println("Requesting Permisison");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
    }

    // USE THIS FOR FORCING LOCATION UPDATE
    public void forceLocationUpdate(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission Granted Immediately");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());
            updateUI();
        }
    }

    // Initialzes location services first connection
    @Override
    public void onConnected(Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission Granted Immediately");
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            System.out.println("Requesting Permisison");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
        if(mRequestingLocationUpdates){
            startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location){
        System.out.println("$ Location Changed $");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission Granted Immediately");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());
            updateUI();
        } else {
            System.out.println("Requesting Permisison");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }




    // These don't need to be implemented but need to stay for the sake of the referenced interfaces
    @Override
    public void onConnectionSuspended(int i ) {
        //Toast t = Toast.makeText(context, "Google Play Services suspended, location services will be disabled...", Toast.LENGTH_LONG);
        //t.show();
        System.out.println("onConnectionSuspended triggered " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast t = Toast.makeText(context, "Unable to connect to Google Play Services, Location services will be disabled...", Toast.LENGTH_LONG);
        //t.show();
        System.out.println("onConnectionFailed triggered");
    }
}
