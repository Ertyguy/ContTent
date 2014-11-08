package com.edaviessmith.consumecontent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.TwitterFeed;


public class TwitterFragment extends Fragment {

    private static String TAG = "TwitterFragment";
    private static TwitterFragment twitterFragment;
    private static ContentActivity act;
    private TwitterFeed twitterFeed;
    private int pos;

    private TextView feedId_tv;

    public static TwitterFragment newInstance(ContentActivity activity, TwitterFeed twitterFeed, int pos) {
        Log.e(TAG, "newInstance");
        act = activity;

        twitterFragment = new TwitterFragment();
        twitterFragment.twitterFeed = twitterFeed;
        twitterFragment.pos = pos;
        return twitterFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twitter, container, false);
        Log.e(TAG, "onCreateView");
        view.setId(pos);

        //TODO getting null pointer (because Twitter feed object is null)
        //feedId_tv = (TextView) view.findViewById(R.id.id_tv);
        //feedId_tv.setText(twitterFeed.feedId);

        return view;
    }
}
