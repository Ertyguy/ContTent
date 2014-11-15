package com.edaviessmith.consumecontent.data;

public class YoutubeItem extends MediaItem{

    private String videoId;
    private int length;
    private String views;
    private int status; // Used internally (watch later, new)

    public YoutubeItem() {
        super();
    }

    public YoutubeItem(String title, long date, String imageMed, String imageHigh, String videoId, int length, String views, int status) {
        super(title, date, imageMed, imageHigh);
        this.videoId = videoId;
        this.length = length;
        this.views = views;
        this.status = status;
    }


    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }
}
