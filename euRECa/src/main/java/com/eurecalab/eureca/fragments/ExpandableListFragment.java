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

    private GlobalState gs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.categoryListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        gs = (GlobalState) getActivity().getApplication();

        if (gs.getCategories() == null || gs.getCategories().isEmpty()) {
            CategoriesAsyncTask task = new CategoriesAsyncTask(getActivity(), this);
            task.execute();
        }
        else{
            adapter = new CategoryAdapter(getActivity(), gs.getFilteredCategories());
            recyclerView.setAdapter(adapter);
        }

        recordButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        ColorCommon.changeColor(getActivity(), recordButton, true);
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
        } else {
            SearchCommon.searchForRecordings(query, gs, adapter);
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
        ActionCommon.hideKeyboard(getActivity());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void callback(Object... args) {
        adapter = new CategoryAdapter(getActivity(), gs.getFilteredCategories());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
