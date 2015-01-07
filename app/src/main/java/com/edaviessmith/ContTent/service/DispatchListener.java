package com.edaviessmith.contTent.service;


import com.edaviessmith.contTent.data.Group;
import com.edaviessmith.contTent.data.MediaFeed;

public interface DispatchListener {

    void binderReady();

    void updatedUsers();
    void updatedGroups();

    void updatedMediaFeed(int mediaFeedId, int feedState);
    void updateMediaFeedDatabase(int userId, int mediaFeedId);

    void updatedUser(int userId);
    void updatedGroup(int id);


    void updateUserChanged();

    void updateGroup(Group group);

    void updateMediaFeed(MediaFeed mediaFeed);


}