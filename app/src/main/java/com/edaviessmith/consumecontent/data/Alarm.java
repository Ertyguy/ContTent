package com.edaviessmith.consumecontent.data;


import java.util.List;

public class Alarm {

    private int id = -1;
    private boolean enabled;
    private int type;
    private long time;
    private boolean onlyWifi;
    private List<Integer> days;

    public Alarm(int id, boolean enabled, int type, long time, boolean wifi, List<Integer> days) {
        this.id = id;
        this.enabled = enabled;
        this.type = type;
        this.time = time;
        this.onlyWifi = wifi;
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
