package com.eurecalab.eureca.net;

import com.eurecalab.eureca.aws.DBManager;
import com.eurecalab.eureca.constants.DynamoDBAction;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.core.Share;

import android.content.Context;
import android.os.AsyncTask;

public class DynamoDBTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private Category category;
    private Recording recording;
    private Share share;
    private int action;
    private Callable callable;
    private Recording foundRecording;

    public DynamoDBTask(Context context, Recording recording, Category category, Share share, Callable callable, int action) {
        this.context = context;
        this.recording = recording;
        this.category = category;
        this.action = action;
        this.callable = callable;
        this.share = share;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DBManager manager = new DBManager(context);
        manager.connect();
        switch (action) {
            case DynamoDBAction.RECORDING:
                manager.storeRecording(recording);
                break;
            case DynamoDBAction.CATEGORY:
                manager.storeCategory(category);
                break;
            case DynamoDBAction.CATEGORY_AND_RECORDING:
                manager.storeRecordingAndCategory(recording, category);
                break;
            case DynamoDBAction.RECORDING_HIT_COUNT:
                manager.shareRecording(share);
                break;
            case DynamoDBAction.DELETE_RECORDING:
                manager.deleteRecording(recording, category);
                break;
            case DynamoDBAction.FIND_RECORDING:
                foundRecording = manager.findRecording(recording);
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (callable != null) {
            if (action == DynamoDBAction.FIND_RECORDING) {
                boolean ok = foundRecording == null ? true : false;
                callable.callback(ok);
            } else {
                callable.callback();
            }
        }
    }

}
