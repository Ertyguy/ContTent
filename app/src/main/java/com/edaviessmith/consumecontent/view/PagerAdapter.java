package com.edaviessmith.consumecontent.view;

import android.support.v4.app.Fragment;

import com.edaviessmith.consumecontent.ContentActivity;
import com.edaviessmith.consumecontent.PlaceholderFragment;
import com.edaviessmith.consumecontent.TwitterFragment;
import com.edaviessmith.consumecontent.YoutubeFragment;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.util.Var;

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

        if(user == null || user.getMediaFeed() == null) return 0;
        return user.getMediaFeed().size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {

        switch(act.getUser().getMediaFeed().get(position).getType()) {
            case Var.TYPE_YOUTUBE_PLAYLIST:case Var.TYPE_YOUTUBE_ACTIVTY: return YoutubeFragment.newInstance(act, position);
            case Var.TYPE_TWITTER: return TwitterFragment.newInstance(act, position);
            default: return PlaceholderFragment.newInstance(position, "Placeholder (nothing to see here)");
        }


    }



    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {

        if(position < user.getMediaFeed().size())  return act.getUser().getMediaFeed().get(position).getName();

        return "Placeholder (nothing to see here)";

    }

}