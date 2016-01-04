package com.reddit.material;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SearchResultsActivity extends AppCompatActivity {

    private static ThingAdapter adapter;
    private static ProgressBar progressBar;
    private SearchFragment fragment;

    public static ThingAdapter getAdapter() {
        return adapter;
    }

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

        EditText searchBox = (EditText) toolbar.findViewById(R.id.search_box);
        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
                    getSupportFragmentManager().beginTransaction().add(R.id.container, (fragment = SearchFragment
                            .newInstance())).addToBackStack("filters").commit();
                }
            }
        });
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    handled = true;
                    String filters = fragment.getFilters();
                    String query = v.getText().toString();
                    if (!filters.isEmpty())
                        query += " " + filters;
                    adapter.clear();
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    progressBar.setVisibility(View.VISIBLE);
                    ConnectionSingleton.getInstance().search(query);
                }
                return handled;
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_results_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(adapter = new ThingAdapter(this));
        ConnectionSingleton.getInstance().search(getIntent().getStringExtra("query"));

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
}
