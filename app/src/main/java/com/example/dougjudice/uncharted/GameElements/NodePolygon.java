package com.example.dougjudice.uncharted.GameElements;

import android.animation.ValueAnimator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
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

    Marker marker;
    ValueAnimator vm;
    Circle c;

    //ArrayList<User> miningUsers; // TODO

    public NodePolygon(PolygonOptions po, ArrayList<ArrayList<Double>> coordinates, int polyID, String name, GoogleMap map){
        super();
        this.polygonOptions = po;
        this.coordinates = coordinates;
        this.polyID = polyID;
        this.name = name;
        this.polygon = map.addPolygon(po);
        this.resourceType = "Rareium";

        this.activeMiners = 1;
        this.marker = null;
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

    public String depleteResourcesOnTick(){
        int miners = this.activeMiners;

        System.out.println("Attempting to deplete resources");
        if(this.resourceCount <= 0){
            this.active = false;
            this.resourceCount = 0;
            return "DEPLETED";
        }
        else{

            int mineRate = (this.activeMiners); // TODO: Make more complicated
            this.resourceCount = this.resourceCount - mineRate;
            System.out.println("Resources depleted! Remaining: " + this.resourceCount);
            this.marker.setSnippet(this.resourceType + ": x" +this.resourceCount); // Changes InfoWindow resource count to reflect actual sum
        }
        return "EXCEPTION";
    }

    //TODO this jawn
    public void postMiningStatusToServer(){}



}
