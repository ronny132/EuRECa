package com.eurecalab.eureca.common;

import com.eurecalab.eureca.R;

import android.app.Activity;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.ImageView;

public class ColorCommon {
	
	public static void changeColor(Activity activity, ImageView view, boolean primary){
		Drawable drawable = view.getDrawable();
		TypedValue colorPrimary = new TypedValue();
        if(primary) {
            activity.getTheme().resolveAttribute(R.attr.colorPrimary, colorPrimary, true);
        }
        else{
            activity.getTheme().resolveAttribute(R.attr.colorAccent, colorPrimary, true);
        }
		drawable.setColorFilter(new PorterDuffColorFilter(colorPrimary.data, Mode.MULTIPLY));
		view.setImageDrawable(drawable);
	}

    public static void changeColor(Activity activity, MenuItem view, boolean primary){
        Drawable drawable = view.getIcon();
        TypedValue colorPrimary = new TypedValue();
        if(primary) {
            activity.getTheme().resolveAttribute(R.attr.colorPrimary, colorPrimary, true);
        }
        else{
            activity.getTheme().resolveAttribute(R.attr.colorAccent, colorPrimary, true);
        }
        drawable.setColorFilter(new PorterDuffColorFilter(colorPrimary.data, Mode.MULTIPLY));
        view.setIcon(drawable);
    }

}
