package com.eurecalab.eureca.ui;

import com.eurecalab.eureca.fragments.ChartFragment;
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
    private final static int CHARTS_FRAGMENT= 1;
	private final static int SETTINGS_FRAGMENT = 2;
	private final static int UPLOAD_FRAGMENT = 3;

	public ViewPagerAdapter(FragmentManager fm, String mTitles[],
			int mNumbOfTabsumb) {
		super(fm);
		this.tabLabels = mTitles;
		this.tabLength = mNumbOfTabsumb;
	}

	@Override
	public Fragment getItem(int position) {
        switch (position){
            case MAIN_FRAGMENT:
                return new ExpandableListFragment();
            case SETTINGS_FRAGMENT:
                return new SettingsFragment();
            case UPLOAD_FRAGMENT:
                return new UploadFragment();
            case CHARTS_FRAGMENT:
                return new ChartFragment();
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
