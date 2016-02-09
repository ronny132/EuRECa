package com.eurecalab.eureca.io;

import java.util.List;

import com.eurecalab.eureca.R;
import com.eurecalab.eureca.common.ColorCommon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FileArrayAdapter extends ArrayAdapter<Option> {

	private Activity context;
	private int layoutID;
	private List<Option> items;

	public FileArrayAdapter(Activity context, int textViewResourceId,
			List<Option> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		layoutID = textViewResourceId;
		items = objects;
	}

	public Option getItem(int i) {
		return items.get(i);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(layoutID, null);
		}
		final Option o = items.get(position);
		if (o != null) {
			RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.fileLayout);

			TextView t1 = (TextView) v.findViewById(R.id.fileName);
			ImageView iv = (ImageView) v.findViewById(R.id.fileIcon);

			if (t1 != null)
				t1.setText(o.getName());

			if (iv != null) {
				if (o.getData().startsWith("File")) {
					iv.setImageResource(R.drawable.file);
				} else if (o.getData().startsWith("Parent")) {
					t1.setText(context.getString(R.string.parent_directory));
					iv.setImageResource(R.drawable.back);
				} else {
					iv.setImageResource(R.drawable.folder);
				}
			}

            ColorCommon.changeColor(context, iv, true);

		}
		return v;
	}

}
