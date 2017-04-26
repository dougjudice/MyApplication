package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dougjudice.uncharted.MapsActivity;
import com.example.dougjudice.uncharted.R;
import com.example.dougjudice.uncharted.UserProfile;
import com.example.dougjudice.uncharted.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dougjudice on 4/1/17.
 */

/*

This class defines the activity for the crafting system.
Items that can be crafted are shown on a grid, users click the item to see a description and resource cost.
Users can craft in the same tooltip that pops up showing information
Crafted items are then stored in the User's inventory

 */

public class CraftingActivity extends AppCompatActivity {

    ListView lv = null;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_craft);

        //final Context c = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_craft_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = (ListView) findViewById(R.id.craft_bar);

        //TODO: Get information from server about user's actual resources here
        final int[] resources = UserProfile.getProfile().getUserMaterials();

        String[] items = {
                "Common Mineral Scanner",
                "Rare Mineral Scanner",
                "Legendary Mineral Scanner",
                "Common Drone",
                "Rare Drone",
                "Legendary Drone",
                "Common Jammer",
                "Rare Jammer",
                "Legendary Jammer",
                "Common Barrier",
                "Rare Barrier",
                "Legendary Barrier"
        };
        Integer[] itemID = {
                R.drawable.mineral_scanner_common,
                R.drawable.mineral_scanner_rare,
                R.drawable.mineral_scanner_legendary,
                R.drawable.commonite_drone,
                R.drawable.rareium_drone,
                R.drawable.legendgem_drone,
                R.drawable.jammer_common,
                R.drawable.jammer_rare,
                R.drawable.jammer_legendary,
                R.drawable.barrier_common,
                R.drawable.barrier_rare,
                R.drawable.barrier_legendary
        };

        // Sets up custom format for item selection using CustomList class
        CustomList adapter = new CustomList(CraftingActivity.this, items, itemID, null);
        lv.setAdapter(adapter);

        // Set up action for click within the listview
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                // convert position in grid to a name
                String s = Utility.getItemNameById(position);
                String message = Utility.getItemMessageById(position);
                final int[] reqResources = Utility.getItemResourceReq(position);

                final int pos = position;

                // This is deciding whether you want to craft or not in a dialog box
                final AlertDialog.Builder builder = new AlertDialog.Builder(CraftingActivity.this);
                builder.setMessage("Are you sure you want to craft " + s + "?\n\n" + message);
                builder.setCancelable(true);

                sp = getSharedPreferences("itemExp", Context.MODE_PRIVATE);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                boolean craftable = true;
                                for(int i = 0; i < reqResources.length; i++){
                                    if(reqResources[i] > resources[i]){
                                        craftable = false;
                                        break;
                                    }
                                }
                                if(craftable){
                                    Toast.makeText(CraftingActivity.this, "Item Crafted!", Toast.LENGTH_SHORT).show();
                                    for(int x = 0; x <reqResources.length; x++){
                                        resources[x] -= reqResources[x];
                                    }
                                    // TODO: Post resources[] to server for user
                                    int usr_id = UserProfile.getProfile().getUserId();



                                    SharedPreferences.Editor editor = sp.edit();



                                    // ...

                                    dialog.cancel();
                                }
                                else{
                                    Toast.makeText(CraftingActivity.this, "You don't have the necessary resources to build this item!", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
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
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MapsActivity.class);
        myIntent.putExtra("timerOn", "yes");
        //startActivityForResult(myIntent,0);
        finish();
        return true;
    }

}
