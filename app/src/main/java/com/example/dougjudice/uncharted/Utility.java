package com.example.dougjudice.uncharted;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dougjudice.uncharted.GameElements.*;

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
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dougjudice on 3/18/17.
 */

public final class Utility {

    public static void printCoordinates(ArrayList<ArrayList<Double>> arr){

        for(int i = 0; i < arr.size(); i++){
            ArrayList<Double> pair = arr.get(i);
            System.out.println(" Coordinate "+ i +" : ");
            System.out.println("X: " + pair.get(0)+ " | Y: " + pair.get(1));
        }
        return;
    }

    // Uses PolyUtil to compute
    public static boolean pointInPolygon(ArrayList<Double> point, GamePolygon poly){

        LatLng userLoc = new LatLng(point.get(0),point.get(1));
        ArrayList<LatLng> coList = new ArrayList<>();
        for(int i = 0; i < poly.getCoordinates().size()-1; i++){
            ArrayList<Double> pair = poly.getCoordinates().get(i);
            coList.add(new LatLng(pair.get(0), pair.get(1)));
        }

        boolean contain = PolyUtil.containsLocation(userLoc, coList, true );

        System.out.println(contain + " result from PolyUtil func");

        return contain;

    }

    // Reverse hashmap lookup: Find Key from Value
    public static Object keyLookup(HashMap<Object,Object> map, Object o){
        for(Map.Entry<Object,Object> e : map.entrySet()){
            Object key = e.getKey();
            Object value = e.getValue();
            if(value.equals(o)){
                return key;
            }
            else {
                continue;
            }
        }
        return null; // Key not found
    }

    // Takes Hashmap of Name:Node pairings and sees if location argument is in any single node
    public static String checkAllIntersections(HashMap<String,NodePolygon> map, Location loc){

        //Iterator it = map.entrySet().iterator();
        for(Map.Entry<String,NodePolygon> entry: map.entrySet()){

            if(entry == null)
                break;

            System.out.println(entry.getKey());

            ArrayList<Double> arrCor = new ArrayList<>();

            double lat = loc.getLatitude();
            double longi = loc.getLongitude();

            arrCor.add(lat);
            arrCor.add(longi);

            if(pointInPolygon(arrCor, (GamePolygon) entry.getValue())){
                System.out.println("Polygon: " + entry.getValue().getName());
                return (String) entry.getKey();
            }

        }
        return null; // not in any polygon
    }

}
