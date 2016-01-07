package com.reddit.material;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.Arrays;

public class YouTubeActivity extends YouTubeBaseActivity {

    private static final String TAG = "YouTubeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        final String url = getIntent().getStringExtra("url");
        YouTubePlayerView youTubePlayer = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubePlayer.initialize(APIKey.getInstance().getAPIKey(APIKey.YOUTUBE_CLIENT_ID_KEY), new YouTubePlayer
                .OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean
                    restored) {
                if (!restored) {
                    if (url.contains("youtube.com")) {
                        youTubePlayer.cueVideo(url.split("(v=)")[1].substring(0, 11));
                    } else if (url.contains("youtu.be")) {
                        String[] array = url.split("t=");
                        String[] times;
                        if (array.length == 1) {
                            youTubePlayer.cueVideo(url.split("/")[3].substring(0, 11));
                        } else {
                            times = url.split("t=")[1].replaceAll("[hms]", ":").split(":");
                            youTubePlayer.cueVideo(url.split("/")[3].substring(0, 11), getTimeInMilliseconds(times));
                        }
                    }
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult
                    youTubeInitializationResult) {
                Toast.makeText(getBaseContext(), "Will load video through browser...", Toast.LENGTH_SHORT).show();
                Util.linkClicked(YouTubeActivity.this, url, true);
                finish();
            }
        });
    }

    private int getTimeInMilliseconds(String[] times) {
        int time = 0;
        Log.d(TAG, "getTimeInMilliseconds: " + Arrays.toString(times));
        for (int i = times.length - 1; i >= 0; i--) {
            time += Integer.parseInt(times[i]) * Math.pow(60, times.length - 1 - i);
        }
        return time * 1000;
    }
}
