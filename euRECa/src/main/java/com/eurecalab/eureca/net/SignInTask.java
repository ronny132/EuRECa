package com.eurecalab.eureca.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.telecom.Call;
import android.widget.Toast;

import com.eurecalab.eureca.R;
import com.eurecalab.eureca.aws.DBManager;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by MeringoloRo on 24/01/2016.
 */
public class SignInTask extends AsyncTask<Void, Void, Void> implements GoogleApiClient.OnConnectionFailedListener {
    private GlobalState gs;
    private ProgressDialog dialog;
    private Activity context;
    private String email;
    private boolean loadFromAws;
    private String name;
    private Callable callable;

    public SignInTask(Activity context, String email, String name, Callable callable) {
        this.context = (FragmentActivity) context;
        gs = (GlobalState) ((Activity) context).getApplication();
        this.email = email;
        this.name = name;
        this.callable = callable;
    }

    @Override
    protected void onPreExecute() {
        loadFromAws = gs.getAuthenticatedUser() == null;
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.signing));
        if (!context.isFinishing()) {
            dialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (loadFromAws) {
            DBManager manager = new DBManager(context);
            manager.connect();
            User result = manager.loadUser(email);
            result.setDisplayName(name);
            gs.setAuthenticatedUser(result);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (!context.isFinishing() && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (callable != null) {
            callable.callback();
        }
//        Toast.makeText(context,email+" is "+(gs.getAuthenticatedUser().isProUser()?"pro user":"free user"),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
