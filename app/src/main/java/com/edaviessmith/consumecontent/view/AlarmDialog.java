package com.edaviessmith.consumecontent.view;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.edaviessmith.consumecontent.NotificationsActivity;
import com.edaviessmith.consumecontent.R;
import com.edaviessmith.consumecontent.data.Alarm;
import com.edaviessmith.consumecontent.util.Var;

import java.util.Calendar;
import java.util.TimeZone;


public class AlarmDialog  extends Dialog implements View.OnClickListener{

    private final static String TAG = "AlarmDialog";

    NotificationsActivity act;
    Alarm alarm;
    TextView every_tv, at_tv, cancel_tv, create_tv;
    View every_divider, at_divider, every_v, onlyWifi_v;
    TimePicker timePicker;
    NumberPicker numberPicker;
    SwitchCompat onlyWifi_sw;

    int alarmType;

    public AlarmDialog(NotificationsActivity activity, Alarm alarm) {
        super(activity);
        this.alarm = alarm;
        this.act = activity;
        init();
    }

    private int getNotificationType() {
        if(alarmType == Var.ALARM_EVERY || alarmType == Var.ALARM_AT) return Var.NOTIFICATION_ALARM;
        if(alarmType == Var.ALARM_BEFORE || alarmType == Var.ALARM_AFTER) return Var.NOTIFICATION_SLEEP;
        return Var.NOTIFICATION_ALARM;
    }

    private void init() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_alarm);

        every_tv = (TextView) findViewById(R.id.every_tv);
        at_tv = (TextView) findViewById(R.id.at_tv);
        cancel_tv = (TextView) findViewById(R.id.cancel_tv);
        create_tv = (TextView) findViewById(R.id.create_tv);

        every_divider = findViewById(R.id.every_divider);
        at_divider = findViewById(R.id.at_divider);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        onlyWifi_v = findViewById(R.id.only_wifi_v);
        onlyWifi_sw = (SwitchCompat) findViewById(R.id.only_wifi_sw);
        onlyWifi_sw.setChecked(alarm.isOnlyWifi());
        every_tv.setOnClickListener(this);
        at_tv.setOnClickListener(this);
        cancel_tv.setOnClickListener(this);
        create_tv.setOnClickListener(this);

        every_v = findViewById(R.id.every_v);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(6);
        numberPicker.setMinValue(1);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(alarmType == Var.ALARM_AT) setAtTime(hourOfDay, minute);
                if(alarmType == Var.ALARM_BEFORE) setBeforeTime(hourOfDay, minute);
                if(alarmType == Var.ALARM_AFTER) setAfterTime(hourOfDay, minute);
            }
        });

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
               setEveryTime(newVal);
            }
        });

        setAlarmType(alarm.getType());

        show();
    }

    private void setAtTime(int hourOfDay, int minute) {
        at_tv.setText("At "+(hourOfDay < 12? hourOfDay: hourOfDay - 12)+":"+String.format("%02d",  minute) + " " + (hourOfDay >= 12? "pm": "am"));
    }

    private void setEveryTime(int newVal) {
        every_tv.setText("Every " + newVal + " hour" + (newVal == 1 ? "" : "s"));
    }

    private void setBeforeTime(int hourOfDay, int minute) {
        every_tv.setText("Before "+(hourOfDay < 12? hourOfDay: hourOfDay - 12)+":"+String.format("%02d",  minute) + " " + (hourOfDay >= 12? "pm": "am"));
    }
    private void setAfterTime(int hourOfDay, int minute) {
        at_tv.setText("After "+(hourOfDay < 12? hourOfDay: hourOfDay - 12)+":"+String.format("%02d",  minute) + " " + (hourOfDay >= 12? "pm": "am"));
    }


    private void setAlarmType(int alarmType) {
        this.alarmType = alarmType;

        if(getNotificationType() == Var.NOTIFICATION_ALARM) {
            every_divider.setVisibility(alarmType == Var.ALARM_EVERY ? View.VISIBLE : View.INVISIBLE);
            at_divider.setVisibility(alarmType == Var.ALARM_AT ? View.VISIBLE : View.INVISIBLE);

            every_v.setVisibility(alarmType == Var.ALARM_EVERY ? View.VISIBLE : View.GONE);
            timePicker.setVisibility(alarmType == Var.ALARM_AT ? View.VISIBLE : View.INVISIBLE);

            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            c.setTimeInMillis(alarm.getTime());
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            numberPicker.setValue((int) (alarm.getTime() / Var.HOUR_MILLI));

            setAtTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            setEveryTime((int) (alarm.getTime() / Var.HOUR_MILLI));
        }
        if(getNotificationType() == Var.NOTIFICATION_SLEEP) {
            every_divider.setVisibility(alarmType == Var.ALARM_BEFORE ? View.VISIBLE : View.INVISIBLE);
            at_divider.setVisibility(alarmType == Var.ALARM_AFTER ? View.VISIBLE : View.INVISIBLE);

            every_v.setVisibility(View.GONE);
            timePicker.setVisibility(View.VISIBLE);
            onlyWifi_v.setVisibility(View.GONE);

            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            c.setTimeInMillis(alarm.getTime());
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            numberPicker.setValue((int) (alarm.getTime() / Var.HOUR_MILLI));

            setBeforeTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            setAfterTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        }
    }

    @Override
    public void onClick(View v) {
        if(getNotificationType() == Var.NOTIFICATION_ALARM) {
            if (every_tv == v) {
                setAlarmType(Var.ALARM_EVERY);
            }
            if (at_tv == v) {
                setAlarmType(Var.ALARM_AT);
            }
        }
        if(getNotificationType() == Var.NOTIFICATION_SLEEP) {
            if (every_tv == v) {
                setAlarmType(Var.ALARM_BEFORE);
            }
            if (at_tv == v) {
                setAlarmType(Var.ALARM_AFTER);
            }
        }
        if(cancel_tv == v) {
            dismiss();
        }

        if(create_tv == v) {

            alarm.setType(alarmType);
            alarm.setEnabled(true);

            if(getNotificationType() == Var.NOTIFICATION_ALARM) alarm.setOnlyWifi(onlyWifi_sw.isChecked());
            else alarm.setOnlyWifi(false);

            if(alarmType == Var.ALARM_EVERY) {
                alarm.setTime(numberPicker.getValue() * Var.HOUR_MILLI);
            }

            if(alarmType == Var.ALARM_AT || alarmType == Var.ALARM_BEFORE || alarmType == Var.ALARM_AFTER) {
                alarm.setTime((timePicker.getCurrentHour() * Var.HOUR_MILLI )+ (timePicker.getCurrentMinute() * Var.MINUTE_MILLI));
            }

            act.addAlarm(alarm);
            dismiss();
        }
    }


}
