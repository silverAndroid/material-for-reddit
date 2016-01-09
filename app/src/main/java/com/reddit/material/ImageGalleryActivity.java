package com.reddit.material;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.reddit.material.libraries.PageIndicator;

import java.util.ArrayList;

public class ImageGalleryActivity extends AppCompatActivity {

    private static ImageGalleryAdapter adapter;
    private static ViewPager pager;
    private static PageIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new ImageGalleryAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        indicator = (PageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        ConnectionSingleton.getInstance().loadAlbum(getIntent().getStringExtra("albumURL"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    public static ImageGalleryAdapter getAdapter() {
        return adapter;
    }

    public static ViewPager getPager() {
        return pager;
    }

    public static PageIndicator getIndicator() {
        return indicator;
    }
}
