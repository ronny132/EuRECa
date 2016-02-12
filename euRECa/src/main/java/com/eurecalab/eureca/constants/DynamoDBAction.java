package com.eurecalab.eureca.constants;

/**
 * Created by MeringoloRo on 27/01/2016.
 */
public interface DynamoDBAction {
    int RECORDING = 0;
    int RECORDING_HIT_COUNT = 1;
    int CATEGORY = 2;
    int CATEGORY_AND_RECORDING = 3;
    int DELETE_RECORDING = 4;
    int FIND_RECORDING = 5;
    int GET_USER_FAVORITES = 6;
    int GET_GLOBAL_FAVORITES = 7;
}
