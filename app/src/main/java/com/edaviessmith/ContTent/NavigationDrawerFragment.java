package com.edaviessmith.contTent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edaviessmith.contTent.data.User;
import com.edaviessmith.contTent.service.ActionDispatch;
import com.edaviessmith.contTent.service.ActionFragment;
import com.edaviessmith.contTent.util.Listener;
import com.edaviessmith.contTent.util.Var;
import com.edaviessmith.contTent.view.Fab;


public class NavigationDrawerFragment extends ActionFragment implements View.OnClickListener{


	/** Remember the position of the selected item. */
	//private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
	/** Per the design guidelines, you should show the drawer on launch until the user manually expands it. This shared preference tracks this.  */
	//private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
	/**  A pointer to the current callbacks instance (the Activity). */
	private NavigationDrawerCallbacks mCallbacks;
	/** Helper component that ties the action bar to the navigation drawer. */
	public ActionBarDrawerToggle actionBarDrawerToggle;

	public DrawerLayout drawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;
	//private boolean mFromSavedInstanceState;
	//private boolean mUserLearnedDrawer;
    private Fab actionFab;
    private View notification_v, settings_v, groups_v, footer;

    public ListAdapter adapter;

	ContentActivity act;
	
	public NavigationDrawerFragment() {
        actionDispatch = new ActionDispatch() {

            @Override
            public void updatedUsers() {
                super.updatedUsers();

                adapter.notifyDataSetChanged();
                Log.d(TAG, "binder is null: "+(getBinder() == null));

            }
        };
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated awareness of the drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		//mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		/*if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}*/

		// Select either the default item (0) or the last selected item.
        //TODO update selected when changing groups

		//selectItem(0);
	}

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     */
    public void setUp() {
        act = (ContentActivity) getActivity();
        mFragmentContainerView = act.findViewById(R.id.navigation_drawer);
        drawerLayout = (DrawerLayout) act.findViewById(R.id.drawer_layout);


        adapter = new ListAdapter(act);
        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);


        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, act.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                //getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                //if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing the navigation drawer automatically in the future.
                    //mUserLearnedDrawer = true;
                    //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    //sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
                //}

                //getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                actionFab.setTranslationX(- (actionFab.getMeasuredWidth() * (1 - slideOffset)));
            }
        };


        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer, per the navigation drawer design guidelines.
        //if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
        //    drawerLayout.openDrawer(mFragmentContainerView);
        //}

        // Defer code dependent on restoration of previous instance state.
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.homeNavDrawerIndicatorClick();
            }
        });

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }


    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nav_drawer, container, false);
		mDrawerListView = (ListView) v.findViewById(R.id.list);
		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});

        footer = inflater.inflate(R.layout.item_list_footer, null, false);
        footer.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 64)));

        mDrawerListView.addFooterView(footer, null, false);

        groups_v = v.findViewById(R.id.groups_v);
        groups_v.setOnClickListener(this);

        actionFab = (Fab) v.findViewById(R.id.action_fab);
        actionFab.setOnClickListener(this);

        notification_v = v.findViewById(R.id.notification_v);
        notification_v.setOnClickListener(this);

        settings_v = v.findViewById(R.id.settings_v);
        settings_v.setOnClickListener(this);
		return v;
	}


    @Override
    public void onClick(View v) {
        if(actionFab == v) {
            Intent i = new Intent(act, AddActivity.class);
            startActivity(i);
        }
        if(notification_v == v) {
            Intent i = new Intent(act, NotificationsActivity.class);
            startActivity(i);
        }
        if(settings_v == v) {
            Intent i = new Intent(act, SettingsActivity.class);
            startActivity(i);
        }
        if(groups_v == v) {
            drawerLayout.closeDrawer(mFragmentContainerView);
            getBinder().fetchGroups();
        }

    }

    public class ListAdapter extends BaseAdapter {

		Context context;
        LayoutInflater inflater;
	    
	    public ListAdapter(Context context) {
            inflater = ((Activity)context).getLayoutInflater();
	        this.context = context;
	    }

        @Override
        public int getCount() {
            return getBinder() == null? 0: getBinder().getUsers().size();
        }

        @Override
        public User getItem(int position) {
            return getBinder().getUsers().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder;
	        
	        if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_nav_drawer, parent, false);
	            holder = new ViewHolder(convertView);
                convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder)convertView.getTag();
	        }
	        
	        final User user = getItem(position);
	        holder.name_tv.setText(user.getName());
            holder.thumbnail_iv.setImageResource(R.drawable.ic_person_grey600_36dp);

            Listener l = Var.getUserThumbnailListener(getBinder(), user, holder.thumbnail_iv);
            getBinder().getImageLoader().DisplayImage(l, user.getThumbnail(), holder.thumbnail_iv);



            holder.edit_iv.setVisibility(user.equals(act.binder.getUser())? View.VISIBLE: View.GONE);
            holder.edit_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.closeDrawer(mFragmentContainerView);
                    Intent i = new Intent(act, AddActivity.class);
                    i.putExtra(Var.INTENT_USER_ID, user.getId());
                    startActivity(i);
                }
            });

	        return convertView;
	    }
	    
	    class ViewHolder {
	        TextView name_tv;
            ImageView thumbnail_iv;
            ImageView edit_iv;

            public ViewHolder(View view) {
                name_tv = (TextView) view.findViewById(R.id.title_tv);
                thumbnail_iv = (ImageView) view.findViewById(R.id.thumbnail_iv);
                edit_iv = (ImageView) view.findViewById(R.id.edit_iv);
            }
	    }
		
	}

    @Override
    protected void onBind() {
        super.onBind();

    }

    public void selectItem(int position) {
        selectItem(position, false);
    }
    public void selectItem(int position, boolean scroll) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
            if(scroll) mDrawerListView.smoothScrollToPosition(position);
		}
		if (drawerLayout != null) {
			drawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}


	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}
}