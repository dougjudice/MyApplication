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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    // Overloaded function that stores inventory
    public static void storeFile(String name, ArrayList<Item> i, Context c){
        FileOutputStream fos;
        try{
            fos = c.openFileOutput(name, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(i);
            oos.close();
            fos.close();
            System.out.println("Inventory store success");
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
            fis.close();
        } catch( FileNotFoundException e){
            e.printStackTrace();
        } catch( IOException e){
            e.printStackTrace();
        }
        return sb.toString();
        // null return value indicates some type of error
    }
    public static ArrayList<Item> fetchInventoryFile(String name, Context c){
        FileInputStream fis;
        StringBuilder sb = null;
        ArrayList<Item> result = null;
        try{
            fis = c.openFileInput(name);
            ObjectInputStream ois = new ObjectInputStream(fis);
            result = (ArrayList<Item>) ois.readObject();
            ois.close();
            fis.close();
        } catch( FileNotFoundException e){
            e.printStackTrace();
        } catch( IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return result;
        // null return value indicates some type of error
    }

    // Item Retreival (used in multiple places)

    public static String getItemNameById(int id){
        switch(id){
            case 0: return "Common Mineral Scanner";
            case 1: return "Rare Mineral Scanner";
            case 2: return "Legendary Mineral Scanner";
            case 3: return "Common Drone";
            case 4: return "Rare Drone";
            case 5: return         "Legendary Drone";
            case 6: return         "Common Jammer";
            case 7: return         "Rare Jammer";
            case 8: return         "Legendary Jammer";
            case 9: return         "Common Barrier";
            case 10: return         "Rare Barrier";
            case 11: return         "Legendary Barrier";
            case 20: return "Commonite";
            case 21: return "Rareium";
            case 22: return "Legendgem";
            default: return "ITEM_NOT_FOUND";
        }
    }
    public static int getItemIdByName(String s){
        switch(s){
            case "Common Mineral Scanner": return 0;
            case "Rare Mineral Scanner": return 1;
            case "Legendary Mineral Scanner": return 2;
            case "Common Drone": return 3;
            case "Rare Drone": return 4;
            case "Legendary Drone": return 5;
            case "Common Jammer": return 6;
            case "Rare Jammer": return 7;
            case "Legendary Jammer": return 8;
            case "Common Barrier": return 9;
            case "Rare Barrier": return 10;
            case "Legendary Barrier": return 11;
            case "Commonite": return 20;
            case "Rareium": return 21;
            case "Legendgem": return 22;

            default: return -1;
        }
    }


    public static String getItemMessageById(int id){
        switch(id){
            case 0: return "The Common Mineral Scanner will let you see Commonite Nodes in the world before they're about to become active. This will last for 6 hours.";
            case 1: return "The Rare Mineral Scanner will let you see Rareium Nodes in the world before they're about to become active. This will last for 6 hours.";
            case 2: return "The Legendary Mineral Scanner will let you see Legendgem Nodes in the world before they're about to become active. This will last for 10 hours.";
            case 3: return "The Common Drone will boost your mining rate by 7 points. This effect will last 1 hour";
            case 4: return "The Rare Drone will boost your mining rate by 12 points. This effect will last 1 hour";
            case 5: return "The Legendary Drone will boost your mining rate by 16 points, and an additional 2 points for every present group member in the same node. This effect will last 1 hour";
            case 6: return "The Common Jammer will lower the mining rate of all non-group members in the same node by 5 points. This effect will last 1 hour";
            case 7: return "The Rare Jammer will lower the mining rate of all non-group members in the same node by 10 points. This effect will last 1 hour";
            case 8: return "The Legendary Jammer will lower the mining rate of all non-group members in the same node by 15 points, and make them more vulnerable to attacks. This effect will last 1 hour";
            case 9: return "The Common Barrier will boost your resistance to enemy attacks by 5 points. This effect will last 1 hour";
            case 10: return "The Rare Barrier will boost your resistance to enemy attacks by 10 points, plus an additional 2 points for every group member in the same node. This effect will last 1 hour";
            case 11: return "The Legendary Barrier will boost your resistance to enemy attacks by 15 points, pluse an additional 5 points for every group member in the same node. This effect will last 1 hour";
            case 20: return "Commonite is the most common gem in the game. It is used to make nearly every item.";
            case 21: return "Rareium is the second rarest gem in the game. It is used to craft many higher-level items.";
            case 22: return "Legendgem is the rarest gem of them all. It is used to craft legendary items with additional effects.";
            default: return "ITEM_NOT_FOUND";
        }
    }
    public static int[] getItemResourceReq(int id){
        switch(id){
            case 0: return new int[] {115,0,0};
            case 1: return new int[] {100,15,0};
            case 2: return new int[] {100,0,15};
            case 3: return new int[] {120,10,0};
            case 4: return new int[] {130,25,0};
            case 5: return new int[] {130,0,20};
            case 6: return new int[] {200,0,0};
            case 7: return new int[] {150,25,0};
            case 8: return new int[] {150,10,25};
            case 9: return new int[] {200,0,0};
            case 10: return new int[] {75,50,0};
            case 11: return new int[] {100,0,50};
            default: return null; // error
        }
    }
    public static Integer getItemImageSource(int id){
        switch(id){
            case 0: return R.drawable.mineral_scanner_common;
            case 1: return R.drawable.mineral_scanner_rare;
            case 2: return       R.drawable.mineral_scanner_legendary;
            case 3: return        R.drawable.commonite_drone;
            case 4: return        R.drawable.rareium_drone;
            case 5: return        R.drawable.legendgem_drone;
            case 6: return        R.drawable.jammer_common;
            case 7: return        R.drawable.jammer_rare;
            case 8: return        R.drawable.jammer_legendary;
            case 9: return        R.drawable.barrier_common;
            case 10: return        R.drawable.barrier_rare;
            case 11: return        R.drawable.barrier_legendary;
            case 20: return R.drawable.commonite;
            case 21: return R.drawable.rarium;
            case 22: return R.drawable.legendgem;

            default: return null;
        }
    }

}
