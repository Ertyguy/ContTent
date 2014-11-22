package com.edaviessmith.consumecontent;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.data.Notification;
import com.edaviessmith.consumecontent.data.NotificationList;
import com.edaviessmith.consumecontent.util.Var;


public class NotificationDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final static String TAG = "NotificationDialog";

    AddActivity act;
    NotificationList notificationList;
    MediaFeed mediaFeed;

    ListView notification_lv;
    NotificationAdapter notificationAdapter;
    View cancel_tv;



    public NotificationDialog(AddActivity activity, NotificationList notificationList, MediaFeed mediaFeed) {
        super(activity);
        this.act = activity;
        this.notificationList = notificationList;
        this.mediaFeed = mediaFeed;

        init();
    }

    private void init() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_notifications);

        notification_lv = (ListView) findViewById(R.id.notification_lv);
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
        mediaFeed.setNotificationId(notificationAdapter.getItem(position).getId());
        act.feedAdapter.notifyDataSetChanged();
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
