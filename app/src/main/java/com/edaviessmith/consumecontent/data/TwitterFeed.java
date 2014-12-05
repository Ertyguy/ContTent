package com.edaviessmith.consumecontent.data;


import android.util.Log;

import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.util.Var;

import java.util.List;

public class TwitterFeed extends MediaFeed {

    private final static String TAG = "TwitterFeed";
    public int nextPageToken = -1;

    public TwitterFeed() {
        setType(Var.TYPE_TWITTER);
        setName("Twitter");
    }


    public TwitterFeed(int id) {
        setType(Var.TYPE_TWITTER);
        setId(id);
    }

    public TwitterFeed(String feedId) {
        super(feedId, "Twitter", Var.TYPE_TWITTER);
    }


    public TwitterFeed(String name, String thumbnail, String channelHandle, String feedId, int type, String displayName){
        super(name, thumbnail, channelHandle, feedId, type);
        setDisplayName(displayName);
    }

    public TwitterFeed(int id, int sort, String name, String thumbnail, String channelHandle, String displayName,String feedId, int type, int notificationId, long lastUpdate) {
        super(id, sort, name, thumbnail, channelHandle, feedId, type, notificationId, lastUpdate);
        setDisplayName(displayName);
    }



    public int getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(int nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<TwitterItem> getItems() {
        return (List<TwitterItem>) super.getItems();
    }

    @Override
    public void setItems(List twitterItems) {
        super.setItems((List<TwitterItem>) twitterItems);
    }


    public boolean addItems(List<TwitterItem> twitterItems) {
        int newer = 0;
        if(getItems() == null || getItems().size() == 0) setItems(twitterItems); //Nothing in list yet
        else if(twitterItems.size() > 0) {

            int itemIndex = 0; //Index of older items (iterate to reduce checks)
            int older;

            for(; newer < twitterItems.size(); newer++) {
                Log.d(TAG, "newer check break " + (twitterItems.get(newer).getDate() <= getItems().get(0).getDate()) + ": " + twitterItems.get(newer).getTitle());
                if(twitterItems.get(newer).getDate() <= getItems().get(0).getDate()) break;     // Number of tweets that are newer
            }

            olderLoop:
            for(older = newer; older < twitterItems.size(); older++) {
                for(; itemIndex < getItems().size(); itemIndex ++) {
                    if(itemIndex == getItems().size() - 1) break olderLoop;
                    if (twitterItems.get(older).getDate() >= getItems().get(itemIndex).getDate()) {
                        break;
                    }
                }
                if(twitterItems.get(older).getTweetId() == getItems().get(itemIndex).getTweetId()) {   //Duplicate Tweet replace with newer info
                    getItems().set(itemIndex, twitterItems.get(older));
                    itemIndex ++; //Not needed but prevent 1 for loop call
                } else {
                    getItems().add(itemIndex, twitterItems.get(older));
                }
            }

            if(newer > 0) {
                Log.d(TAG, "newer " + (newer) + twitterItems.size());
                getItems().addAll(0, twitterItems.subList(0, newer)); // Prepend newer tweets
            }
            if(older < twitterItems.size()) {
                getItems().addAll(twitterItems.subList(older, twitterItems.size() - 1)); // Post pend newer tweets
            }

        } else {
            return false; //twitter list is empty
        }

        return newer <= DB.PAGE_SIZE;
    }

    @Override
    public String toString() {
        return "\tput("+(getSort())+", new TwitterFeed(\""
                + getName() + '"' +
                ", \"" + getThumbnail() + '"' +
                ", \"" + getChannelHandle() + '"' +
                ", \"" + getFeedId() + '\"' +
                ", " + getType() +
                ", \"" + getDisplayName() + '\"' +"));";
    }

}
