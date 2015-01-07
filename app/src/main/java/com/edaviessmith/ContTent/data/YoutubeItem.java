package com.edaviessmith.contTent.data;

import com.edaviessmith.contTent.util.Var;

public class YoutubeItem extends MediaItem{

    private String videoId;

    private String duration;
    private int views;
    private int likes;
    private int dislikes;

    public YoutubeItem() {
        super();
        setType(Var.TYPE_UPLOAD);
    }

    public YoutubeItem(String title, String description, long date, String imageMed, String imageHigh, String videoId, String duration, int views, int status) {
        super(Var.TYPE_UPLOAD, title, description, date, imageMed, imageHigh, status);
        this.videoId = videoId;
        this.duration = duration;
        this.views = views;
    }

    public YoutubeItem(int type, String title, String description, long date, String imageMed, String imageHigh,  int status, String videoId, String duration, int views, int likes, int dislikes) {
        super(type, title, description, date, imageMed, imageHigh, status);
        this.videoId = videoId;
        this.duration = duration;
        this.views = views;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getLength() {
        return duration;
    }

    public void setLength(String duration) {
        this.duration = duration;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
