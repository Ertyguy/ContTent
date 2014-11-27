package com.edaviessmith.consumecontent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.YoutubeItem;
import com.edaviessmith.consumecontent.db.AndroidDatabaseManager;
import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.util.ActionActivity;
import com.edaviessmith.consumecontent.util.ActionDispatch;
import com.edaviessmith.consumecontent.util.ImageLoader;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.view.VideoPlayerFragment;
import com.edaviessmith.consumecontent.view.VideoPlayerLayout;

import java.util.Date;

public class ContentActivity extends ActionActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener {

    private static final String TAG_TASK_FRAGMENT = "task_fragment";


    private NavigationDrawerFragment navigationDrawerFragment;
    private MediaFeedFragment mediaFeedFragment;
    private GroupFragment groupFragment;
    private VideoPlayerLayout videoPlayerLayout;
    private VideoPlayerFragment videoPlayerFragment;

    //private List<Group> groups;
    //private List<User> users;
    //public NotificationList notificationList;

    Toolbar toolbar;
    private CharSequence actionBarTitle;
    public ImageLoader imageLoader;
    ImageView actionSettings, actionEdit;
    View actionDelete, actionNotification;
    TextView videoTitle_tv, videoViews_tv, videoDescription_tv, videoDate_tv;

    //public int selectedUser, selectedGroup,
            public int contentState = -1;

