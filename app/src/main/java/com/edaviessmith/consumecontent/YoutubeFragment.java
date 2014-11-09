package com.edaviessmith.consumecontent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.YoutubeFeed;


public class YoutubeFragment extends Fragment {

    private static String TAG = "YoutubeFragment";
    private static YoutubeFragment youtubeFragment;
    private static ContentActivity act;
    private YoutubeFeed youtubeFeed;
    private int pos;

    private TextView feedId_tv;

    public static YoutubeFragment newInstance(ContentActivity activity, YoutubeFeed youtubeFeed, int pos) {
        Log.e(TAG, "newInstance");
        act = activity;

        youtubeFragment = new YoutubeFragment();
        youtubeFragment.youtubeFeed = youtubeFeed;
        youtubeFragment.pos = pos;

        return youtubeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        view.setId(pos);
        Log.e(TAG, "onCreateView");

        //TODO getting null pointer (because Youtube feed object is null)
        //feedId_tv = (TextView) view.findViewById(R.id.id_tv);
        //feedId_tv.setText(youtubeFeed.feedId);
        
        return view;
    }
}
