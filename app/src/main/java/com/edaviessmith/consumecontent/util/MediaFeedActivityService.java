package com.edaviessmith.consumecontent.util;


import android.app.IntentService;
import android.content.Intent;

import com.edaviessmith.consumecontent.data.YoutubeItem;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

public class MediaFeedActivityService extends IntentService {

    private static String TAG = "MemberActivityService";

    private static List<Member> memberList;
    private static List<YoutubeItem> youtubeItemList;
    private static int threadCounter;

    public MediaFeedActivityService(){
        super("MediaFeedActivityService");
    }


    @Override
    protected void onHandleIntent(Intent arg0) {

        Var.setNextAlarm(this); //Set next Alarm

       /* memberList = MemberORM.getMembersbyStatus(this, Constants.FAVORITE);
        youtubeItemList = new ArrayList<YoutubeItem>();
        threadCounter = 0;

        for(Member member : memberList) {
            YoutubeItem youtubeItem = YoutubeItemORM.getMemberLatestYoutubeItem(this, member.getId());
            if(youtubeItem != null) {
                youtubeItemList.add(youtubeItem);
            } else {
                //No videos for the user
                threadCounter ++;
                new YoutubePlaylist(this, member.getId(), null).execute(member.getUploadsId());
            }

        }
*/
        checkMemberActivity();
    }


    //Update latestVideo if checked member exists in memberActivities or add then start notification
    private void checkMemberActivity() {

        //Only create notifications after all thread have completed
        if(threadCounter > 0) {
            return;
        } else {
            threadCounter = 0;
            createNotification();
        }

    }




    private void createNotification() {
        List<Member> updatedMembers = new ArrayList<Member>();

       /* for(int i=0; i< memberList.size(); i++) {
            Member member = memberList.get(i);
            if(youtubeItemList.get(i).getMemberId() != member.getId()) {
                youtubeItemList.add(i, YoutubeItemORM.getMemberLatestYoutubeItem(this, member.getId()));
            }
            YoutubeItem youtubeItem = youtubeItemList.get(i);

            try {
                boolean newYoutubeItem = new YoutubePlaylist(this, member.getId(),youtubeItem.getVideoId()).execute(member.getUploadsId()).get();

                if(newYoutubeItem) {
                    updatedMembers.add(member);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        if(updatedMembers.size() > 0) {
            String title = "Mindcrack News";
            String text = "New upload from "+updatedMembers.get(0).getName();


            Intent intent = new Intent(this, Members.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.PREF_MEMBER, updatedMembers.get(0).getId());


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(PendingIntent.getActivity(this, updatedMembers.get(0).getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT));

            if(Util.notificationsIconEnabled()) {
                builder.setSmallIcon(updatedMembers.get(0).getIcon());
            } else {
                builder.setSmallIcon(R.drawable.ic_launcher);
            }
            //Next 3 members as sub-icons
            for(int i = 1; i < updatedMembers.size() && i<= 3; i++) {
                Member mem = updatedMembers.get(i);

                Intent in = new Intent(this, Members.class);
                in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra(Constants.PREF_MEMBER, mem.id);

                builder.addAction(mem.getIcon(), mem.getName(), PendingIntent.getActivity(this, mem.getId(), in, PendingIntent.FLAG_UPDATE_CURRENT));
            }

            Notification notification = builder.build();

            // Hide the notification after it's selected
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);

        }*/
    }


}
