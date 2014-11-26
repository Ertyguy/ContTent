package com.edaviessmith.consumecontent.db;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

import com.edaviessmith.consumecontent.data.Alarm;
import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.Notification;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.data.YoutubeFeed;
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
        addDefaultGroupsUsersMediaFeeds(db);
    }

    private void addDefaultGroupsUsersMediaFeeds(SQLiteDatabase db) {

        final Group mindcrackGroup = new Group(0, "Mindcrack", "https://yt3.ggpht.com/-9QlCRBfW1nI/AAAAAAAAAAI/AAAAAAAAAAA/O3hR1mPFSDw/s100-c-k-no/photo.jpg", true);
        final Group roosterTeethGroup = new Group(1, "Rooster Teeth", "https://lh6.googleusercontent.com/-hddEYyXVeZM/AAAAAAAAAAI/AAAAAAAAAAA/ghwEL1-FHdE/photo.jpg", true);

        mindcrackGroup.setUsers(new SparseArray<User>() {{
                put(0, new User(0, "Adlingtont", "https://lh4.googleusercontent.com/-6DIByMHfFbw/AAAAAAAAAAI/AAAAAAAAAAA/GMmF_KtKbtQ/photo.jpg", new SparseArray<YoutubeFeed>() {{
                    put(0, new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/ecGJWrXgBGM/default.jpg", "UCfhM3yJ8a_sAJj0qOKb40gw", "UUfhM3yJ8a_sAJj0qOKb40gw", 0, -1));
                    put(1, new YoutubeFeed(1, "Liked videos", "https://i.ytimg.com/vi/xc4J9kUPdk8/default.jpg", "UCfhM3yJ8a_sAJj0qOKb40gw", "LLfhM3yJ8a_sAJj0qOKb40gw", 0, -1));
                    put(2, new YoutubeFeed(2, "Favorites", "https://i.ytimg.com/vi/J5Vr9Rs8fBs/default.jpg", "UCfhM3yJ8a_sAJj0qOKb40gw", "FLfhM3yJ8a_sAJj0qOKb40gw", 0, -1));
                    put(3, new YoutubeFeed(3, "Activity", "null", "UCfhM3yJ8a_sAJj0qOKb40gw", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(mindcrackGroup); }}));

                put(1, new User(0, "AnderZEL", "https://lh3.googleusercontent.com/-3PhwKIB2SZQ/AAAAAAAAAAI/AAAAAAAAAAA/aUoAYV_j-iI/photo.jpg", new SparseArray<YoutubeFeed>() {{
                        put(0, new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/LHY08mBxps8/default.jpg", "UC-_VTaWqRsZ1nzZLHQIGwQA", "UU-_VTaWqRsZ1nzZLHQIGwQA", 0, -1));
                        put(0, new YoutubeFeed(1, "Liked videos", "https://i.ytimg.com/vi/EMCrZdNM2tM/default.jpg", "UC-_VTaWqRsZ1nzZLHQIGwQA", "LL-_VTaWqRsZ1nzZLHQIGwQA", 0, -1));
                        put(0, new YoutubeFeed(2, "Activity", "null", "UC-_VTaWqRsZ1nzZLHQIGwQA", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(mindcrackGroup); }}));

            put(2, new User(0, "Arkas", "https://lh6.googleusercontent.com/-Tef2i7GdFqQ/AAAAAAAAAAI/AAAAAAAAAAA/7MuQK1LEUq8/photo.jpg", new SparseArray<YoutubeFeed>() {{
                         put(0,new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/RmdNI4gzA8k/default.jpg", "UCStPXwuYhdUu-B6fkVgi3vQ", "UUStPXwuYhdUu-B6fkVgi3vQ", 0, -1));
                         put(1,new YoutubeFeed(1, "Liked videos", "https://i.ytimg.com/vi/PSZxmZmBfnU/default.jpg", "UCStPXwuYhdUu-B6fkVgi3vQ", "LLStPXwuYhdUu-B6fkVgi3vQ", 0, -1));
                         put(2,new YoutubeFeed(2, "Activity", "null", "UCStPXwuYhdUu-B6fkVgi3vQ", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(mindcrackGroup); }}));

            put(3, new User(0, "Aureylian", "https://lh6.googleusercontent.com/-M0gttE6PTjI/AAAAAAAAAAI/AAAAAAAAAAA/7mbiZA77004/photo.jpg", new SparseArray<YoutubeFeed>() {{
                         put(0,new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/pNBkFXF-b44/default.jpg", "UCM2FHDmMP92caH9aK7RwEdw", "UUM2FHDmMP92caH9aK7RwEdw", 0, -1));
                         put(1,new YoutubeFeed(1, "Liked videos", "https://i.ytimg.com/vi/lkL6X4I2BnY/default.jpg", "UCM2FHDmMP92caH9aK7RwEdw", "LLM2FHDmMP92caH9aK7RwEdw", 0, -1));
                         put(2,new YoutubeFeed(2, "Activity", "null", "UCM2FHDmMP92caH9aK7RwEdw", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(mindcrackGroup); }}));

            put(4, new User(0, "AvidyaZen", "https://lh4.googleusercontent.com/-b4Bsg4yjL-E/AAAAAAAAAAI/AAAAAAAAAAA/6bNDEdr1ftk/photo.jpg", new SparseArray<YoutubeFeed>() {{
                        put(0,new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/qB5uE5hTLjc/default.jpg", "UCDREKsabG-MOTPxmJOHctEw", "UUDREKsabG-MOTPxmJOHctEw", 0, -1));
                        put(1,new YoutubeFeed(1, "Liked videos", "https://i.ytimg.com/vi/YKvNreOw0v4/default.jpg", "UCDREKsabG-MOTPxmJOHctEw", "LLDREKsabG-MOTPxmJOHctEw", 0, -1));
                        put(2,new YoutubeFeed(2, "Favorites", "https://i.ytimg.com/vi/nGeKSiCQkPw/default.jpg", "UCDREKsabG-MOTPxmJOHctEw", "FLDREKsabG-MOTPxmJOHctEw", 0, -1));
                        put(3,new YoutubeFeed(3, "Activity", "null", "UCDREKsabG-MOTPxmJOHctEw", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(mindcrackGroup); }}));

            put(5, new User(0, "BdoubleO100", "https://lh3.googleusercontent.com/-N9bzILzOWE8/AAAAAAAAAAI/AAAAAAAAAAA/tE8mBnO9E-M/photo.jpg", new SparseArray<YoutubeFeed>() {{
                         put(0,new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/a5TMGHUiurY/default.jpg", "UClu2e7S8atp6tG2galK9hgg", "UUlu2e7S8atp6tG2galK9hgg", 0, -1));
                         put(1,new YoutubeFeed(1, "Liked videos", "https://i.ytimg.com/vi/Yvy5FMebvXM/default.jpg", "UClu2e7S8atp6tG2galK9hgg", "LLlu2e7S8atp6tG2galK9hgg", 0, -1));
                         put(2,new YoutubeFeed(2, "Activity", "null", "UClu2e7S8atp6tG2galK9hgg", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(mindcrackGroup); }}));

            put(6, new User(0, "BlameTheController", "https://lh3.googleusercontent.com/-MHIPpVzLK7U/AAAAAAAAAAI/AAAAAAAAAAA/XHj6X59K2ko/photo.jpg", new SparseArray<YoutubeFeed>() {{
                         put(0,new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/mpPUQesvTxk/default.jpg", "UCmSwqv2aPbuOGiuii2TeaLQ", "UUmSwqv2aPbuOGiuii2TeaLQ", 0, -1));
                         put(1,new YoutubeFeed(1, "Liked videos", "https://i.ytimg.com/vi/cZWsW1a0aNc/default.jpg", "UCmSwqv2aPbuOGiuii2TeaLQ", "LLmSwqv2aPbuOGiuii2TeaLQ", 0, -1));
                         put(2,new YoutubeFeed(2, "Activity", "null", "UCmSwqv2aPbuOGiuii2TeaLQ", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(mindcrackGroup); }}));

            put(7, new User(0, "Coestar", "https://lh6.googleusercontent.com/-3KtWXObWKRs/AAAAAAAAAAI/AAAAAAAAAAA/XYDY6AF-Yb8/photo.jpg", new SparseArray<YoutubeFeed>() {{
                         put(0,new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/Hfzds05yO1Q/default.jpg", "UCf-p8DeUzTqmsGW1tFOArTg", "UUf-p8DeUzTqmsGW1tFOArTg", 0, -1));
                         put(1,new YoutubeFeed(1, "Liked videos", "https://i.ytimg.com/vi/pNBkFXF-b44/default.jpg", "UCf-p8DeUzTqmsGW1tFOArTg", "LLf-p8DeUzTqmsGW1tFOArTg", 0, -1));
                         put(2,new YoutubeFeed(2, "Activity", "null", "UCf-p8DeUzTqmsGW1tFOArTg", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(mindcrackGroup); }}));

            }});

        roosterTeethGroup.setUsers(new SparseArray<User>() {{
            put(0, new User(0, "Rooster Teeth", "https://lh6.googleusercontent.com/-hddEYyXVeZM/AAAAAAAAAAI/AAAAAAAAAAA/ghwEL1-FHdE/photo.jpg", new SparseArray<YoutubeFeed>() {{
                     put(0,new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/x3DQhsELdG0/default.jpg", "UCzH3iADRIq1IJlIXjfNgTpA", "UUzH3iADRIq1IJlIXjfNgTpA", 0, -1));
                     put(1,new YoutubeFeed(1, "Liked videos", "https://i.ytimg.com/vi/5W2f2MumEng/default.jpg", "UCzH3iADRIq1IJlIXjfNgTpA", "LLzH3iADRIq1IJlIXjfNgTpA", 0, -1));
                     put(2,new YoutubeFeed(2, "Activity", "null", "UCzH3iADRIq1IJlIXjfNgTpA", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(roosterTeethGroup); }}));

            put(1, new User(0, "LetsPlay", "https://lh5.googleusercontent.com/-f70aQff_iKE/AAAAAAAAAAI/AAAAAAAAAAA/-R40EvoOKf0/photo.jpg", new SparseArray<YoutubeFeed>() {{
                     put(0,new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/r69opIlqTI8/default.jpg", "UCkxctb0jr8vwa4Do6c6su0Q", "UUkxctb0jr8vwa4Do6c6su0Q", 0, -1));
                     put(1,new YoutubeFeed(1, "Liked videos", "https://i.ytimg.com/vi/VT6VrznzqMY/default.jpg", "UCkxctb0jr8vwa4Do6c6su0Q", "LLkxctb0jr8vwa4Do6c6su0Q", 0, -1));
                     put(2,new YoutubeFeed(2, "Activity", "null", "UCkxctb0jr8vwa4Do6c6su0Q", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(roosterTeethGroup); }}));

            put(2, new User(0, "The Know", "https://lh5.googleusercontent.com/-0lKHa9Wjo54/AAAAAAAAAAI/AAAAAAAAAAA/fVDFDWwii7A/photo.jpg", new SparseArray<YoutubeFeed>() {{
                    put(0,new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/iVsIPFIcrsw/default.jpg", "UC4w_tMnHl6sw5VD93tVymGw", "UU4w_tMnHl6sw5VD93tVymGw", 0, -1));
                    put(1,new YoutubeFeed(1, "Activity", "null", "UC4w_tMnHl6sw5VD93tVymGw", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(roosterTeethGroup); }}));

            put(3, new User(0, "The Slow Mo Guys", "https://lh3.googleusercontent.com/-hdZED2lNuKE/AAAAAAAAAAI/AAAAAAAAAAA/ppDB-or2f7I/photo.jpg", new SparseArray<YoutubeFeed>() {{
                    put(0,new YoutubeFeed(0, "Uploads", "https://i.ytimg.com/vi/RkLn2gR7SyE/default.jpg", "UCUK0HBIBWgM2c4vsPhkYY4w", "UUUK0HBIBWgM2c4vsPhkYY4w", 0, -1));
                    put(1,new YoutubeFeed(1, "Liked videos", "https://i.ytimg.com/vi/RkLn2gR7SyE/default.jpg", "UCUK0HBIBWgM2c4vsPhkYY4w", "LLUK0HBIBWgM2c4vsPhkYY4w", 0, -1));
                    put(2,new YoutubeFeed(2, "Favorites", "https://i.ytimg.com/vi/tmwvt-bNLY0/default.jpg", "UCUK0HBIBWgM2c4vsPhkYY4w", "FLUK0HBIBWgM2c4vsPhkYY4w", 0, -1));
                    put(3,new YoutubeFeed(3, "Activity", "null", "UCUK0HBIBWgM2c4vsPhkYY4w", "null", 1, -1));
                }}, new ArrayList<Group>() {{add(roosterTeethGroup); }}));

        }});

        List<Group> groups = new ArrayList<Group>() {{
            add(mindcrackGroup);
            add(roosterTeethGroup);
        }};

        GroupORM.saveGroups(db, groups);
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

    @Override
    public void onConfigure(SQLiteDatabase db) {
        if (android.os.Build.VERSION.SDK_INT >= 16)
            db.setForeignKeyConstraintsEnabled(true);
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

    public static String setForeignKey(int foreignKeyId) {
        return DB.isValid(foreignKeyId) ? String.valueOf(foreignKeyId): null;
    }

    public static int getForeignKey(Cursor c, String columnName) {
        if(c.isNull(c.getColumnIndex(columnName))) return -1;
        return c.getInt(c.getColumnIndex(columnName));
    }
}
