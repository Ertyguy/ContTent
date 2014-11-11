package com.edaviessmith.consumecontent.data;


import com.edaviessmith.consumecontent.util.Var;

import java.util.List;

public class YoutubeFeed extends MediaFeed {

    private List<YoutubeItem> youtubeItems;

    private boolean isVisible;

    public YoutubeFeed() {
        setType(Var.TYPE_YOUTUBE_ACTIVTY);
        setName("Activity");
    }

    public YoutubeFeed(String feedId) {
        super(feedId, "Youtube", Var.TYPE_YOUTUBE_ACTIVTY);
        setVisible(true);
    }

    public YoutubeFeed(String feedId, String name) {
        super(feedId, name, Var.TYPE_YOUTUBE_ACTIVTY);
        setVisible(true);
    }

    public YoutubeFeed(int id, int sort, String name, String thumbnail, String feedId, int type, boolean isVisible) {
        super(id, sort, name, thumbnail, feedId, type);
        this.isVisible = isVisible;
    }

    public List<YoutubeItem> getYoutubeItems() {
        return youtubeItems;
    }

    public void setYoutubeItems(List<YoutubeItem> youtubeItems) {
        this.youtubeItems = youtubeItems;
    }

    public boolean isVisible() {return isVisible; }

    public void setVisible(boolean isVisible) {this.isVisible = isVisible; }

}
