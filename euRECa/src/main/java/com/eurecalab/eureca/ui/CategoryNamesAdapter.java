package com.eurecalab.eureca.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.eurecalab.eureca.core.Category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryNamesAdapter extends BaseAdapter {
	private Context context;
	private Collection<Category> categories;

	public CategoryNamesAdapter(Context context, Collection<Category> categories) {
		super();
		this.categories = categories;
		this.context = context;
	}

	@Override
	public int getCount() {
		//NB: The last category is "Preferiti" and it doesn't need to be considered
		return categories.size() - 1;
	}

	@Override
	public Object getItem(int position) {
        Iterator<Category> it = categories.iterator();
        int count = 0;
        while(it.hasNext()){
            Category next = it.next();
            if(count == position){
                return next;
            }
            count++;
        }
		return null;
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
			convertView = li.inflate(android.R.layout.simple_dropdown_item_1line, parent,
					false);
			holder = new ViewHolder();
			holder.baseView = (TextView) convertView.findViewById(android.R.id.text1);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		String name = ((Category) getItem(position)).getName();
		holder.baseView.setText(name);
		
		return convertView;
	}

	private static class ViewHolder {
		private TextView baseView;
	}

}
