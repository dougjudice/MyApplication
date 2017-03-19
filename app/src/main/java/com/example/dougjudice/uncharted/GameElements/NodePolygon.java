package com.example.dougjudice.uncharted.GameElements;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

/**
 * Created by dougjudice on 3/18/17.
 */

public class NodePolygon extends GamePolygon {

    boolean active;     // This determines whether or not the node is currently distributing resources
    int resourceCount;  // This is a counter for the amount of remaining resources when filled


    public NodePolygon(PolygonOptions po, ArrayList<ArrayList<Double>> coordinates, int polyID, String name, GoogleMap map){
        super();
        this.polygonOptions = po;
        this.coordinates = coordinates;
        this.polyID = polyID;
        this.name = name;
        this.polygon = map.addPolygon(po);
    }
}
