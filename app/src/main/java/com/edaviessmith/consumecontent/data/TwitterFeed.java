package com.edaviessmith.consumecontent.data;


import com.edaviessmith.consumecontent.util.Var;

public class TwitterFeed extends MediaFeed {


    //public List<TwitterItem> twitterItems;

    public String displayName;

    public TwitterFeed() {
        setType(Var.TYPE_TWITTER);

        setName("Twitter");
    }


    public TwitterFeed(int id) {
        setType(Var.TYPE_TWITTER);
        setId(id);
    }

    public TwitterFeed(String feedId) {
        super(feedId, "Twitter", Var.TYPE_TWITTER);
    }

    public TwitterFeed(String feedId, String name) {
        super(feedId, name, Var.TYPE_TWITTER);
    }

    public TwitterFeed(int id, int sort, String name,  String displayName, String thumbnail, String feedId, int type) {
        super(id, sort, name, thumbnail, feedId, type);
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


}
