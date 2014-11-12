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

    public static YoutubeFragment newInstance(ContentActivity activity, int pos) {
        Log.i(TAG, "newInstance");
        act = activity;

        youtubeFragment = new YoutubeFragment();

        Bundle args = new Bundle();
        args.putInt("pos", pos);
        youtubeFragment.setArguments(args);

        return youtubeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);

        pos = getArguments() != null ? getArguments().getInt("pos") : -1; //Detect if the argument doesn't exist

        view.setId(pos);
        Log.i(TAG, "onCreateView");

        feedId_tv = (TextView) view.findViewById(R.id.id_tv);
        feedId_tv.setText(act.getUser().getYoutubeChannel().getYoutubeFeeds().get(pos).getFeedId());
        
        return view;
    }
}
