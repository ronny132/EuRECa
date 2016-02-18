package com.eurecalab.eureca.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import com.eurecalab.eureca.constants.FragmentConstants;
import com.eurecalab.eureca.constants.GenericConstants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class GlobalState extends Application {
    private Recording playingRecording;
    private List<Category> filteredCategories;
    private Collection<Category> categories;
    private User authenticatedUser;

    private Fragment[] fragments;

    @Override
    public void onCreate() {
        super.onCreate();
        filteredCategories = new LinkedList<>();
        categories = new TreeSet<>();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .memoryCacheExtraOptions(480, 800)
                .discCacheExtraOptions(480, 800, Bitmap.CompressFormat.PNG, 75, null)
                .threadPoolSize(3)
                        // default
                .threadPriority(Thread.NORM_PRIORITY - 1)
                        // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                        // default
                .imageDownloader(
                        new BaseImageDownloader(getApplicationContext())) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs().build();

        ImageLoader.getInstance().init(config);

        fragments = new Fragment[FragmentConstants.FRAGMENT_ARRAY_LENGTH];
    }

    public void stopPlayingSound() throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        if (playingRecording != null) {
            playingRecording.resetMediaPlayer();
        }
    }

    public void setPlayingRecording(Recording playingRecording) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        this.playingRecording = playingRecording;
        if (playingRecording != null) {
            playingRecording.play();
        }
    }

    public void setFilteredCategories(List<Category> categories) {
        this.filteredCategories = categories;
    }

    public List<Category> getFilteredCategories() {
        return filteredCategories;
    }

    public Collection<Category> getCategories() {
        return categories;
    }

    public void setCategories(Collection<Category> categories) {
        this.categories = categories;
    }

    public void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setFragmentAt(Fragment fragment, int position) {
        fragments[position] = fragment;
    }

    public Fragment getFragmentAt(int position){
        return fragments[position];
    }

    public Fragment getCurrentFragment(){
        for (int i = 0; i < fragments.length; i++) {
            Fragment f = fragments[i];
            if(f.isVisible()){
                return f;
            }
        }
        return null;
    }
}
