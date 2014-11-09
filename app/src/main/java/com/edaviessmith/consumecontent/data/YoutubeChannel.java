package com.edaviessmith.consumecontent.data;


import java.util.ArrayList;
import java.util.List;

public class YoutubeChannel {
    String displayName;
    String channelId;
    String thumbnail;
    List<YoutubeFeed> youtubeFeeds;


    public YoutubeChannel(){
        youtubeFeeds = new ArrayList<YoutubeFeed>();
    }

    public YoutubeChannel(String displayName, String channelId, String thumbnail) {
        this.displayName = displayName;
        this.channelId = channelId;
        this.thumbnail = thumbnail;
        youtubeFeeds = new ArrayList<YoutubeFeed>();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
