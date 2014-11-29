package com.edaviessmith.consumecontent.data;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public class User extends Content{


	public SparseArray mediaFeed; //Id should be the sort value
    private List<Group> groups;

    private SparseArray<MediaFeed> removed;


    public User () {
        mediaFeed = new SparseArray();
        groups = new ArrayList<Group>();

    }

    public User(int id, int sort, String name, String thumbnail) {
        super(id, sort, name, thumbnail);
    }

    public User(int id, int sort, String name, String thumbnail, SparseArray mediaFeed) {
        super(id, sort, name, thumbnail);
        this.mediaFeed = mediaFeed;
    }

    public User(String name, String thumbnail, SparseArray mediaFeed, List<Group> groups) {
        super(name, thumbnail);
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
        return "}}); \n\nnew User(" + getId() +
                ", " + getSort() +
                ", \"" + getName() + '\"' +
                ", \"" + getThumbnail() + '\"' +
                ", new ArrayList<YoutubeFeed>(){{";
    }


}