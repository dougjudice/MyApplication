package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dougjudice.uncharted.MapsActivity;
import com.example.dougjudice.uncharted.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by dougjudice on 3/25/17.
 */

public class GroupActivity extends AppCompatActivity {

    ListView lv;
    JSONArray friendList;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.friend_fab);
        lv = (ListView) findViewById(R.id.group_list);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(GroupActivity.this, PopUp.class));
            }
        });
        /*
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken == null)
            System.out.println("Null accessToken");
        System.out.println("Access token initialized");

        // Get Friend List

        Bundle params = new Bundle();
        params.putString("fields", "name");

        CustomList adapter;

        GraphRequest graphRequestAsyncTask = new GraphRequest(
                accessToken,
                //AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        System.out.println("In onCompleted");
                        //Intent intent = new Intent(GroupActivity.this,FriendsList.class);
                        try {
                            System.out.println("Trying");
                            friendList = response.getJSONObject().getJSONArray("data");
                            if(friendList == null)
                                System.out.println("Null ret");
                            else
                                System.out.println("Ret success");

                            String[] friendNames = new String[friendList.length()];
                            Integer[] itemID = new Integer[friendList.length()];
                            try{
                                for(int i = 0; i < friendList.length(); i++){
                                    friendNames[i] = (friendList.getJSONObject(i).getString("name"));
                                    itemID[i] = R.drawable.about_img;
                                }
                                CustomList adapter = new CustomList(GroupActivity.this, friendNames, itemID);
                                lv.setAdapter(adapter);



                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                            //intent.putExtra("jsondata", rawName.toString());
                            //startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        graphRequestAsyncTask.setParameters(params);
        graphRequestAsyncTask.executeAsync();
    */

    }

    // Handle events from toolbar buttons
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId() == R.id.change_name){
            System.out.println("Clicked Bookmark Menu");

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Enter new name:");
            alert.setMessage("");

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //TODO: Post update to server
                    TextView tv = (TextView) findViewById(R.id.group_name);
                    tv.setText(input.getText());
                    System.out.println("Sent to DB");
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();

            return true;
        }

        Intent myIntent = new Intent(getApplicationContext(), MapsActivity.class);
        myIntent.putExtra("timerOn", "yes");
        startActivityForResult(myIntent,0);
        return true;
    }

    // Creates the actionbar menu and 'inflates' it
    @Override
    public boolean onCreateOptionsMenu(final Menu menu){
        getMenuInflater().inflate(R.menu.group_options_menu, menu);
        return true;
    }

}
