package com.edaviessmith.consumecontent;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.Notification;
import com.edaviessmith.consumecontent.db.NotificationORM;
import com.edaviessmith.consumecontent.util.Var;

import java.util.List;


public class NotificationsActivity extends ActionBarActivity implements View.OnClickListener {
    String TAG = "NotificationsActivity";

    Toolbar toolbar;
    List<Notification> notifications;
    ListView notification_lv;
    NotificationAdapter notificationAdapter;

    View allNotifications_v, mobileNotificatiosn_v, vibrations_v;
    SwitchCompat allNotifications_sw, mobileNotificatiosn_sw, vibrations_sw;
    boolean isAllNotificationsEnabled, isMobileNotificationsEnabled, isVibrationsEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        notifications = NotificationORM.getNotifications(this);

        isAllNotificationsEnabled = Var.getBoolPreference(this, Var.PREF_ALL_NOTIFICATIONS);
        isMobileNotificationsEnabled = Var.getBoolPreference(this, Var.PREF_MOBILE_NOTIFICATIONS);
        isVibrationsEnabled = Var.getBoolPreference(this, Var.PREF_VIBRATIONS);


        View header = getLayoutInflater().inflate(R.layout.header_notifications, null, false);

        notification_lv = (ListView) findViewById(R.id.notification_lv);
        notification_lv.addHeaderView(header, null, false);

        allNotifications_v = header.findViewById(R.id.all_notifications_v);
        allNotifications_sw = (SwitchCompat) header.findViewById(R.id.all_notifications_sw);
        allNotifications_sw.setChecked(isAllNotificationsEnabled);
        mobileNotificatiosn_v = header.findViewById(R.id.mobile_notifications_v);
        mobileNotificatiosn_sw = (SwitchCompat) header.findViewById(R.id.mobile_notifications_sw);
        mobileNotificatiosn_sw.setChecked(isMobileNotificationsEnabled);
        vibrations_v = header.findViewById(R.id.vibrations_v);
        vibrations_sw = (SwitchCompat) header.findViewById(R.id.vibrations_sw);
        vibrations_sw.setChecked(isVibrationsEnabled);

        allNotifications_v.setOnClickListener(this);
        mobileNotificatiosn_v.setOnClickListener(this);
        vibrations_v.setOnClickListener(this);

        notificationAdapter = new NotificationAdapter(this);
        notification_lv.setAdapter(notificationAdapter);
    }


    public class NotificationAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        Context context;

        public NotificationAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return notifications.size();
        }

        @Override
        public Notification getItem(int position) {
            return notifications.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_youtube_feed, parent, false);
                holder = new ViewHolder();
                holder.image_iv = (ImageView) convertView.findViewById(R.id.thumbnail_iv);
                holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
                //holder.visible_sw = (SwitchCompat) convertView.findViewById(R.id.visible_sw);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Notification notification = getItem(position);

            holder.image_iv.setImageResource(R.drawable.ic_youtube_icon);
            //if(notification.getThumbnail() != null) imageLoader.DisplayImage(notification.getThumbnail(), holder.image_iv);
            holder.name_tv.setText(notification.getName());
            //holder.visible_sw.setChecked(notification.isVisible());

            return convertView;

        }

        class ViewHolder {
            ImageView image_iv;
            TextView name_tv;
            //SwitchCompat visible_sw;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        if(allNotifications_v == v) {
            isAllNotificationsEnabled = !isAllNotificationsEnabled;
            Var.setBoolPreference(this, Var.PREF_ALL_NOTIFICATIONS, isAllNotificationsEnabled);
            allNotifications_sw.setChecked(isAllNotificationsEnabled);
        }
        if(mobileNotificatiosn_v == v) {
            isMobileNotificationsEnabled = !isMobileNotificationsEnabled;
            Var.setBoolPreference(this, Var.PREF_MOBILE_NOTIFICATIONS, isMobileNotificationsEnabled);
            mobileNotificatiosn_sw.setChecked(isMobileNotificationsEnabled);
        }
        if(vibrations_v == v) {
            isVibrationsEnabled = !isVibrationsEnabled;
            Var.setBoolPreference(this, Var.PREF_VIBRATIONS, isVibrationsEnabled);
            vibrations_sw.setChecked(isVibrationsEnabled);
        }
    }
}
