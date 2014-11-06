package com.edaviessmith.consumecontent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.Media;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.view.FragmentStateCachePagerAdapter;
import com.edaviessmith.consumecontent.view.NavigationDrawerFragment;

import java.util.ArrayList;
import java.util.List;

public class Content extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	
	
	
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private static final String TAG_TASK_FRAGMENT = "task_fragment";
	private TaskFragment taskFragment;
	private List<User> users;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);

		mTitle = getTitle();
		
		
		users = new ArrayList<User>() {{
		
			add(new User("Adam", new ArrayList<Media>() {{
				add(new Media(0, "Youtube uploads"));
				add(new Media(0, "Youtube activity"));
				add(new Media(1, "Twitter Feed"));
			}}));
			
			add(new User("Ben", new ArrayList<Media>() {{
				add(new Media(0, "Youtube uploads"));
                add(new Media(1, "Twitter Feed"));
                add(new Media(1, "Twitter Channel"));
			}}));
			
			add(new User("Chris", new ArrayList<Media>() {{
				add(new Media(3, "Reddit Feed"));
			}}));
		}};
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(this, R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), users);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
	    taskFragment = (TaskFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);
	    taskFragment = TaskFragment.newInstance(this, users.get(0));
	    fragmentManager.beginTransaction().replace(R.id.container, taskFragment, TAG_TASK_FRAGMENT).commit();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		if(users != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
		    taskFragment = (TaskFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);
		    taskFragment = TaskFragment.newInstance(this, users.get(position));
		    fragmentManager.beginTransaction().replace(R.id.container, taskFragment, TAG_TASK_FRAGMENT).commit();
		}
		
	}


	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen if the drawer is not showing. Otherwise, let the drawer decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.content, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	public static class PagerAdapter extends FragmentStateCachePagerAdapter {
		
		User user;
		Content act;

        public PagerAdapter(Content act, User user) {
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
            Fragment frag = null;
            switch(user.media.get(position).type) {
                case 0: frag = YoutubeFragment.newInstance(act, user.media.get(position), position); break;
                case 1: frag = TwitterFragment.newInstance(act, user.media.get(position), position); break;
                default: frag =  FirstFragment.newInstance(position, user.media.get(position).type+" ");
            }

            return frag;
        }



        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return user.media.get(position).name;
        }

	}
	
	
	public static class FirstFragment extends Fragment {
	    // Store instance variables
	    private String title;
	    private int page;

	    // newInstance constructor for creating fragment with arguments
	    public static FirstFragment newInstance(int page, String title) {
	        FirstFragment fragmentFirst = new FirstFragment();
	        Bundle args = new Bundle();
	        args.putInt("someInt", page);
	        args.putString("someTitle", title);
	        fragmentFirst.setArguments(args);
	        return fragmentFirst;
	    }

	    // Store instance variables based on arguments passed
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        page = getArguments().getInt("someInt", 0);
	        title = getArguments().getString("someTitle");
	    }

	    // Inflate the view for the fragment based on layout XML
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.fragment_first, container, false);
	        TextView tvLabel = (TextView) view.findViewById(R.id.tv_label);
	        tvLabel.setText(page + " -- " + title);
	        return view;
	    }
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class TaskFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_USER = "arg_user";
		public FragmentStateCachePagerAdapter adapterViewPager;
		static Content act;
		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static TaskFragment newInstance(Content act, User user) {
			TaskFragment.act = act;
			TaskFragment fragment = new TaskFragment(user);
			/*Bundle args = new Bundle();
			Log.d("Content","nav fragment "+user.name);
			args.putSerializable(ARG_USER, user);
			//args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			//args.putString(ARG_PAGE_NAME, pageName);
			fragment.setArguments(args);*/
			return fragment;
		}

		User user;
        public TaskFragment() { }
		public TaskFragment(User user) { 
			this.user = user;
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_content, container, false);
			
			ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp_pager);
	        adapterViewPager = new PagerAdapter(act, user);
	        viewPager.setAdapter(adapterViewPager);
	        
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

}
