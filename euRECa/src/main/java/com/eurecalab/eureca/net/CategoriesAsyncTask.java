package com.eurecalab.eureca.net;

import java.util.Collection;
import java.util.List;

import com.eurecalab.eureca.R;
import com.eurecalab.eureca.aws.DBManager;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class CategoriesAsyncTask extends AsyncTask<Void, Void, Void> {
    private Activity context;
    private GlobalState gs;
    private boolean reload;
//    private ProgressDialog dialog;
    private Callable callable;

    public CategoriesAsyncTask(Context context, Callable callable) {
        this.context = (Activity) context;
        gs = (GlobalState) ((Activity) context).getApplication();
        reload = true;
        this.callable = callable;
    }

    @Override
    protected void onPreExecute() {
        Collection<Category> storedCategories = gs.getCategories();
        reload = storedCategories == null || storedCategories.isEmpty();
//        dialog = new ProgressDialog(context);
//        dialog.setMessage(context.getString(R.string.updating));
//        if (!context.isFinishing()) {
//            dialog.show();
//        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (reload) {
            DBManager manager = new DBManager(context);
            manager.connect();
            User user = gs.getAuthenticatedUser();
            if(user == null){
                //TODO: utente non loggato? E' possibile?
            }
            else {
                manager.downloadCategories(gs.getCategories(), gs.getFilteredCategories(), user.getEmail());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
//        if (!context.isFinishing() && dialog.isShowing()) {
//            dialog.dismiss();
//        }
        if (callable != null) {
            callable.callback();
        }
        // Toast.makeText(context, toPopulate.size()+" Categorie caricate",
        // Toast.LENGTH_LONG).show();s
    }

}
