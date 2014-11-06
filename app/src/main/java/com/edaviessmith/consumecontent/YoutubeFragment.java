package com.edaviessmith.consumecontent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edaviessmith.consumecontent.data.Media;


public class YoutubeFragment extends Fragment {

    private static String TAG = "YoutubeFragment";
    private static YoutubeFragment youtubeFragment;
    private static Content act;
    private Media media;
    private int pos;

    public static YoutubeFragment newInstance(Content activity, Media media, int pos) {
        Log.e(TAG, "newInstance");
        act = activity;

        youtubeFragment = new YoutubeFragment();
        youtubeFragment.media = media;
        youtubeFragment.pos = pos;

        return youtubeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        view.setId(pos);
        Log.e(TAG, "onCreateView");


        return view;
    }
}
