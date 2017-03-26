package com.example.dougjudice.uncharted.GameElements;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.example.dougjudice.uncharted.Utility;
import com.example.dougjudice.uncharted.ResourceType;

import java.util.ArrayList;

/**
 * Created by dougjudice on 3/18/17.
 */

public class NodePolygon extends GamePolygon {

    boolean active;     // This determines whether or not the node is currently distributing resources
    int resourceCount;  // This is a counter for the amount of remaining resources when filled
    int activeMiners;   // Denotes number of users mining a node
    int remainingTicks; // Denotes how many game-ticks are left before resource disappears
    Marker marker;
    //ArrayList<User> miningUsers; // TODO

    public NodePolygon(PolygonOptions po, ArrayList<ArrayList<Double>> coordinates, int polyID, String name, GoogleMap map){
        super();
        this.polygonOptions = po;
        this.coordinates = coordinates;
        this.polyID = polyID;
        this.name = name;
        this.polygon = map.addPolygon(po);
        //this.miningUsers = new ArrayList<User>();
        this.activeMiners = 1;
        this.marker = map.addMarker(new MarkerOptions()
                .position(Utility.centroid(coordinates))
                .title(name));
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

    public int getResourceCount(){
        return this.resourceCount;
    }

    public String depleteResourcesOnTick(){
        int miners = this.activeMiners;
        //String status = "";
        System.out.println("Attempting to deplete resources");
        if(this.resourceCount <= 0){
            this.active = false;
            this.resourceCount = 0;
            return "DEPLETED";
        }
        else{
            System.out.println("Resources depleted!");
            int mineRate = (this.activeMiners); // TODO: Make more complicated
            this.resourceCount = this.resourceCount - mineRate;
        }
        return "EXCEPTION";
    }

    public void postMiningStatusToServer(){}

}
