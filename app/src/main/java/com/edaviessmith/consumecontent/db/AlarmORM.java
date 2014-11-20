package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.Alarm;

import java.util.ArrayList;
import java.util.List;

public class AlarmORM {
    static final String TAG = "AlarmORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_ALARM +" (" +
            DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DB.COL_NOTIFICATION     + " INTEGER, " +
            DB.COL_ENABLED          + " INTEGER, " +
            DB.COL_TYPE             + " INTEGER, " +
            DB.COL_TIME             + " INTEGER, " +
            DB.COL_ONLY_WIFI        + " INTEGER, " +
            DB.COL_DAYS             + " TEXT, "     +
            "FOREIGN KEY("+DB.COL_NOTIFICATION +") REFERENCES "+DB.TABLE_NOTIFICATION+"("+DB.COL_ID+")" +");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_ALARM;



    public static List<Alarm> getAlarms(SQLiteDatabase database, int notificationId) {
        List<Alarm>  alarms= new ArrayList<Alarm>();

        Cursor cursor = database.query(false, DB.TABLE_ALARM, null, DB.COL_NOTIFICATION + " = " + notificationId, null, null, null, DB.ORDER_BY_SORT, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                alarms.add(cursorToAlarm(cursor));
                cursor.moveToNext();
            }
            Log.i(TAG, "Alarms loaded successfully.");
        }

        return alarms;

    }




    public static void saveAlarms(SQLiteDatabase database, List<Alarm> alarms, int notificationId) {
        for(int i=0; i< alarms.size(); i++) {
            saveAlarm(database, alarms.get(i), notificationId, i);
        }
    }

    public static void saveAlarm(SQLiteDatabase database, Alarm alarm, int notificationId, int sort) {

        if(DB.isValid(alarm.getId())) {
            database.update(DB.TABLE_ALARM, alarmToContentValues(alarm, notificationId, false), DB.COL_ID + " = " + alarm.getId(), null);
        } else {
            database.insert(DB.TABLE_ALARM, null, alarmToContentValues(alarm, notificationId, false));
        }

    }
    

    private static ContentValues alarmToContentValues(Alarm alarm, int notificationId, boolean includeId) {
        ContentValues values = new ContentValues();
        if(includeId) values.put(DB.COL_ID, alarm.getId());
        values.put(DB.COL_NOTIFICATION, notificationId);
        values.put(DB.COL_ENABLED, alarm.isEnabled());
        values.put(DB.COL_TYPE, alarm.getType());
        values.put(DB.COL_TIME, alarm.getTime());
        values.put(DB.COL_ONLY_WIFI, alarm.isOnlyWifi());
        values.put(DB.COL_DAYS, DB.integerListToString(alarm.getDays()));
        return values;
    }

    private static Alarm cursorToAlarm(Cursor cursor) {
        return new Alarm(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                        (cursor.getInt(cursor.getColumnIndex(DB.COL_ENABLED)) == 1),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)),
                         cursor.getLong(cursor.getColumnIndex(DB.COL_TIME)),
                        (cursor.getInt(cursor.getColumnIndex(DB.COL_ONLY_WIFI)) == 1),
                        DB.stringToIntegerList(cursor.getString(cursor.getColumnIndex(DB.COL_DAYS))) );
    }

}