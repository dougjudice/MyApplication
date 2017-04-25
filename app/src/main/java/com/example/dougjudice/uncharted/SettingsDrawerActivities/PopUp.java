package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dougjudice.uncharted.GameElements.PlayerGroup;
import com.example.dougjudice.uncharted.R;
import com.example.dougjudice.uncharted.UserProfile;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by dougjudice on 4/9/17.
 */

public class PopUp extends Activity {

    ListView lv;
    JSONArray friendList;
    PlayerGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        // Set up popup window parameters
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        // Set up close button
        Button closeButton = (Button) findViewById(R.id.close_activity);
        closeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        // These default to the width of the phone
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Define dimensions of the popup window
        getWindow().setLayout((int)(width*.8),(int)(height*.8));

        group = getIntent().getExtras().getParcelable("group");

        // Build listview
        lv = (ListView) findViewById(R.id.friend_list);

        // Get Facebook token
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken == null)
            System.out.println("Null accessToken");
        System.out.println("Access token initialized");

        // Define what you want from Facebook Token
        Bundle params = new Bundle();
        params.putString("fields", "name");

        // Perform graph request, runs in own UI thread so all UI support actions need to be included within this function
        GraphRequest graphRequestAsyncTask = new GraphRequest(
                accessToken,
                //AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            friendList = response.getJSONObject().getJSONArray("data");
                            // String pictureUrl = response.getJSONObject("picture").getJSONObject("data").getString("url");
                            if(friendList == null)
                                System.out.println("Null ret");
                            else
                                System.out.println("Ret success");

                            String[] friendNames = new String[friendList.length()];
                            Integer[] itemID = new Integer[friendList.length()];
                            String[] facebookIds = new String[friendList.length()];
                            try{
                                for(int i = 0; i < friendList.length(); i++){

                                    // ADD FRIENDS FROM FACEBOOK TO friendNames[i] etc.

                                    System.out.println("Adding))");
                                    friendNames[i] = friendList.getJSONObject(i).getString("name");
                                    facebookIds[i] = friendList.getJSONObject(i).getString("id");
                                    itemID[i] = R.drawable.about_img;
                                }

                                final CustomList adapter = new CustomList(PopUp.this, friendNames, itemID,facebookIds);
                                lv.setAdapter(adapter);

                                // Action on items in the listview
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                        // convert position in grid to a name
                                        String clickedName = adapter.getStringByPos(position);
                                        final String facebookId = adapter.getFacebookIdByPos(position);
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(PopUp.this);

                                        builder.setMessage("Are you sure you want to add " + clickedName + " " + " to " + group.name + "?");
                                        builder.setCancelable(true);

                                        builder.setPositiveButton(
                                                "Yes",
                                                new DialogInterface.OnClickListener(){
                                                    public void onClick(DialogInterface dialog, int id){
                                                        //Toast.makeText(PopUp.this, "Friend Added!", Toast.LENGTH_SHORT).show();
                                                        // TODO: send  DB, if more than 6 are in the group say no
                                                        Intent resultIntent = new Intent();
                                                        resultIntent.putExtra("friendFacebookId", facebookId);
                                                        setResult(RESULT_OK, resultIntent);
                                                        finish();
                                                    }
                                                });
                                        builder.setNegativeButton(
                                                "No",
                                                new DialogInterface.OnClickListener(){
                                                    public void onClick(DialogInterface dialog, int id){
                                                        dialog.cancel();
                                                        setResult(RESULT_CANCELED);
                                                        finish();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                });


                            } catch (JSONException e){
                                e.printStackTrace();
                                showError("Could not get data from Facebook");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showError("Could not get data from Facebook");
                        }
                    }
                }
        );
        graphRequestAsyncTask.setParameters(params);
        graphRequestAsyncTask.executeAsync(); // Above block of code doesn't execute until this line is reached
    }

    private void showError(String errorMessage) {

        new AlertDialog.Builder(this)
                .setMessage(errorMessage)
                .setTitle(R.string.group_error)
                .create()
                .show();
    }
}
