package com.edaviessmith.consumecontent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.data.Notification;
import com.edaviessmith.consumecontent.data.NotificationList;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.util.Var;

import java.util.List;


public class NotificationDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final static String TAG = "NotificationDialog";

    AddActivity addActivity;
    GroupFragment groupFragment;
    NotificationList notificationList;
    List<MediaFeed> mediaFeeds;
    List<User> selectedUsers;

    ListView notification_lv;
    NotificationAdapter notificationAdapter;
    View cancel_tv;

    int dialogType;

    private final static int DIALOG_MEDIA_FEED = 0;
    private final static int DIALOG_SELECTED_USERS = 1;
    private final static int DIALOG_SELECTED_FEEDS = 1;

    public NotificationDialog(AddActivity activity, NotificationList notificationList, List<MediaFeed> mediaFeeds) {
        super(activity);
        dialogType = DIALOG_MEDIA_FEED;
        this.addActivity = activity;
        this.notificationList = notificationList;
        this.mediaFeeds = mediaFeeds;
        init();
    }


    public NotificationDialog(GroupFragment fragment, NotificationList notificationList, List<User> selectedUsers) {
        super(fragment.act);
        dialogType = DIALOG_SELECTED_USERS;
        this.groupFragment = fragment;
        this.notificationList = notificationList;
        this.selectedUsers = selectedUsers;
        init();
    }

    private void init() {

        Activity act = dialogType == DIALOG_MEDIA_FEED? addActivity: groupFragment.act;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_notifications);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * (Var.isDeviceLandscape(act) ? 0.65 : 0.95));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        getWindow().setAttributes(params);


        View disableNotification = act.getLayoutInflater().inflate(R.layout.item_notification, null, false);
        ((ImageView) disableNotification.findViewById(R.id.icon_iv)).setImageResource(R.drawable.ic_alarm_off_grey600_36dp);
        ((TextView) disableNotification.findViewById(R.id.name_tv)).setText("No Notifications");

        notification_lv = (ListView) findViewById(R.id.notification_lv);
        notification_lv.addHeaderView(disableNotification, new Notification(-1, "No Notifications", Var.NOTIFICATION_DISABLE), true);

        notification_lv.setOnItemClickListener(this);
        notificationAdapter = new NotificationAdapter(act);
        notification_lv.setAdapter(notificationAdapter);

        cancel_tv = findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(this);

        show();
    }


    @Override
    public void onClick(View v) {

        if(cancel_tv == v) {
            dismiss();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(dialogType == DIALOG_MEDIA_FEED) {
            for(MediaFeed mediaFeed: mediaFeeds)
            mediaFeed.setNotificationId(notificationAdapter.getItem(position - 1).getId());
            addActivity.feedAdapter.notifyDataSetChanged();
        }
        if(dialogType == DIALOG_SELECTED_USERS) {
            int notificationId = position > 0? notificationAdapter.getItem(position - 1).getId(): -1;
            for(User user: selectedUsers) {
                List<MediaFeed> mediaFeeds = user.getCastMediaFeed();
                if(mediaFeeds.size() > 0) mediaFeeds.get(0).setNotificationId(notificationId);
                if(!DB.isValid(notificationId)) {       //Disable all alarms
                    for(MediaFeed mediaFeed: mediaFeeds) {
                        mediaFeed.setNotificationId(notificationId);
                    }
                }
            }
            groupFragment.editGroupAdapter.notifyDataSetChanged();
            groupFragment.clearSelection(-1);

        }
        dismiss();
    }

    public class NotificationAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public NotificationAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return notificationList.getNotifications().size();
        }

        @Override
        public Notification getItem(int position) {
            return notificationList.getNotifications().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_notification, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Notification notification = getItem(position);
            holder.name_tv.setText(notification.getName());

            holder.nextAlarm_tv.setText(Var.getNextNotificationAlarm(notification, notificationList.getScheduleNotification()));

            return convertView;

        }

        class ViewHolder {
            ImageView icon_iv;
            TextView name_tv;
            TextView nextAlarm_tv;

            public ViewHolder(View view) {
                icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
                name_tv = (TextView) view.findViewById(R.id.name_tv);
                nextAlarm_tv = (TextView) view.findViewById(R.id.next_alarm_tv);
            }
        }


    }


}
