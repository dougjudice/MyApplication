package com.example.dougjudice.uncharted.GameElements;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

/**
 * Created by dougjudice on 3/25/17.
 */

public class BasePolygon extends GamePolygon {

    boolean underAttack;
    int attackers;

    public BasePolygon(PolygonOptions po, ArrayList<ArrayList<Double>> coordinates, int polyID, String name, GoogleMap map){
        super();
        this.polygonOptions = po;
        this.coordinates = coordinates;
        this.polyID = polyID;
        this.name = name;
        this.polygon = map.addPolygon(po);
        //this.miningUsers = new ArrayList<User>();
    }



}
