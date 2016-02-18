package com.eurecalab.eureca.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eurecalab.eureca.R;
import com.eurecalab.eureca.constants.DynamoDBAction;
import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.core.ShareClassification;
import com.eurecalab.eureca.net.DynamoDBFavoritesTask;
import com.eurecalab.eureca.ui.FavoritesAdapter;

import java.util.LinkedList;
import java.util.List;

public class ChartFragment extends Fragment implements Callable {
    private RecyclerView recordingList;
    private GlobalState gs;
    private FavoritesAdapter adapter;
    private List<Recording> favorites;
    private View loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chart, container,
                false);

        recordingList = (RecyclerView) rootView.findViewById(R.id.recordingList);
        recordingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        loading = rootView.findViewById(R.id.loading);

        gs = (GlobalState) getActivity().getApplication();

        DynamoDBFavoritesTask task = new DynamoDBFavoritesTask(getActivity(), null, null,
                GenericConstants.DEFAULT_SEARCH_LIMIT, this, DynamoDBAction.GET_GLOBAL_FAVORITES);
        task.execute();

        return rootView;
    }

    @Override
    public void callback(Object... args) {
        if (args.length == 1 && args[0] instanceof List) {
            favorites = new LinkedList<>();
            List<ShareClassification> shareClassifications = (List<ShareClassification>) args[0];
            List<Recording> list = new LinkedList<>();
            for (ShareClassification sc : shareClassifications) {
                Recording recording = sc.getRecording();
                recording.setContext(getActivity());
                list.add(recording);
            }
            favorites.addAll(list);
        }
        if (getActivity() != null) {
            adapter = new FavoritesAdapter(favorites, getActivity());
            recordingList.setAdapter(adapter);
            loading.setVisibility(View.GONE);
        }
    }
}
