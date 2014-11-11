package com.edaviessmith.consumecontent.data;

import java.util.List;

public class User extends Content{

    //TODO feed list is in youtubechannel now
	public List<MediaFeed> media;

    private YoutubeChannel youtubeChannel;
    private TwitterFeed twitterFeed;

    private boolean isNotification;


    public User () {
        youtubeChannel = new YoutubeChannel();
        twitterFeed = new TwitterFeed();
        isNotification = true;
    }

    //Depricated
    public User (String name, List<MediaFeed> media) {
		setName(name);
		this.media = media;
	}

    public User(int id, int sort, String name, String thumbnail, boolean isNotification) {
        super(id, sort, name, thumbnail);
        setNotification(isNotification);

        youtubeChannel = new YoutubeChannel();
        twitterFeed = new TwitterFeed();
    }

    public List<MediaFeed> getMedia() {
        return media;
    }

    public void setMedia(List<MediaFeed> media) {
        this.media = media;
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
}