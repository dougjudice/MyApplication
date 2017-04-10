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

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.8));

        // Friend Stuff

        lv = (ListView) findViewById(R.id.friend_list);

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
                                CustomList adapter = new CustomList(PopUp.this, friendNames, itemID);
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
    }
}
