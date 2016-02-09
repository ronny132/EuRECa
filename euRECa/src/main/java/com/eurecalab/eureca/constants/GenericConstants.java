package com.eurecalab.eureca.constants;

import java.text.SimpleDateFormat;

/**
 * Created by MeringoloRo on 25/01/2016.
 */
public interface GenericConstants {
    String DATE_FORMAT = "yyyyMMdd HH:mm:ss";
    SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
    String DATE_INFINITE = "39991231 23:59:59";
    long MAX_ACCEPTED_FILESIZE = 307200;
    String RETURN_TO_UPLOAD_ACTIVITY = "upload";
    int YOUTUBE_SORT_INDEX = 0;
    int DISNEY_SORT_INDEX = 1;
    int MOVIE_SORT_INDEX = 2;
    int TV_SERIES_SORT_INDEX = 3;
    int CARTOONS_SORT_INDEX = 4;
    int SOCCER_SORT_INDEX = 5;
    int SOUND_EFFECTS_SORT_INDEX = 6;
    int POLITICS_SORT_INDEX = 7;
    int DIALETTI_SORT_INDEX = 8;
    int FAVORITES_SORT_INDEX = 9;
    String SELECTED_RECORDING = "selected recording";
}
