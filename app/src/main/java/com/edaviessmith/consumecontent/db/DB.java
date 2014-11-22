package com.edaviessmith.consumecontent.db;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.edaviessmith.consumecontent.data.Alarm;
import com.edaviessmith.consumecontent.data.Notification;
import com.edaviessmith.consumecontent.util.Var;

import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {

    static final String DB_NAME = "consumecontent.db";
    static final int DB_VERSION = 1;

    private static Context context;

    @SuppressWarnings("static-access")
    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GroupORM.SQL_CREATE_TABLE);
        db.execSQL(UserORM.SQL_CREATE_TABLE);
        db.execSQL(GroupUserORM.SQL_CREATE_TABLE);
        db.execSQL(MediaFeedORM.SQL_CREATE_TABLE);
        db.execSQL(YoutubeItemORM.SQL_CREATE_TABLE);
        db.execSQL(TwitterItemORM.SQL_CREATE_TABLE);
        db.execSQL(NotificationORM.SQL_CREATE_TABLE);
        db.execSQL(AlarmORM.SQL_CREATE_TABLE);

        addDefaultNotifications(db);
    }

    private void addDefaultNotifications(SQLiteDatabase db) {


        final List<Integer> weekdays = new ArrayList<Integer>(){{
            add(0); add(1); add(1); add(1); add(1); add(1); add(0);
        }};
        final List<Integer> weekends = new ArrayList<Integer>(){{
            add(1); add(0); add(0); add(0); add(0); add(0); add(1);
        }};
        final List<Integer> everyday = new ArrayList<Integer>(){{
            add(1); add(1); add(1); add(1); add(1); add(1); add(1);
        }};

        final List<Alarm> scheduleAlarms = new ArrayList<Alarm>() {{
            add(new Alarm(true, Var.ALARM_BETWEEN, 28800000, 84900000, false, weekdays));
            add(new Alarm(true, Var.ALARM_BETWEEN, 25200000, 86400000, false, weekends));
        }};

        final List<Alarm> hourlyAlarms = new ArrayList<Alarm>() {{
            add(new Alarm(true, Var.ALARM_EVERY, 3600000, true, everyday));
            add(new Alarm(true, Var.ALARM_EVERY, 10800000, false, everyday));
        }};

        final List<Alarm> fewHoursAlarms = new ArrayList<Alarm>() {{
            add(new Alarm(true, Var.ALARM_EVERY, 7200000, true, everyday));
            add(new Alarm(true, Var.ALARM_EVERY, 14400000, false, everyday));
        }};

        final List<Alarm> busyWeekAlarms = new ArrayList<Alarm>() {{
            add(new Alarm(true, Var.ALARM_EVERY, 3600000, true, weekends));
            add(new Alarm(true, Var.ALARM_EVERY, 7200000, false, weekends));
            add(new Alarm(true, Var.ALARM_AT, 44400000, false, weekdays));
            add(new Alarm(true, Var.ALARM_AT, 71100000, false, weekdays));
        }};

        List<Notification> notifications = new ArrayList<Notification>(){{
            add(new Notification("Schedule Time", Var.NOTIFICATION_SCHEDULE, scheduleAlarms));
            add(new Notification("Hourly on Wifi", Var.NOTIFICATION_ALARM, hourlyAlarms));
            add(new Notification("Every few hours", Var.NOTIFICATION_ALARM, fewHoursAlarms));
            add(new Notification("Busy week, free weekends", Var.NOTIFICATION_ALARM, busyWeekAlarms));
        }};

        NotificationORM.saveNotifications(db, notifications);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(GroupORM.SQL_DROP_TABLE);
        db.execSQL(UserORM.SQL_DROP_TABLE);
        db.execSQL(GroupUserORM.SQL_DROP_TABLE);
        db.execSQL(MediaFeedORM.SQL_DROP_TABLE);
        db.execSQL(YoutubeItemORM.SQL_DROP_TABLE);
        db.execSQL(TwitterItemORM.SQL_DROP_TABLE);

        if(oldVersion <= 1) {
            //addNewMember(db, 3);
            //addNewMember(db, 23);
        }
        if(oldVersion <= 2) {
            //addNewMember(db, 7);
        }



    }



    ///// Database Tables //////
    static final String TABLE_GROUP = "groups";
    static final String TABLE_USER = "users";
    static final String TABLE_GROUP_USER = "groups_users";  //Many to Many
    static final String TABLE_MEDIA_FEED = "media_feeds";
    static final String TABLE_YOUTUBE_ITEM = "youtube_items";
    static final String TABLE_TWITTER_ITEM = "twitter_items";
    static final String TABLE_NOTIFICATION = "notifications";
    static final String TABLE_ALARM = "alarms";



    public static String ORDER_BY_SORT = DB.COL_SORT + " ASC";
    public static String ORDER_BY_DATE = DB.COL_DATE + " DESC";
    public static String ORDER_BY_ID   = DB.COL_ID   + " ASC";

    //// Database Columns /////
    static final String COL_ID = "id";
    static final String COL_SORT = "sort";
    static final String COL_NAME = "name";
    static final String COL_THUMBNAIL = "thumbnail";
    static final String COL_VISIBILITY = "visibility";

    static final String COL_TYPE = "type";
    static final String COL_FEED_ID = "feed_id";
    static final String COL_CHANNEL_HANDLE = "channel_handle";  //ChannelId or TwitterHandle
    static final String COL_LAST_UPDATE= "last_update";

    static final String COL_ENABLED = "enabled";
    static final String COL_TIME = "time";
    static final String COL_TIME_BETWEEN = "time_between";
    static final String COL_DAYS = "days";
    static final String COL_ONLY_WIFI = "only_wifi";

    //Foreign id keys
    static final String COL_GROUP = "group_id";
    static final String COL_USER = "user_id";
    static final String COL_MEDIA_FEED = "media_feed_id";
    static final String COL_NOTIFICATION = "notification_id";

    static final String COL_TITLE = "title";
    static final String COL_DATE =  "date";
    static final String COL_IMAGE_MED = "image_med";
    static final String COL_IMAGE_HIGH = "image_high";

    static final String COL_VIDEO_ID = "video_id";
    static final String COL_DESCRIPTION = "description";
    static final String COL_DURATION = "duration";
    static final String COL_VIEWS = "views";
    static final String COL_LIKES = "likes";
    static final String COL_DISLIKES = "dislikes";
    static final String COL_STATUS = "status";
    static final String COL_TWEET_ID = "tweet_id";

    public static final int PAGE_SIZE = 20; //Number of items to save



    //Util methods

    public static boolean isValid(int i) { //Check if integer has been set
        return i != -1;
    } //TODO should this be in the DB class?


    public static String strSeparator = ",";
    public static String integerListToString(List<Integer> integerList){
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i<integerList.size(); i++) sb.append((i == 0? "":strSeparator) + integerList.get(i));

        return sb.toString();
    }
    public static List<Integer> stringToIntegerList(String str){
        String[] arr = str.split(strSeparator);
        List<Integer> integerList = new ArrayList<Integer>();
        for(String a: arr) integerList.add(Integer.decode(a));
        return integerList;
    }


    ///Helper class to view database for debugging

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

}
