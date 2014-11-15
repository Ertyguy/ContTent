package com.edaviessmith.consumecontent.data;


import com.edaviessmith.consumecontent.util.Var;

import java.util.List;

public class YoutubeFeed extends MediaFeed {

    //private List<YoutubeItem> items;

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
        //items = new ArrayList<YoutubeItem>();

    }

    public YoutubeFeed(MediaFeed mediaFeed) {
        super(mediaFeed.getId(), mediaFeed.getSort(), mediaFeed.getName(), mediaFeed.getThumbnail(), mediaFeed.getChannelHandle(), mediaFeed.getFeedId(), mediaFeed.getType());
        //items = new ArrayList<YoutubeItem>();
        for(int i=0; i<10; i++)
            getItems().add(new YoutubeItem());
    }

    public List<YoutubeItem> getItems() {
        return (List<YoutubeItem>) super.getItems();
    }

    public void setItems(List youtubeItems) {
        super.setItems(youtubeItems);
    }

    public boolean isVisible() {return isVisible; }

    public void setVisible(boolean isVisible) {this.isVisible = isVisible; }




    public String toString() {
        return "YoutubeFeed ("+getId()+", "+getName()+", "+getFeedId()+", "+getThumbnail()+")";
    }

}
