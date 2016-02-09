package com.eurecalab.eureca.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Observable;

import com.eurecalab.eureca.common.FileCommon;
import com.eurecalab.eureca.constants.S3Action;
import com.eurecalab.eureca.net.S3Task;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

@DynamoDBDocument
@DynamoDBTable(tableName = "Sound")
public class Recording extends Observable implements Callable, Serializable{
	private String name;
	private String fileName;
	private transient MediaPlayer mediaPlayer;
	private String path;
	private transient Context context;
	private String tags;
	private boolean playing;
	private String owner;
	private Category category;
	private String categoryName;
	private boolean showMore;
	
	public Recording() {
		playing = false;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@DynamoDBHashKey (attributeName = "Name")
	public String getName() {
		return name;
	}

	public String getFileName() {
		return fileName;
	}

	public Recording(String name, String fileName, Context context, String owner) {
		this.name = name;
		this.fileName = fileName;
		this.context = context;
		this.path = FileCommon.getPath(context, fileName).getAbsolutePath();
		this.mediaPlayer = new MediaPlayer();
		this.owner = owner;
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}
	
	@DynamoDBAttribute(attributeName = "Owner")
	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	@DynamoDBIgnore
	public String getPath() {
		return path;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@DynamoDBAttribute(attributeName = "Tags")
	public String getTags() {
		return tags;
	}

	public void resetMediaPlayer() {
		if (mediaPlayer != null) {
			this.mediaPlayer.stop();
			this.mediaPlayer.reset();
			this.mediaPlayer.release();
		}
		this.mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		playing = false;
		setChanged();
		notifyObservers();
	}

	public void play() {
		if (mediaPlayer == null) {
			resetMediaPlayer();
		}
		File file = new File(path);
		if(!file.exists()){
			new S3Task(context, this, null, this, S3Action.DOWNLOAD).execute();
		}
		else{
			playSound();
			playing = true;
			setChanged();
			notifyObservers();
		}
	}
	
	private void playSound(){
		try {
//			Log.v("PATH", path);
			mediaPlayer.setDataSource(context, Uri.parse(path));
			mediaPlayer.prepare();
			mediaPlayer.start();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					playing = false;
					setChanged();
					notifyObservers();
				}
			});
		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setContext(Context context) {
		this.context = context;
		this.path = FileCommon.getPath(context, fileName).getAbsolutePath();
	}

	@DynamoDBIgnore
	public boolean isPlaying() {
		return playing;
	}
	
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	@Override
	public void callback(Object ... args) {
		playSound();
		playing = true;
		setChanged();
		notifyObservers();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Recording)) {
			return false;
		}
		Recording other = (Recording) o;
		return other.getName().equals(name);
	}

    @DynamoDBIgnore
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.categoryName = category.getName();
    }

    @DynamoDBAttribute(attributeName = "CategoryName")
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @DynamoDBIgnore
    public boolean isShowMore() {
        return showMore;
    }

    public void setShowMore(boolean showMore) {
        this.showMore = showMore;
    }
}
