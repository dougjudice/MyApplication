package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dougjudice.uncharted.MapsActivity;
import com.example.dougjudice.uncharted.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dougjudice on 3/26/17.
 */

public class ResourceActivity extends AppCompatActivity {

    ListView lv = null;

    SharedPreferences sp;

    int itemReturnId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myresource);

        // Set up shared preferences for getting user info
        sp = getSharedPreferences("ItemPref", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //LayoutInflater inflater = (LayoutInflater)   getBaseContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        //LinearLayout mContainer = inflater.inflate(R.layout.activity_myresource, null);
        lv = (ListView) findViewById(R.id.my_resources);

        //TODO: Get information from server about user's actual inventory

        ArrayList<String> inventory = new ArrayList<>();

        for(int i = 0; i < inventory.size(); i++){
            // TODO: Load inventory
        }

        String[] items = {
                // Utility.getItemNameById(inventory.get(i))...
                "Rareium",
                "Commonite",
                "Legendgem",
                "Mineral Scanner"
        };
        Integer[] itemID = {
                R.drawable.rarium,
                R.drawable.commonite,
                R.drawable.legendgem,
                R.drawable.mineral_scanner_common
        };

        // Sets up custom format for item  selection
        CustomList adapter = new CustomList(ResourceActivity.this, items, itemID, null);

        lv.setAdapter(adapter);

        // Set up action for click within the listview
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                System.out.println("item clicked");
                String selection = lv.getItemAtPosition(position).toString();

                if(!selection.equals("Commonite") || !selection.equals("Rareium") || !selection.equals("Legendgem")) {
                    useItem(selection);
                }
                else{
                    Toast.makeText(ResourceActivity.this, "Cannot use a raw item!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MapsActivity.class);
        myIntent.putExtra("timerOn", "yes");
        //startActivityForResult(myIntent,0);
        finish();
        return true;
    }

    public void useItem(String item){

        final boolean itemInUse = sp.getBoolean("itemInUse", false);

        if(itemInUse){
            Toast.makeText(ResourceActivity.this, "You can only have one item active at a time", Toast.LENGTH_SHORT).show();
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(ResourceActivity.this);
        builder.setMessage("Are you sure you want to use " + item + "?");
        builder.setCancelable(true);

        boolean six_hour = false;

        if(item.contains("Scanner")){
            six_hour = true;
        }

        final boolean hour = six_hour;

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        Toast.makeText(ResourceActivity.this, "Item Used!", Toast.LENGTH_SHORT).show();
                        // TODO: Post item usage to server
                        itemReturnId = 0;

                        SharedPreferences.Editor editor = sp.edit();

                        editor.putBoolean("itemInUse",true);
                        editor.putInt("itemReturnId",itemReturnId);


                        Long time = System.currentTimeMillis();

                        // Find expiration time
                        if(hour){
                            time += 15000;
                            System.out.println("Adding 15 seconds to item");
                        }
                        else if(!hour){
                            time += 3600000;
                        }

                        editor.putLong("itemExpTime", time);
                        editor.commit();
                        System.out.println("Item placed successfully : " + sp.getBoolean("itemInUse",false));

                        dialog.cancel();
                    }
                });
        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
