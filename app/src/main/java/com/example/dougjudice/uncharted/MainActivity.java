package com.example.dougjudice.uncharted;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final CallbackManager callbackManager = CallbackManager.Factory.create();
    private final List<String> fbPermissions = Arrays.asList("public_profile", "user_friends");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isProperlyLoggedIn()) {
            authenticateWithLoginServer();
        }

        showFacebookLogin();
    }

    private boolean isProperlyLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken != null) {
            if (fbPermissions.equals(Arrays.asList(accessToken.getPermissions().toArray()))) {
                return true;
            }
            LoginManager.getInstance().logOut();
        }

        return false;
    }

    private void finishedLoading(String placesJson) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.login_spinner);
        progressBar.setVisibility(View.GONE);
        goToMapActivity(placesJson);
    }

    private void authenticateWithLoginServer() {
        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);
        loginButton.setVisibility(View.INVISIBLE);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.login_spinner);
        progressBar.setVisibility(View.VISIBLE);

        Resources res = getResources();
        String scheme = res.getString(R.string.login_server_protocol);
        String host = res.getString(R.string.login_server_host);
        String loginEndPoint = res.getString(R.string.login_server_login_endpoint);
        String tokenParameter = res.getString(R.string.login_server_token_parameter);

        HttpUrl url = new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .port(res.getInteger(R.integer.login_server_port))
                .addPathSegments(loginEndPoint)
                .addQueryParameter(tokenParameter, AccessToken.getCurrentAccessToken().getToken())
                .build();

        Request request = new Request.Builder().url(url).build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onNetworkError(e.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    UserProfile.getProfile().setId(Integer.parseInt(response.body().string()));
                    populateData();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onNetworkError(getResources().getString(R.string.login_server_connection_error));
                        }
                    });
                }
            }
        });
    }

    private void populateData() {
        Bundle params = new Bundle();
        params.putString("fields", "picture.type(large),name");

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String pictureUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");

                            OkHttpClient client = new OkHttpClient();
                            fetchAndSetProfilePictureFromFb(client, pictureUrl); // runs async
                            fetchAndSetPlacesData(client);// runs async

                            UserProfile.getProfile().setName(object.getString("name"));
                        } catch (JSONException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onNetworkError(getResources().getString(R.string.problem_with_facebook));
                                }
                            });
                        }
                    }
                }
        );

        request.setParameters(params);
        request.executeAsync();
    }

    private void fetchAndSetPlacesData(final OkHttpClient client) {
        Resources res = getResources();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(res.getString(R.string.login_server_host))
                .port(res.getInteger(R.integer.login_server_port))
                .addPathSegment(res.getString(R.string.login_server_places_endpoint))
                .build();

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                if (client.dispatcher().runningCallsCount() > 0) {
                    client.dispatcher().cancelAll();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onNetworkError(e.getLocalizedMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String json = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finishedLoading(json);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onNetworkError(getResources().getString(R.string.login_server_places_error));
                        }
                    });
                }
            }
        });
    }

    private void fetchAndSetProfilePictureFromFb(final OkHttpClient client, String urlString) {
        final Request request = new Request.Builder().url(urlString).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (client.dispatcher().runningCallsCount() > 0) {
                    client.dispatcher().cancelAll();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onNetworkError(e.getLocalizedMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    UserProfile.getProfile().setPicture(BitmapFactory.decodeStream(response.body().byteStream()));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onNetworkError(getResources().getString(R.string.problem_with_facebook));
                        }
                    });
                }
            }
        });
    }

    private void onNetworkError(String message) {
        LoginManager.getInstance().logOut();
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.login_spinner);
        progressBar.setVisibility(View.GONE);

        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);
        loginButton.setVisibility(View.VISIBLE);

        showError(message);
    }

    private void showFacebookLogin() {

        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);
        loginButton.setVisibility(View.VISIBLE);
        loginButton.setReadPermissions(fbPermissions);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                authenticateWithLoginServer();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                onNetworkError(error.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void goToMapActivity(String placesJson) {
        final Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("placesJson", placesJson);
        startActivity(intent);
    }

    private void showError(String errorMessage) {

        new AlertDialog.Builder(this)
                .setMessage(errorMessage)
                .setTitle(R.string.login_error)
                .create()
                .show();
    }
}
