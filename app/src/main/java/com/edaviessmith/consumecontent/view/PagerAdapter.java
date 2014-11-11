package com.edaviessmith.consumecontent.view;

import android.support.v4.app.Fragment;

import com.edaviessmith.consumecontent.ContentActivity;
import com.edaviessmith.consumecontent.PlaceholderFragment;
import com.edaviessmith.consumecontent.TwitterFragment;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.YoutubeFragment;
import com.edaviessmith.consumecontent.data.TwitterFeed;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.data.YoutubeFeed;

public class PagerAdapter extends FragmentStateCachePagerAdapter {

    User user;
    ContentActivity act;

    public PagerAdapter(ContentActivity act, User user) {
        super(act.getSupportFragmentManager());
        this.act = act;
        this.user = user;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        if(user == null || user.media == null) return 0;
        return user.media.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {

        switch(user.media.get(position).getType()) {
            case Var.TYPE_YOUTUBE_PLAYLIST: case Var.TYPE_YOUTUBE_ACTIVTY: return YoutubeFragment.newInstance(act, (YoutubeFeed) user.media.get(position), position);
            case Var.TYPE_TWITTER: return TwitterFragment.newInstance(act, (TwitterFeed) user.media.get(position), position);
            default: return PlaceholderFragment.newInstance(position, user.media.get(position).getType() + " ");
        }

    }



    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return user.media.get(position).getName();
    }

}