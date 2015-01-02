package com.edaviessmith.consumecontent.service;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

import com.edaviessmith.consumecontent.ContentActivity;
import com.edaviessmith.consumecontent.R;
import com.edaviessmith.consumecontent.data.NotificationList;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.data.YoutubeFeed;
import com.edaviessmith.consumecontent.db.MediaFeedORM;
import com.edaviessmith.consumecontent.db.UserORM;
import com.edaviessmith.consumecontent.util.ImageLoader;
import com.edaviessmith.consumecontent.util.Var;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaFeedActivityService extends IntentService {

    private static String TAG = "MemberActivityService";

    private int threadCounter;
    private List<Integer> updatedMediaFeedIds;
    private ActionDispatch actionDispatch;
    List<YoutubeFeed> youtubeFeeds;
    ImageLoader imageLoader;
    final static ExecutorService tpe = Executors.newSingleThreadExecutor();


    public MediaFeedActivityService(){
        super("MediaFeedActivityService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        actionDispatch = new ActionDispatch() {
            @Override
            public void updateMediaFeedDatabase(int userId, int mediaFeedId) {
                super.updateMediaFeedDatabase(userId, mediaFeedId);
                updatedMediaFeedIds.add(mediaFeedId);

                for(final YoutubeFeed youtubeFeed: youtubeFeeds) {
                    if(youtubeFeed.getId() == mediaFeedId) {
                        threadCounter ++;
                        tpe.submit(new Runnable() {
                            @Override
                            public void run() {
                                MediaFeedORM.saveYoutubeFeedItems(MediaFeedActivityService.this, youtubeFeed);
                                threadCounter--;
                                checkMediaFeedUpdated();
                            }
                        });
                    }
                }

            }

            @Override
            public void updatedMediaFeed(int mediaFeedId, int feedState) {
                super.updatedMediaFeed(mediaFeedId, feedState);

                threadCounter --;
                checkMediaFeedUpdated();
            }
        };

        int notificationId = intent.getIntExtra(Var.NOTIFY_NOTIFICATION_ID, -1);

        imageLoader = new ImageLoader(this);
        Var.setNextAlarm(this, new NotificationList(this)); //Set next Alarm

        youtubeFeeds = MediaFeedORM.getYoutubeFeedsByNotificationId(this, notificationId); //run in sync
        updatedMediaFeedIds = new ArrayList<Integer>();

        threadCounter = 0;
        for(YoutubeFeed youtubeFeed : youtubeFeeds) {
            threadCounter ++;
            new YoutubeFeedAsyncTask(this, youtubeFeed, youtubeFeed.getUserId(), actionDispatch).execute("");

        }

    }


    private void checkMediaFeedUpdated() {
        if(threadCounter > 0) return;
        threadCounter = 0;

        if(updatedMediaFeedIds.size() == 0) return;


        List<User> users = UserORM.getUsersByMediaFeeds(this, updatedMediaFeedIds);

        if(users.size() > 0) {
            String title = "Consume Content";
            String text = "New upload from "+ users.get(0).getName();

            //TODO create intent from notification also this notification is kind of garbage right now
            Intent intent = new Intent(this, ContentActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.putExtra(Constants.PREF_MEMBER, updatedMembers.get(0).getId());


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(PendingIntent.getActivity(this, users.get(0).getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSmallIcon(R.drawable.ic_notification);

            if(Var.isEmpty(users.get(0).getThumbnail())) {
                Bitmap icon = imageLoader.getBitmap(users.get(0).getThumbnail());
                if(icon != null) builder.setLargeIcon(icon);
            }

            //Next 3 members as sub-icons
            for(int i = 1; i < users.size() && i<= 3; i++) {
                User user = users.get(i);

                Intent in = new Intent(this, ContentActivity.class);
                //in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //in.putExtra(Constants.PREF_MEMBER, user.id);

                //TODO reason to save icon as
                NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_youtube_white, user.getName(), PendingIntent.getActivity(this, user.getId(), in, PendingIntent.FLAG_UPDATE_CURRENT));

                builder.addAction(action);

            }

            android.app.Notification notification = builder.build();

            // Hide the notification after it's selected
            notification.flags |= android.app.Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);

        }

    }

}
