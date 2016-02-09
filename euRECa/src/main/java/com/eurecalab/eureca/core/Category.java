package com.eurecalab.eureca.core;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "Category")
public class Category implements Comparable<Category>, Serializable{
	
	private String name;
	private String iconFileName;
	private List<Recording> recordings;
	private String colorHex;
	private int sortIndex;
	private boolean recordingListVisible;
	
	public Category() {
		recordings = new LinkedList<Recording>();
        recordingListVisible = false;
	}
	
	public Category(String name) {
		this.name = name;
		recordings = new LinkedList<Recording>();
        recordingListVisible = false;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setRecordings(List<Recording> recordings) {
		this.recordings = recordings;
	}
	
	@DynamoDBHashKey (attributeName = "Name")
	public String getName() {
		return name;
	}
	
	@DynamoDBAttribute (attributeName = "Sounds")
	public List<Recording> getRecordings() {
		return recordings;
	}
	
	public void addRecording(Recording recording){
		recordings.add(recording);
	}
	
	public boolean removeRecording(Recording recording){
		return recordings.remove(recording);
	}

	public Recording getRercordingAt(int childPosition) {
		return recordings.get(childPosition);
	}

	public int size() {
		return recordings.size();
	}
	
	@DynamoDBAttribute(attributeName = "ColorHex")
	public String getColorHex() {
		return colorHex;
	}
	
	public void setColorHex(String colorHex) {
		this.colorHex = colorHex;
	}
	
	public void setIconFileName(String iconFileName) {
		this.iconFileName = iconFileName;
	}
	
	@DynamoDBAttribute(attributeName = "IconFileName")
	public String getIconFileName() {
		return iconFileName;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Category)) {
			return false;
		}
		Category other = (Category) o;
		return other.getName().equals(name);
	}

    @DynamoDBAttribute(attributeName = "SortIndex")
    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }


    @Override
    public int compareTo(Category another) {
        int otherSortIndex = another.getSortIndex();
        return sortIndex - otherSortIndex;
    }

    public void setRecordingListVisible(boolean recordingListVisible) {
        this.recordingListVisible = recordingListVisible;
    }

    @DynamoDBIgnore
    public boolean isRecordingListVisible() {
        return recordingListVisible;
    }
}
