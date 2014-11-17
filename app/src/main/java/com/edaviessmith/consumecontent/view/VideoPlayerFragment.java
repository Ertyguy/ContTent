package com.edaviessmith.consumecontent.view;

import android.os.Bundle;

import com.edaviessmith.consumecontent.util.Var;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class VideoPlayerFragment extends YouTubePlayerSupportFragment {

    private String currentVideoID = "video_id";
    private YouTubePlayer activePlayer;

    public static VideoPlayerFragment newInstance(String url) {

        VideoPlayerFragment playerYouTubeFrag = new VideoPlayerFragment();

        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        playerYouTubeFrag.setArguments(bundle);

        return playerYouTubeFrag;
    }

    public void init() {

        initialize(Var.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) { }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                if (!wasRestored) {
                    activePlayer.loadVideo(getArguments().getString("url"), 0);

                }

            }
        });


    }

    public void onYouTubeVideoPaused() {
        activePlayer.pause();
    }
}