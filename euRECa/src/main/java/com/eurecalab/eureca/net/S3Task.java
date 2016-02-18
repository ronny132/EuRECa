package com.eurecalab.eureca.net;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.eurecalab.eureca.R;
import com.eurecalab.eureca.aws.S3Manager;
import com.eurecalab.eureca.constants.S3Action;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Recording;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class S3Task extends AsyncTask<Void, Void, Void> implements TransferListener {
    private Activity context;
    private Recording recording;
//    private ProgressDialog dialog;
    private S3Manager manager;
    private Callable callable;
    private int action;
    private File toUpload;
    private List<String> list = null;

    public S3Task(Context context, Recording recording, File toUpload, Callable callable, int action) {
        this.context = (Activity) context;
        this.recording = recording;
        this.callable = callable;
        manager = new S3Manager(context);
        this.action = action;
        this.toUpload = toUpload;
    }

    public void download() {
        TransferObserver observer = manager.download(recording);
        observer.setTransferListener(this);
    }

    public void upload() {
        TransferObserver observer = manager.upload(recording, toUpload);
        observer.setTransferListener(this);
    }

    public void delete() {
        manager.delete(recording);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (action != S3Action.DELETE) {
//            dialog = new ProgressDialog(context);
            String message = "";
            switch (action) {
                case S3Action.DOWNLOAD:
                    message = context.getString(R.string.downloading);
                    break;
                case S3Action.UPLOAD:
                    message = context.getString(R.string.uploading);
                    break;
                case S3Action.LIST:
                    message = "Updating...";
                    break;
            }
//            dialog.setMessage(message);
//            if (!context.isFinishing()) {
//                dialog.show();
//            }
        }
        manager.connect();
    }

    @Override
    protected Void doInBackground(Void... params) {
        switch (action) {
            case S3Action.DOWNLOAD:
                download();
                break;
            case S3Action.UPLOAD:
                upload();
                break;
            case S3Action.DELETE:
                delete();
                break;
            case S3Action.LIST:
                list();
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(action == S3Action.LIST) {
            if (callable != null) {
                callable.callback(list);
            }
//            if (dialog != null && dialog.isShowing() && !context.isFinishing()) {
//                dialog.dismiss();
//            }
        }
    }

    private void list() {
        list = manager.list();
    }

    @Override
    public void onError(int id, Exception ex) {
//        if (dialog != null && dialog.isShowing() && !context.isFinishing()) {
//            dialog.dismiss();
//        }
        Toast.makeText(context, context.getString(R.string.download_error), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        int percentage = (int) (bytesCurrent / bytesTotal * 100);
//        dialog.setProgress(percentage);
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        if (state.equals(TransferState.COMPLETED)) {
//            if (dialog != null && dialog.isShowing() && !context.isFinishing()) {
//                dialog.dismiss();
//            }
            if(callable!= null){
                callable.callback();
            }
        }
    }
}
