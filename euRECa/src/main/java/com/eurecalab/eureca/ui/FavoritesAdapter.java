package com.eurecalab.eureca.ui;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eurecalab.eureca.R;
import com.eurecalab.eureca.common.ActionCommon;
import com.eurecalab.eureca.common.ColorCommon;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.Recording;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private List<Recording> items;
    private Activity context;
    private LayoutInflater inflater;
    private GlobalState globalState;

    public FavoritesAdapter(List<Recording> items, Activity context) {
        this.items = items;
        this.context = context;
        inflater = LayoutInflater.from(context);
        globalState = (GlobalState) context.getApplication();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements Observer {
        protected View rootView;
        protected TextView name;
        protected FloatingActionButton preview;
        protected FloatingActionButton share;
        protected ImageView more;
        protected Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            name = (TextView) itemView.findViewById(R.id.soundName);
            preview = (FloatingActionButton) itemView
                    .findViewById(R.id.soundPlay);
            share = (FloatingActionButton) itemView
                    .findViewById(R.id.soundShare);
            more = (ImageView) itemView.findViewById(R.id.more);
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
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.recording_layout,
                viewGroup, false);

        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Recording recording = items.get(position);
        if (recording == null) return;
        recording.deleteObservers();
        recording.addObserver(holder);
        holder.name.setText(recording.getName());

        holder.preview.setOnClickListener(new View.OnClickListener() {

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

        holder.share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActionCommon.share(recording, context, globalState.getAuthenticatedUser());
            }
        });

        ColorCommon.changeColor(context, holder.share, true);

        holder.more.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
