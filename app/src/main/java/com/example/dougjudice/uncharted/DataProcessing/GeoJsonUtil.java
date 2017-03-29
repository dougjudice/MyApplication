package com.example.dougjudice.uncharted.DataProcessing;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.JsonReader;
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
import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dougjudice on 3/18/17.
 */

// Cannot instantiate this class it is simply a utility for parsing GeoJSON Data
public class GeoJsonUtil {

    private static int p_count = 0;

    /*
    %
    %   FOR FUTURE USE
    %   Node info will be stored on the sever and loaded every time so users don't need to update app on incoming change.
    %   Implement a method to receive a JSON package, and this function will convert it to the NodePolygon object. SEE --hanselgriddle.json-- IN res/raw FOR CORRECT FORMAT
    %   Use schema I provide in the three JSON packages ini res/raw
     */

    public static NodePolygon generatePolygon(JSONObject object, GoogleMap map){
        System.out.println("Creating Polygon...");

        // Note: in ArrayList<Double> coor, latitude is always stored first (0), longitude always stored second (1)

        PolygonOptions p = new PolygonOptions();
        ArrayList<ArrayList<Double>> coor = new ArrayList<>();

        // Parse entire JSON file
        try{
            JSONArray xArray = object.getJSONArray("coordinates-x");
            JSONArray yArray = object.getJSONArray("coordinates-y");
            for(int i = 0; i < xArray.length(); i++){
                String sx = xArray.getString(i);
                String sy = yArray.getString(i);
                ArrayList<Double> set = new ArrayList<Double>();
                set.add(Double.parseDouble(sx)); set.add(Double.parseDouble(sy));

                coor.add(set);

                // System.out.println(" sx: " + sx + " , sy: " + sy);
                p.add(new LatLng(Double.parseDouble(sx),Double.parseDouble(sy)));
            }

            p.fillColor(Color.BLUE);
            System.out.println("Success");

            String name = object.getString("name");

            NodePolygon np = new NodePolygon(p, coor, p_count, name, map);
            np.getPolygon().setStrokeWidth(1.0f); // Sets how thick lines around polygon are
            p_count++;

            return np;

        }
        catch(org.json.JSONException e){
            e.printStackTrace();
            System.out.println(" %% PARSE FAILURE %% ");
        }

        return null;
    }

}
