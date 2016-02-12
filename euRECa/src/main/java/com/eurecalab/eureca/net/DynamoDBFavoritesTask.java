package com.eurecalab.eureca.net;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.eurecalab.eureca.aws.DBManager;
import com.eurecalab.eureca.constants.DynamoDBAction;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.core.User;

import java.util.Date;
import java.util.List;

public class DynamoDBFavoritesTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private GlobalState gs;
    private User user;
    private int action;
    private Callable callable;
    private List<Recording> searchResult;
    private Date lowerBound;
    private int limit;

    public DynamoDBFavoritesTask(Context context, User user, Date lowerBound, int limit, Callable callable, int action) {
        this.context = context;
        gs = (GlobalState) ((Activity) context).getApplication();
        this.action = action;
        this.callable = callable;
        this.user = user;
        this.lowerBound = lowerBound;
        this.limit = limit;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DBManager manager = new DBManager(context);
        manager.connect();
        switch (action) {
            case DynamoDBAction.GET_USER_FAVORITES:
                searchResult = manager.getFavorites(user.getEmail(), lowerBound, limit);
                break;
            case DynamoDBAction.GET_GLOBAL_FAVORITES:
                searchResult = manager.getFavorites(null, lowerBound, limit);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (callable != null) {
            callable.callback(searchResult);
        }
    }

}
