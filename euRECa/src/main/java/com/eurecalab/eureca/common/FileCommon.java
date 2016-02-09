package com.eurecalab.eureca.common;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class FileCommon {

	public static File getPath(Context context, String filename) {
		File result;
		if (isExternalStorageWritable()) {
			File sdCard = Environment.getExternalStorageDirectory();
//            String sdCard = "/sdcard";
			File mainDir = new File(sdCard, context.getFilesDir().getAbsolutePath());
			if(!mainDir.exists()){
				mainDir.mkdirs();
			}

//            File mainDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

//            File mainDir = new File("/sdcard/Telegram/Telegram Documents");

            result = new File(mainDir, filename);
        } else {
			result = new File(context.getFilesDir(), filename);
		}
		return result;
	}

	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public static String getPathfromUri(Uri uri) {
		if(uri.toString().startsWith("file://"))
			return uri.getPath();
		return null;
	}

}
