package com.eurecalab.eureca.constants;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    int DEFAULT_USER_SEARCH_LIMIT = 15;
    int DEFAULT_SEARCH_LIMIT = 15;

    String YOUTUBE_CATEGORY = "Youtube";
    String DISNEY_CATEGORY = "Disney";
    String MOVIE_CATEGORY = "Film";
    String TV_SERIES_CATEGORY = "Serie TV";
    String CARTOONS_CATEGORY = "Cartoni";
    String SOCCER_CATEGORY = "Calcio";
    String SOUND_EFFECTS_CATEGORY = "Effetti Sonori";
    String POLITICS_CATEGORY = "Politici";
    String DIALETTI_CATEGORY = "Dialetti";
    String FAVORITES_CATEGORY = "Preferiti";
}
