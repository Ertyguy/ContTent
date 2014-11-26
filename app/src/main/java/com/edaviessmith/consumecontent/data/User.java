package com.edaviessmith.consumecontent.data;

import java.util.ArrayList;
import java.util.List;

public class User extends Content{


	public List mediaFeed;
    private List<Group> groups;

    private List <MediaFeed> removed;


    public User () {
        mediaFeed = new ArrayList();
        groups = new ArrayList<Group>();

    }

    public User(int id, int sort, String name, String thumbnail) {
        super(id, sort, name, thumbnail);
    }

    public User(int id, int sort, String name, String thumbnail, List mediaFeed) {
        super(id, sort, name, thumbnail);
        this.mediaFeed = mediaFeed;
    }

    public User(int sort, String name, String thumbnail, List mediaFeed, List<Group> groups) {
        super(sort, name, thumbnail);
        this.mediaFeed = mediaFeed;
        this.groups = groups;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List getMediaFeed() {
        return mediaFeed;
    }

    public void setMediaFeed(List mediaFeed) {
        this.mediaFeed = mediaFeed;
    }

    public List<MediaFeed> getCastMediaFeed() {
        return mediaFeed;
    }

    public List<MediaFeed> getRemoved() {
        return removed;
    }

    //Utility method to update mediaFeeds and set removed feeds
    public void addMediaFeed(List<MediaFeed> mediaFeeds) {
        removed = new ArrayList<MediaFeed>();
        removed.addAll(getCastMediaFeed());
        removed.removeAll(mediaFeeds);

        setMediaFeed(mediaFeeds);
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