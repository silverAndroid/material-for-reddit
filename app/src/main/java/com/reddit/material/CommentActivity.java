package com.reddit.material;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CommentActivity";
    private static CommentAdapter adapter;
    private static SwipeRefreshLayout refresh;
    private static ProgressBar loading;

    public static CommentAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Post post = null;
        String permalink = "";
        if (getIntent().hasExtra("post"))
            post = (Post) getIntent().getSerializableExtra("post");
        else if (getIntent().hasExtra("permalink"))
            permalink = getIntent().getStringExtra("permalink");

        loading = (ProgressBar) findViewById(R.id.loading);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(adapter = new CommentAdapter(post, CommentActivity.this));
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        getLinkData(post == null ? permalink : post.getPermalink());

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
        getLinkData(postPermalink);
    }

    private void getLinkData(String permalink) {
        final Post[] post = new Post[1];
        AsyncHttpClient subredditClient = new AsyncHttpClient();
        final PersistentCookieStore cookieStore = new PersistentCookieStore(getBaseContext());
        subredditClient.setCookieStore(cookieStore);
        subredditClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        subredditClient.get("https://www.reddit.com" + permalink.replace("/?ref=search_posts", "") + "/.json", new
                JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        try {
                            JSONArray postJSON = response.getJSONObject(0).getJSONObject("data").getJSONArray
                                    ("children");
                            post[0] = Util.generatePost(postJSON.getJSONObject(0).getJSONObject("data"));
                            adapter.setPost(post[0]);
                            JSONArray commentsJSON = response.getJSONObject(1).getJSONObject("data").getJSONArray
                                    ("children");
                            adapter.addComments(commentsJSON);

                            if (loading.getVisibility() == View.GONE)
                                refresh.setRefreshing(false);
                            else
                                loading.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                            errorResponse) {
                        Log.e(TAG, "onFailure: " + errorResponse.toString(), throwable);
                        Log.d(TAG, "onFailure: " + this.getRequestURI().toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        Log.e(TAG, "onFailure: " + responseString, throwable);
                        Log.d(TAG, "onFailure: " + this.getRequestURI().toString());
                    }
                });
    }
}
