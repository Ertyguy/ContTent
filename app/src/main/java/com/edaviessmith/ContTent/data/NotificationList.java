package com.edaviessmith.contTent.data;


import android.content.Context;

import com.edaviessmith.contTent.db.NotificationORM;

import java.util.List;

public class NotificationList {

    List<Notification> notifications;
    Notification scheduleNotification;


    public NotificationList(Context context) {
        notifications = NotificationORM.getNotifications(context);
        scheduleNotification = notifications.get(0);
        notifications.remove(scheduleNotification);
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public Notification getNotification(int notificationId) {
        for(Notification notification: getNotifications()) {
            if(notification.getId() == notificationId) return notification;
        }
        return null;
    }

    public Notification getScheduleNotification() {
        return scheduleNotification;
    }

    public void setScheduleNotification(Notification scheduleNotification) {
        this.scheduleNotification = scheduleNotification;
    }
}
