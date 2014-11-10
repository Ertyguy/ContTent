package com.edaviessmith.consumecontent.data;


import java.util.ArrayList;
import java.util.List;

public class YoutubeChannel {

    String channelId;
    String name;
    String thumbnail;
    List<YoutubeFeed> youtubeFeeds;


    public YoutubeChannel(){
        youtubeFeeds = new ArrayList<YoutubeFeed>();
    }

    public YoutubeChannel(String name, String channelId, String thumbnail) {
        this.name = name;
        this.channelId = channelId;
        this.thumbnail = thumbnail;
        youtubeFeeds = new ArrayList<YoutubeFeed>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumnail) {
        this.thumbnail = thumnail;
    }

    public List<YoutubeFeed> getYoutubeFeeds() {
        return youtubeFeeds;
    }

    public void setYoutubeFeeds(List<YoutubeFeed> youtubeFeeds) {
        this.youtubeFeeds = youtubeFeeds;
    }
}
