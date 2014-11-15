package com.edaviessmith.consumecontent.data;

import java.util.ArrayList;
import java.util.List;

public class MediaFeed {

    private int id = -1;
    private int sort;
    private String name;
    private String thumbnail;

    private String channelHandle;
    private String feedId;
    private int type;

    private List items;

    public MediaFeed() { }

    public MediaFeed(String feedId, String name) {
        this.feedId = feedId;
        this.name = name;
    }

    public MediaFeed(String feedId, String name, int type) {
        this.feedId = feedId;
        this.name = name;
        this.type = type;

        this.items = new ArrayList();
    }

    public MediaFeed(int id, int sort, String name, String feedId, String thumbnail) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.feedId = feedId;
        this.thumbnail = thumbnail;

    }

    public MediaFeed(String name, String feedId, String thumbnail) {
        this.name = name;
        this.feedId = feedId;
        this.thumbnail = thumbnail;

        this.items = new ArrayList();
    }

    public MediaFeed(int id, int sort, String name, String thumbnail, String feedId, int type) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.thumbnail = thumbnail;
        this.feedId = feedId;
        this.type = type;

        this.items = new ArrayList();
    }

    public MediaFeed(int id, int sort, String name, String thumbnail, String channelHandle, String feedId, int type) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.thumbnail = thumbnail;
        this.channelHandle = channelHandle;
        this.feedId = feedId;
        this.type = type;
        this.items = new ArrayList();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
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

    public String getChannelHandle() {
        return channelHandle;
    }

    public void setChannelHandle(String channelHandle) {
        this.channelHandle = channelHandle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }
}