package com.reddit.material;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class CommentActivity extends AppCompatActivity {

    private static CommentAdapter adapter;
    private static SwipeRefreshLayout refresh;
    private static ProgressBar loading;

    public static CommentAdapter getAdapter() {
        return adapter;
    }

    public static ProgressBar getProgressBar() {
        return loading;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Post post = (Post) getIntent().getSerializableExtra("post");

        loading = (ProgressBar) findViewById(R.id.loading);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(adapter = new CommentAdapter(post, this));
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        ConnectionSingleton.getInstance().getLinkData(post.getPermalink());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh() {
        String postPermalink = adapter.clearData();
        ConnectionSingleton.getInstance().getLinkData(postPermalink);
    }

    public static SwipeRefreshLayout getSwipeRefreshLayout() {
        return refresh;
    }
}
