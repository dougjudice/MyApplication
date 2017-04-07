package com.example.dougjudice.uncharted;

import android.content.Context;
import android.location.Location;

import com.example.dougjudice.uncharted.GameElements.*;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    // Takes user coordinates as 'point' (latitude,longitude), and a Polygon you want to check if the point is in.
    public static boolean pointInPolygon(ArrayList<Double> point, GamePolygon poly){

        LatLng userLoc = new LatLng(point.get(0),point.get(1));
        ArrayList<LatLng> coList = new ArrayList<>();
        for(int i = 0; i < poly.getCoordinates().size()-1; i++){
            ArrayList<Double> pair = poly.getCoordinates().get(i);
            coList.add(new LatLng(pair.get(0), pair.get(1)));
        }

        boolean contain = PolyUtil.containsLocation(userLoc, coList, true );

        //System.out.println(contain + " result from PolyUtil func");

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

    // Convert <double> to LatLng
    public static LatLng convertCoor(ArrayList<Double> cor){
        LatLng l = new LatLng(cor.get(0),cor.get(1));
        return l;
    }

    // Use to get center of a polygon, by passing NodePolygon's 'coordinates' parameter
    public static LatLng centroid(ArrayList<ArrayList<Double>> points) {

        double centroid[] = { 0.0, 0.0};

        //LatLng centroid = null;

        for (int i = 0; i < points.size(); i++) {
            centroid[0] += points.get(i).get(0);
            centroid[1] += points.get(i).get(1);
        }

        int totalPoints = points.size();
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;

        LatLng retVal = new LatLng(centroid[0],centroid[1]);

        return retVal;
    }

    // Use this function to store a file to the device's internal memory
    public static void storeFile(String name, String body, Context c){
        FileOutputStream fos;
        try{
            fos = c.openFileOutput(name, Context.MODE_PRIVATE);
            fos.write(body.getBytes());
            fos.close();
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    // Use this function to retreive a file from the device's internal memory, in string format
    public static String fetchFile(String name, Context c){
        FileInputStream fis;
        StringBuilder sb = null;
        try{
            fis = c.openFileInput(name);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            sb = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }
        } catch( FileNotFoundException e){
            e.printStackTrace();
        } catch( IOException e){
            e.printStackTrace();
        }
        return sb.toString();
        // null return value indicates some type of error
    }

}
