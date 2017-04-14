package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ListView;

import com.example.dougjudice.uncharted.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by dougjudice on 4/9/17.
 */

public class PopUp extends Activity {

    ListView lv;
    JSONArray friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        // Set up popup window parameters
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        // These default to the width of the phone
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Define dimensions of the popup window
        getWindow().setLayout((int)(width*.8),(int)(height*.8));

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
                        //Intent intent = new Intent(GroupActivity.this,FriendsList.class);
                        try {
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
                                CustomList adapter = new CustomList(PopUp.this, friendNames, itemID);
                                lv.setAdapter(adapter);



                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        graphRequestAsyncTask.setParameters(params);
        graphRequestAsyncTask.executeAsync(); // Above block of code doesn't execute until this line is reached
    }
}
