package com.eurecalab.eureca.common;

import java.io.File;
import java.util.Date;

import com.eurecalab.eureca.R;
import com.eurecalab.eureca.constants.DynamoDBAction;
import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.constants.S3Action;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.core.Share;
import com.eurecalab.eureca.core.User;
import com.eurecalab.eureca.net.DynamoDBTask;
import com.eurecalab.eureca.net.S3Task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ActionCommon {

	public static void share(final Recording recording, final Context context, final User user, View view) {
		File path = new File(recording.getPath());
		if (!path.exists()) {
			if(view!=null) {
                final Snackbar snackbar = Snackbar.make(view, R.string.downloading, Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                new S3Task(context, recording, null, new Callable() {

                    @Override
                    public void callback(Object... args) {
                        if(snackbar.isShownOrQueued()){
                            snackbar.dismiss();
                        }
                        shareAudio(recording, context, user);
                    }
                }, S3Action.DOWNLOAD).execute();
            }
		}
		else{
			shareAudio(recording, context, user);
		}
	}

	private static void shareAudio(Recording recording, Context context, User user) {
		Intent sendIntent = new Intent();
		Uri uri = Uri.fromFile(new File(recording.getPath()));

		Log.v("PATH", recording.getPath());
		Log.v("URI", uri.toString());

		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.setType("audio/*");
		sendIntent.putExtra(Intent.EXTRA_STREAM, uri);

		context.startActivity(Intent.createChooser(sendIntent, context
                .getResources().getText(R.string.share)));

        Share share = new Share();
        share.setUsername(user.getEmail());
        share.setRecording(recording);

        Date date = new Date();

        share.setDate(date);
        share.setId(System.currentTimeMillis());

		DynamoDBTask persister = new DynamoDBTask(context, recording, null, share, null, DynamoDBAction.RECORDING_HIT_COUNT);
		persister.execute();
	}

	public static void hideKeyboard(Activity activity){
		InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (activity.getCurrentFocus() == null) {
			return;
		}
		inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

}
