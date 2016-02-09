package com.eurecalab.eureca.ui;

import com.eurecalab.eureca.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;

public class ThemeSwitcher {
	private int myColor;
	private boolean isReverse;
	private Activity activity;
	
	public ThemeSwitcher(Activity activity) {
		this.activity = activity;
		SharedPreferences sharedPreferences = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		myColor = sharedPreferences.getInt(
				activity.getString(R.string.saved_color),
				R.color.color_primary_light_blue);
		isReverse = sharedPreferences.getBoolean(
				activity.getString(R.string.saved_reverse), false);
	}

	public void changeToTheme(int color,
			boolean reverse) {
		myColor = color;
		isReverse = reverse;
		
		activity.finish();
		activity.startActivity(new Intent(activity, activity.getClass()));
	}

	public void onActivityCreateSetTheme() {
		Resources resources = activity.getResources();
		TypedArray colorArray = resources
				.obtainTypedArray(R.array.theme_colors);
		TypedArray themesArray = resources.obtainTypedArray(R.array.themes);
		TypedArray reverseThemesArray = resources
				.obtainTypedArray(R.array.themes_reverse);

		

		for (int i = 0; i < colorArray.length(); i++) {
			int color = colorArray.getColor(i, -1);
			if (color == myColor) {
				if (isReverse) {
					activity.setTheme(reverseThemesArray.getResourceId(i,
							R.style.AppThemeReverse));
				} else {
					activity.setTheme(themesArray.getResourceId(i,
							R.style.AppThemeDefault));
				}
			}
		}

		colorArray.recycle();
		themesArray.recycle();
		reverseThemesArray.recycle();
	}

}
