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
        int type = act.binder.getUser().getCastMediaFeed().valueAt(position).getType();
        if(Var.isTypeYoutube(type)) {
            return YoutubeFragment.newInstance(act.binder.getSelectedUser(), act.binder.getUser().getCastMediaFeed().valueAt(position).getId());
        } else if(type == Var.TYPE_TWITTER) {
            return TwitterFragment.newInstance(act, position);
        }
        return PlaceholderFragment.newInstance(position, "Placeholder (nothing to see here)");
    }



    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {

        if(position < user.getMediaFeed().size())  return act.binder.getUser().getCastMediaFeed().valueAt(position).getName();

        return "Placeholder (nothing to see here)";

    }

}