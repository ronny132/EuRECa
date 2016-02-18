package com.eurecalab.eureca.fragments;

import java.util.List;
import java.util.Locale;

import com.eurecalab.eureca.R;
import com.eurecalab.eureca.common.ActionCommon;
import com.eurecalab.eureca.common.ColorCommon;
import com.eurecalab.eureca.common.SearchCommon;
import com.eurecalab.eureca.constants.DynamoDBAction;
import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.net.CategoriesAsyncTask;
import com.eurecalab.eureca.net.DynamoDBFavoritesTask;
import com.eurecalab.eureca.ui.CategoryAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class ExpandableListFragment extends Fragment implements OnClickListener, OnQueryTextListener, Callable {
    private CategoryAdapter adapter;
    private SearchView searchView;
    private FloatingActionButton recordButton;
    private RecyclerView recyclerView;
    private View loading;
    private Activity activity;

    private GlobalState gs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        activity = getActivity();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.categoryListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        loading = rootView.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        gs = (GlobalState) activity.getApplication();

        if (gs.getCategories() == null || gs.getCategories().isEmpty()) {
            CategoriesAsyncTask task = new CategoriesAsyncTask(activity, this);
            task.execute();
        } else {
            adapter = new CategoryAdapter(activity, gs.getFilteredCategories());
            recyclerView.setAdapter(adapter);
        }

        recordButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        ColorCommon.changeColor(activity, recordButton, true);
        recordButton.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnSearchClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(searchView)) {
            searchView.setOnQueryTextListener(this);
        }

    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query.trim().length() == 0) {
            SearchCommon.resetRecordings(gs, adapter);
            adapter = new CategoryAdapter(activity, gs.getFilteredCategories());
            recyclerView.setAdapter(adapter);
        } else {
            SearchCommon.searchForRecordings(query, gs, adapter);
            adapter = new CategoryAdapter(activity, gs.getFilteredCategories());
            recyclerView.setAdapter(adapter);
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        SearchCommon.searchForRecordings(query, gs, adapter);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionCommon.hideKeyboard(activity);
        if (gs != null && gs.getFilteredCategories() != null && recyclerView != null) {
//            adapter = new CategoryAdapter(activity, gs.getFilteredCategories());
//            recyclerView.setAdapter(adapter);
            if (gs.getFilteredCategories().size() > 0) {
                loading.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void callback(Object... args) {
        adapter = new CategoryAdapter(activity, gs.getFilteredCategories());
        recyclerView.setAdapter(adapter);
        loading.setVisibility(View.GONE);
    }

}
