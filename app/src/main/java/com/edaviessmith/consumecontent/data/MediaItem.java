package com.edaviessmith.consumecontent.data;


public class MediaItem {

    private int id;

    private String title;
    private long date;

    private String imageMed;
    private String imageHigh;

    private int status; // Used internally (watch later, new)

    public MediaItem() { }

    public MediaItem(String title, long date, String imageMed, String imageHigh) {
        this.title = title;
        this.date = date;
        this.imageMed = imageMed;
        this.imageHigh = imageHigh;
    }

    public MediaItem(String title, long date, String imageMed, String imageHigh, int status) {
        this.title = title;
        this.date = date;
        this.imageMed = imageMed;
        this.imageHigh = imageHigh;
        this.status = status;
    }

    public MediaItem(int id, String title, long date, String imageMed, String imageHigh) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.imageMed = imageMed;
        this.imageHigh = imageHigh;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getImageMed() {
        return imageMed;
    }

    public void setImageMed(String imageMed) {
        this.imageMed = imageMed;
    }

    public String getImageHigh() {
        return imageHigh;
    }

    public void setImageHigh(String imageHigh) {
        this.imageHigh = imageHigh;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String toString() {
        return id +", "+ title +", "+ date +", "+ imageMed +", "+ imageHigh;
    }
}
