package com.edaviessmith.contTent.data;


import java.util.ArrayList;
import java.util.List;

public class YoutubeChannel extends MediaFeed {

    List<YoutubeFeed> youtubeFeeds;


    public YoutubeChannel(){
        youtubeFeeds = new ArrayList<YoutubeFeed>();
    }



    public List<YoutubeFeed> getYoutubeFeeds() {
        return youtubeFeeds;
    }

    public void setYoutubeFeeds(List<YoutubeFeed> youtubeFeeds) {
        this.youtubeFeeds = youtubeFeeds;
    }

    public String toString() {
        return "YoutubeChannel ("+getId()+", "+getName()+", "+getFeedId()+", "+getThumbnail()+")";
    }

}