    public ContentActivity() {

        actionDispatch = new ActionDispatch() {

            @Override
            public void updatedUsers() {
                super.updatedUsers();


                openUsers();
            }

            @Override
            public void updatedGroups() {
                super.updatedGroups();

                openGroups();
            }

            @Override
            public void updateUserChanged() {
                super.updateUserChanged();

                openUsers();
            }
        };

    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        videoTitle_tv = (TextView) findViewById(R.id.video_title_tv);
        videoViews_tv = (TextView) findViewById(R.id.video_views_tv);
        videoDescription_tv = (TextView) findViewById(R.id.video_description_tv);
        videoDate_tv = (TextView) findViewById(R.id.video_date_tv);


        videoPlayerLayout = (VideoPlayerLayout) findViewById(R.id.video_player_v);
        videoPlayerLayout.init(this);

		actionBarTitle = getTitle();
        imageLoader = new ImageLoader(this);

        //selectedGroup = Var.getIntPreference(this, Var.PREF_SELECTED_GROUP);


        actionDelete = findViewById(R.id.action_delete);
        actionDelete.setOnClickListener(this);
        actionNotification = findViewById(R.id.action_notification);
        actionNotification.setOnClickListener(this);

        actionEdit = (ImageView) findViewById(R.id.action_edit);
        actionEdit.setOnClickListener(this);

        actionSettings = (ImageView) findViewById(R.id.action_settings);
        if(actionSettings != null) actionSettings.setOnClickListener(this);


        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            // TODO: The system bars are visible. Make any desired adjustments to your UI, such as showing the action bar or other navigational controls.
                        } else {
                            // TODO: The system bars are NOT visible. Make any desired adjustments to your UI, such as hiding the action bar or other navigational controls.
                        }
                        videoPlayerLayout.invalidate();
                        //Log.d(TAG, "system ui listener resize view here");
            }});


        //Init Navigation Drawer
        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onBind() {
        super.onBind();
        navigationDrawerFragment.setUp();

        toggleState(Var.LIST_USERS);
        updateData();
    }



    public void startVideo(YoutubeItem youtubeItem) {
        videoPlayerFragment = VideoPlayerFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.video_v, videoPlayerFragment).commit();

        videoPlayerFragment.init(youtubeItem.getVideoId());
        videoPlayerLayout.open();

        videoTitle_tv.setText(youtubeItem.getTitle());
        videoViews_tv.setText(youtubeItem.getViews() + " views");
        videoDescription_tv.setText(youtubeItem.getDescription());
        videoDate_tv.setText(Var.simpleDate.format(new Date(youtubeItem.getDate())));
    }


    public boolean isVideoPlaying() {
        return videoPlayerFragment != null && videoPlayerFragment.activePlayer != null && videoPlayerFragment.activePlayer.isPlaying();
    }

    public void setVideoPlaying(boolean isVideoPlaying) {
        if(videoPlayerFragment != null) videoPlayerFragment.toggleVideoPlayback(isVideoPlaying);
        updateUIVisibility();
    }

    public void toggleVideoControls(boolean show) {
        if(videoPlayerFragment != null) videoPlayerFragment.toggleControls(show);
    }

    public void toggleVideoPlayback(boolean play) {
        if(videoPlayerFragment != null) videoPlayerFragment.toggleVideoPlayback(play);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        updateUIVisibility();
    }

    @SuppressLint("NewApi")
    public void updateUIVisibility() {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            if (!isVideoPlaying() || getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

            }

            if (isVideoPlaying()&& getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }

        }
    }


	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle(actionBarTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*if (!navigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen if the drawer is not showing. Otherwise, let the drawer decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.content, menu);
			restoreActionBar();
			return true;
		}*/
        //getMenuInflater().inflate(R.menu.content, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
		/*int id = item.getItemId();
		if (id == R.id.action_settings) {
            Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
            startActivity(dbmanager);
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}


    @Override
    public void onBackPressed() {
        if(videoPlayerLayout != null && !videoPlayerLayout.isDismiss && !videoPlayerLayout.isMinimized) {
            videoPlayerLayout.minimize();
            return;
        }
        if(contentState != Var.LIST_USERS) {
            if(groupFragment != null && groupFragment.groupState != GroupFragment.GROUPS_LIST) {
                groupFragment.toggleState(GroupFragment.GROUPS_LIST);
            } else {
                toggleState(Var.LIST_USERS);
            }
            return;
        }
        super.onBackPressed();

    }

    //public List<Group> getGroups() {
    //    return groups;
    //}

    public void toggleState(int state) {
        this.contentState = state;

        if(!DB.isValid(binder.getSelectedUser())) contentState = Var.LIST_GROUPS;
        Log.d(TAG, "toggleState "+contentState);

        actionEdit.setVisibility(contentState == Var.LIST_GROUPS ? View.VISIBLE: View.GONE);

        toggleEditActions(false);

        //TODO nav drawer should have an update call (only when users has changed)
    }

    public void updateData() {
        if (contentState == Var.LIST_USERS) {
            binder.fetchUsersByGroup(binder.getSelectedUser());
        }
        if (contentState == Var.LIST_GROUPS) {
            binder.fetchGroups();
        }
    }


    public void setGroup(Group group) {

        //toggleState(Var.LIST_USERS);

        //if(group.getId() == binder.getSelectedGroup()) {
            //binder.setSelectedUser(0);
            //toggleState(Var.LIST_USERS);
            //openUsers();
        //}
        binder.setSelectedGroup(group.getId());


        /*binder.setSelectedUser(0); */


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        if(binder != null && (contentState != Var.LIST_USERS ||  binder.getUser() == null || binder.getUser().getSort() != position)) {
            binder.setSelectedUser(position);
            toggleState(Var.LIST_USERS);
        }

    }


    @Override
    public void onClick(View v) {

        if(actionNotification == v) {
            groupFragment.setNotifications();
        }

        if(actionDelete == v) {
            groupFragment.deleteConfirmation();
        }

        if(actionEdit == v) {
            groupFragment.toggleState(groupFragment.groupState == GroupFragment.GROUPS_LIST ? GroupFragment.GROUPS_ALL : GroupFragment.GROUPS_LIST);
        }

        if(actionSettings == v) {
            startActivity(new Intent(ContentActivity.this, AndroidDatabaseManager.class));
        }

    }

    public void toggleEditActions(boolean show) {
        actionNotification.setVisibility(show ? View.VISIBLE: View.GONE);
        actionDelete.setVisibility(show ? View.VISIBLE: View.GONE);
    }

    public void openGroups() {
        //Init Groups
        groupFragment = GroupFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, groupFragment).commit();

        navigationDrawerFragment.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
    }

    public void openUsers() {
        toggleState(Var.LIST_USERS);
        navigationDrawerFragment.adapter.notifyDataSetChanged();

        //Init MediaFeed
        mediaFeedFragment = MediaFeedFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mediaFeedFragment).commit();

        if(binder.getUser() != null)
            getSupportActionBar().setTitle(binder.getUser().getName());

        navigationDrawerFragment.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
    }
}
