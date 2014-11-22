package com.edaviessmith.consumecontent;

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
    TextView title_tv, every_tv, at_tv, cancel_tv, create_tv;
    View every_divider, at_divider, every_v, onlyWifi_v;
    TimePicker timePicker;
    NumberPicker numberPicker;
    SwitchCompat onlyWifi_sw;

    private static int ALARM_FROM = 10;
    private static int ALARM_TO = 11;

    int alarmType;
    int tabType = ALARM_FROM; //Only used for ACTIVE_TIME

    private long time;
    private long timeBetween;


    public AlarmDialog(NotificationsActivity activity, Alarm alarm) {
        super(activity);
        this.alarm = alarm;
        this.act = activity;
        time = alarm.getTime();
        timeBetween = alarm.getTimeBetween();

        init();
    }

    private void init() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_alarm);

        title_tv = (TextView) findViewById(R.id.title_tv);
        every_tv = (TextView) findViewById(R.id.every_tv);
        at_tv = (TextView) findViewById(R.id.at_tv);
        cancel_tv = (TextView) findViewById(R.id.cancel_tv);
        create_tv = (TextView) findViewById(R.id.create_tv);

        every_divider = findViewById(R.id.every_divider);
        at_divider = findViewById(R.id.at_divider);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        onlyWifi_v = findViewById(R.id.only_wifi_v);
        onlyWifi_sw = (SwitchCompat) findViewById(R.id.only_wifi_sw);

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
                if(alarmType == Var.ALARM_BETWEEN) {
                    if(tabType == ALARM_FROM) setTimeFrom(hourOfDay, minute);
                    if(tabType == ALARM_TO) setTimeTo(hourOfDay, minute);
                }
            }
        });

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
               setEveryTime(newVal);
            }
        });

        if(alarm.getType() == Var.ALARM_BETWEEN) {
            onlyWifi_v.setVisibility(View.GONE);
            every_v.setVisibility(View.GONE);
            timePicker.setVisibility(View.VISIBLE);

            title_tv.setText("Alarms Between");
        } else {

            onlyWifi_sw.setChecked(alarm.isOnlyWifi());

        }

        setAlarmType(alarm.getType());

        show();
    }

    private void setAtTime(int hourOfDay, int minute) {
        time = (hourOfDay * Var.HOUR_MILLI)+ (minute * Var.MINUTE_MILLI);
        at_tv.setText("At " + Var.getTimeText(hourOfDay, minute));
    }

    private void setEveryTime(int newVal) {
        every_tv.setText("Every " + newVal + " hour" + (newVal == 1 ? "" : "s"));
    }

    private void setTimeFrom(int hourOfDay, int minute) {
        time = (hourOfDay * Var.HOUR_MILLI)+ (minute * Var.MINUTE_MILLI);
        every_tv.setText("From " + Var.getTimeText(hourOfDay, minute));
    }
    private void setTimeTo(int hourOfDay, int minute) {
        timeBetween = (hourOfDay * Var.HOUR_MILLI)+ (minute * Var.MINUTE_MILLI);
        at_tv.setText("To "+ Var.getTimeText(hourOfDay, minute));
    }


    private void setTabType(int tabType) {
        this.tabType = tabType;
        setAlarmType(alarmType);
    }

    private void setAlarmType(int alarmType) {
        this.alarmType = alarmType;

        if(alarmType == Var.ALARM_BETWEEN) {
            every_divider.setVisibility(tabType == ALARM_FROM ? View.VISIBLE : View.INVISIBLE);
            at_divider.setVisibility(tabType == ALARM_TO ? View.VISIBLE : View.INVISIBLE);

            Calendar calFrom = Calendar.getInstance(TimeZone.getTimeZone("UTC"));  // From Time
            calFrom.setTimeInMillis(time);
            Calendar calTo = Calendar.getInstance(TimeZone.getTimeZone("UTC"));  // To Time
            calTo.setTimeInMillis(timeBetween);

            if(tabType == ALARM_FROM) {
                timePicker.setCurrentHour(calFrom.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(calFrom.get(Calendar.MINUTE));
            }
            setTimeFrom(calFrom.get(Calendar.HOUR_OF_DAY), calFrom.get(Calendar.MINUTE));

            if(tabType == ALARM_TO) {
                timePicker.setCurrentHour(calTo.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(calTo.get(Calendar.MINUTE));
            }
            setTimeTo(calTo.get(Calendar.HOUR_OF_DAY), calTo.get(Calendar.MINUTE));

        } else {
            every_divider.setVisibility(alarmType == Var.ALARM_EVERY ? View.VISIBLE : View.INVISIBLE);
            at_divider.setVisibility(alarmType == Var.ALARM_AT ? View.VISIBLE : View.INVISIBLE);

            every_v.setVisibility(alarmType == Var.ALARM_EVERY ? View.VISIBLE : View.GONE);
            timePicker.setVisibility(alarmType == Var.ALARM_AT ? View.VISIBLE : View.INVISIBLE);

            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            c.setTimeInMillis(time);
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            numberPicker.setValue((int) (time / Var.HOUR_MILLI));

            setAtTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            setEveryTime((int) (time / Var.HOUR_MILLI));
        }
    }

    @Override
    public void onClick(View v) {

        if(alarmType == Var.ALARM_BETWEEN) {
            if (every_tv == v) setTabType(ALARM_FROM);
            if (at_tv == v) setTabType(ALARM_TO);
        } else {
            if (every_tv == v) setAlarmType(Var.ALARM_EVERY);
            if (at_tv == v) setAlarmType(Var.ALARM_AT);
        }

        if(cancel_tv == v) {
            dismiss();
        }

        if(create_tv == v) {

            alarm.setType(alarmType);
            alarm.setEnabled(true);

            if(alarmType != Var.ALARM_BETWEEN) alarm.setOnlyWifi(onlyWifi_sw.isChecked());
            else alarm.setOnlyWifi(false);

            if(alarmType == Var.ALARM_EVERY) alarm.setTime(numberPicker.getValue() * Var.HOUR_MILLI);
            else if(alarmType == Var.ALARM_AT || alarmType == Var.ALARM_BETWEEN) alarm.setTime(time);

            if(alarmType == Var.ALARM_BETWEEN) alarm.setTimeBetween(timeBetween);

            act.addAlarm(alarm);
            dismiss();
        }
    }


}
