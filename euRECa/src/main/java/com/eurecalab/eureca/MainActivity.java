package com.eurecalab.eureca;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.android.vending.billing.IInAppBillingService;
import com.eurecalab.eureca.constants.FragmentConstants;
import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.constants.PermissionConstants;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.core.User;
import com.eurecalab.eureca.fragments.SettingsFragment;
import com.eurecalab.eureca.fragments.UploadFragment;
import com.eurecalab.eureca.net.DynamoDBTask;
import com.eurecalab.eureca.net.SignInTask;
import com.eurecalab.eureca.net.UpgradeTask;
import com.eurecalab.eureca.ui.SlidingTabLayout;
import com.eurecalab.eureca.ui.ThemeSwitcher;
import com.eurecalab.eureca.ui.ViewPagerAdapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    public ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private SlidingTabLayout tabs;
    private String[] tabLabels;
    private int tabLength;
    private GlobalState gs;
    private User authenticatedUser;

    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeSwitcher(this).onActivityCreateSetTheme();
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            init();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PermissionConstants.REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void init() {
        TypedValue colorAccentValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, colorAccentValue, true);

        toolbar.setTitleTextColor(colorAccentValue.data);

        TypedArray tabsArray = getResources().obtainTypedArray(R.array.tabLabels);
        tabLength = tabsArray.length();

        gs = (GlobalState) getApplication();
        authenticatedUser = gs.getAuthenticatedUser();

        if (authenticatedUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(GenericConstants.RETURN_TO_UPLOAD_ACTIVITY, true);
            startActivity(intent);
        } else {

            if(authenticatedUser.getAdmin() == GenericConstants.FALSE_INT){
                tabLength -= 1;
            }

            if (!authenticatedUser.isProUser()) {
                tabLength -= 1;
            }

            tabLabels = new String[tabLength];
            for (int i = 0; i < tabLength; i++) {
                tabLabels[i] = tabsArray.getString(i);
            }
            tabsArray.recycle();

            viewPagerAdapter = new ViewPagerAdapter(this, getSupportFragmentManager(), tabLabels, tabLength);
            viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(viewPagerAdapter);

            tabs = (SlidingTabLayout) findViewById(R.id.tabs);
//            tabs.setDistributeEvenly(true);
            tabs.useTextColorAccent(false);

            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    TypedValue color = new TypedValue();
                    getTheme().resolveAttribute(R.attr.colorPrimary, color, true);
                    return color.data;
                }
            });

            tabs.setViewPager(viewPager);

            mServiceConn = new ServiceConnection() {
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mService = null;
                }

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mService = IInAppBillingService.Stub.asInterface(service);
                }
            };

            Intent serviceIntent =
                    new Intent("com.android.vending.billing.InAppBillingService.BIND");
            serviceIntent.setPackage("com.android.vending");
            bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalState globalState = (GlobalState) getApplication();
        try {
            globalState.stopPlayingSound();
            globalState.setPlayingRecording(null);
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalState gs = (GlobalState) getApplication();
        Collection<Category> categories = gs.getCategories();
        List<Category> filteredCategories = new LinkedList<>();
        if (categories != null) {
            for (Category category : categories) {
                List<Recording> recordings = category.getRecordings();
                for (Recording recording : recordings) {
                    recording.setContext(this);
                }
                filteredCategories.add(category);
            }
        }
        Collections.sort(filteredCategories, new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                return c1.getSortIndex() - c2.getSortIndex();
            }
        });
        gs.setFilteredCategories(filteredCategories);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionConstants.REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GenericConstants.PURCHASE_REQUEST_CODE){
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    if(sku.equals(GenericConstants.PREMIUM_VERSION_SKU)){
                        Snackbar.make(viewPager, R.string.now_premium_user, Snackbar.LENGTH_LONG).show();
                        gs.getAuthenticatedUser().setProVersionExpireDate(GenericConstants.DATE_INFINITE);
                        UpgradeTask task = new UpgradeTask(this);
                        task.execute();
                        Fragment currentFragment = gs.getFragmentAt(FragmentConstants.SETTINGS_FRAGMENT);
                        if(currentFragment != null  && currentFragment instanceof SettingsFragment && currentFragment.isVisible()){
                            SettingsFragment settingsFragment = (SettingsFragment) currentFragment;
                            settingsFragment.updateUI();
                        }
                    }
                }
                catch (JSONException e) {
                    Snackbar.make(viewPager, R.string.purchase_failed, Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == GenericConstants.FILE_CHOOSER_ACTIVITY){
            Fragment currentFragment = gs.getFragmentAt(FragmentConstants.UPLOAD_FRAGMENT);
            if(currentFragment != null  && currentFragment instanceof UploadFragment && currentFragment.isVisible()){
                UploadFragment fragment = (UploadFragment) currentFragment;
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
