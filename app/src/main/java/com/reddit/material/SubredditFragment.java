package com.reddit.material;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SubredditFragment extends Fragment {

    private static SubredditFragment instance;
    private SubredditAdapter adapter;
    private SwipeRefreshLayout refresh;

    public static SubredditFragment newInstance() {
        SubredditFragment fragment = new SubredditFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("r/", "");
        fragment.setArguments(bundle);
        instance = fragment;
        return fragment;
    }

    public static SubredditFragment newInstance(String subreddit) {
        SubredditFragment fragment = new SubredditFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("r/", subreddit);
        fragment.setArguments(bundle);
        instance = fragment;
        return fragment;
    }

    public static SubredditFragment getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subreddit, container, false);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        refresh.setRefreshing(true);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SubredditAdapter(getContext());
        recyclerView.setAdapter(adapter);
        ConnectionSingleton.getInstance().getSubredditData(getArguments().getString("r/"));
        return view;
    }

    public void refresh() {
        adapter.clearPosts();
        ConnectionSingleton.getInstance().getSubredditData(getArguments().getString("r/"));
        refresh.setRefreshing(false);
    }

    public SubredditAdapter getAdapter() {
        return adapter;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return refresh;
    }
}
