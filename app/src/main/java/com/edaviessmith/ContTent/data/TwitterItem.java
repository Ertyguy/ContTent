package com.edaviessmith.contTent.data;

public class TwitterItem extends MediaItem{

    public long tweetId;
    public String tweetThumbnail;

    public TwitterItem() { }


    public TwitterItem(String title, long date, String imageMed, String imageHigh, long tweetId, String tweetThumbnail) {
        super(title, date, imageMed, imageHigh);
        this.tweetId = tweetId;
        this.tweetThumbnail = tweetThumbnail;
    }



    public TwitterItem(int type, String title, String description, long date, String imageMed, String imageHigh, int status, long tweetId, String tweetThumbnail) {
        super(type, title, description, date, imageMed, imageHigh, status);
        this.tweetId = tweetId;
        this.tweetThumbnail = tweetThumbnail;
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public String getTweetThumbnail() {
        return tweetThumbnail;
    }

    public void setTweetThumbnail(String tweetThumbnail) {
        this.tweetThumbnail = tweetThumbnail;
    }
}
