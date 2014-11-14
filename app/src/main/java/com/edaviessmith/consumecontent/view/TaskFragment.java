package com.edaviessmith.consumecontent.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edaviessmith.consumecontent.ContentActivity;
import com.edaviessmith.consumecontent.R;
import com.edaviessmith.consumecontent.util.Var;

/**
 * A placeholder fragment containing a simple view.
 */
public class TaskFragment extends Fragment {

    private static final String ARG_USER = "arg_user";
    public FragmentStateCachePagerAdapter adapterViewPager;
    public SlidingTabLayout slidingTabLayout;
    static ContentActivity act;


    public static TaskFragment newInstance(ContentActivity act) {
        TaskFragment.act = act;
        TaskFragment fragment = new TaskFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp_pager);
        adapterViewPager = new PagerAdapter(act, act.getUser());
        viewPager.setAdapter(adapterViewPager);

        slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                if(act.getUser().getMediaFeed().get(position).getType() == Var.TYPE_YOUTUBE_PLAYLIST || act.getUser().getMediaFeed().get(position).getType() == Var.TYPE_YOUTUBE_ACTIVTY) {
                    return act.getResources().getColor(R.color.red_youtube);
                }else if(act.getUser().getMediaFeed().get(position).getType() == Var.TYPE_TWITTER) {
                    return act.getResources().getColor(R.color.blue_twitter);
                }

                /*if(position < act.getUser().getYoutubeChannel().getYoutubeFeeds().size()) {
                    return act.getResources().getColor(R.color.red_youtube);
                } else if(act.getUser().getTwitterFeed() != null && position == act.getUser().getYoutubeChannel().getYoutubeFeeds().size() ) {
                    return act.getResources().getColor(R.color.blue_twitter);
                }*/
                return act.getResources().getColor(R.color.accent);


            }

            @Override
            public int getDividerColor(int position) {
                return 0;
            }
        });


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Call methods in activity when the fragment has been loaded
        //((Content) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        //((Content) activity).onSectionAttached(getArguments().getString(ARG_PAGE_NAME));

    }



}