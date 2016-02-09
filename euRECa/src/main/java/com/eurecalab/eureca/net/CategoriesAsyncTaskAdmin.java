package com.eurecalab.eureca.net;

import java.util.List;

import com.eurecalab.eureca.aws.DBManager;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.GlobalState;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class CategoriesAsyncTaskAdmin extends AsyncTask<Void, Void, Void> {
	private Activity context;
	private GlobalState gs;
	private Callable callable;

	public CategoriesAsyncTaskAdmin(Context context, Callable callable) {
		this.context = (Activity) context;
		gs = (GlobalState) ((Activity) context).getApplication();
		this.callable = callable;
	}

	@Override
	protected Void doInBackground(Void... params) {
		DBManager manager = new DBManager(context);
		manager.connect();
		manager.downloadCategories(gs.getCategories());
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		callable.callback();
	}

}
