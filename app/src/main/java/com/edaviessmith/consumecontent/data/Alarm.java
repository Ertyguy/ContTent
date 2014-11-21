package com.edaviessmith.consumecontent.data;


import com.edaviessmith.consumecontent.util.Var;

import java.util.ArrayList;
import java.util.List;

public class Alarm {

    private int id = -1;
    private boolean enabled;
    private int type;
    private long time;
    private boolean onlyWifi;
    private List<Integer> days;

    public Alarm(int notificationType) {
        this.enabled = true;
        this.type = (notificationType == Var.NOTIFICATION_ALARM? Var.ALARM_EVERY: Var.ALARM_BEFORE);
        this.time = 10800000;       // 3 hours
        this.onlyWifi = false;

        this.days = new ArrayList<Integer>();
        for(int i=0; i<7; i++) this.days.add(1);
    }

    public Alarm(boolean enabled, int type, long time, boolean onlyWifi, List<Integer> days) {
        this.enabled = enabled;
        this.type = type;
        this.time = time;
        this.onlyWifi = onlyWifi;
        this.days = days;
    }

    public Alarm(int id, boolean enabled, int type, long time, boolean onlyWifi, List<Integer> days) {
        this.id = id;
        this.enabled = enabled;
        this.type = type;
        this.time = time;
        this.onlyWifi = onlyWifi;
        this.days = days;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isOnlyWifi() {
        return onlyWifi;
    }

    public void setOnlyWifi(boolean onlyWifi) {
        this.onlyWifi = onlyWifi;
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }
}
