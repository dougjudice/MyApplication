package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dougjudice.uncharted.GameElements.PlayerGroup;
import com.example.dougjudice.uncharted.MapsActivity;
import com.example.dougjudice.uncharted.R;
import com.example.dougjudice.uncharted.UserProfile;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dougjudice on 3/25/17.
 */

public class GroupActivity extends AppCompatActivity {

    ListView lv;
    JSONArray friendList;
    FloatingActionButton fab;
    PlayerGroup group;

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
            public void onClick(View v) {
                Intent intent = new Intent(GroupActivity.this, PopUp.class);

                if (UserProfile.getProfile().getGroupId() == null) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Future<PlayerGroup> result = executor.submit(new CreateNewGroupCallable());
                    // Add loading spinner
                    try {
                        group = result.get();
                        UserProfile.getProfile().setGroupId(group.groupId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError(e.getLocalizedMessage());
                    }
                }

                intent.putExtra("group", group);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("onActivityResult Fired");
        System.out.println("requestCode: " + requestCode + "// resultCode: " + resultCode);
        if (resultCode == RESULT_OK) {
            String friendFacebookId = data.getExtras().getString("friendFacebookId");
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<PlayerGroup> result = executor.submit(new AddFriendToGroupCallable(friendFacebookId, group.groupId));
            try {
                group = result.get();
                updateGroupDisplayList(group);
                Toast.makeText(GroupActivity.this, "Friend Added!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                showError(e.getLocalizedMessage());
            }

            //updateGroupDisplayList(group); //TODO Why is this here?
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Integer groupId = UserProfile.getProfile().getGroupId();
        if (groupId != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<PlayerGroup> result = executor.submit(new GetGroupByGroupIdCallable(groupId));
            try {
                group = result.get();
            } catch (Exception e) {
                e.printStackTrace();
                showError(e.getLocalizedMessage());
            }
        }
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
        //startActivityForResult(myIntent,0);
        finish();
        return true;
    }

    // Creates the actionbar menu and 'inflates' it
    @Override
    public boolean onCreateOptionsMenu(final Menu menu){
        getMenuInflater().inflate(R.menu.group_options_menu, menu);
        return true;
    }

    // Refresh listview
    private void updateGroupDisplayList(PlayerGroup group) {

        ArrayList<String> names = group.getNameList();
        ArrayList<Bitmap> pics = group.getPictureList();

        if(names == null) {
            System.out.println("NULL_NAME_LIST"); // Implies empty group or no group
            return;
        }

        String[] groupNames = names.toArray(new String[names.size()]);
        Bitmap[] groupPics = pics.toArray(new Bitmap[pics.size()]);

        CustomList adapter = new CustomList(GroupActivity.this, groupNames, groupPics, null, this);

        lv.setAdapter(adapter);
        System.out.println("SUCCESS_ADAPT");
        return;
    }

    private class AddFriendToGroupCallable implements Callable<PlayerGroup> {

        private String facebookId;
        private int groupId;

        public AddFriendToGroupCallable(String facebookId, int groupId) {
            this.facebookId = facebookId;
            this.groupId = groupId;
        }

        @Override
        public PlayerGroup call() throws Exception {
            Resources res = getResources();
            String scheme = res.getString(R.string.login_server_protocol);
            String host = res.getString(R.string.login_server_host);
            String endpoint = res.getString(R.string.server_user_by_facebook_id_endpoint);

            HttpUrl url = new HttpUrl.Builder()
                    .scheme(scheme)
                    .host(host)
                    .port(res.getInteger(R.integer.login_server_port))
                    .addPathSegments(endpoint)
                    .addPathSegment(facebookId)
                    .build();

            Request request = new Request.Builder().url(url).build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                UserProfile user = UserProfile.deserialize(response.body().string());
                return AddUserToGroup(user.getUserId(), groupId);
            }

            throw new Exception("Could not get user by facebook ID. Response from server: " + response.code());
        }

        private PlayerGroup AddUserToGroup(int userId, int groupId) throws Exception {

            Resources res = getResources();
            String scheme = res.getString(R.string.login_server_protocol);
            String host = res.getString(R.string.login_server_host);
            String endpoint = res.getString(R.string.server_group_members_endpoint);

            HttpUrl url = new HttpUrl.Builder()
                    .scheme(scheme)
                    .host(host)
                    .port(res.getInteger(R.integer.login_server_port))
                    .addPathSegments(endpoint)
                    .addPathSegment(Integer.toString(groupId))
                    .addPathSegment(Integer.toString(userId))
                    .build();

            Request request = new Request.Builder().url(url).post(null).build();
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return PlayerGroup.deserialize(response.body().string());
            }

            throw new Exception("Could not add user to group. Response from server: " + response.code());
        }
    }

    private class CreateNewGroupCallable implements Callable<PlayerGroup> {

        @Override
        public PlayerGroup call() throws Exception {
            Resources res = getResources();
            String scheme = res.getString(R.string.login_server_protocol);
            String host = res.getString(R.string.login_server_host);
            String loginEndPoint = res.getString(R.string.server_new_group_endpoint);
            String userId = Integer.toString(UserProfile.getProfile().getUserId());

            HttpUrl url = new HttpUrl.Builder()
                    .scheme(scheme)
                    .host(host)
                    .port(res.getInteger(R.integer.login_server_port))
                    .addPathSegments(loginEndPoint)
                    .addPathSegment(userId)
                    .build();

            Request request = new Request.Builder().url(url).build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return PlayerGroup.deserialize(response.body().string());
            }

            throw new Exception("Could not create group. Response from server: " + response.code());
        }
    }

    private class GetGroupByGroupIdCallable implements Callable<PlayerGroup> {

        private int groupId;

        public GetGroupByGroupIdCallable(int groupId) {
            this.groupId = groupId;
        }

        @Override
        public PlayerGroup call() throws Exception {
            Resources res = getResources();
            String scheme = res.getString(R.string.login_server_protocol);
            String host = res.getString(R.string.login_server_host);
            String endpoint = res.getString(R.string.server_group_endpoint);

            HttpUrl url = new HttpUrl.Builder()
                    .scheme(scheme)
                    .host(host)
                    .port(res.getInteger(R.integer.login_server_port))
                    .addPathSegments(endpoint)
                    .addPathSegment(Integer.toString(groupId))
                    .build();

            Request request = new Request.Builder().url(url).build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return PlayerGroup.deserialize(response.body().string());
            }

            throw new Exception("Could not get group. Response from server: " + response.code());
        }
    }

    private void showError(String errorMessage) {

        new AlertDialog.Builder(this)
                .setMessage(errorMessage)
                .setTitle(R.string.group_error)
                .create()
                .show();
    }
}
