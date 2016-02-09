package com.eurecalab.eureca.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eurecalab.eureca.R;
import com.eurecalab.eureca.UploadActivity;
import com.eurecalab.eureca.common.ActionCommon;
import com.eurecalab.eureca.common.ColorCommon;
import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.constants.DynamoDBAction;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.net.DynamoDBTask;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class RecordingAdapter extends
        RecyclerView.Adapter<RecordingAdapter.ViewChildHolder> implements DialogInterface.OnClickListener, Callable {
    private List<Recording> recordings;
    private int layoutID;
    private FragmentActivity context;
    private GlobalState globalState;
    private Recording selectedRecording;
    private RecyclerView list;

    public RecordingAdapter(Context context, int resource, List<Recording> objects, RecyclerView list) {
        super();
        this.recordings = objects;
        this.layoutID = resource;
        if (context instanceof FragmentActivity) {
            this.context = (FragmentActivity) context;
            globalState = (GlobalState) ((Activity) context).getApplication();
        }
        this.list = list;
    }

    public static class ViewChildHolder extends RecyclerView.ViewHolder
            implements Observer {
        protected View rootView;
        protected TextView name;
        protected FloatingActionButton preview;
        protected FloatingActionButton share;
        protected ImageView more;
        protected Context context;

        public ViewChildHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            name = (TextView) itemView.findViewById(R.id.soundName);
            preview = (FloatingActionButton) itemView
                    .findViewById(R.id.soundPlay);
            share = (FloatingActionButton) itemView
                    .findViewById(R.id.soundShare);
            more = (ImageView) itemView.findViewById(R.id.more);
        }

        public ViewChildHolder(View itemView, Context context) {
            this(itemView);
            this.context = context;
        }

        @Override
        public void update(Observable observable, Object data) {
            Activity activity = (Activity) context;

            Recording recording = (Recording) observable;
            if (recording.isPlaying()) {
                preview.setImageResource(R.drawable.stop);
                ColorCommon.changeColor(activity, preview, true);

            } else {
                preview.setImageResource(R.drawable.play);
                ColorCommon.changeColor(activity, preview, true);
            }
        }

    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    @Override
    public void onBindViewHolder(final ViewChildHolder holder,
                                 final int position) {
        final Recording recording = recordings.get(position);
        if (recording == null) return;
        recording.deleteObservers();
        recording.addObserver(holder);
        holder.name.setText(recording.getName());

        holder.preview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (recording.isPlaying()) {
                    try {
                        globalState.stopPlayingSound();
                        globalState.setPlayingRecording(null);
                    } catch (IllegalArgumentException | SecurityException
                            | IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        globalState.stopPlayingSound();
                        recording.setContext(context);
                        globalState.setPlayingRecording(recording);
                    } catch (IllegalArgumentException | SecurityException
                            | IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        ColorCommon.changeColor(context, holder.preview, true);

        holder.share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActionCommon.share(recording, context, globalState.getAuthenticatedUser());
            }
        });

        ColorCommon.changeColor(context, holder.share, true);

        if (recording.getOwner().equals(globalState.getAuthenticatedUser().getEmail())) {
            recording.setShowMore(true);
        }

        if (recording.isShowMore()) {
            TypedValue colorPrimary = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorPrimary, colorPrimary, true);

            boolean primary = true;

            if(colorPrimary.data == ContextCompat.getColor(context, R.color.white)){
                primary = false;
            }

            ColorCommon.changeColor(context, holder.more, primary);

            holder.more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedRecording = recording;
                    showMorePopup(v);
                }
            });

            holder.more.setVisibility(View.VISIBLE);

        } else{
            holder.more.setVisibility(View.GONE);
        }

    }

    public void showMorePopup(View v) {
        IconizedMenu popup = new IconizedMenu(context, v);
        popup.getMenuInflater().inflate(R.menu.more_menu, popup.getMenu());

        int count = popup.getMenu().size();

        TypedValue colorPrimary = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, colorPrimary, true);
        boolean primary = true;

        if (colorPrimary.data == ContextCompat.getColor(context, R.color.white)) {
            primary = false;
        }

        for (int i = 0; i < count; i++) {
            MenuItem item = popup.getMenu().getItem(i);
            ColorCommon.changeColor(context, item, primary);
        }

        popup.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_edit:
                        Intent intent = new Intent(context, UploadActivity.class);
                        intent.putExtra(GenericConstants.SELECTED_RECORDING, selectedRecording);
                        context.startActivity(intent);
                        selectedRecording.getCategory().setRecordingListVisible(false);
                        list.setVisibility(View.GONE);
                        return true;
                    case R.id.menu_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(context.getString(R.string.are_you_sure)).setPositiveButton(context.getString(R.string.yes), RecordingAdapter.this)
                                .setNegativeButton(context.getString(R.string.no), RecordingAdapter.this).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                DynamoDBTask persister = new DynamoDBTask(context, selectedRecording, selectedRecording.getCategory(), null, this, DynamoDBAction.DELETE_RECORDING);
                persister.execute();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    }

    @Override
    public ViewChildHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutID,
                parent, false);

        ViewChildHolder holder = new ViewChildHolder(v, context);
        return holder;
    }

    public void callback(Object... args) {
        recordings.remove(selectedRecording);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                ViewGroup.LayoutParams params = list.getLayoutParams();
                params.height = (int) (recordings.size() * 70.5 * metrics.density);
                list.setLayoutParams(params);
            }
        });
    }
}
