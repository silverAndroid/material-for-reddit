package com.reddit.material;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.reddit.material.libraries.org.droidparts.widget.ClearableEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = "SearchResultsActivity";
    private static ThingAdapter adapter;
    private static ProgressBar progressBar;
    private SearchFragment fragment;
    private String query;
    private String filters;

    public static void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.loading);
        hideProgressBar();

        final ClearableEditText searchBox = (ClearableEditText) toolbar.findViewById(R.id.search_box);
        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment == null) {
                    getSupportFragmentManager().beginTransaction().add(R.id.container, (fragment = SearchFragment
                            .newInstance(query))).addToBackStack("filters").commit();
                }
            }
        });
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    handled = true;
                    search(v.getText().toString());

                    View view = SearchResultsActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                return handled;
            }
        });

        ImageButton searchButton = (ImageButton) toolbar.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(searchBox.getText().toString());

                View view = SearchResultsActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_results_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(adapter = new ThingAdapter(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment = SearchFragment.newInstance())
                .commit();
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

    private void search(String query) {
        if (fragment != null) {
            filters = fragment.getFilters();
        }
        if (!filters.isEmpty())
            query += query.isEmpty() ? filters : (" " + filters);
        this.query = query;
        adapter.clear();
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            getSupportFragmentManager().popBackStack();
        }
        progressBar.setVisibility(View.VISIBLE);
        searchQuery(query);
        fragment = null;
    }

    private void searchQuery(final String query) {
        final ArrayList<Thing> results = new ArrayList<>();
        final boolean[] subredditsComplete = {false};
        final boolean[] postsComplete = {false};

        AsyncHttpClient subredditsClient = new AsyncHttpClient();
        final PersistentCookieStore cookieStore = new PersistentCookieStore(getBaseContext());
        subredditsClient.setCookieStore(cookieStore);
        subredditsClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        HashMap<String, String> bParams = new HashMap<>();
        bParams.put("sort", "relevance");
        bParams.put("q", query);
        RequestParams parameters = new RequestParams(bParams);
        subredditsClient.get("https://www.reddit.com/subreddits/search.json", parameters, new JsonHttpResponseHandler
                () {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray subredditsArray = response.getJSONObject("data").getJSONArray("children");
                    for (int i = 0; i < subredditsArray.length(); i++) {
                        JSONObject subredditJSON = subredditsArray.getJSONObject(i);
                        String kind = subredditJSON.getString("kind");
                        if (kind.equals("t5")) {
                            Subreddit subreddit = Util.generateSubreddit(subredditJSON.getJSONObject("data"));
                            results.add(subreddit);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                subredditsComplete[0] = true;
                completeSearch(postsComplete[0], subredditsComplete[0], results);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                    errorResponse) {
                Log.e(TAG, "onFailure: " + errorResponse.toString(), throwable);
                subredditsComplete[0] = true;
                completeSearch(postsComplete[0], subredditsComplete[0], results);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: " + query, throwable);
                subredditsComplete[0] = true;
                completeSearch(postsComplete[0], subredditsComplete[0], results);
            }
        });

        AsyncHttpClient linksClient = new AsyncHttpClient();
        linksClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        HashMap<String, String> bodyParams = new HashMap<>();
        bodyParams.put("q", query);
        bodyParams.put("sort", "relevance");
        bodyParams.put("t", "all");
        RequestParams params = new RequestParams(bodyParams);
        linksClient.get(getBaseContext(), "https://www.reddit.com/search.json", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, "onSuccess: " + this.getRequestURI());
                Log.d(TAG, "onSuccess: " + response.toString());
                try {
                    JSONArray linksArray = response.getJSONObject("data").getJSONArray("children");
                    for (int i = 0; i < linksArray.length(); i++) {
                        JSONObject result = linksArray.getJSONObject(i);
                        String kind = result.getString("kind");
                        if (kind.equals("t3")) {
                            Post post = Util.generatePost(result.getJSONObject("data"));
                            results.add(post);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                postsComplete[0] = true;
                completeSearch(postsComplete[0], subredditsComplete[0], results);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "onFailure: " + errorResponse.toString(), throwable);
                postsComplete[0] = true;
                completeSearch(postsComplete[0], subredditsComplete[0], results);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: " + query, throwable);
                postsComplete[0] = true;
                completeSearch(postsComplete[0], subredditsComplete[0], results);
            }
        });
    }

    private void completeSearch(boolean subredditsComplete, boolean postsComplete, ArrayList<Thing> results) {
        if (subredditsComplete && postsComplete) {
            adapter.addItems(results);
            hideProgressBar();
        }
    }
}
