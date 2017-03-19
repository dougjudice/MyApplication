package com.example.dougjudice.uncharted.GameElements;

/**
 * Created by dougjudice on 3/18/17.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dougjudice.uncharted.DataProcessing.*;

import com.example.dougjudice.uncharted.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.geometry.Point;

import java.util.ArrayList;

public abstract class GamePolygon {

    ArrayList<ArrayList<Double>> coordinates;
    PolygonOptions polygonOptions;
    Polygon polygon;
    String name;
    int polyID;

    // based on Ray-Casting see link in Utility
    public boolean isPointInPolygon(ArrayList<Double> coordinates){
        if(Utility.pointInPolygon(coordinates, this))
            return true;
        else
            return false;
    }

    public PolygonOptions getPolygonOptions(){
        return this.polygonOptions;
    }
    public int getPolyID(){
        return this.polyID;
    }
    public String getName(){
        return this.name;
    }
    public ArrayList<ArrayList<Double>> getCoordinates(){
        return this.coordinates;
    }
    public Polygon getPolygon(){
        return this.polygon;
    }


    public void setPolygon(Polygon p){
        this.polygon = p;
    }
}
