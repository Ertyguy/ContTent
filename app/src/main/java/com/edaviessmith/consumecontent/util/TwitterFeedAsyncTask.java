package com.edaviessmith.consumecontent.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.edaviessmith.consumecontent.data.TwitterFeed;
import com.edaviessmith.consumecontent.data.TwitterItem;
import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.service.ActionDispatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;


public class TwitterFeedAsyncTask extends AsyncTask<Integer, Void, String> {

    private final static String TAG = "TwitterFeedAsyncTask";
    final List<TwitterItem> twitterItems = new ArrayList<TwitterItem>();

    private Context context;
    private TwitterFeed twitterFeed;
    private ActionDispatch actionDispatch;
    private TwitterUtil twitter;
    private int userId;

    private boolean cancel;

    public TwitterFeedAsyncTask(Context context, TwitterFeed twitterFeed, TwitterUtil twitter, int userId, ActionDispatch actionDispatch) {
        this.context = context;
        this.twitterFeed = twitterFeed;
        this.actionDispatch = actionDispatch;
        this.twitter = twitter;
        this.userId = userId;
    }
    
    @Override
    protected String doInBackground(Integer... params) {
        try {

            //TODO make this a beautiful toast like Chrome (use Handler)
            if (!Var.isNetworkAvailable(context)) {
                //handler.sendMessage(handler.obtainMessage(1, Var.FEED_OFFLINE, youtubeFeed.getId(), null));
                actionDispatch.updatedMediaFeed(twitterFeed.getId(), Var.FEED_OFFLINE);
                cancel = true;
                return null;
            }


            Log.d(TAG, "feed_id: "+twitterFeed.getFeedId() + "- "+twitterFeed.getNextPageToken());
            long userId = Long.parseLong(twitterFeed.getFeedId());

            //ResponseList<twitter4j.Status> response = twitter.getAppTwitter().getUserTimeline(userId);

            ResponseList<twitter4j.Status> response = (DB.isValid(params[0]) && twitterFeed.getNextPageToken() > 0) ?
                    (twitter.getAppTwitter().getUserTimeline(userId, new Paging(twitterFeed.getNextPageToken(), 20))):
                    (twitter.getAppTwitter().getUserTimeline(userId)) ;

            if(response == null || response.size() == 0)
                twitterFeed.setNextPageToken(0);
            else {
                twitterFeed.setNextPageToken((twitterFeed.getNextPageToken()+1));

                //int index = 20 * page;
                for (twitter4j.Status status : response) {
                    if (status != null) {
                        TwitterItem item = new TwitterItem();



                        item.setTweetId(status.getId());

                        if(status.getRetweetedStatus() != null) {
                            item.setTitle(status.getRetweetedStatus().getUser().getName() + " @" + status.getRetweetedStatus().getUser().getScreenName());
                            item.setDescription(status.getRetweetedStatus().getText());

                            item.setTweetThumbnail(status.getRetweetedStatus().getUser().getBiggerProfileImageURL());
                            item.setType(Var.TYPE_RETWEET);
                        } else {
                            item.setDescription(status.getText());
                            item.setTweetThumbnail(status.getUser().getBiggerProfileImageURL());
                            item.setType(Var.TYPE_TWEET);
                        }

                        if(status.getMediaEntities().length > 0) {
                            item.setImageHigh(status.getMediaEntities()[0].getMediaURL());
                            //tweet.getMediaEntities()[0].getType()
                        } else if(status.getExtendedMediaEntities().length > 0) {
                            item.setImageHigh(status.getExtendedMediaEntities()[0].getMediaURL());
                        }

                        item.setDate(status.getCreatedAt().getTime());

                        twitterItems.add(item);
                    }
                }
            }

        } catch (Throwable t) {
            Log.e(TAG, "getFeed failed");
            t.printStackTrace();
            actionDispatch.updatedMediaFeed(twitterFeed.getId(), Var.FEED_WARNING);
            cancel = true;
            return null;
        }

        Collections.sort(twitterItems, new Comparator<TwitterItem>() {
            public int compare(TwitterItem m1, TwitterItem m2) {
                return (m1.getDate() > m2.getDate() ? -1 : (m1.getDate() == m2.getDate() ? 0 : 1));
            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG,"adding twitter items "+twitterItems.size());
        if(!cancel) {
            if (twitterFeed.addItems(twitterItems)) {
                actionDispatch.updateMediaFeedDatabase(userId, twitterFeed.getId());
            }

            //int feed = twitterFeed.getNextPageToken() ? Var.FEED_END: Var.FEED_WAITING;

            Log.d(TAG, "twitter feed updated "+twitterFeed.getNextPageToken());
            actionDispatch.updatedMediaFeed(twitterFeed.getId(), Var.FEED_WAITING);
        }
    }


}

