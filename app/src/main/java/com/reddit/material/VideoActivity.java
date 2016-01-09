package com.reddit.material;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.koushikdutta.ion.Ion;

public class VideoActivity extends AppCompatActivity {

    private final String ARG_URL = "video_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String url = getIntent().getStringExtra("url");
        VideoView view = (VideoView) findViewById(R.id.video);
        view.setMediaController(new MediaController(VideoActivity.this, true));
        ProgressBar loading = (ProgressBar) findViewById(R.id.loading);
        if (ConstantMap.getInstance().isGIF(url))
            ConnectionSingleton.getInstance().loadGIF(url, view, loading, ARG_URL);
        else
            ConnectionSingleton.getInstance().loadVideo(url, view, loading);

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
}
