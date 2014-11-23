package com.edaviessmith.consumecontent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.data.YoutubeItem;
import com.edaviessmith.consumecontent.db.AndroidDatabaseManager;
import com.edaviessmith.consumecontent.db.UserORM;
import com.edaviessmith.consumecontent.util.ImageLoader;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.view.VideoPlayerFragment;
import com.edaviessmith.consumecontent.view.VideoPlayerLayout;

import java.util.Date;
import java.util.List;

public class ContentActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static String TAG = "ContentActivity";
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
    private VideoPlayerLayout videoPlayerLayout;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private static final String TAG_TASK_FRAGMENT = "task_fragment";
	private MediaFeedFragment mediaFeedFragment;
	private List<User> users;
    Toolbar toolbar;
    public ImageLoader imageLoader;
    ImageView actionSettings;
    VideoPlayerFragment videoPlayerFragment;

    TextView videoTitle_tv, videoViews_tv, videoDescription_tv, videoDate_tv;

    public int userPos;
    //boolean isVideoPlaying;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        videoTitle_tv = (TextView) findViewById(R.id.video_title_tv);
        videoViews_tv = (TextView) findViewById(R.id.video_views_tv);
        videoDescription_tv = (TextView) findViewById(R.id.video_description_tv);
        videoDate_tv = (TextView) findViewById(R.id.video_date_tv);


        videoPlayerLayout = (VideoPlayerLayout) findViewById(R.id.video_player_v);
        videoPlayerLayout.init(this);

		mTitle = getTitle();
        imageLoader = new ImageLoader(this);


		users = UserORM.getUsers(this);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(this, R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));


	    mediaFeedFragment = (MediaFeedFragment) getSupportFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);
	    mediaFeedFragment = MediaFeedFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mediaFeedFragment, TAG_TASK_FRAGMENT).commit();

        actionSettings = (ImageView) findViewById(R.id.action_settings);
        actionSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContentActivity.this, AndroidDatabaseManager.class));
            }
        });


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

    }

    @Override
    protected void onResume() {
        super.onResume();
        //users = UserORM.getUsers(this);

        mNavigationDrawerFragment.adapter.notifyDataSetChanged();
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		if(users != null) {
            userPos = position;
			FragmentManager fragmentManager = getSupportFragmentManager();
		    mediaFeedFragment = (MediaFeedFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);
		    mediaFeedFragment = MediaFeedFragment.newInstance();
		    fragmentManager.beginTransaction().replace(R.id.container, mediaFeedFragment, TAG_TASK_FRAGMENT).commit();
		}

	}


    public User getUser() {
        return (userPos < users.size() ? users.get(userPos): null);
    }

    public User getUser(int pos) {
        return (pos < users.size() ? users.get(pos): null);
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
        //return videoPlayerFragment == null || videoPlayerFragment.activePlayer == null || videoPlayerFragment.activePlayer.isPlaying();
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
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*if (!mNavigationDrawerFragment.isDrawerOpen()) {
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
            Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
            startActivity(dbmanager);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


    @Override
    public void onBackPressed() {
        if(videoPlayerLayout != null && !videoPlayerLayout.isDismiss && !videoPlayerLayout.isMinimized) {
            videoPlayerLayout.minimize();
        } else {
            super.onBackPressed();
        }
    }
}
