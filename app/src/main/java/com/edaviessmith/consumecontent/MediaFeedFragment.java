package com.edaviessmith.consumecontent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.view.FragmentStateCachePagerAdapter;
import com.edaviessmith.consumecontent.view.PagerAdapter;
import com.edaviessmith.consumecontent.view.SlidingTabLayout;


public class MediaFeedFragment extends Fragment {

    private static final String TAG = "MediaFeedFragment";

    public FragmentStateCachePagerAdapter adapterViewPager;
    public SlidingTabLayout slidingTabLayout;
    private ContentActivity act;

    public static MediaFeedFragment newInstance() {
        return new MediaFeedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_feed, container, false);
        act = (ContentActivity) getActivity();

        Log.d(TAG, act.getUser().getName());

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp_pager);
        adapterViewPager = new PagerAdapter(act, act.getUser());
        viewPager.setAdapter(adapterViewPager);

        slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                int type = ((MediaFeed) act.getUser().getMediaFeed().get(position)).getType();
                if(Var.isTypeYoutube(type)) {
                    return act.getResources().getColor(R.color.red_youtube);
                }else if(type == Var.TYPE_TWITTER) {
                    return act.getResources().getColor(R.color.blue_twitter);
                }

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