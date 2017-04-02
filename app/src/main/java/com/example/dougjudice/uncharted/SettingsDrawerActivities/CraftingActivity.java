package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.dougjudice.uncharted.MapsActivity;
import com.example.dougjudice.uncharted.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dougjudice on 4/1/17.
 */

public class CraftingActivity extends AppCompatActivity {

    ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myresource);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = (ListView) findViewById(R.id.my_resources);

        List<String> resourceList = new ArrayList<String>();

        //GameItem rareium = new GameItem()

        /*
        for(int i = 0 ; i < 10 ; i++){
            resourceList.add(r + i);
        }
*/
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
