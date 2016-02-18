package com.eurecalab.eureca.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.eurecalab.eureca.R;
import com.eurecalab.eureca.aws.DBManager;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class UpgradeTask extends AsyncTask<Void, Void, Void> {
    private GlobalState gs;
    private Activity context;

    public UpgradeTask(Activity context) {
        this.context = context;
        gs = (GlobalState) context.getApplication();
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Void... params) {
        DBManager manager = new DBManager(context);
        manager.connect();
        manager.storeUser(gs.getAuthenticatedUser());
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

}
