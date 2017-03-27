package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

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

        //TODO: Populate with user's resources via their settings and JSON/ Serverlink
        List<String> resourceList = new ArrayList<String>();
        String r = "";
        for(int i = 0 ; i < 10 ; i++){
            resourceList.add(r + i);
        }

        // populate list
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.simple_array_view,
                resourceList
        );
        lv.setAdapter(arrayAdapter);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivityForResult(myIntent,0);
        return true;
    }



}
