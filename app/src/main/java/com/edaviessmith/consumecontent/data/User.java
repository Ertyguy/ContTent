package com.edaviessmith.consumecontent.data;

import java.util.List;

public class User {
	public String name;
    //TODO feed list is in youtubechannel now
	public List<MediaFeed> media;

    public YoutubeChannel youtubeChannel;
    public TwitterFeed twitterFeed;

    public User () {
        youtubeChannel = new YoutubeChannel();
    }

    public User (String name, List<MediaFeed> media) {
		this.name = name;
		this.media = media;
	}
}