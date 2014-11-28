package com.edaviessmith.consumecontent.util;

import android.util.Log;

import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.data.User;

public class ActionDispatch implements DispatchListener {


    @Override
    public void binderReady() {
        Log.d("Dispatch", "binderReady ");
    }

    @Override
    public void updatedUsers() {}

    @Override
    public void updatedGroups() {

    }

    @Override
    public void updatedMediaFeed(int mediaFeedId, int feedState) {

    }

    @Override
    public void updateMediaFeedDatabase(int userId, int mediaFeedId) {

    }

    @Override
    public void updateUserChanged() {

    }

    @Override
    public void updateGroup(Group group) {}

    @Override
    public void updateUser(User user) {}

    @Override
    public void updateMediaFeed(MediaFeed mediaFeed) {}
}