package com.edaviessmith.consumecontent.data;

import java.util.List;

public class User {
    private String name;
    //TODO feed list is in youtubechannel now
	public List<MediaFeed> media;

    private YoutubeChannel youtubeChannel;
    private TwitterFeed twitterFeed;
    private String thumbnail;

    private boolean enableNotifications;

    public User () {
        youtubeChannel = new YoutubeChannel();
        twitterFeed = new TwitterFeed();
        enableNotifications = true;
    }

    //Depricated
    public User (String name, List<MediaFeed> media) {
		this.name = name;
		this.media = media;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}