package com.edaviessmith.consumecontent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edaviessmith.consumecontent.data.Media;


public class TwitterFragment extends Fragment {

    private static String TAG = "TwitterFragment";
    private static TwitterFragment twitterFragment;
    private static Content act;
    private Media media;
    private int pos;

    public static TwitterFragment newInstance(Content activity, Media media, int pos) {
        Log.e(TAG, "newInstance");
        act = activity;

        twitterFragment = new TwitterFragment();
        twitterFragment.media = media;
        twitterFragment.pos = pos;
        return twitterFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twitter, container, false);
        Log.e(TAG, "onCreateView");

        view.setId(pos);

        return view;
    }
}
