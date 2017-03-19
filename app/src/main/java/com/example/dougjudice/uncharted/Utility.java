package com.example.dougjudice.uncharted;

import android.content.Context;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dougjudice on 3/18/17.
 */

public final class Utility {

    // Draws the necessary polygons over all significant New Brunswick Landmarks
    public static void drawPolys(GoogleMap map){

        // Add Polygon over Olive Branch
        PolygonOptions oliveBranch = new PolygonOptions()
                .add(new LatLng(40.501306,-74.452853)) // SW corner
                .add(new LatLng(40.501510,-74.453080)) // NW corner
                .add(new LatLng(40.501574,-74.452974))
                .add(new LatLng(40.501362,-74.452758))
                .add(new LatLng(40.501306,-74.452853)) // closes polygon
                .fillColor(Color.BLUE);  // Set color

        PolygonOptions oldeQueens = new PolygonOptions()
                .add(new LatLng(40.498995,-74.451999)) // NW corner
                .add(new LatLng(40.498842,-74.452105)) // SW corner
                .add(new LatLng(40.498807,-74.4520164))
                .add(new LatLng(40.498956,-74.451913))
                .add(new LatLng(40.498995,-74.451999)) // closes polygon
                .fillColor(Color.BLUE);  // Set color

        PolygonOptions hanselGriddle = new PolygonOptions()
                .add(new LatLng(40.499265,-74.452806)) // NW corner
                .add(new LatLng(40.499061,-74.452940)) // SW corner
                .add(new LatLng(40.498984,-74.452735))
                .add(new LatLng(40.499187,-74.452605))
                .add(new LatLng(40.499265,-74.452806)) // closes polygon
                .fillColor(Color.BLUE);  // Set color

        map.addPolygon(oliveBranch);
        map.addPolygon(oldeQueens);
        map.addPolygon(hanselGriddle);

        return;
    }

    // Based on Ray-Casting Algorithm : http://rosettacode.org/wiki/Ray-casting_algorithm
    public static boolean pointInPolygon(ArrayList<Double> point, GamePolygon poly){
        int crossings = 0;
        ArrayList<ArrayList<Double>> coor = poly.getCoordinates();
        coor.remove(4); // remove final index which closes the loop

        //for each edge
        for(int i = 0; i < coor.size(); i++){
            ArrayList<Double> p1 = coor.get(i);
            int j = i + 1;

            // to close last edge, take first point of polygon
            if(j > coor.size()) {
                j = 0;
            }

            ArrayList<Double> p2 = coor.get(j);
            if(rayCrossesSegment(point, p1, p2)){
                crossings++;
            }

        }

        // odd number of crossings?
        return (crossings % 2 == 1);

    }
    public static boolean rayCrossesSegment(ArrayList<Double> point, ArrayList<Double> a, ArrayList<Double> b){
        double px = point.get(0), py = point.get(1), ax = a.get(0), ay = a.get(1), bx = b.get(0), by = b.get(1);

        if(ay > by){
            ax = b.get(1);
            ay = b.get(0);
            bx = a.get(1);
            by = a.get(0);
        }

        // alter longitude to cater for 180 degree crossings
        if (px < 0 || ax < 0 || bx < 0) { px += 360; ax += 360; bx += 360; }
        // if point has same lat as a or b, increase slightly py
        if (py == ay || py == by) py += 0.00000001;

        // if point above , below, or to right of seg, return false
        if((py > by || py < ay) || (px > Math.max(ax, bx))){
            return false;
        }
        else if (px < Math.min(ax,bx)){
            return true;
        }
        // else if two conditions not met, compare slope of segment [a,b], (red) and segment [a,p] (blue) , see if point is to the left of [a,b] or not
        else{
            double red = (ax!=bx) ? ((by - ay) / (bx - ax)) : Double.POSITIVE_INFINITY;
            double blue = (ax!=px) ? ((by - ay) / (px - ax)) : Double.POSITIVE_INFINITY;
            return (blue >= red);
        }
    }

    // Reverse hashmap lookup: Find Key from Value
    public static Object keyLookup(HashMap<Object,Object> map, Object o){
        for(Map.Entry<Object,Object> e : map.entrySet()){
            Object key = e.getKey();
            Object value = e.getValue();
            if(value.equals(o) == true){
                return key;
            }
            else {
                continue;
            }
        }
        return null; // Key not found
    }


}
