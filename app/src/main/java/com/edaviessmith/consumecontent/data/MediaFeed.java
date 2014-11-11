package com.edaviessmith.consumecontent.data;

public class MediaFeed {

    private int id;
    private String name;

    private String feedId; //Also used for channelId
    private String thumbnail;
    private int type;
    public MediaFeed() { }

    public MediaFeed(String feedId, String name) {
        this.feedId = feedId;
        this.name = name;
    }

    public MediaFeed(String feedId, String name, int type) {
        this.feedId = feedId;
        this.name = name;
        this.type = type;
    }

    public MediaFeed(int id, String name, String feedId, String thumbnail) {
        this.id = id;
        this.name = name;
        this.feedId = feedId;
        this.thumbnail = thumbnail;
    }

    public MediaFeed(String name, String feedId, String thumbnail) {
        this.name = name;
        this.feedId = feedId;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}