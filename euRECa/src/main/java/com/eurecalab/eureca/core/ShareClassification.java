package com.eurecalab.eureca.core;

public class ShareClassification implements Comparable<ShareClassification>{
    private int shareCount;
    private Recording recording;

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public Recording getRecording() {
        return recording;
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShareClassification that = (ShareClassification) o;

        return !(recording != null ? !recording.equals(that.recording) : that.recording != null);

    }

    @Override
    public int hashCode() {
        return recording != null ? recording.hashCode() : 0;
    }

    @Override
    public int compareTo(ShareClassification another) {
        int otherShareCount = another.shareCount;
        return otherShareCount - shareCount;
    }
}
