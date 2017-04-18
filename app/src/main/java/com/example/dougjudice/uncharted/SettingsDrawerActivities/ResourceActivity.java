package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.content.DialogInterface;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myresource);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //LayoutInflater inflater = (LayoutInflater)   getBaseContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        //LinearLayout mContainer = inflater.inflate(R.layout.activity_myresource, null);
        lv = (ListView) findViewById(R.id.my_resources);

        //TODO: Get information from server about user's actual resources

        String[] items = {
                "Rareium",
                "Commonite",
                "Legendgem",
                "Mineral Scanner"
        };
        Integer[] itemID = {
                R.drawable.rarium,
                R.drawable.commonite,
                R.drawable.legendgem,
                R.drawable.laser
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

                // TODO: Account for all items later with a switch statement
                if(selection.equals("Mineral Scanner")){
                    useItem(selection);
                }
                else{
                    Toast.makeText(ResourceActivity.this, "Cannot use this item!", Toast.LENGTH_SHORT).show();
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(ResourceActivity.this);
        builder.setMessage("Are you sure you want to use " + item + "?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        Toast.makeText(ResourceActivity.this, "Item Used!", Toast.LENGTH_SHORT).show();
                        // TODO: Activate item within MapsActivity?
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
