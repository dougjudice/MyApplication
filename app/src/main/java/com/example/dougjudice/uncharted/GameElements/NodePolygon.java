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


    public double computeMineRate(){
        double result = 1.0;

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
        System.out.println("DEFENSE: " + DEFENSE);

        double DEF_SCORE = DEFENSE / 120;

        for(int i = 0; i < JAM_COUNT.length; i++){
            switch(i){
                case 0: ATTACK=ATTACK - (int)Math.floor(((JAM_COUNT[i] * 5)) * DEF_SCORE) + (DRN_COUNT[i] * 7);
                case 1: ATTACK=ATTACK - (int)Math.floor(((JAM_COUNT[i] * 10)) * DEF_SCORE) + (DRN_COUNT[i] * 12);
                case 2: ATTACK=ATTACK - (int)Math.floor(((JAM_COUNT[i] * 15)) * DEF_SCORE) + (DRN_COUNT[i] * 16) + (COMPANIONS * 2);
                default: break;
            }
        }
        System.out.println("ATTACK: " + ATTACK);

        result = ATTACK / 100;
        return result;
    }

}
