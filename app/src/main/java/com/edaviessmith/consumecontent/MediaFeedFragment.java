package com.edaviessmith.consumecontent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.service.ActionDispatch;
import com.edaviessmith.consumecontent.service.ActionFragment;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.view.FragmentStateCachePagerAdapter;
import com.edaviessmith.consumecontent.view.PagerAdapter;
import com.edaviessmith.consumecontent.view.SlidingTabLayout;


public class MediaFeedFragment extends ActionFragment {

    public FragmentStateCachePagerAdapter adapterViewPager;
    public SlidingTabLayout slidingTabLayout;
    private ContentActivity act;
    ViewPager viewPager;


    public static MediaFeedFragment newInstance() {
        return new MediaFeedFragment();
    }

    public MediaFeedFragment() {
        actionDispatch = new ActionDispatch() {

            @Override
            public void binderReady() {
                super.binderReady();


            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_feed, container, false);
        act = (ContentActivity) getActivity();


        viewPager = (ViewPager) view.findViewById(R.id.vp_pager);
        slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);

        adapterViewPager = new PagerAdapter(act, getBinder().getUser());
        viewPager.setAdapter(adapterViewPager);

        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                int type = ((MediaFeed) getBinder().getUser().getMediaFeed().valueAt(position)).getType();
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