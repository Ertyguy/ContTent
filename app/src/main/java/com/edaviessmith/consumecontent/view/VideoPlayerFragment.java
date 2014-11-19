package com.edaviessmith.consumecontent.view;

import android.util.Log;

import com.edaviessmith.consumecontent.ContentActivity;
import com.edaviessmith.consumecontent.util.Var;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class VideoPlayerFragment extends YouTubePlayerSupportFragment {

    private String currentVideoID = "video_id";
    public YouTubePlayer activePlayer;
    private static ContentActivity act;

    public boolean tryStop; //Prevent player from starting after stopping while loading
    public int glitchPlayCount; //Replay video after resize ToS glitch


    public static VideoPlayerFragment newInstance(ContentActivity activity ) {
        act = activity;
        VideoPlayerFragment playerYouTubeFrag = new VideoPlayerFragment();

        return playerYouTubeFrag;
    }





    public void init(final String url) {

        initialize(Var.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) { }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                toggleControls(false);
                if (!wasRestored) {
                    activePlayer.loadVideo(url, 0);
                }
                activePlayer.setShowFullscreenButton(false);
                activePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                    @Override
                    public void onPlaying() {
                        if(tryStop) {
                            Log.e("VideoPlayerFramgnet","try stop");
                            activePlayer.pause();
                            tryStop = false;
                        }
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


    public void toggleVideoPlayback(boolean play) {
        if(activePlayer != null){
            if(!activePlayer.isPlaying()) tryStop = true;

            if(play) activePlayer.play();
            else activePlayer.pause();
        }
    }


}