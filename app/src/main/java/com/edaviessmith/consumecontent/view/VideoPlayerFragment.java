package com.edaviessmith.consumecontent.view;

import android.os.Bundle;

import com.edaviessmith.consumecontent.ContentActivity;
import com.edaviessmith.consumecontent.util.Var;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class VideoPlayerFragment extends YouTubePlayerSupportFragment {

    private String currentVideoID = "video_id";
    private YouTubePlayer activePlayer;
    private static ContentActivity act;

    public static VideoPlayerFragment newInstance(ContentActivity activity, String url) {
        act = activity;
        VideoPlayerFragment playerYouTubeFrag = new VideoPlayerFragment();

        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        playerYouTubeFrag.setArguments(bundle);


        return playerYouTubeFrag;
    }

    public void init() {

        initialize(Var.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                toggleControls(false);
                if (!wasRestored) {
                    activePlayer.loadVideo(getArguments().getString("url"), 0);

                }
                activePlayer.setShowFullscreenButton(false);
                activePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                    @Override
                    public void onPlaying() {
                        act.setVideoPlaying(true);
                    }

                    @Override
                    public void onPaused() {
                        act.setVideoPlaying(false);
                    }

                    @Override
                    public void onStopped() {
                        if(act.isVideoPlaying()) activePlayer.play(); //TODO check if stopped because of obscurity
                        act.setVideoPlaying(false);

                    }

                    @Override
                    public void onBuffering(boolean b) {

                    }

                    @Override
                    public void onSeekTo(int i) {

                    }
                });

            }

        });

    }

    public void toggleControls(boolean show) {
        if(activePlayer != null)
        activePlayer.setPlayerStyle(show ? YouTubePlayer.PlayerStyle.DEFAULT: YouTubePlayer.PlayerStyle.CHROMELESS);
    }

    public void onYouTubeVideoPaused() {
        activePlayer.pause();
    }


}