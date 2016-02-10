package com.eurecalab.eureca.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.widget.ImageView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.eurecalab.eureca.R;
import com.eurecalab.eureca.aws.S3Manager;
import com.eurecalab.eureca.common.FileCommon;
import com.eurecalab.eureca.core.Callable;

import java.io.File;

public class ImageDownloader extends AsyncTask<Void, Void, Void> implements TransferListener {
    private Activity context;
    private String imageName;
    private S3Manager manager;
    private Callable callable;
    private File path;
    private ImageView view;

    public ImageDownloader(Context context, String url, Callable callable, ImageView view) {
        this.context = (Activity) context;
        this.imageName = url;
        this.callable = callable;
        manager = new S3Manager(context);
        this.path = FileCommon.getPath(context, imageName);
        this.view = view;
    }

    public void download() {
        TransferObserver observer = manager.downloadImage(imageName, path);
        observer.setTransferListener(this);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        manager.connect();
    }

    @Override
    protected Void doInBackground(Void... params) {
        download();
        return null;
    }

    @Override
    public void onError(int id, Exception ex) {
        Snackbar.make(view, context.getString(R.string.download_error), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        int percentage = (int) (bytesCurrent / bytesTotal * 100);
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        if (state.equals(TransferState.COMPLETED)) {
            if (callable != null) {
                callable.callback(path, view);
            }
        }
    }
}
