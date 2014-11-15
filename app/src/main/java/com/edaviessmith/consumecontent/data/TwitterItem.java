package com.edaviessmith.consumecontent.data;

public class TwitterItem extends MediaItem{

    public long tweetId;

    public TwitterItem() { }


    public TwitterItem(String title, long date, String imageMed, String imageHigh, long tweetId) {
        super(title, date, imageMed, imageHigh);
        this.tweetId = tweetId;
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

}
