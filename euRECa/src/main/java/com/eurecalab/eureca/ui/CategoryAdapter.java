package com.eurecalab.eureca.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.eurecalab.eureca.R;
import com.eurecalab.eureca.UploadActivity;
import com.eurecalab.eureca.common.ActionCommon;
import com.eurecalab.eureca.common.ColorCommon;
import com.eurecalab.eureca.common.FileCommon;
import com.eurecalab.eureca.constants.DynamoDBAction;
import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.net.DynamoDBTask;
import com.eurecalab.eureca.net.ImageDownloader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class CategoryAdapter extends
        ExpandableRecyclerAdapter<CategoryAdapter.CategoryViewHolder, CategoryAdapter.RecordingViewHolder> implements DialogInterface.OnClickListener, Callable {

    private Activity context;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    private GlobalState globalState;
    private Recording selectedRecording;
    private LayoutInflater inflater;
    private int selectedCategoryPosition;
    private int selectedRecordingPosition;
    private Snackbar snackbar;

    public CategoryAdapter(Activity context, List<Category> objects) {
        super(objects);

        this.context = context;
        if (context instanceof FragmentActivity) {
            this.context = context;
            globalState = (GlobalState) context.getApplication();
        }
        this.options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.category_default)
                .showImageForEmptyUri(R.drawable.category_default) // resource or drawable
                .showImageOnFail(R.drawable.category_default) // resource or drawable
                .resetViewBeforeLoading(false) // default
                .cacheInMemory(true) // default
                .cacheOnDisc(true) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .build();
        imageLoader = ImageLoader.getInstance();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                DynamoDBTask persister = new DynamoDBTask(context, selectedRecording, selectedRecording.getCategory(), null, this, DynamoDBAction.DELETE_RECORDING);
                persister.execute();
                notifyChildItemRemoved(selectedCategoryPosition, selectedRecordingPosition);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    }

    public class CategoryViewHolder extends ParentViewHolder {
        protected TextView name;
        protected ImageView icon;
        protected View rootView;
        protected View handle;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            this.name = (TextView) itemView.findViewById(R.id.categoryName);
            this.icon = (ImageView) itemView.findViewById(R.id.categoryIcon);
            this.handle = itemView.findViewById(R.id.categoryHandle);
        }

    }

    public static class RecordingViewHolder extends ChildViewHolder
            implements Observer {
        protected View rootView;
        protected TextView name;
        protected FloatingActionButton preview;
        protected FloatingActionButton share;
        protected ImageView more;
        protected Snackbar snackbar;

        public RecordingViewHolder(View itemView) {
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
            Recording recording = (Recording) observable;
            Activity activity = (Activity) recording.getContext();

            if (recording.isPlaying()) {
                if(snackbar != null && snackbar.isShownOrQueued()){
                    snackbar.dismiss();
                }
                preview.setImageResource(R.drawable.stop);
                ColorCommon.changeColor(activity, preview, true);

            } else {
                preview.setImageResource(R.drawable.play);
                ColorCommon.changeColor(activity, preview, true);
            }
        }

    }

    private void downloadImage(String filename, ImageView imageView) {
        ImageDownloader downloader = new ImageDownloader(context, filename, this, imageView);
        downloader.execute();
    }

    public void callback(Object... args) {
        if (args.length == 2 && args[0] instanceof File && args[1] instanceof ImageView) {
            File path = (File) args[0];
            ImageView imageView = (ImageView) args[1];
            loadImage(path, imageView);
        } else {
//            notifyItemRemoved(mItemList.indexOf(selectedRecording));
//            mItemList.remove(selectedRecording);
        }
    }

    private void loadImage(File path, ImageView imageView) {
        imageLoader.displayImage("file://" + path.getAbsolutePath(), imageView, options);
    }

    @Override
    public CategoryViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.category_layout_expanded,
                viewGroup, false);

        CategoryViewHolder holder = new CategoryViewHolder(v);
        return holder;
    }

    @Override
    public RecordingViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.recording_layout,
                viewGroup, false);

        RecordingViewHolder holder = new RecordingViewHolder(v);
        return holder;
    }

    @Override
    public void onBindParentViewHolder(final CategoryViewHolder holder, int position, ParentListItem object) {
        final Category category = (Category) object;
        holder.name.setText(category.getName());
        holder.handle.setBackgroundColor(Color.parseColor(category.getColorHex()));

        File imagePath = FileCommon.getPath(context, category.getIconFileName());
        if (!imagePath.exists()) {
            downloadImage(category.getIconFileName(), holder.icon);
        } else {
            loadImage(imagePath, holder.icon);
        }
    }

    @Override
    public void onBindChildViewHolder(final RecordingViewHolder holder, int position, Object object) {
        final Recording recording = (Recording) object;
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
                        snackbar = Snackbar.make(holder.preview, R.string.downloading, Snackbar.LENGTH_INDEFINITE);
                        holder.snackbar = snackbar;
                        snackbar.show();
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
                ActionCommon.share(recording, context, globalState.getAuthenticatedUser(), holder.share);
            }
        });

        ColorCommon.changeColor(context, holder.share, true);

        if (recording.getOwner().equals(globalState.getAuthenticatedUser().getEmail())) {
            if (recording.getCategory() == null || recording.getCategory().getName().equals(GenericConstants.FAVORITES_CATEGORY)) {
                recording.setShowMore(false);
            } else {
                recording.setShowMore(true);
            }
        }

        if (recording.isShowMore()) {
            TypedValue colorPrimary = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorPrimary, colorPrimary, true);

            boolean primary = true;

            if (colorPrimary.data == ContextCompat.getColor(context, R.color.white)) {
                primary = false;
            }

            ColorCommon.changeColor(context, holder.more, primary);

            holder.more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedRecording = recording;
                    Category selectedCategory = recording.getCategory();
                    selectedRecordingPosition = selectedCategory.getRecordings().indexOf(recording);
                    selectedCategoryPosition = selectedCategory.getSortIndex();
                    showMorePopup(v);
                }
            });

            holder.more.setVisibility(View.VISIBLE);

        } else {
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
                        notifyChildItemRemoved(selectedCategoryPosition, selectedRecordingPosition);
                        return true;
                    case R.id.menu_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(context.getString(R.string.are_you_sure)).setPositiveButton(context.getString(R.string.yes), CategoryAdapter.this)
                                .setNegativeButton(context.getString(R.string.no), CategoryAdapter.this).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

}
