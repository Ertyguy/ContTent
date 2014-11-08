package com.edaviessmith.consumecontent.data;


import com.edaviessmith.consumecontent.util.Var;

import java.util.List;

public class TwitterFeed extends MediaFeed {

    public String feedId;
    public List<TwitterItem> twitterItems;

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

}
