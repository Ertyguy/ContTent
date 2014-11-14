package com.edaviessmith.consumecontent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TwitterFragment extends Fragment {

    private static String TAG = "TwitterFragment";
    private static TwitterFragment twitterFragment;
    private static ContentActivity act;
    private int pos;

    private TextView feedId_tv;

    public static TwitterFragment newInstance(ContentActivity activity, int pos) {
        Log.i(TAG, "newInstance");
        act = activity;

        twitterFragment = new TwitterFragment();

        Bundle args = new Bundle();
        args.putInt("pos", pos);
        twitterFragment.setArguments(args);

        return twitterFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twitter, container, false);
        pos = getArguments() != null ? getArguments().getInt("pos") : -1; //Detect if the argument doesn't exist

        view.setId(pos);
        Log.i(TAG, "onCreateView");

        feedId_tv = (TextView) view.findViewById(R.id.id_tv);
        //feedId_tv.setText(act.getUser().getTwitterFeed().getFeedId());

        return view;
    }
}
