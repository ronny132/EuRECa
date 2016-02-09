package com.eurecalab.eureca.ui;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.eurecalab.eureca.R;
import com.eurecalab.eureca.common.FileCommon;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.net.ImageDownloader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends
        RecyclerView.Adapter<CategoryAdapter.ViewGroupHolder> implements Callable{

    private int layoutID;
    private Collection<Category> items;
    private Activity context;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    public CategoryAdapter(Activity context, int textViewResourceId,
                           Collection<Category> objects) {
        super();
        layoutID = textViewResourceId;
        this.items = objects;
        this.context = context;
        this.options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.icon)
                .showImageForEmptyUri(R.drawable.icon) // resource or drawable
                .showImageOnFail(R.drawable.icon) // resource or drawable
                .resetViewBeforeLoading(false) // default
                .cacheInMemory(true) // default
                .cacheOnDisc(true) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .build();
        imageLoader = ImageLoader.getInstance();
    }

    public class ViewGroupHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected ImageView icon;
        protected View rootView;
        protected RecyclerView soundListView;
        protected View handle;

        public ViewGroupHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            this.name = (TextView) itemView.findViewById(R.id.categoryName);
            this.icon = (ImageView) itemView.findViewById(R.id.categoryIcon);
            this.soundListView = (RecyclerView) itemView
                    .findViewById(R.id.soundsListView);
            this.soundListView.setLayoutManager(new LinearLayoutManager(context));
            this.handle = itemView.findViewById(R.id.categoryHandle);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private Category getItem(int position){
        Iterator<Category> it = items.iterator();
        int count = 0;
        while(it.hasNext()){
            Category next = it.next();
            if(count == position){
                return next;
            }
            count++;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewGroupHolder holder, final int position) {
        final Category category = getItem(position);
        holder.name.setText(category.getName());

        File imagePath = FileCommon.getPath(context, category.getIconFileName());
        if(!imagePath.exists()){
            downloadImage(category.getIconFileName(), holder.icon);
        }
        else{
            loadImage(imagePath, holder.icon);
        }

        List<Recording> recordings = category.getRecordings();
        RecordingAdapter childAdapter = new RecordingAdapter(context, R.layout.recording_layout, recordings, holder.soundListView);
        holder.soundListView.setAdapter(childAdapter);

        holder.handle.setBackgroundColor(Color.parseColor(category.getColorHex()));

        if(category.isRecordingListVisible()){
            holder.soundListView.setVisibility(View.VISIBLE);
            LinearLayoutManager lm = new MyLinearLayoutManager(context, MyLinearLayoutManager.VERTICAL, false);
            holder.soundListView.setLayoutManager(lm);
            holder.soundListView.smoothScrollToPosition(category.getRecordings().size() - 1);
        }
        else{
            holder.soundListView.setVisibility(View.GONE);
        }

        holder.rootView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(category.isRecordingListVisible()){
                    category.setRecordingListVisible(false);
                    holder.soundListView.setVisibility(View.GONE);
                }
                else{
                    category.setRecordingListVisible(true);
                    holder.soundListView.setVisibility(View.VISIBLE);
                    LinearLayoutManager lm = new MyLinearLayoutManager(context, MyLinearLayoutManager.VERTICAL, false);
                    holder.soundListView.setLayoutManager(lm);
                }
            }
        });
    }

    private void downloadImage(String filename, ImageView imageView) {
        ImageDownloader downloader = new ImageDownloader(context, filename, this, imageView);
        downloader.execute();
    }
    
    public void callback(Object ... args){
        if(args.length == 2 && args[0] instanceof File && args[1] instanceof ImageView){
            File path = (File) args[0];
            ImageView imageView = (ImageView) args[1];
            loadImage(path, imageView);
        }
    }

    private void loadImage(File path, ImageView imageView) {
        imageLoader.displayImage("file://"+path.getAbsolutePath(), imageView, options);
    }

    @Override
    public ViewGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutID,
                parent, false);

        ViewGroupHolder holder = new ViewGroupHolder(v);
        return holder;
    }
}
