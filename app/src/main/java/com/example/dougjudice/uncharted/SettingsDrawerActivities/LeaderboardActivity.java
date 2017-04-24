package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.dougjudice.uncharted.MapsActivity;
import com.example.dougjudice.uncharted.R;

/**
 * Created by dougjudice on 4/23/17.
 */

public class LeaderboardActivity extends AppCompatActivity {

    ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.leaderboard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MapsActivity.class);
        myIntent.putExtra("timerOn", "yes");
        //startActivityForResult(myIntent,0);
        finish();
        return true;
    }
}
