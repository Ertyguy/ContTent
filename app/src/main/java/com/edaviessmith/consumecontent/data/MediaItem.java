package com.edaviessmith.consumecontent.data;


public class MediaItem {

    private int id;

    private int type;
    private String title;
    private String description;
    private long date;

    //Used as single image or Array of strings {"","",""}
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

    public MediaItem(int type, String title, String description, long date, String imageMed, String imageHigh, int status) {
        this.type = type;
        this.title = title;
        this.description = description;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
