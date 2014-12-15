package com.edaviessmith.consumecontent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.service.ActionDispatch;
import com.edaviessmith.consumecontent.service.ActionFragment;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.view.FragmentStateCachePagerAdapter;
import com.edaviessmith.consumecontent.view.PagerAdapter;
import com.edaviessmith.consumecontent.view.SlidingTabLayout;


public class MediaFeedFragment extends ActionFragment {

    public FragmentStateCachePagerAdapter adapterViewPager;
    //public PagerAdapter adapterViewPager;
    public SlidingTabLayout slidingTabLayout;
    private ContentActivity act;
    private User user;
    ViewPager viewPager;
    boolean setupRun;


    public static MediaFeedFragment newInstance() {
        return new MediaFeedFragment();
    }

    public MediaFeedFragment() {
        actionDispatch = new ActionDispatch() {

            @Override
            public void binderReady() {
                super.binderReady();

                Log.d(TAG, "binderReady");
            }
            @Override
            public void updatedUser(int userId) {
                super.updatedUsers();
                Log.d(TAG, "updatedUser "+userId);
                if(user.getId() == userId)
                    adapterViewPager.notifyDataSetChanged();

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_feed, container, false);
        act = (ContentActivity) getActivity();

        viewPager = (ViewPager) view.findViewById(R.id.vp_pager);
        slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);

        if(getBinder() != null) {
            setupUser.run();
            setupRun = true;
        }

        return view;
    }


    @Override
    protected void onBind() {
        super.onBind();
        if(!setupRun) setupUser.run();
    }

    Runnable setupUser = new Runnable() {
        @Override
        public void run() {
            user = getBinder().getUser();


            adapterViewPager = new PagerAdapter(getChildFragmentManager(), user);
            viewPager.setAdapter(adapterViewPager);

            slidingTabLayout.setViewPager(viewPager);
            slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    int type = (user.getMediaFeedSort(position)).getType();
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
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cleanFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Call methods in activity when the fragment has been loaded
        //((Content) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        //((Content) activity).onSectionAttached(getArguments().getString(ARG_PAGE_NAME));

    }

    @Override
    public void cleanFragment() {
        super.cleanFragment();

        if(viewPager != null) {
            for (int i = 0; i < viewPager.getChildCount(); i++) {
                ActionFragment frag = adapterViewPager.getRegisteredFragment(i);
                if (frag != null) {
                    frag.cleanFragment();
                }
            }
        }


        adapterViewPager = null;
        slidingTabLayout = null;
        act = null;
        user = null;
        viewPager = null;
        Log.d(TAG, "cleaning up view");


    }

}