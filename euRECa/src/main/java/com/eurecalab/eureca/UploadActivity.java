package com.eurecalab.eureca;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.User;
import com.eurecalab.eureca.fragments.UploadFragment;
import com.eurecalab.eureca.net.SignInTask;
import com.eurecalab.eureca.ui.ThemeSwitcher;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import java.util.LinkedList;
import java.util.List;

@SuppressLint("NewApi")
public class UploadActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, Callable {
    private GlobalState gs;
    private Toolbar toolbar;
    private User authenticatedUser;
    private GoogleApiClient mGoogleApiClient;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeSwitcher(this).onActivityCreateSetTheme();
        setContentView(R.layout.activity_standalone);

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        gs = (GlobalState) getApplication();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        this.savedInstanceState = savedInstanceState;


    }

    @Override
    public void onStart() {
        super.onStart();

        silentSignIn();
    }

    public void silentSignIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);

        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });


        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            SignInTask task = new SignInTask(this, acct.getEmail(), acct.getDisplayName(), this);
            task.execute();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(GenericConstants.RETURN_TO_UPLOAD_ACTIVITY, true);
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void callback(Object... args) {
        checkProUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        silentSignIn();
    }

    private void checkProUser() {
        authenticatedUser = gs.getAuthenticatedUser();
        if (authenticatedUser != null) {
            if (authenticatedUser.isProUser()) {

                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction().add(R.id.container, new UploadFragment()).commit();

                }
            } else {

                Toast.makeText(this, getString(R.string.functionality_available_in_pro), Toast.LENGTH_LONG).show();
                Intent appIntent = new Intent(this, MainActivity.class);
                startActivity(appIntent);
                finish();
            }
        }
    }
}
