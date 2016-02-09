package com.eurecalab.eureca.ui;

import com.eurecalab.eureca.fragments.ExpandableListFragment;
import com.eurecalab.eureca.fragments.SettingsFragment;
import com.eurecalab.eureca.fragments.UploadFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

	private String[] tabLabels;
	private int tabLength;
	
	private final static int MAIN_FRAGMENT = 0;
	private final static int SETTINGS_FRAGMENT = 1;
	private final static int UPLOAD_FRAGMENT = 2;

	public ViewPagerAdapter(FragmentManager fm, String mTitles[],
			int mNumbOfTabsumb) {
		super(fm);
		this.tabLabels = mTitles;
		this.tabLength = mNumbOfTabsumb;
	}

	@Override
	public Fragment getItem(int position) {
		if (position == MAIN_FRAGMENT) {
			ExpandableListFragment fragment = new ExpandableListFragment();
			return fragment;
		}
		else if(position == SETTINGS_FRAGMENT){
			SettingsFragment fragment = new SettingsFragment();
			return fragment;
		}
		else if(position == UPLOAD_FRAGMENT){
			UploadFragment fragment = new UploadFragment();
			return fragment;
		}
		return null;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return tabLabels[position];
	}

	@Override
	public int getCount() {
		return tabLength;
	}

}
