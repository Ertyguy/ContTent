package com.edaviessmith.contTent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.contTent.data.Notification;
import com.edaviessmith.contTent.util.Var;

import java.util.ArrayList;
import java.util.List;

public class NotificationORM {
    static final String TAG = "NotificationORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_NOTIFICATION +" (" +
            DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DB.COL_NAME   	        + " TEXT, " +
            DB.COL_TYPE             + " INTEGER " + ");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_NOTIFICATION;


    public static List<Notification> getNotifications(Context context) {
        return getNotificationsByType(context, -1);
    }

    public static List<Notification> getNotificationsByType(Context context, int notificationType) {
        DB databaseHelper = new DB(context);
        List<Notification> notifications = new ArrayList<Notification>();
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {
            Cursor cursor = database.query(false, DB.TABLE_NOTIFICATION, null, (DB.isValid(notificationType)? (DB.COL_TYPE+" = "+notificationType): null), null, null, null, DB.ORDER_BY_ID, null);

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Notification notification = cursorToNotification(cursor);

                    notification.setAlarms(AlarmORM.getAlarms(database, notification.getId()));

                    notifications.add(notification);
                    cursor.moveToNext();
                }

            }
            Log.i("NotificationORM", "Notifications loaded successfully :"+notifications.size());
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }

        return notifications;
    }

    //Barebone ScheduleNotification for Alarm
    public static Notification getScheduleNotifications(Context context) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Notification notification = null;

        database.beginTransaction();
        try {
            Cursor cursor = database.query(false, DB.TABLE_NOTIFICATION, null, DB.COL_TYPE+" = "+ Var.NOTIFICATION_SCHEDULE, null, null, null, DB.ORDER_BY_ID, null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) notification = cursorToNotification(cursor);
                //notification.setAlarms(AlarmORM.getAlarms(database, notification.getId()));
                //cursor.moveToNext();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }

        return notification;
    }

    public static void saveNotification(Context context, Notification notification) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {


            if(DB.isValid(notification.getId())) {
                database.update(DB.TABLE_NOTIFICATION, notificationToContentValues(notification, false), DB.COL_ID + " = " + notification.getId(), null);
            } else {
                notification.setId((int) database.insert(DB.TABLE_NOTIFICATION, null, notificationToContentValues(notification, false)));
            }

            AlarmORM.saveAlarms(database, notification.getAlarms(), notification.getId());

            database.setTransactionSuccessful();

            Log.d(TAG, "Notification saved with id:" + notification.getId());
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }

    }

    public static void saveNotifications(SQLiteDatabase database, List<Notification> notifications) {

        try {
            for(Notification notification: notifications) {

                if (DB.isValid(notification.getId())) {
                    database.update(DB.TABLE_NOTIFICATION, notificationToContentValues(notification, false), DB.COL_ID + " = " + notification.getId(), null);
                } else {
                    notification.setId((int) database.insert(DB.TABLE_NOTIFICATION, null, notificationToContentValues(notification, false)));
                }

                AlarmORM.saveAlarms(database, notification.getAlarms(), notification.getId());

                Log.d(TAG, "Notification saved with id:" + notification.getId());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static ContentValues notificationToContentValues(Notification notification, boolean includeId) {
        ContentValues values = new ContentValues();
        if(includeId) values.put(DB.COL_ID, notification.getId());
        values.put(DB.COL_NAME, notification.getName());
        values.put(DB.COL_TYPE, notification.getType());
        return values;
    }

    private static Notification cursorToNotification(Cursor cursor) {
        return new Notification(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                         cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                        cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)));
    }

}