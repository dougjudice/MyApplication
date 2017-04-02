package com.example.dougjudice.uncharted;

// Android Imports
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.support.v7.widget.Toolbar;

// My Imports
import com.example.dougjudice.uncharted.DataProcessing.*;
import com.example.dougjudice.uncharted.GameElements.*;

// Maps imports
import com.example.dougjudice.uncharted.SettingsDrawerActivities.CraftingActivity;
import com.example.dougjudice.uncharted.SettingsDrawerActivities.GroupActivity;
import com.example.dougjudice.uncharted.SettingsDrawerActivities.ResourceActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;

// JSON Imports
import org.json.JSONObject;

// Java Imports
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener {

    // For testing purposes only TODO: Remove Later
    ArrayList<String> polyFields = new ArrayList<>();

    // Hashmap that maps every polygon's name to its respective polygon for a quick  reference and lookup
    private HashMap pairPolyMap = new HashMap<>();

    // Hashmap that maps every polygon's name to the actual Node Polygon object
    private HashMap pairNodeMap = new HashMap<>();

    private GoogleApiClient mGoogleApiClient;
    private Timer timer = new Timer();

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;

    Location mLastLocation;       // Contains user last location, updated via listener or otherwise ... analogous to current location most of the time
    String mLastUpdateTime;       // Contains last time of user location update

    boolean mRequestingLocationUpdates = true;
    boolean firstStart = true;
    boolean locationSet = false;
    TextView resourceToolTip;

    boolean inNode;

    private DrawerLayout mDrawerLayout;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;

    private Circle lastUserCircle;
    private long pulseDuration = 1000;
    private ValueAnimator lastPulseAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // DrawerLayout Init
        mTitle = "Test";
        mDrawerTitle = "Test 2";
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Sets up NavigationView (the actual drawer)
        NavigationView navView = (NavigationView) findViewById(R.id.left_drawer);
        if(navView == null){
            System.out.println("Null NavigationView error");
        }
        navView.setNavigationItemSelectedListener(this);

        // Set up ActionBar (thing on the top of the maps_activity)
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);

        // Put the above two together and make them animated/function properly
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Start the constant tick by creating new Timer Thread (Native Android OS call)
        timer.schedule(new MyTimerTask(), 1000, 2000); // Timer set to 2-second interval (?)

        // Autocomplete Fragment (Goes inbetween Hamburger drawer icon & Toolbar Menu)
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        resourceToolTip = (TextView)findViewById(R.id.mine_notification_tip);

        // Autocomplete Fragment options & listeners
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                // Perform actoin on Place object returned
                System.out.println(" @@@@ GOT PLACE : " + place.getName());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17.0f));
                Log.i("tag", "Plaes: " + place.getName());
            }
            @Override
            public void onError(Status status) {
                System.out.println(" @@@@ Places Select Error");
                Log.i("tag2", "An error occurred: " + status);
            }
        });

        // Sets up Location services and enables getting user location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // Creates a new thread that's on a timer set to 2-second quanta
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

    // Creates the actionbar menu and 'inflates' it
    @Override
    public boolean onCreateOptionsMenu(final Menu menu){
        getMenuInflater().inflate(R.menu.tool_menu, menu);
        return true;
    }

    /***
    *
    *   ANDROID APP LIFECYCLE FUNCTIONS
    *   START,STOP,PAUSE,RESUME
     ***/

    protected void onStart(){
        System.out.println("STARTING");

        // needed to enable Location Services
        mGoogleApiClient.connect();
        super.onStart();
    }
    protected void onStop(){
        System.out.println("STOPPING");

        super.onStop();
    }
    @Override
    public void onPause(){
        System.out.println("PAUSING");
        super.onPause();
    }
    @Override
    public void onResume(){

        super.onResume();
        if(firstStart) {
            return;
        }
        System.out.println("RESUMING");
        timer.schedule(new MyTimerTask(), 1000, 2000);
        updateUI();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mGoogleApiClient.disconnect();
        timer.purge();
        timer.cancel();
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

        try{
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));

            if(!success){
                System.out.println("MapStyle parse fail");
            }
        }catch(Resources.NotFoundException e){
            e.printStackTrace();
        }

        // for testing only TODO: Remove when JSON properly implemented to grab all polygons
        polyFields.add("hanselgriddle");
        polyFields.add("olivebranch");
        polyFields.add("oldequeens");

        // Establish Permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }

        // Initialize all polygons
        for(int i = 0; i < polyFields.size(); i++) {
            JSONObject obj = GeoJsonUtil.bootJSON(getApplicationContext(), polyFields.get(i));
            NodePolygon np = GeoJsonUtil.generatePolygon(obj,mMap);
            np.getPolygon().setClickable(true);
            np.setResource(1000);
            pairPolyMap.put(np.getPolygon(),np.getName());
            pairNodeMap.put(np.getName(),np);
            addCustomMarker(np);

        }

        // Whenever a polygon is clicked, gets that polygon and performs action on it
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {

            public void onPolygonClick(Polygon pg){

                // Determine which polygon was clicked
                String key = (String) pairPolyMap.get(pg);
                NodePolygon clickedNode = (NodePolygon) pairNodeMap.get(key);

                // Perform action on NodePolygon
                System.out.println("Last Location: lat: " + mLastLocation.getLatitude() + " long : " + mLastLocation.getLongitude() +" timestamp: " + mLastUpdateTime);
                clickedNode.setPolygon(pg);
                forceLocationUpdate(); // default timethread executed statement

                // Fetches marker object with info contained in NodePolygon object TODO: Include additional info about resources
                clickedNode.getMarker().showInfoWindow();

                Toast t = Toast.makeText(context, "Opening " + clickedNode.getName() + " node...", Toast.LENGTH_SHORT );
                t.show();
            }
        });
    }
    private void addCustomMarker(NodePolygon np){
        if(mMap == null)
            return;

        Marker m = np.getMarker();
        m = mMap.addMarker(new MarkerOptions()
                .position(Utility.centroid(np.getCoordinates()))
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.rarium))) // Need to update this and view_custom_marker
                .anchor(0.5f, 0.5f)
                .title(np.getName()));
        np.setMarker(m);
    }

    // Utility Function for updating Coordinates
    public void updateUI(){
        //System.out.println("Correcting location... ");
        double lat = mLastLocation.getLatitude();
        double longi = mLastLocation.getLongitude();
        LatLng currLoc = new LatLng(lat,longi);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 16.5f));
    }
    // Utility Function for updating Moving Camera (necessary sometimes)
    public void updateUIHard(){
        //System.out.println("Correcting location... ");
        double lat = mLastLocation.getLatitude();
        double longi = mLastLocation.getLongitude();
        LatLng currLoc = new LatLng(lat,longi);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 17.0f));
    }

    // *****                                             *****
    // **                                                   **
    // *                                                     *
    // * GOOGLE PLAY SERVICES LOCATION INFORMATION & METHODS *
    // *                                                     *
    // **                                                   **
    // *****                                             *****

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

    // Linked to ThreadTimer, executes every 2 Seconds
    public void forceLocationUpdate(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission Granted Immediately");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());

            // Use this to check if User is within a polygon. checkAllIntersections returns a String which can act as a key to get the appropriate NodePolygon
            if(mLastLocation == null){
                System.out.println("ERROR: NO USER LOCATION");
                return;
            }
            //locationSet = true;
            if(pairNodeMap != null) {
                if(locationSet == false){
                    updateUIHard();
                    locationSet = true;
                }
                //System.out.println("Checking system");
                String state = Utility.checkAllIntersections(pairNodeMap, mLastLocation);
                System.out.println("PULSING");
                pulseMap();
                //addPulsatingEffect(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
                if (state != null){ // User is in a polygon
                    System.out.println("User is currently within: " + state);
                    NodePolygon overlap = (NodePolygon) pairNodeMap.get(state);
                    if(!inNode){

                        // Set toolTip to color, state
                        resourceToolTip.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.rariumBlue));
                        resourceToolTip.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
                        resourceToolTip.setText("Currently Mining " + state);
                        inNode = true;
                    }
                    overlap.depleteResourcesOnTick();
                    //System.out.println(" ### REMAINING RESOURCES IN " + state + " : " + overlap.getResourceCount());
                }
                else { // User not in any polygon
                    System.out.println("User not in any polygon");

                    // Set toolTip to transparent
                    if(inNode) {
                        resourceToolTip.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
                        resourceToolTip.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
                        resourceToolTip.setText("");
                        inNode = false;
                        // 40.5014
                    }
                }
            }

            updateUI(); // Used to set the map view if needed
        }
    }

    // Initializes location services first connection
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

    // Location changed listener( should fire on update tick normally, else in Emulator will fire when you change the location manually)
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
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        // if nav drawer open, hide action items related to content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    // Process clicked options from menu (three vertical dot) tab on right of ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            System.out.println("Ret True");
            return true;
        }
        if(item.getItemId() == R.id.my_location){
            updateUIHard(); // forces camera to user location
            System.out.println("Clicked Bookmark Menu");
        }

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // Map DrawerLayout items/options to actions, Group Activity shown as an example
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Context c = getApplicationContext();

        if (id == R.id.nav_group) {
            // Start Group Activity
            Toast t = Toast.makeText(c, "Opening Group Activity", Toast.LENGTH_SHORT );
            t.show();
            startActivity(new Intent(this, GroupActivity.class));
            setContentView(R.layout.activity_group);
        } else if (id == R.id.nav_gallery) {
            Toast t = Toast.makeText(c, "Opening My Resources Activity", Toast.LENGTH_SHORT );
            t.show();
            startActivity(new Intent(this, ResourceActivity.class));
            setContentView(R.layout.activity_myresource);

        }
        else if (id == R.id.nav_manage) {
            Toast t = Toast.makeText(c, "Opening Crafting Activity", Toast.LENGTH_SHORT );
            t.show();
            startActivity(new Intent(this, CraftingActivity.class));
            setContentView(R.layout.activity_craft);

        } else if (id == R.id.nav_share) {
            Toast t = Toast.makeText(c, "Opening Nav_Share Activity", Toast.LENGTH_SHORT );
            t.show();

        } else if (id == R.id.nav_send) {
            Toast t = Toast.makeText(c, "Opening Nav_Send Activity", Toast.LENGTH_SHORT );
            t.show();

        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // Marker Animation Functions
    private Bitmap getMarkerBitmapFromView(int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private void addPulsatingEffect(LatLng userLatlng, NodePolygon np){
        if(np.getVm() != null){
            np.getVm().cancel();
            Log.d("onLocationUpdated: ","cancelled" );
        }
        if(np.getCircle() != null)
            np.getCircle().setCenter(userLatlng);

        final LatLng l = userLatlng;
        final NodePolygon nep = np;
        np.setVm(valueAnimate(14f, pulseDuration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(nep.getCircle() != null)
                    nep.getCircle().setRadius((Float) animation.getAnimatedValue());
                else {
                    nep.setCircle(mMap.addCircle(new CircleOptions()
                            .center(new LatLng(l.latitude, l.longitude))
                            .radius((Float) animation.getAnimatedValue())
                            .strokeColor(Color.parseColor("#7f0ffff3"))
                            .fillColor(Color.parseColor("#4f80fcdd"))
                    ));
                }
            }
        }));

    }

    protected ValueAnimator valueAnimate(float accuracy,long duration, ValueAnimator.AnimatorUpdateListener updateListener){
        Log.d( "valueAnimate: ", "called");
        ValueAnimator va = ValueAnimator.ofFloat(0,accuracy);
        va.setDuration(duration);
        va.addUpdateListener(updateListener);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(ValueAnimator.RESTART);

        va.start();
        return va;
    }
    public void pulseMap(){
        //HashMap.Entry<String, NodePolygon> entry = null;

        for(Object key : pairNodeMap.keySet()) {
            NodePolygon np = (NodePolygon) pairNodeMap.get(key);
            System.out.println("PULSING : " + np.getName());
            addPulsatingEffect(np.getMarker().getPosition(), np);
        }
        return;
    }

}
