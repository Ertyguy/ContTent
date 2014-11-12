package com.edaviessmith.consumecontent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.db.AndroidDatabaseManager;
import com.edaviessmith.consumecontent.db.UserORM;
import com.edaviessmith.consumecontent.view.TaskFragment;

import java.util.List;

public class ContentActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static String TAG = "ContentActivity";
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
		
		
		users = UserORM.getUsers(this);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(this, R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), users);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
	    taskFragment = (TaskFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);
	    taskFragment = TaskFragment.newInstance(this);
	    fragmentManager.beginTransaction().replace(R.id.container, taskFragment, TAG_TASK_FRAGMENT).commit();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		if(users != null) {
            userPos = position;
			FragmentManager fragmentManager = getSupportFragmentManager();
		    taskFragment = (TaskFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);
		    taskFragment = TaskFragment.newInstance(this);
		    fragmentManager.beginTransaction().replace(R.id.container, taskFragment, TAG_TASK_FRAGMENT).commit();
		}
		
	}
    int userPos;
    public User getUser() {
        return (userPos < users.size() ? users.get(userPos): null);
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
        getMenuInflater().inflate(R.menu.content, menu);

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


}
