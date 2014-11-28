package com.edaviessmith.consumecontent.service;


import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.data.User;

public interface DispatchListener {

    void binderReady();

    void updatedUsers();
    void updatedGroups();

    void updatedMediaFeed(int mediaFeedId, int feedState);
    void updateMediaFeedDatabase(int userId, int mediaFeedId);

    void updateUserChanged();

    void updateGroup(Group group);
    void updateUser(User user);
    void updateMediaFeed(MediaFeed mediaFeed);
}