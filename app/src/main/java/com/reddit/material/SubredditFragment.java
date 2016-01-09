package com.reddit.material;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class SubredditFragment extends Fragment {

    private static final String ARG_R = "r/";
    private static final String ARG_SORT = "sort";
    private static final String ARG_TIME = "time";

    private static SubredditFragment instance;
    private PostAdapter adapter;
    private SwipeRefreshLayout refresh;
    private ProgressBar loading;

    public static SubredditFragment newInstance() {
        return newInstance("frontpage");
    }

    public static SubredditFragment newInstance(String subreddit) {
        return newInstance(subreddit, "hot", "");
    }

    public static SubredditFragment newInstance(String subreddit, String sort) {
        return newInstance(subreddit, sort, "");
    }

    public static SubredditFragment newInstance(String subreddit, String sort, String time) {
        SubredditFragment fragment = new SubredditFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(ARG_R, subreddit.equalsIgnoreCase("frontpage") ? "" : subreddit);
        bundle.putString(ARG_SORT, sort.equalsIgnoreCase("hot") ? "" : sort);
        bundle.putString(ARG_TIME, time);
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
        loading = (ProgressBar) view.findViewById(R.id.loading);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                }.run();
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PostAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        ConnectionSingleton.getInstance().getSubredditData(getArguments().getString(ARG_R), getArguments().getString
                (ARG_SORT), getArguments().getString(ARG_TIME));
        return view;
    }

    public void refresh() {
        adapter.clearPosts();
        ConnectionSingleton.getInstance().getSubredditData(getArguments().getString(ARG_R), getArguments().getString
                (ARG_SORT), getArguments().getString(ARG_TIME));
    }

    public PostAdapter getAdapter() {
        return adapter;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return refresh;
    }

    public ProgressBar getProgressBar() {
        return loading;
    }
}
