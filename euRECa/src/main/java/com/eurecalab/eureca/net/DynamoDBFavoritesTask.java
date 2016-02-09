package com.eurecalab.eureca.net;

import android.content.Context;
import android.os.AsyncTask;

import com.eurecalab.eureca.aws.DBManager;
import com.eurecalab.eureca.constants.DynamoDBAction;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.core.User;

import java.util.List;

public class DynamoDBFavoritesTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private User user;
    private int action;
    private Callable callable;
    private List<Recording> searchResult;

    public DynamoDBFavoritesTask(Context context, User user, Callable callable, int action) {
        this.context = context;
        this.action = action;
        this.callable = callable;
        this.user = user;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DBManager manager = new DBManager(context);
        manager.connect();
        switch (action) {
            case DynamoDBAction.GET_USER_FAVORITES:
                searchResult = manager.getUserFavorites(user.getEmail());
                break;
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
