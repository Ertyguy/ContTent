package com.edaviessmith.contTent.view;

import android.os.Handler;
import android.util.Log;

import com.edaviessmith.contTent.ContentActivity;
import com.edaviessmith.contTent.util.Var;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class VideoPlayerFragment extends YouTubePlayerSupportFragment {

    private final static String TAG = "VideoPlayerFragment";
    private String currentVideoID = "video_id";
    public YouTubePlayer activePlayer;
    private static ContentActivity act;

    public boolean tryStop; //Prevent player from starting after stopping while loading
    public int glitchPlayCount; //Replay video after resize ToS glitch
    public boolean isBuffering;

    public static VideoPlayerFragment newInstance(ContentActivity activity ) {
        act = activity;
        //VideoPlayerFragment playerYouTubeFrag = new VideoPlayerFragment();

        return new VideoPlayerFragment();
    }



    public void init(final String url) {

        initialize(Var.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) { }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                toggleControls(true);
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

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                act.updateUIVisibility();
                            }
                        }, 5000);
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
                        isBuffering = b;
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

    public void toggleVideoPlayback(boolean play) {
        if(activePlayer != null){
            if(isBuffering) tryStop = true;

            if(play) activePlayer.play();
            else activePlayer.pause();
        }
    }


}