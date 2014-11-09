package com.edaviessmith.consumecontent.data;


import com.edaviessmith.consumecontent.util.Var;

import java.util.List;

public class YoutubeFeed extends MediaFeed {

    public String feedId;   //Not required for YT_ACTIVITY
    public List<YoutubeItem> youtubeItems;
    public int feedType;

    //Used for search
    public String image;

    public boolean vis;

    public YoutubeFeed() {
        this.type = Var.TYPE_YOUTUBE;
        this.feedType = Var.YT_ACTIVITY;
        this.name = "Activity";
    }

    public YoutubeFeed(String feedId) {
        this.type = Var.TYPE_YOUTUBE;
        this.feedId = feedId;
        this.name = "Youtube";
        this.feedType = Var.YT_FEED;
        setVisible(true);
    }

    public YoutubeFeed(String feedId, String name) {
        this.type = Var.TYPE_YOUTUBE;
        this.feedId = feedId;
        this.name = name;
        this.feedType = Var.YT_FEED;
        setVisible(true);
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

    public boolean isVisible() {return vis; }

    public void setVisible(boolean vis) {this.vis = vis; }

}
