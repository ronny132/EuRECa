package com.eurecalab.eureca.ui;

import com.eurecalab.eureca.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ColorSpinnerAdapter extends BaseAdapter {
	private Context context;
	private int[] colors;

	public ColorSpinnerAdapter(Context context, int[] colors) {
		super();
		this.colors = colors;
		this.context = context;
	}

	@Override
	public int getCount() {
		return colors.length;
	}

	@Override
	public Object getItem(int position) {
		return colors[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.color_adapter_layout, parent,
					false);
			holder = new ViewHolder();
			holder.baseView = convertView.findViewById(R.id.colorLayout);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		int color = (Integer) getItem(position);
		holder.baseView.setBackgroundColor(color);
		
		return convertView;
	}

	private static class ViewHolder {
		private View baseView;
	}

}
