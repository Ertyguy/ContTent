package com.edaviessmith.consumecontent.data;

import java.util.ArrayList;
import java.util.List;

public class MediaFeed {

    private int id = -1;
    private int sort;
    private String name;
    private String thumbnail;
    private String channelHandle;
    private String displayName;
    private String feedId;
    private int type;

    private int userId;
    private int notificationId = -1;
    private long lastUpdate;

    private List items;


    public MediaFeed() { }


    public MediaFeed(String feedId, String name, int type) {
        this.feedId = feedId;
        this.name = name;
        this.type = type;

        this.items = new ArrayList();
    }


/*    public MediaFeed(int id, int sort, String name, String thumbnail, String channelHandle, String feedId, int type) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.thumbnail = thumbnail;
        this.channelHandle = channelHandle;
        this.feedId = feedId;
        this.type = type;
        this.items = new ArrayList();

    }*/

    public MediaFeed(String name, String thumbnail, String channelHandle, String feedId, int type) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.channelHandle = channelHandle;
        this.feedId = feedId;
        this.type = type;
    }

    public MediaFeed(int id, int sort, String name, String thumbnail, String channelHandle, String feedId, int type, int notificationId, long lastUpdate) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.thumbnail = thumbnail;
        this.channelHandle = channelHandle;
        this.feedId = feedId;
        this.type = type;
        this.notificationId = notificationId;
        this.lastUpdate = lastUpdate;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    @Override
    public String toString() {
        return "add(new MediaFeed(" + id +
                ", " + sort +
                ", \"" + name + '"' +
                ", \"" + thumbnail + '"' +
                ", \"" + channelHandle + '"' +
                ", \"" + feedId + '\"' +
                ", " + type +
                ", " + notificationId +
                "));";
    }
}