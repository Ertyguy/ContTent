package com.edaviessmith.consumecontent.data;

import java.util.ArrayList;
import java.util.List;

public class User extends Content{

    //TODO feed list is in youtubechannel now
	public List<MediaFeed> media;

    private YoutubeChannel youtubeChannel;
    private TwitterFeed twitterFeed;

    private boolean isNotification;

    private List<Group> groups; //TODO need to thoroughly hash out this relationship

    public User () {
        youtubeChannel = new YoutubeChannel();
        twitterFeed = new TwitterFeed();
        groups = new ArrayList<Group>();
        isNotification = true;
    }

    //Depricated
    public User (String name, List<MediaFeed> media) {
		setName(name);
		this.media = media;
	}

    public User(int id, int sort, String name, String thumbnail, int youtubeChannelId, int twitterFeedId, boolean isNotification) {
        super(id, sort, name, thumbnail);
        setNotification(isNotification);

        youtubeChannel = new YoutubeChannel(youtubeChannelId);
        twitterFeed = new TwitterFeed(twitterFeedId);
    }


    public YoutubeChannel getYoutubeChannel() {
        return youtubeChannel;
    }

    public void setYoutubeChannel(YoutubeChannel youtubeChannel) {
        this.youtubeChannel = youtubeChannel;
    }

    public TwitterFeed getTwitterFeed() {
        return twitterFeed;
    }

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
}