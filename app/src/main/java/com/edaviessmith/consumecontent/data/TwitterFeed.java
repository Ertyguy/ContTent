package com.edaviessmith.consumecontent.data;


import com.edaviessmith.consumecontent.util.Var;

import java.util.List;

public class TwitterFeed extends MediaFeed {

    public String feedId;
    public List<TwitterItem> twitterItems;

    public String thumbnail;
    public String name;
    public String displayName;


    public TwitterFeed() {
        this.type = Var.TYPE_TWITTER;
        this.name = "Twitter";
    }

    public TwitterFeed(String feedId) {
        this.type = Var.TYPE_TWITTER;
        this.feedId = feedId;
        this.name = "Twitter";
    }

    public TwitterFeed(String feedId, String name) {
        this.type = Var.TYPE_TWITTER;
        this.feedId = feedId;
        this.name = name;
    }


    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public List<TwitterItem> getTwitterItems() {
        return twitterItems;
    }

    public void setTwitterItems(List<TwitterItem> twitterItems) {
        this.twitterItems = twitterItems;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
