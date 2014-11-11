package com.edaviessmith.consumecontent.data;


import java.util.ArrayList;
import java.util.List;

public class YoutubeChannel extends MediaFeed {

    List<YoutubeFeed> youtubeFeeds;


    public YoutubeChannel(){
        youtubeFeeds = new ArrayList<YoutubeFeed>();
    }

    public YoutubeChannel(String name, String feedId, String thumbnail) {
        super(name, feedId, thumbnail);
        youtubeFeeds = new ArrayList<YoutubeFeed>();
    }

    public YoutubeChannel(int id, int sort, String name, String thumbnail, String feedId, int type) {
        super(id, sort, name, thumbnail, feedId, type);
    }

    public List<YoutubeFeed> getYoutubeFeeds() {
        return youtubeFeeds;
    }

    public void setYoutubeFeeds(List<YoutubeFeed> youtubeFeeds) {
        this.youtubeFeeds = youtubeFeeds;
    }
}
