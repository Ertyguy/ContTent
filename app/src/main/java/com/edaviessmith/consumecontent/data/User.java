package com.edaviessmith.consumecontent.data;

import java.util.ArrayList;
import java.util.List;

public class User extends Content{


	public List<MediaFeed> mediaFeed;

    //TODO: @Deprecated  channel and feed are back to mediaFeed items again
    private YoutubeChannel youtubeChannel;
    private TwitterFeed twitterFeed;

    private boolean isNotification;

    private List<Group> groups; //TODO need to thoroughly hash out this relationship

    public User () {
        //youtubeChannel = new YoutubeChannel();
        //twitterFeed = new TwitterFeed();
        mediaFeed = new ArrayList<MediaFeed>();
        groups = new ArrayList<Group>();
        isNotification = true;
    }

    public User(int id, int sort, String name, String thumbnail, /*int youtubeChannelId, int twitterFeedId,*/ boolean isNotification) {
        super(id, sort, name, thumbnail);
        setNotification(isNotification);

        /*youtubeChannel = new YoutubeChannel(youtubeChannelId);
        twitterFeed = new TwitterFeed(twitterFeedId);*/
    }

    @Deprecated
    public YoutubeChannel getYoutubeChannel() {
        return youtubeChannel;
    }
    @Deprecated
    public void setYoutubeChannel(YoutubeChannel youtubeChannel) {
        this.youtubeChannel = youtubeChannel;
    }
    @Deprecated
    public TwitterFeed getTwitterFeed() {
        return twitterFeed;
    }
    @Deprecated
    public void setTwitterFeed(TwitterFeed twitterFeed) {
        this.twitterFeed = twitterFeed;
    }

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean isNotification) {
        this.isNotification = isNotification;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<MediaFeed> getMediaFeed() {
        return mediaFeed;
    }

    public void setMediaFeed(List<MediaFeed> mediaFeed) {
        this.mediaFeed = mediaFeed;
    }
}