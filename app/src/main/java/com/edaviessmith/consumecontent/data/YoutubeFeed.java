package com.edaviessmith.consumecontent.data;


import android.util.Log;

import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.util.Var;

import java.util.List;

public class YoutubeFeed extends MediaFeed {

    final static String TAG = "YoutubeFeed";

    public YoutubeFeed() {
        setType(Var.TYPE_YOUTUBE_ACTIVTY);
        setName("Activity");
    }

    public YoutubeFeed(String feedId) {
        super(feedId, "Youtube", Var.TYPE_YOUTUBE_ACTIVTY);
    }

    public YoutubeFeed(int id, int sort, String name, String thumbnail, String channelHandle, String feedId, int type, int notificationId, long lastUpdate) {
        super(id, sort, name, thumbnail, channelHandle, feedId, type, notificationId, lastUpdate);
    }

    public List<YoutubeItem> getItems() {
        return (List<YoutubeItem>) super.getItems();
    }

    @Override
    public void setItems(List youtubeItems) {
        super.setItems((List<YoutubeItem>) youtubeItems);
    }

    /**
     * Merge new items  ordered by getDate()
     * @param youtubeItems list of new youtubeItems
     * @return true if database should be updated
     */
    public boolean addItems(List<YoutubeItem> youtubeItems) {

        int newer = 0;
        if(getItems() == null || getItems().size() == 0) setItems(youtubeItems); //Nothing in list yet
        else if(youtubeItems.size() > 0) {

            int itemIndex = 0; //Index of older items (iterate to reduce checks)
            int older;

            for(; newer < youtubeItems.size(); newer++) {
                Log.d(TAG, "newer check break "+(youtubeItems.get(newer).getDate() <= getItems().get(0).getDate()) + ": "+youtubeItems.get(newer).getTitle());
                if(youtubeItems.get(newer).getDate() <= getItems().get(0).getDate()) break;     // Number of youtubeItems that are newer
            }

            olderLoop:
            for(older = newer; older < youtubeItems.size(); older++) {
                for(; itemIndex < getItems().size(); itemIndex ++) {
                    if(itemIndex == getItems().size() - 1) break olderLoop;
                    if (youtubeItems.get(older).getDate() >= getItems().get(itemIndex).getDate()) {
                        //Log.d(TAG, "oolder check break "+older);
                        break;
                    }
                }
                if(youtubeItems.get(older).getVideoId().equals(getItems().get(itemIndex).getVideoId())) {   //Duplicate Video replace with newer info
                    //Log.d(TAG, "set "+itemIndex+" n: "+youtubeItems.get(older).getTitle());
                    getItems().set(itemIndex, youtubeItems.get(older));
                    itemIndex ++; //Not needed but prevent 1 for loop call
                } else {
                    //Log.d(TAG, "add "+itemIndex+" n: "+youtubeItems.get(older).getTitle());
                    getItems().add(itemIndex, youtubeItems.get(older));
                }
            }

            if(newer > 0) {
                Log.d(TAG, "newer " + (newer) + youtubeItems.size());
                getItems().addAll(0, youtubeItems.subList(0, newer)); // Prepend newer youtubeItems
            }
            if(older < youtubeItems.size()) {
                getItems().addAll(youtubeItems.subList(older, youtubeItems.size() - 1)); // Post pend newer youtubeItems
                //Log.d(TAG, "older  "+older+" - " + (youtubeItems.size() - 1));
            }

        } else {
            return false; //youtubeItem list is empty
        }
        return newer <= DB.PAGE_SIZE;
    }

    public String toString() {
        return "YoutubeFeed ("+getId()+", "+getName()+", "+getFeedId()+", "+getThumbnail()+")";
    }

}
