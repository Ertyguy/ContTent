package com.edaviessmith.consumecontent.util;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.edaviessmith.consumecontent.ContentActivity;
import com.edaviessmith.consumecontent.R;
import com.edaviessmith.consumecontent.data.Notification;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.data.YoutubeFeed;
import com.edaviessmith.consumecontent.db.MediaFeedORM;
import com.edaviessmith.consumecontent.db.NotificationORM;
import com.edaviessmith.consumecontent.db.UserORM;

import java.util.ArrayList;
import java.util.List;

public class MediaFeedActivityService extends IntentService {

    private static String TAG = "MemberActivityService";

    private static int threadCounter;
    private List<Integer> updatedMediaFeedIds;


    public MediaFeedActivityService(){
        super("MediaFeedActivityService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        int notificationId = intent.getIntExtra(Var.NOTIFY_NOTIFICATION_ID, -1);

        List<Notification> notifications = NotificationORM.getNotifications(this);
        Notification scheduleNotification = notifications.get(0);
        notifications.remove(scheduleNotification);
        Var.setNextAlarm(this, notifications, scheduleNotification); //Set next Alarm

        List<YoutubeFeed> youtubeFeeds = MediaFeedORM.getMediaFeedsByNotificationId(this, notificationId);
        updatedMediaFeedIds = new ArrayList<Integer>();
        threadCounter = 0;

        for(YoutubeFeed youtubeFeed : youtubeFeeds) {

            if(youtubeFeed.getItems().size() == 0) {    //No videos for the user
                threadCounter ++;
                new YoutubeFeedAsyncTask(this, youtubeFeed, handler).execute("");
            }

        }

        checkMediaFeedUpdated();
    }


    //Update latestVideo if checked member exists in memberActivities or add then start notification
    private void checkMediaFeedUpdated() {

        //Only create notifications after all thread have completed
        if(threadCounter > 0) return;

        threadCounter = 0;

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
                    .setContentIntent(PendingIntent.getActivity(this, users.get(0).getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT));

             builder.setSmallIcon(R.drawable.ic_launcher);

            //Next 3 members as sub-icons
            for(int i = 1; i < users.size() && i<= 3; i++) {
                User user = users.get(i);

                Intent in = new Intent(this, ContentActivity.class);
                //in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //in.putExtra(Constants.PREF_MEMBER, user.id);

                //TODO reason to save icon as
                builder.addAction(R.drawable.ic_launcher, user.getName(), PendingIntent.getActivity(this, user.getId(), in, PendingIntent.FLAG_UPDATE_CURRENT));
            }

            android.app.Notification notification = builder.build();

            // Hide the notification after it's selected
            notification.flags |= android.app.Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);

        }

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 0) {
                threadCounter --;

                if(msg.arg1 == 1) {     //Feed had new content
                    updatedMediaFeedIds.add(msg.arg2);
                }
            }

        }
    };

}
