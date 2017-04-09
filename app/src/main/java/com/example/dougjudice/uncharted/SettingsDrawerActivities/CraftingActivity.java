package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_craft);

        //final Context c = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_craft_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = (ListView) findViewById(R.id.craft_bar);

        //TODO: Get information from server about user's actual resources

        String[] items = {
                "Mineral Scanner",
                "Group Linker"
        };
        Integer[] itemID = {
                R.drawable.laser,
                R.drawable.linker
        };

        // Sets up custom format for item  selection
        CustomList adapter = new CustomList(CraftingActivity.this, items, itemID);

        lv.setAdapter(adapter);

        // Set up action for click within the listview
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                // convert position in grid to a name
                String s = getNameById(position);
                String message = "The Mineral Scanner will boost your mining rate by 50%! \n Crafting it will cost: \n * 100 Commonite \n * 50 Rareium";

                // This is deciding whether you want to craft or not in a dialog box
                final AlertDialog.Builder builder = new AlertDialog.Builder(CraftingActivity.this);
                builder.setMessage("Are you sure you want to craft " + s + "?\n" + message);
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                // TODO: check user inventory to see if they can craft selected item
                                Toast.makeText(CraftingActivity.this, "Item Crafted!", Toast.LENGTH_SHORT).show();
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
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MapsActivity.class);
        myIntent.putExtra("timerOn", "yes");
        startActivityForResult(myIntent,0);
        return true;
    }

    public String getNameById(int id){
        switch(id){
            case 0: return "Mineral Scanner";
            case 1: return "Mining Linker";
            default: return "ITEM_NOT_FOUND";
        }
    }
}
