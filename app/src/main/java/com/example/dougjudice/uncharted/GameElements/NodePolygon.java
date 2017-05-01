package com.example.dougjudice.uncharted.GameElements;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dougjudice.uncharted.R;
import com.example.dougjudice.uncharted.UserProfile;
import com.example.dougjudice.uncharted.Utility;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

/**
 * Created by dougjudice on 3/18/17.
 */

public class NodePolygon extends GamePolygon {

    boolean active;     // This determines whether or not the node is currently distributing resources
    int resourceCount;  // This is a counter for the amount of remaining resources when filled
    String resourceType;// Denotes the name of the resource within the node. 'NONE' when there is none.
    int activeMiners;   // Denotes number of users mining a node
    int remainingTicks; // Denotes how many game-ticks are left before resource disappears
    int take;

    Marker marker;
    ValueAnimator vm;
    Circle c;

    public NodePolygon(PolygonOptions po, ArrayList<ArrayList<Double>> coordinates, int polyID, String name, GoogleMap map){
        super();
        this.polygonOptions = po;
        this.coordinates = coordinates;
        this.polyID = polyID;
        this.name = name;
        this.polygon = map.addPolygon(po);
        this.resourceType = generateRandom();

        this.activeMiners = 1; //TODO: change
        this.marker = null;
    }

    public String generateRandom(){
        double r = Math.random();
        System.out.println("r: " + r);
        if(r >= 0 && r <= .2){
            return "Legendgem";
        }
        else if(r > .2 && r <= .55){
            return "Rareium";
        }
        else{
            return "Commonite";
        }
    }

    public ValueAnimator getVm(){
        return this.vm;
    }
    public Circle getCircle(){
        return this.c;
    }
    public void setVm(ValueAnimator vm){
        this.vm = vm;
    }
    public void setCircle(Circle c){
        this.c = c;
    }

    // Sets up necessary information to show node is ready to be mined
    // Should be initiated by Server
    public void setResource(int num1){
        this.resourceCount = num1;
        this.remainingTicks = 1000; // 1000 * 2 second ticks = 2000 second uptime
        this.active = true;
    }

    public Marker getMarker(){
        return this.marker;
    }
    public void setMarker(Marker m){
        this.marker = m;
    }

    public int getResourceCount(){

        return this.resourceCount;
    }

    public String getResourceType(){
        return this.resourceType;
    }

    public int getActiveMiners(){
        // TODO: From server
        int miners = 1;
        return miners;
    }
    public int getTake(){
        return this.take;
    }

    /**
     * Sends an integer to the server with how much of a resource this user mined over one server tick (or series of user ticks)
     * Returns a status string
     *
     * @return
     */
    public String depleteResourcesOnTick(){

        System.out.println("Attempting to deplete resources");
        if(this.resourceCount <= 0){
            this.active = false;
            this.resourceCount = 0;
            return "NO_NODE";
        }
        else{
            int takeaway = (int) Math.floor(getMineralHardness() * computeMineRate());
            this.take = takeaway;
            // TODO: Send takeaway to server, deplete node by this amount, and return takeaway to user

            System.out.println("Resources depleted! Remaining: " + this.resourceCount);
            this.resourceCount = this.resourceCount -takeaway;

            String serverResponse = "";
            if(serverResponse.equals("SUCCESS")){
                int[] loot = new int[] {0,0,0};
                switch(this.resourceType){
                    case "Commonite": loot[0] = takeaway;
                    case "Rareium": loot[1] = takeaway;
                    case "Legendgem": loot[2] = takeaway;
                }
                UserProfile.getProfile().updateUserMaterials(loot);
            }

            this.marker.setSnippet(this.resourceType + ": x" +this.resourceCount); // Changes InfoWindow resource count to reflect actual sum
        }
        return "EXCEPTION";
    }

    public void addMarkerInfo(int miners, Integer imgSrc, GoogleMap mMap, Context c){
        if(mMap == null)
            return;

        Marker m = this.getMarker();
        m = mMap.addMarker(new MarkerOptions()
                .position(Utility.centroid(this.getCoordinates()))
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(imgSrc, miners, c))) // Need to update this and view_custom_marker
                .anchor(0.5f, 0.5f)
                .snippet(""+getResourceType()+ ": x"+getResourceCount())
                .title(this.getName()));
        this.setMarker(m);
    }

    // Use to refresh the number of users here
    public void updateMarkerInfo(int miners, Integer imgSrc, Context c){

        this.marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(imgSrc, miners, c)));
        this.marker.setSnippet(this.resourceType + ": x" + this.resourceCount);
        //m.set
    }

    private Bitmap getMarkerBitmapFromView(int resId, int miners, Context c) {

        View customMarkerView = ((LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        TextView textView = (TextView) customMarkerView.findViewById(R.id.miner_count);

        if(miners > 0 && miners <= 9){
            textView.setText(""+miners);
        }
        else if(miners > 9){
            textView.setText("9+");
        }

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

    /**
     * Algorithm to compute a user's mining rate when they are on a node
     * Requires server to get the number of jammers in a node not in your group, and your group's items
     * Returns double that has the mine rate as %
     *
     * @return
     */
    public double computeMineRate(){

        int ATTACK = 100; int DEFENSE = 100;

        // in order: COMMON/RARE/LEGENDARY
        int[] JAM_COUNT = new int[] {0,0,0}; // TODO: GET NUMBER OF ENEMY JAMMERS IN SAME NODE
        int[] BAR_COUNT = new int[] {0,0,0}; // TODO: GET NUMBER OF GROUP BARRIERS IN SAME NODE
        int[] DRN_COUNT = new int[] {0,0,0}; // TODO: GET NUMBER OF GROUP DRONES IN SAME NODE

        int COMPANIONS = 0; // TODO: GET NUMBER OF GROUP MEMBERS IN SAME NODE;

        if(JAM_COUNT[2] > 0){
            // Defense lowered by legendary jammer
            DEFENSE = DEFENSE - JAM_COUNT[2]*15;
        }

        // Compute Defense
        for(int i = 0; i < BAR_COUNT.length; i++){
            switch(i){
                case 0: DEFENSE=DEFENSE + (BAR_COUNT[i] * 5);
                case 1: DEFENSE=DEFENSE + (BAR_COUNT[i] * 10) + (COMPANIONS * 2);
                case 2: DEFENSE=DEFENSE + (BAR_COUNT[i] * 15) + (COMPANIONS * 5);
                default: break;
            }
        }

        double DEF_SCORE = DEFENSE / 120;

        for(int i = 0; i < JAM_COUNT.length; i++){
            switch(i){
                case 0: ATTACK=ATTACK - (int)Math.floor(((JAM_COUNT[i] * 5)) * DEF_SCORE) + (DRN_COUNT[i] * 7);
                case 1: ATTACK=ATTACK - (int)Math.floor(((JAM_COUNT[i] * 10)) * DEF_SCORE) + (DRN_COUNT[i] * 12);
                case 2: ATTACK=ATTACK - (int)Math.floor(((JAM_COUNT[i] * 15)) * DEF_SCORE) + (DRN_COUNT[i] * 16) + (COMPANIONS * 2);
                default: break;
            }
        }

        return ATTACK / 100;
    }

    public int getMineralHardness(){
        switch(this.resourceType){
            case "Commonite": return 15;
            case "Rareium": return 12;
            case "Legendgem": return 10;
            default: return 1; // should never happen
        }
    }
}
