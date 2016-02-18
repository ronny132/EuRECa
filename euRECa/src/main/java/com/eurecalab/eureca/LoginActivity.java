package com.eurecalab.eureca;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eurecalab.eureca.common.Networking;
import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.net.SignInTask;
import com.eurecalab.eureca.ui.ThemeSwitcher;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

@SuppressLint("NewApi")
public class LoginActivity extends AppCompatActivity implements OnClickListener, GoogleApiClient.OnConnectionFailedListener, Callable {
    private SignInButton signInButton;
    private TextView mStatusTextView;
    private GoogleApiClient mGoogleApiClient;
    private boolean returnToUpload;

    public static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeSwitcher(this).onActivityCreateSetTheme();
        setContentView(R.layout.activity_standalone_no_toolbar);

        Intent intent = getIntent();
        returnToUpload = intent.getBooleanExtra(GenericConstants.RETURN_TO_UPLOAD_ACTIVITY, false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);

            LoginActivity activity = (LoginActivity) getActivity();

            activity.signInButton = (SignInButton) rootView.findViewById(R.id.sign_in_button);
            activity.signInButton.setSize(SignInButton.SIZE_WIDE);
            activity.signInButton.setOnClickListener(activity);

            activity.mStatusTextView = (TextView) rootView.findViewById(R.id.status_text_view);

            return rootView;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(signInButton)) {
            if (!Networking.isNetworkAvailable(this)) {
                Snackbar.make(signInButton, R.string.network_not_available, Snackbar.LENGTH_LONG).show();
            } else {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (!result.isSuccess()) {
                Snackbar.make(signInButton, R.string.unable_to_login, Snackbar.LENGTH_LONG).show();
            }
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Snackbar.make(signInButton, "Account: " + acct.getDisplayName(), Snackbar.LENGTH_LONG).show();
            SignInTask task = new SignInTask(this, acct.getEmail(), acct.getDisplayName(), this);
            task.execute();
        } else {
            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            signInButton.setVisibility(View.GONE);
        } else {
            mStatusTextView.setText("");
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!Networking.isNetworkAvailable(this)) {
            Snackbar.make(signInButton, R.string.network_not_available, Snackbar.LENGTH_LONG).show();
        } else {

            SignInTask task = new SignInTask(this, "ronnymeringolo@gmail.com", "Ronny Meringolo", this);
            task.execute();

//            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//            if (opr.isDone()) {
//                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//                // and the GoogleSignInResult will be available instantly.
//                Snackbar.make(signInButton, R.string.connecting_using_stored_credentials, Snackbar.LENGTH_LONG).show();
//
//                GoogleSignInResult result = opr.get();
//                handleSignInResult(result);
//            } else {
//                // If the user has not previously signed in on this device or the sign-in has expired,
//                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//                // single sign-on will occur in this branch.
//                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                    @Override
//                    public void onResult(GoogleSignInResult googleSignInResult) {
//                        handleSignInResult(googleSignInResult);
//                    }
//                });
//            }
        }
    }

    @Override
    public void callback(Object... args) {
        if (!returnToUpload) {
            Intent appIntent = new Intent(this, MainActivity.class);
            startActivity(appIntent);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (returnToUpload) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
