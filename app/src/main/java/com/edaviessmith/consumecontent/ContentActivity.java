package com.edaviessmith.consumecontent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.YoutubeItem;
import com.edaviessmith.consumecontent.db.AndroidDatabaseManager;
import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.service.ActionActivity;
import com.edaviessmith.consumecontent.service.ActionDispatch;
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


    Toolbar toolbar;
    private CharSequence actionBarTitle;
    ImageView actionSettings, actionEdit;
    View actionDelete, actionNotification;
    TextView videoTitle_tv, videoViews_tv, videoDescription_tv, videoDate_tv;

    public int contentState = -1;

    public ContentActivity() {

        actionDispatch = new ActionDispatch() {

            @Override
            public void binderReady() {
                super.binderReady();


                //FIXME Throws nullpointer when pausing and resuming app
                if(mediaFeedFragment != null) mediaFeedFragment.adapterViewPager.notifyDataSetChanged();
                Log.d(TAG, "binderReady");
            }

            @Override
            public void updatedUsers() {
                super.updatedUsers();

                navigationDrawerFragment.selectItem(binder.getUser().getSort(), true);

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

            @Override
            public void updatedUser(int userId) {
                super.updatedUsers();
                Log.d(TAG,"updatedUser "+userId);
                navigationDrawerFragment.adapter.notifyDataSetChanged();
                openUsers();

                //TODO not updating
                if(contentState == Var.LIST_USERS && userId == binder.getSelectedUser()) {

                } else {

                }

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


        actionDelete = findViewById(R.id.action_delete);
        actionDelete.setOnClickListener(this);
        actionNotification = findViewById(R.id.action_notification);
        actionNotification.setOnClickListener(this);

        actionEdit = (ImageView) findViewById(R.id.action_edit);
        //actionEdit.setOnClickListener(this);

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

    @Override
    protected void onPause() {
        super.onPause();
        if(binder != null)
            Var.setNextAlarm(this, binder.getNotificationList());
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
            if(groupFragment != null && groupFragment.groupState != GroupFragment.GROUPS_LIST) {
                groupFragment.toggleState(GroupFragment.GROUPS_LIST);
            } else {
                toggleState(Var.LIST_USERS);
            }
			return true;
		}
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
                return;
            } else if(DB.isValid(binder.getSelectedGroup()) && DB.isValid(binder.getSelectedUser())) {
                openUsers();
                return;
            }
        }

        super.onBackPressed();
    }


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
            binder.fetchUsers();
        }

        if (contentState == Var.LIST_GROUPS) {
            binder.fetchGroups();
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Log.d(TAG, "onNavigationDrawerItemSelected "+position);
        if(binder != null){// && (contentState != Var.LIST_USERS ||  binder.getUser() == null || binder.getUser().getSort() != position)) {
            Log.d(TAG, "onNavigationDrawerItemSelected "+ position + " - "+binder.getUser().getSort());
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
        toggleState(Var.LIST_GROUPS);
        groupFragment = GroupFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, groupFragment).commit();

        toggleNavDrawerIndicator(false);
    }

    public void openUsers() {
        toggleState(Var.LIST_USERS);
        navigationDrawerFragment.adapter.notifyDataSetChanged();

        //Init MediaFeed
        mediaFeedFragment = MediaFeedFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mediaFeedFragment).commit();

        if(binder.getUser() != null)
            getSupportActionBar().setTitle(binder.getUser().getName());

        toggleNavDrawerIndicator(true);
    }

    public void toggleNavDrawerIndicator(boolean show) {
        Log.d(TAG, "toggleNavDrawerIndicator " + show);
        boolean validGroupUser = DB.isValid(binder.getSelectedGroup()) && DB.isValid(binder.getSelectedUser());
        //The order matters
        if(!show) navigationDrawerFragment.actionBarDrawerToggle.setDrawerIndicatorEnabled(show);
        getSupportActionBar().setDisplayHomeAsUpEnabled(!show && validGroupUser);
        getSupportActionBar().setHomeButtonEnabled(!show && validGroupUser);
        if(show) navigationDrawerFragment.actionBarDrawerToggle.setDrawerIndicatorEnabled(show);


        navigationDrawerFragment.drawerLayout.setDrawerLockMode(show ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationDrawerFragment.actionBarDrawerToggle.syncState();

    }

    public void homeNavDrawerIndicatorClick() {

        if(groupFragment != null && groupFragment.groupState != GroupFragment.GROUPS_LIST) {
            groupFragment.toggleState(GroupFragment.GROUPS_LIST);
        } else {
            openUsers();
        }

    }


}
