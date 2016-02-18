package com.eurecalab.eureca.ui;

import com.eurecalab.eureca.constants.FragmentConstants;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.fragments.AdminFragment;
import com.eurecalab.eureca.fragments.ChartFragment;
import com.eurecalab.eureca.fragments.ExpandableListFragment;
import com.eurecalab.eureca.fragments.SettingsFragment;
import com.eurecalab.eureca.fragments.UploadFragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private String[] tabLabels;
    private int tabLength;
    private GlobalState gs;

    public ViewPagerAdapter(Activity context, FragmentManager fm, String mTitles[],
                            int mNumbOfTabsumb) {
        super(fm);
        gs = (GlobalState) context.getApplication();
        this.tabLabels = mTitles;
        this.tabLength = mNumbOfTabsumb;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch (position) {
            case FragmentConstants.MAIN_FRAGMENT:
                f = new ExpandableListFragment();
                break;
            case FragmentConstants.SETTINGS_FRAGMENT:
                f = new SettingsFragment();
                break;
            case FragmentConstants.UPLOAD_FRAGMENT:
                f = new UploadFragment();
                break;
            case FragmentConstants.CHARTS_FRAGMENT:
                f = new ChartFragment();
                break;
            case FragmentConstants.ADMIN_FRAGMENT:
                f = new AdminFragment();
                break;
        }
        gs.setFragmentAt(f, position);
        return f;
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
