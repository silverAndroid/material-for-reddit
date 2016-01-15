package com.reddit.material;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class VideoActivity extends AppCompatActivity {

    VideoView videoView;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String url = getIntent().getStringExtra("url");
        videoView = (VideoView) findViewById(R.id.video);
        videoView.setMediaController(new MediaController(VideoActivity.this, true));
        loading = (ProgressBar) findViewById(R.id.loading);
        if (ConstantMap.getInstance().isGIF(url))
            loadGIF(url);
        else
            loadVideo(url);

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

    private void loadGIF(String url) {
        url = url.replace(".gifv", ".gif");
        final String modifiedURL = url;
        AsyncHttpClient gifLoader = new AsyncHttpClient();
        gifLoader.setUserAgent(ConstantMap.getInstance().getUserAgent());
        gifLoader.get("http://gfycat.com/cajax/checkUrl/" + url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getBoolean("urlKnown")) {
                        videoView.setVideoURI(Uri.parse(response.getString("mp4Url")));
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                loading.setVisibility(View.GONE);
                                videoView.start();
                                mp.setLooping(true);
                            }
                        });
                    } else
                        uploadGIF(modifiedURL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void uploadGIF(String url) {
        AsyncHttpClient gifUploader = new AsyncHttpClient();
        gifUploader.setUserAgent(ConstantMap.getInstance().getUserAgent());
        gifUploader.get("http://upload.gfycat.com/transcode?fetchUrl=" + url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (videoView != null) {
                    try {
                        videoView.setVideoURI(Uri.parse(response.getString("mp4Url")));
                        videoView.start();
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                loading.setVisibility(View.GONE);
                                mp.setLooping(true);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void loadVideo(String url) {
        videoView.setVideoURI(Uri.parse(url));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                loading.setVisibility(View.GONE);
                videoView.start();
            }
        });
    }
}
