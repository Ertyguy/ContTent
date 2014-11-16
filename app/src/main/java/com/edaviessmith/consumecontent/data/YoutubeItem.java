package com.edaviessmith.consumecontent.data;

import com.edaviessmith.consumecontent.util.Var;

public class YoutubeItem extends MediaItem{

    private String videoId;
    private int type;
    private String description;
    private String duration;
    private int views;
    private int likes;
    private int dislikes;
    private int status; // Used internally (watch later, new)

    public YoutubeItem() {
        super();
        this.type = Var.TYPE_UPLOAD;
    }

    public YoutubeItem(String title, long date, String imageMed, String imageHigh, String videoId, String duration, int views, int status) {
        super(title, date, imageMed, imageHigh);
        this.videoId = videoId;
        this.duration = duration;
        this.views = views;
        this.status = status;
        this.type = Var.TYPE_UPLOAD;
    }

    public YoutubeItem(String title, long date, String imageMed, String imageHigh, String videoId, int type, String description, String duration, int views, int likes, int dislikes, int status) {
        super(title, date, imageMed, imageHigh);
        this.videoId = videoId;
        this.type = type;
        this.description = description;
        this.duration = duration;
        this.views = views;
        this.likes = likes;
        this.dislikes = dislikes;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
