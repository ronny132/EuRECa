package com.eurecalab.eureca.core;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.eurecalab.eureca.constants.GenericConstants;

import java.text.ParseException;
import java.util.Date;

@DynamoDBTable(tableName = "Share")
public class Share {
    private long id;
    private String username;
    private Recording recording;
    private Date date;
    private String shareDate;
    private String recordingName;

    public Share() {
    }

    @DynamoDBHashKey(attributeName = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "Username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBAttribute(attributeName = "Recording")
    public Recording getRecording() {
        return recording;
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
        this.recordingName = recording.getName();
    }

    @DynamoDBAttribute(attributeName = "RecordingName")
    public String getRecordingName() {
        return recordingName;
    }

    public void setRecordingName(String recordingName) {
        this.recordingName = recordingName;
    }

    @DynamoDBIgnore
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        this.shareDate = GenericConstants.DATE_FORMATTER.format(date);
    }

    @DynamoDBAttribute(attributeName = "ShareDate")
    public String getShareDate() {
        return shareDate;
    }

    public void setShareDate(String shareDate) {
        this.shareDate = shareDate;
        try {
            this.date = GenericConstants.DATE_FORMATTER.parse(shareDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
