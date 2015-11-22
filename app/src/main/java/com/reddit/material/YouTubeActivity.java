package com.reddit.material;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

public class YouTubeActivity extends YouTubeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        YouTubePlayerView youTubePlayer = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubePlayer.initialize(APIKey.getInstance().getAPIKey(APIKey.YOUTUBE_CLIENT_ID_KEY), new YouTubePlayer
                .OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean
                    restored) {
                if (!restored) {
                    Log.d("url", getIntent().getStringExtra("url").split("/")[3]);
                    youTubePlayer.cueVideo(getIntent().getStringExtra("url").split("/")[3]);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult
                    youTubeInitializationResult) {
                Toast.makeText(getBaseContext(), "Failed to load video!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
