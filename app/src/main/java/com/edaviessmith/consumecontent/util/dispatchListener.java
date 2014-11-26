package com.edaviessmith.consumecontent.util;


import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.data.User;

public interface DispatchListener {

    void binderReady();

    void updatedUsers();
    void updatedGroups();

    void updatedUserMediaFeed(int userId, int mediaFeedId);

    void updateGroup(Group group);
    void updateUser(User user);
    void updateMediaFeed(MediaFeed mediaFeed);
}