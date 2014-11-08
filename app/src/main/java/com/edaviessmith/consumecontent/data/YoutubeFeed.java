package com.edaviessmith.consumecontent.data;


import com.edaviessmith.consumecontent.util.Var;

import java.util.List;

public class YoutubeFeed extends MediaFeed {

    public String feedId;
    public List<YoutubeItem> youtubeItems;

    //Used for search
    public String image;
    public String type_name;

    public YoutubeFeed(String feedId) {
        this.type = Var.TYPE_YOUTUBE;
        this.feedId = feedId;
        this.name = "Youtube";
    }

    public YoutubeFeed(String feedId, String name) {
        this.type = Var.TYPE_YOUTUBE;
        this.feedId = feedId;
        this.name = name;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public List<YoutubeItem> getYoutubeItems() {
        return youtubeItems;
    }

    public void setYoutubeItems(List<YoutubeItem> youtubeItems) {
        this.youtubeItems = youtubeItems;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

}
