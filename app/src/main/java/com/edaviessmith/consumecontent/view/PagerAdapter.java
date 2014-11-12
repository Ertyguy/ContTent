package com.edaviessmith.consumecontent.view;

import android.support.v4.app.Fragment;

import com.edaviessmith.consumecontent.ContentActivity;
import com.edaviessmith.consumecontent.PlaceholderFragment;
import com.edaviessmith.consumecontent.TwitterFragment;
import com.edaviessmith.consumecontent.YoutubeFragment;
import com.edaviessmith.consumecontent.data.User;

public class PagerAdapter extends FragmentStateCachePagerAdapter {

    private static String TAG = "PagerAdapter";
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

        if(user == null || user.getYoutubeChannel() == null || user.getYoutubeChannel().getYoutubeFeeds() == null) return 0;
        return user.getYoutubeChannel().getYoutubeFeeds().size() + (user.getTwitterFeed() != null? 1: 0);
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        if(position < user.getYoutubeChannel().getYoutubeFeeds().size()) {
            return YoutubeFragment.newInstance(act, position);
        } else if(user.getTwitterFeed() != null && position == user.getYoutubeChannel().getYoutubeFeeds().size() ) {
            return TwitterFragment.newInstance(act, position);
        } else {
            return PlaceholderFragment.newInstance(position, "Placeholder (nothing to see here)");
        }

    }



    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        if(position < user.getYoutubeChannel().getYoutubeFeeds().size()) {
            return user.getYoutubeChannel().getYoutubeFeeds().get(position).getName();
        } else if(user.getTwitterFeed() != null && position == user.getYoutubeChannel().getYoutubeFeeds().size() ) {
            return "Twitter";//user.getTwitterFeed().getName();
        } else {
            return "Placeholder (nothing to see here)";
        }
    }

}