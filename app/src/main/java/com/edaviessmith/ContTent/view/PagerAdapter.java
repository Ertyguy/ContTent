package com.edaviessmith.contTent.view;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.edaviessmith.contTent.PlaceholderFragment;
import com.edaviessmith.contTent.TwitterFragment;
import com.edaviessmith.contTent.YoutubeFragment;
import com.edaviessmith.contTent.data.MediaFeed;
import com.edaviessmith.contTent.data.User;
import com.edaviessmith.contTent.util.Var;

public class PagerAdapter extends FragmentStateCachePagerAdapter {

    private static String TAG = "PagerAdapter";
    User user;

    public PagerAdapter(FragmentManager childManager, User user) {
        super(childManager);
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
        MediaFeed mediaFeed = user.getMediaFeedSort(position);
        int type = mediaFeed.getType();
        if(Var.isTypeYoutube(type)) {
            return YoutubeFragment.newInstance(user.getId(), mediaFeed.getId());
        } else if(type == Var.TYPE_TWITTER) {
            return TwitterFragment.newInstance(user.getId(), mediaFeed.getId());
        }
        return PlaceholderFragment.newInstance(position, "Placeholder (nothing to see here)");
    }



    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {

        if(position < user.getMediaFeed().size())  return user.getMediaFeedSort(position).getName();

        return "Placeholder (nothing to see here)";

    }

}