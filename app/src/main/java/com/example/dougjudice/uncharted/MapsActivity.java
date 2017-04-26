package com.example.dougjudice.uncharted;

// Android Imports
import android.Manifest;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dougjudice.uncharted.DataProcessing.GeoJsonUtil;
import com.example.dougjudice.uncharted.GameElements.NodePolygon;
import com.example.dougjudice.uncharted.SettingsDrawerActivities.AboutActivity;
import com.example.dougjudice.uncharted.SettingsDrawerActivities.CraftingActivity;
import com.example.dougjudice.uncharted.SettingsDrawerActivities.GroupActivity;
import com.example.dougjudice.uncharted.SettingsDrawerActivities.LeaderboardActivity;
import com.example.dougjudice.uncharted.SettingsDrawerActivities.ResourceActivity;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

// My Imports
// Maps imports
// JSON Imports
// Java Imports

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener, ServiceCallback {

    // Hashmap that maps every polygon's name to its respective polygon for a quick  reference and lookup
    private HashMap pairPolyMap = new HashMap<>();

    // Hashmap that maps every polygon's name to the actual Node Polygon object
    private HashMap pairNodeMap = new HashMap<>();

    private GoogleApiClient mGoogleApiClient;
    private Timer timer = new Timer();
    boolean timerOn = false;

    int counter;

    boolean itemFaded = false;
    boolean canExpire = false; // Used for showing the toast 'Item Expired!'

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;

    Location mLastLocation;       // Contains user last location, updated via listener or otherwise ... analogous to current location most of the time
    String mLastUpdateTime;       // Contains last time of user location update

    boolean mRequestingLocationUpdates = true;
    boolean firstStart = true;
    boolean pulseStart = false;
    boolean locationSet = false;
    TextView resourceToolTip;
    ImageView inUseItem;

    boolean placesJsonInit = false;
    String placesJson;

    boolean inNode;

    LatLng NewBrunswick = new LatLng(40.5031574, -74.451819);

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

        // Start Service
        /*
        if(PulseService.running == false) {
            startService(new Intent(this, PulseService.class));
            registerReceiver(broadcastReceiver, new IntentFilter(
                    PulseService.BROADCAST_ACTION));
            PulseService.running = true;
        */
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

        View header = navView.getHeaderView(0);

        ImageView profilePic = (ImageView) header.findViewById(R.id.profilePic);
        profilePic.setImageBitmap(UserProfile.getProfile().getPicture());

        TextView nameField = (TextView) header.findViewById(R.id.name_field);
        nameField.setText(UserProfile.getProfile().getName());

        // Save placesJson through onCreate to avoid destruction of intent state
        if(!placesJsonInit) {
            placesJson = getIntent().getStringExtra("placesJson");
            if(placesJson == null); // Don't do anything if placesJson is null, it should already be initialized and set in the internal memory for the devices!
            else{
                Utility.storeFile("placesJson",placesJson,this);
                placesJsonInit = true;
            }
        }
        //System.out.println(placesJson);

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

        // Set up ActionBar options
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Autocomplete Fragment (Goes inbetween Hamburger drawer icon & Toolbar Menu)
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        resourceToolTip = (TextView)findViewById(R.id.mine_notification_tip);
        inUseItem = (ImageView) findViewById(R.id.active_item_img);

        // Autocomplete Fragment options & listeners
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                // Perform actoin on Place object returned
                System.out.println(" @@@@ GOT PLACE : " + place.getName());

                LatLng currLoc = place.getLatLng();
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(currLoc,19.0f);
                mMap.animateCamera(location);

                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17.0f));
                //Log.i("tag", "Plaes: " + place.getName());
            }
            @Override
            public void onError(Status status) {
                System.out.println(" @@@@ Places Select Error");
                //Log.i("tag2", "An error occurred: " + status);
            }
        });

        // Sets up Location services and enables getting user location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    // For Service thread
    PulseService mService;
    boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            // Bound to LocalService, cast the IBinder and get LocalService instance
            System.out.println("On Service Connected called");
            PulseService.LocalBinder binder = (PulseService.LocalBinder) service;
            mService = binder.getService();
            mService.forceStartTimer();
            mBound = true;
            mService.setCallback(MapsActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


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
        System.out.println("Service is: " + mBound);
        if(!mBound) {
            Intent intent = new Intent(this, PulseService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
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
        updateUI();
    }
    @Override
    protected void onDestroy(){
        System.out.println("DESTROYING");
        super.onDestroy();

        // When there's no activity in the activity stack, and you press back, onDestroy is called. Need to make sure service is unbound to prevent ServiceCallback leak exception
        PulseService.cancelTimer();
        unbindService(mConnection); // Stop service from running in background
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

        // Add a marker in NB and move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NewBrunswick, 16.0f)); // max zoom is 21.0f

        try{
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
            if(!success){
                System.out.println("MapStyle parse fail");
            }
        }catch(Resources.NotFoundException e){
            e.printStackTrace();
        }

        // Establish Permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }

        //String placesJson = getIntent().getStringExtra("placesJson");
        placesJson = Utility.fetchFile("placesJson", this);
        try {
            JSONArray placesJsonArray = new JSONArray(placesJson);
            for (int i=0; i<placesJson.length(); i++) {
                JSONObject polyJsonObj = placesJsonArray.getJSONObject(i);
                NodePolygon np = GeoJsonUtil.generatePolygon(polyJsonObj,mMap);
                np.getPolygon().setClickable(true);
                np.setResource(1000); // TODO: Get from server
                pairPolyMap.put(np.getPolygon(),np.getName());
                pairNodeMap.put(np.getName(),np);
                addCustomMarker(np);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Disables the Google Maps redirect button in bottom right from appearing when you click a marker
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Whenever a polygon is clicked, gets that polygon and performs action on it
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {

            public void onPolygonClick(Polygon pg){

                // Determine which polygon was clicked
                String key = (String) pairPolyMap.get(pg);
                NodePolygon clickedNode = (NodePolygon) pairNodeMap.get(key);

                // Perform action on NodePolygon
                System.out.println("Last Location: lat: " + mLastLocation.getLatitude() + " long : " + mLastLocation.getLongitude() +" timestamp: " + mLastUpdateTime);
                clickedNode.setPolygon(pg);
                //forceLocationUpdate(); // default timethread executed statement

                // Fetches marker object with info contained in NodePolygon object TODO: Include additional info about resources, player count, expiration timer
                clickedNode.getMarker().showInfoWindow();

                Toast t = Toast.makeText(context, "Opening " + clickedNode.getName() + " node...", Toast.LENGTH_SHORT );
                t.show();
            }
        });
    }
    private void addCustomMarker(NodePolygon np){
        if(mMap == null)
            return;


        np.addMarkerInfo(1, R.drawable.rarium, mMap, this);
        /*
        Marker m = np.getMarker();
        m = mMap.addMarker(new MarkerOptions()
                .position(Utility.centroid(np.getCoordinates()))
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.rarium))) // Need to update this and view_custom_marker
                .anchor(0.5f, 0.5f)
                .snippet(""+np.getResourceCount())
                .title(np.getName()));
        np.setMarker(m);
        */
    }

    // Utility Function for updating Coordinates
    public void updateUI(){
        double lat = mLastLocation.getLatitude();
        double longi = mLastLocation.getLongitude();
        LatLng currLoc = new LatLng(lat,longi);

    }
    // Utility Function for updating Moving Camera (necessary sometimes)
    public void updateUIHard(){
        //System.out.println("Correcting location... ");
        double lat = mLastLocation.getLatitude();
        double longi = mLastLocation.getLongitude();
        LatLng currLoc = new LatLng(lat,longi);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(currLoc,19.0f);
        mMap.animateCamera(location);
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

    // TODO: Delete this
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            System.out.println("Broadcast Received");
            //forceLocationUpdate(intent);
        }
    };

    // Linked to PulseService, executes every 5 Seconds
    public void forceLocationUpdate(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission Granted Immediately");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());

            if(!pulseStart && mLastLocation != null){
                pulseStart = true;
                Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.fade_out);
                ImageView splash = (ImageView) findViewById(R.id.load_splash);
                splash.startAnimation(animFadeOut);
            }
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

                // See if item is expired
                Long time = System.currentTimeMillis();
                SharedPreferences sp = getSharedPreferences("ItemPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                Long expTime = sp.getLong("itemExpTime", 0L);
                if(time >= expTime){
                    editor.putBoolean("itemInUse",false);
                    editor.putInt("itemReturnId",-1);
                    editor.commit();
                }

                //System.out.println("Checking system");
                String state = Utility.checkAllIntersections(pairNodeMap, mLastLocation);
                System.out.println("PULSING");
                pulseMap();
                counter++;
                updateItem();

                // Color the tool tip that appears below the toolbar
                if (state != null){ // User is in a polygon
                    System.out.println("User is currently within: " + state);
                    NodePolygon overlap = (NodePolygon) pairNodeMap.get(state);
                    if(!inNode){

                        Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down);

                        // Set toolTip to color, state
                        resourceToolTip.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.rariumBlue));
                        resourceToolTip.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
                        resourceToolTip.setText("Currently Mining " + state); // TODO: Show mining rate here also
                        resourceToolTip.startAnimation(animSlideDown);
                        inNode = true;
                    }
                    overlap.depleteResourcesOnTick();
                    //System.out.println(" ### REMAINING RESOURCES IN " + state + " : " + overlap.getResourceCount());
                }
                else { // User not in any polygon
                    System.out.println("User not in any polygon");

                    // Set toolTip to transparent
                    if(inNode) {

                        Animation animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up);
                        //resourceToolTip.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
                        //resourceToolTip.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
                        //resourceToolTip.setText("");
                        resourceToolTip.startAnimation(animSlideUp);
                        inNode = false;
                        // 40.5014
                    }
                }
            }
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
        if(item.getItemId() == R.id.my_location && mLastLocation != null){
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
            startActivity(new Intent(this, GroupActivity.class));
            //setContentView(R.layout.activity_group);
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this, ResourceActivity.class));
            //setContentView(R.layout.activity_myresource);

        }
        else if (id == R.id.nav_manage) {
            startActivity(new Intent(this, CraftingActivity.class));
            //setContentView(R.layout.activity_craft);

        } else if (id == R.id.nav_share) {
            startActivity(new Intent(this, LeaderboardActivity.class));

        } else if (id == R.id.nav_send) {
            Toast t = Toast.makeText(c, "Opening About Activity", Toast.LENGTH_SHORT );
            t.show();
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.logout){

            final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setMessage("Are you sure you want to log out?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            Toast.makeText(MapsActivity.this, "Successfully logged out of CrowdForce", Toast.LENGTH_SHORT).show();

                            LoginManager.getInstance().logOut();

                            //stopService(new Intent(this, PulseService.class));
                            startActivity(new Intent(getBaseContext(), MainActivity.class));
                            finish();
                            dialog.cancel();
                        }
                    });
            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            //setContentView(R.layout.activity_main);
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void addPulsatingEffect(LatLng userLatlng, NodePolygon np){
        if(np.getVm() != null){
            np.getVm().cancel();
            //Log.d("onLocationUpdated: ","cancelled" );
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
        //Log.d( "valueAnimate: ", "called");
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
            //System.out.println("PULSING : " + np.getName());
            addPulsatingEffect(np.getMarker().getPosition(), np);
        }
        return;
    }

    // Updates UI to reflect item is in use
    public void updateItem(){
        SharedPreferences sp = getSharedPreferences("ItemPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        //TODO: Testing, remove this later when item expiration info is moved to server
        /*
        if(counter > 20){
            editor.putBoolean("itemInUse",false);
            editor.putInt("itemReturnId",-1);
            editor.commit();
        }
        */

        boolean itemInUse = sp.getBoolean("itemInUse",false);

        System.out.println("Counter : " + counter + " itemInUse =  " + itemInUse);
        if(itemInUse){
            final int itemId = sp.getInt("itemReturnId",-1);
            if(itemId < 0){
                System.out.println("ITEM_NOT_FOUND_ERROR");
                return;
            }
            Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.fade_in);
            inUseItem.setImageResource(Utility.getItemImageSource(itemId)); // change item pic
            inUseItem.setAnimation(animFadeIn);

            Long l = sp.getLong("itemExpTime",0L);
            Long time = System.currentTimeMillis();
            final int sec = (int) (l - time) / 1000;

            inUseItem.setClickable(true);
            inUseItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MapsActivity.this, Utility.getItemNameById(itemId) + " has " + sec + " seconds remaining.", Toast.LENGTH_SHORT).show();
                }
            });

            itemFaded = false;
            canExpire = true;
        }
        else if(!itemFaded && !itemInUse){
            Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.fade_out);
            inUseItem.startAnimation(animFadeOut);
            itemFaded = true;
            inUseItem.setClickable(false);
            if(canExpire) {
                Toast.makeText(MapsActivity.this, "Item Expired!", Toast.LENGTH_SHORT).show();
                canExpire = false;
            }
        }
        else{
            return;
        }

    }

}
