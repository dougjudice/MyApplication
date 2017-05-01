package com.example.dougjudice.uncharted.SettingsDrawerActivities;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.example.dougjudice.uncharted.GameElements.PlayerGroup;
import com.example.dougjudice.uncharted.MapsActivity;
import com.example.dougjudice.uncharted.R;
import com.example.dougjudice.uncharted.UserProfile;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by dougjudice on 3/25/17.
 */

public class GroupActivity extends AppCompatActivity {

    ListView lv;
    JSONArray friendList;
    FloatingActionButton fab;
    PlayerGroup group;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.friend_fab);
        lv = (ListView) findViewById(R.id.group_list);
        tv = (TextView) findViewById(R.id.group_name);

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
                if(group.getNameList().size() >= 4){
                    Toast.makeText(GroupActivity.this, "Your group is the maximum size!", Toast.LENGTH_SHORT).show();
                    return;
                }
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
            String friendName = data.getExtras().getString("friendName");
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<UserProfile> result = executor.submit(new AddFriendToGroupCallable(friendFacebookId, group.groupId));
            try {
                UserProfile friend = result.get();
                friend.setName(friendName);
                group.members.add(friend);
            } catch (Exception e) {
                e.printStackTrace();
                showError(e.getLocalizedMessage());
                return;
            }

            updateGroupDisplayList(group);
            Toast.makeText(GroupActivity.this, "Friend Added!", Toast.LENGTH_SHORT).show();
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
                return;
            }
        }

        if(group != null)  updateGroupDisplayList(group);
    }

    // Handle events from toolbar buttons
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId() == R.id.change_name && UserProfile.getProfile().getGroupId() != null) {
            System.out.println("Clicked Bookmark Menu");

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Enter new name:");
            alert.setMessage("");

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    tv.setText(input.getText()); // TODO: Make linked to server, for now bypassed to always just change

                    Resources res = getResources();
                    String scheme = res.getString(R.string.login_server_protocol);
                    String host = res.getString(R.string.login_server_host);
                    String endpoint = res.getString(R.string.server_groups_endpoint);
                    String groupNameSegment = res.getString(R.string.server_groups_name_segment);

                    HttpUrl url = new HttpUrl.Builder()
                            .scheme(scheme)
                            .host(host)
                            .port(res.getInteger(R.integer.login_server_port))
                            .addPathSegments(endpoint)
                            .addPathSegment(Integer.toString(UserProfile.getProfile().getGroupId()))
                            .addPathSegment(groupNameSegment)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), input.getText().toString()))
                            .build();

                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.setText(input.getText());
                                    }
                                });
                            }
                        }
                    });
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
    }

    private class AddFriendToGroupCallable implements Callable<UserProfile> {

        private String facebookId;
        private int groupId;

        AddFriendToGroupCallable(String facebookId, int groupId) {
            this.facebookId = facebookId;
            this.groupId = groupId;
        }

        @Override
        public UserProfile call() throws Exception {
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
                AddUserToGroup(user.getUserId(), groupId);
                user.setGroupId(groupId);
                return user;
            }

            throw new Exception("Could not get user by facebook ID. Response from server: " + response.code());
        }

        private void AddUserToGroup(int userId, int groupId) throws Exception {

            Resources res = getResources();
            String scheme = res.getString(R.string.login_server_protocol);
            String host = res.getString(R.string.login_server_host);
            String endpoint = res.getString(R.string.server_groups_endpoint);
            String membersSegment = res.getString(R.string.server_groups_members_segment);

            HttpUrl url = new HttpUrl.Builder()
                    .scheme(scheme)
                    .host(host)
                    .port(res.getInteger(R.integer.login_server_port))
                    .addPathSegments(endpoint)
                    .addPathSegment(Integer.toString(groupId))
                    .addPathSegment(membersSegment)
                    .addPathSegment(Integer.toString(userId))
                    .build();

            Request request = new Request.Builder().url(url).post(RequestBody.create(null, new byte[]{})).build();
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new Exception("Could not add user to group. Response from server: " + response.code());
            }
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
                PlayerGroup group = PlayerGroup.deserialize(response.body().string());
                group.members = getUsersFacebookData(group.members);
                return group;
            }

            throw new Exception("Could not get group. Response from server: " + response.code());
        }

        public List<UserProfile> getUsersFacebookData(List<UserProfile> users) throws Exception {
            Collection<GraphRequest> requests = new ArrayList<>();

            for (final UserProfile user : users) {
                GraphRequest request = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + user.getFacebookId(),
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                JSONObject userJson = response.getJSONObject();
                                try {
                                    user.setName(userJson.getString("name"));

                                    String pictureEndpoint = userJson.getJSONObject("picture").getJSONObject("data").getString("url");
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url(pictureEndpoint).build();
                                    Response pictureResponse = client.newCall(request).execute();

                                    if (pictureResponse.isSuccessful()) {
                                        Bitmap picture = BitmapFactory.decodeStream(pictureResponse.body().byteStream());
                                        user.setPicture(picture);
                                    } else {
                                        throw new Exception("Error retrieving data from Facebook. Facebook server returned " + pictureResponse.code());
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
                Bundle params = new Bundle();
                params.putString("fields", "picture.type(large),name");
                request.setParameters(params);
                requests.add(request);
            }

            GraphRequestBatch batchRequest = new GraphRequestBatch(requests);
            batchRequest.executeAndWait();
            return users;
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
