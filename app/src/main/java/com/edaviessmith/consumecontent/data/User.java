package com.edaviessmith.consumecontent.data;

import android.util.SparseArray;

import com.edaviessmith.consumecontent.db.DB;

import java.util.ArrayList;
import java.util.List;

public class User extends Content{


	private SparseArray mediaFeed;
    private List<Group> groups;

    //private List<String> thumbnails;
    private SparseArray<MediaFeed> removed;


    public User () {
        mediaFeed = new SparseArray();
        groups = new ArrayList<Group>();

    }

    public User(int id, int sort, String name, int thumb, String thumbnail) {
        super(id, sort, name, thumb, thumbnail);
    }


    //TODO need to include sort
    @Deprecated
    public User(String name, String thumbnail, SparseArray mediaFeed, List<Group> groups) {
        super(name, thumbnail);
        this.mediaFeed = mediaFeed;
        this.groups = groups;
    }

    public User(String name, int thumb, String thumbnails, int sort, SparseArray mediaFeed, List<Group> groups) {
        super(sort, name, thumb, thumbnails);
        this.mediaFeed = mediaFeed;
        this.groups = groups;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public SparseArray getMediaFeed() {
        return mediaFeed;
    }

    public void setMediaFeed(SparseArray mediaFeed) {
        this.mediaFeed = mediaFeed;
    }

    public MediaFeed getMediaFeedSort(int sort) {
        for(int i=0; i < mediaFeed.size(); i++) {
            if(((MediaFeed)mediaFeed.valueAt(i)).getSort() == sort) return (MediaFeed) mediaFeed.valueAt(i);
        }

        return null;
    }

    public SparseArray<MediaFeed> getCastMediaFeed() {
        return mediaFeed;
    }

    public SparseArray<MediaFeed> getRemoved() {
        return removed;
    }

    public void clearRemoved() {
        removed = null;
    }

    //Utility method to update mediaFeeds and set removed feeds
    public void addMediaFeed(List<MediaFeed> mediaFeeds) {
        removed = getCastMediaFeed().clone();
        SparseArray<MediaFeed> feeds = new SparseArray<MediaFeed>();
        for(int i=0; i< mediaFeeds.size(); i++) {
            removed.remove(mediaFeeds.get(i).getId());
            feeds.put(i, mediaFeeds.get(i));
        }

        setMediaFeed(feeds);
    }



    @Override
    public String toString() {
        return "}}, new ArrayList<Group>() {{add(groupName); }})); \n\n" +
                "put("+getSort()+" new User(" +
                "\"" + getName() + '\"' +
                ", " + getThumb() +
                ", \"" + DB.stringListToString(getThumbnails()) + '\"' +
                ", " + getSort() +
                ", new SparseArray<YoutubeFeed>(){{";
        }


}