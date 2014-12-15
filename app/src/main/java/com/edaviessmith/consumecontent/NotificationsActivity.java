package com.edaviessmith.consumecontent;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.edaviessmith.consumecontent.data.Alarm;
import com.edaviessmith.consumecontent.data.Notification;
import com.edaviessmith.consumecontent.db.NotificationORM;
import com.edaviessmith.consumecontent.service.ActionActivity;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.view.Fab;

import java.util.List;


public class NotificationsActivity extends ActionActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "NotificationsActivity";

    Toolbar toolbar;
    //List<Notification> notifications;
    //NotificationList notificationList;
    Notification editNotification;
    ListView notification_lv, alarm_lv;
    NotificationAdapter notificationAdapter;
    AlarmAdapter alarmAdapter;

    Fab add_fab, addAlarm_fab, save_fab;
    View alarm_v, allNotifications_v, mobileNotifications_v, vibrations_v, playSound_v, schedule_v, footer;
    SwitchCompat allNotifications_sw, mobileNotifications_sw, vibrations_sw, playSound_sw;
    boolean isAllNotificationsEnabled, isMobileNotificationsEnabled, isVibrationsEnabled, isPlaySoundEnabled;
    EditText notificationName_tv;
    TextView scheduleTimeExplanation_tv;

    public static final int NOTIFICATIONS_LIST = 0;
    public static final int ALARMS_LIST = 1;

    private int listType = NOTIFICATIONS_LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //notificationList = new NotificationList(this);
        editNotification = new Notification();

        isAllNotificationsEnabled = Var.getBoolPreference(this, Var.PREF_ALL_NOTIFICATIONS);
        isMobileNotificationsEnabled = Var.getBoolPreference(this, Var.PREF_MOBILE_NOTIFICATIONS);
        isVibrationsEnabled = Var.getBoolPreference(this, Var.PREF_VIBRATIONS);
        isPlaySoundEnabled =  Var.getBoolPreference(this, Var.PREF_PLAY_SOUND);

        View header = getLayoutInflater().inflate(R.layout.header_notifications, null, false);
        View alarmHeader = getLayoutInflater().inflate(R.layout.header_alarms, null, false);
        footer = getLayoutInflater().inflate(R.layout.item_list_divider, null, false);
        footer.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 48)));

        alarm_v = findViewById(R.id.alarm_v);
        save_fab = (Fab) findViewById(R.id.save_fab);

        alarm_lv = (ListView) findViewById(R.id.alarm_lv);
        alarm_lv.addHeaderView(alarmHeader, null, false);
        alarm_lv.addFooterView(footer, null, false);
        alarm_lv.setFooterDividersEnabled(false);
        addAlarm_fab = (Fab) alarmHeader.findViewById(R.id.add_alarm_fab);

        notification_lv = (ListView) findViewById(R.id.notification_lv);
        notification_lv.addHeaderView(header, null, false);

        add_fab = (Fab) header.findViewById(R.id.add_fab);
        allNotifications_v = header.findViewById(R.id.all_notifications_v);
        allNotifications_sw = (SwitchCompat) header.findViewById(R.id.all_notifications_sw);
        allNotifications_sw.setChecked(isAllNotificationsEnabled);
        mobileNotifications_v = header.findViewById(R.id.mobile_notifications_v);
        mobileNotifications_sw = (SwitchCompat) header.findViewById(R.id.mobile_notifications_sw);
        mobileNotifications_sw.setChecked(isMobileNotificationsEnabled);
        vibrations_v = header.findViewById(R.id.vibrations_v);
        vibrations_sw = (SwitchCompat) header.findViewById(R.id.vibrations_sw);
        vibrations_sw.setChecked(isVibrationsEnabled);
        playSound_v = header.findViewById(R.id.play_sound_v);
        playSound_sw = (SwitchCompat) header.findViewById(R.id.play_sound_sw);
        playSound_sw.setChecked(isPlaySoundEnabled);
        schedule_v = header.findViewById(R.id.schedule_v);

        notificationName_tv = (EditText) alarmHeader.findViewById(R.id.notification_name_tv);
        scheduleTimeExplanation_tv = (TextView) alarmHeader.findViewById(R.id.schedule_time_explanation_tv);

        save_fab.setOnClickListener(this);
        addAlarm_fab.setOnClickListener(this);

        add_fab.setOnClickListener(this);
        allNotifications_v.setOnClickListener(this);
        mobileNotifications_v.setOnClickListener(this);
        vibrations_v.setOnClickListener(this);
        playSound_v.setOnClickListener(this);
        schedule_v.setOnClickListener(this);

        notificationAdapter = new NotificationAdapter(this);
        notification_lv.setAdapter(notificationAdapter);
        notification_lv.setOnItemClickListener(this);

        alarmAdapter = new AlarmAdapter(this);
        alarm_lv.setAdapter(alarmAdapter);

        toggleList(listType);

    }


    public void toggleList(int listType) {
        this.listType = listType;

        alarm_v.setVisibility((listType == ALARMS_LIST) ? View.VISIBLE: View.GONE);

        if(listType == NOTIFICATIONS_LIST) {
            notificationAdapter.notifyDataSetChanged();
        }
        if(listType == ALARMS_LIST) {
            notificationName_tv.setText(editNotification.getName());
            notificationName_tv.setEnabled(editNotification.getType() == Var.NOTIFICATION_ALARM);
            scheduleTimeExplanation_tv.setVisibility(editNotification.getType() == Var.NOTIFICATION_SCHEDULE? View.VISIBLE: View.GONE);
            alarmAdapter.notifyDataSetChanged();

        }
    }

    public void addAlarm(Alarm alarm) {
        if(!editNotification.getAlarms().contains(alarm)) editNotification.getAlarms().add(alarm);
        alarmAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        editNotification = new Notification(binder.getNotificationList().getNotifications().get(position - 1));
        toggleList(ALARMS_LIST);
    }

    public class NotificationAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public NotificationAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return binder!= null ? binder.getNotificationList().getNotifications().size() : 0;
        }

        @Override
        public Notification getItem(int position) {
            return binder.getNotificationList().getNotifications().get(position);
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
            holder.nextAlarm_tv.setText(Var.getNextNotificationAlarm(notification, binder.getNotificationList().getScheduleNotification()));

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

    public class AlarmAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        Context context;

        public AlarmAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return editNotification.getAlarms().size();
        }

        @Override
        public Alarm getItem(int position) {
            return editNotification.getAlarms().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            footer.setVisibility(getCount() == 0? View.GONE: View.VISIBLE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_alarm, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }

            final ViewHolder holder = (ViewHolder) convertView.getTag();
            final Alarm alarm = getItem(position);


            holder.time_tv.setText(Var.getAlarmText(alarm));
            holder.time_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlarmDialog(NotificationsActivity.this, alarm);
                }
            });
            holder.time_tv.setTextSize(alarm.getType() == Var.ALARM_BETWEEN? 18: 26);

            holder.onlyWifi_iv.setVisibility(alarm.isOnlyWifi()? View.VISIBLE: View.GONE);

            if(alarm.getDays().size() == 7) { //Make sure the week is there
                setDayTb(alarm, holder.sun_tb);
                setDayTb(alarm, holder.mon_tb);
                setDayTb(alarm, holder.tue_tb);
                setDayTb(alarm, holder.wed_tb);
                setDayTb(alarm, holder.thu_tb);
                setDayTb(alarm, holder.fri_tb);
                setDayTb(alarm, holder.sat_tb);
            }

            View.OnClickListener dayOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int day = getDayIndex((ToggleButton) v);
                    List<Integer> days = getItem(position).getDays();

                    if(editNotification.getType() == Var.NOTIFICATION_SCHEDULE) {  //Toggle off other alarms for this day
                        if(days.get(day) == 0) {
                            for(Alarm a : editNotification.getAlarms()) {
                                a.getDays().set(day, 0);
                            }
                            days.set(day, 1);
                        }
                        notifyDataSetChanged();
                    } else {
                        days.set(day, (days.get(day) == 1 ? 0 : 1));   //Toggle opposite
                        holder.nextAlarm_tv.setText(alarm.isEnabled() ? Var.getNextAlarmTimeText(alarm, binder.getNotificationList().getScheduleNotification()) : "disabled");
                    }

                }
            };

            holder.sun_tb.setOnClickListener(dayOnClickListener);
            holder.mon_tb.setOnClickListener(dayOnClickListener);
            holder.tue_tb.setOnClickListener(dayOnClickListener);
            holder.wed_tb.setOnClickListener(dayOnClickListener);
            holder.thu_tb.setOnClickListener(dayOnClickListener);
            holder.fri_tb.setOnClickListener(dayOnClickListener);
            holder.sat_tb.setOnClickListener(dayOnClickListener);

            holder.enabled_sw.setChecked(alarm.isEnabled());
            holder.enabled_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    holder.nextAlarm_tv.setText(isChecked? Var.getNextAlarmTimeText(alarm, binder.getNotificationList().getScheduleNotification()): "disabled");
                }
            });

            holder.nextAlarm_tv.setText(alarm.isEnabled()? Var.getNextAlarmTimeText(alarm, binder.getNotificationList().getScheduleNotification()): "disabled");


            holder.delete_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editNotification.getAlarms().remove(alarm);
                    AlarmAdapter.this.notifyDataSetChanged();
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView time_tv;
            ImageView delete_iv;
            ToggleButton sun_tb, mon_tb, tue_tb, wed_tb, thu_tb, fri_tb, sat_tb;
            SwitchCompat enabled_sw;
            TextView nextAlarm_tv;
            ImageView onlyWifi_iv;

            public ViewHolder(View view) {
                time_tv = (TextView) view.findViewById(R.id.time_tv);
                delete_iv = (ImageView) view.findViewById(R.id.delete_iv);
                sun_tb = (ToggleButton) view.findViewById(R.id.sun_tb);
                mon_tb = (ToggleButton) view.findViewById(R.id.mon_tb);
                tue_tb = (ToggleButton) view.findViewById(R.id.tue_tb);
                wed_tb = (ToggleButton) view.findViewById(R.id.wed_tb);
                thu_tb = (ToggleButton) view.findViewById(R.id.thu_tb);
                fri_tb = (ToggleButton) view.findViewById(R.id.fri_tb);
                sat_tb = (ToggleButton) view.findViewById(R.id.sat_tb);
                enabled_sw = (SwitchCompat) view.findViewById(R.id.enabled_sw);
                nextAlarm_tv = (TextView) view.findViewById(R.id.next_alarm_tv);
                onlyWifi_iv = (ImageView) view.findViewById(R.id.only_wifi_iv);
            }
        }

        private void setDayTb(Alarm alarm, ToggleButton tb) {
            tb.setChecked(alarm.getDays().get(getDayIndex(tb)) == 1);
        }

        private int getDayIndex(ToggleButton tb) {
            if(tb.getId() == R.id.sun_tb) return 0;
            if(tb.getId() == R.id.mon_tb) return 1;
            if(tb.getId() == R.id.tue_tb) return 2;
            if(tb.getId() == R.id.wed_tb) return 3;
            if(tb.getId() == R.id.thu_tb) return 4;
            if(tb.getId() == R.id.fri_tb) return 5;
            if(tb.getId() == R.id.sat_tb) return 6;
            return 0;
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
    protected void onPause() {
        super.onPause();
        Var.setNextAlarm(this, binder.getNotificationList());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home) {
            if(listType != NOTIFICATIONS_LIST) {
                toggleList(NOTIFICATIONS_LIST);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(listType != NOTIFICATIONS_LIST) {
            toggleList(NOTIFICATIONS_LIST);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {


        if(save_fab == v) {
            editNotification.setName(notificationName_tv.getText().toString().trim());
            //TODO: Make sure data is valid before trying to save
            NotificationORM.saveNotification(this, editNotification);


            if(editNotification.getType() == Var.NOTIFICATION_ALARM){
                if(binder.getNotificationList().getNotification(editNotification.getId()) != null)
                    binder.getNotificationList().getNotifications().set(binder.getNotificationList().getNotifications().indexOf(binder.getNotificationList().getNotification(editNotification.getId())), editNotification);
                else binder.getNotificationList().getNotifications().add(editNotification);
            }
            if(editNotification.getType() == Var.NOTIFICATION_SCHEDULE) binder.getNotificationList().setScheduleNotification(new Notification(editNotification));

            alarmAdapter.notifyDataSetChanged();

            toggleList(NOTIFICATIONS_LIST);
        }

        if(addAlarm_fab == v) {

            new AlarmDialog(this, new Alarm(editNotification.getType()));
        }

        if(add_fab == v) {
            editNotification = new Notification();
            toggleList(ALARMS_LIST);
        }
        if(allNotifications_v == v) {
            isAllNotificationsEnabled = !isAllNotificationsEnabled;
            Var.setBoolPreference(this, Var.PREF_ALL_NOTIFICATIONS, isAllNotificationsEnabled);
            allNotifications_sw.setChecked(isAllNotificationsEnabled);
        }
        if(mobileNotifications_v == v) {
            isMobileNotificationsEnabled = !isMobileNotificationsEnabled;
            Var.setBoolPreference(this, Var.PREF_MOBILE_NOTIFICATIONS, isMobileNotificationsEnabled);
            mobileNotifications_sw.setChecked(isMobileNotificationsEnabled);
        }
        if(vibrations_v == v) {
            isVibrationsEnabled = !isVibrationsEnabled;
            Var.setBoolPreference(this, Var.PREF_VIBRATIONS, isVibrationsEnabled);
            vibrations_sw.setChecked(isVibrationsEnabled);
        }
        if(playSound_v == v) {
            isPlaySoundEnabled = !isPlaySoundEnabled;
            Var.setBoolPreference(this, Var.PREF_PLAY_SOUND, isPlaySoundEnabled);
            playSound_sw.setChecked(isPlaySoundEnabled);
        }

        if(schedule_v == v) {
            editNotification = new Notification(binder.getNotificationList().getScheduleNotification());
            toggleList(ALARMS_LIST);
        }

    }
}
