package com.edaviessmith.consumecontent.service;


import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.MediaFeed;

public interface DispatchListener {

    void binderReady();

    void updatedUsers();
    void updatedGroups();

    void updatedMediaFeed(int mediaFeedId, int feedState);
    void updatedUser(int userId);
    void updateMediaFeedDatabase(int userId, int mediaFeedId);

    void updateUserChanged();

    void updateGroup(Group group);

    void updateMediaFeed(MediaFeed mediaFeed);
}