package com.example.dougjudice.uncharted;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {

    private static final int LOGIN_FLOW_TRACKING_CODE = 3682;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //AccessToken accessToken = AccountKit.getCurrentAccessToken();

        //if (accessToken != null) {
            startActivity(new Intent(this, MapsActivity.class));
        //} else {
            setContentView(R.layout.activity_main);
           // handleLogin();
        //}
        finish();
    }

    private void handleLogin() {
        final Button emailButton = (Button) findViewById(R.id.email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAccountKitLogin(LoginType.EMAIL);
            }
        });

        final Button smsButton = (Button) findViewById(R.id.sms_button);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAccountKitLogin(LoginType.PHONE);
            }
        });
    }

    private void launchAccountKitLogin(LoginType type) {
        final Intent loginIntent = new Intent(this, AccountKitActivity.class);

        AccountKitConfiguration.AccountKitConfigurationBuilder configBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(type,
                        AccountKitActivity.ResponseType.TOKEN);

        loginIntent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configBuilder.build());

        startActivityForResult(loginIntent, LOGIN_FLOW_TRACKING_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_FLOW_TRACKING_CODE) {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            checkLoginResult(result);
        }
    }

    private void checkLoginResult(AccountKitLoginResult loginResult) {
        String message;
        if (loginResult.getError() != null) {
            message = loginResult.getError().getErrorType().getMessage();
            showError(loginResult.getError().getUserFacingMessage());
        } else if (loginResult.wasCancelled()) {
            message = "Login Cancelled";
        } else {
            if (loginResult.getAccessToken() != null) {
                message = "Success:" + loginResult.getAccessToken().getAccountId();
            } else {
                message = String.format(
                        "Success:%s...",
                        loginResult.getAuthorizationCode().substring(0,10));
            }

            startActivity(new Intent(this, MapsActivity.class));
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showError(String errorMessage) {

        new AlertDialog.Builder(this)
                .setMessage(errorMessage)
                .setTitle(R.string.login_error)
                .create()
                .show();
    }
}
