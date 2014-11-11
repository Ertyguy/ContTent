package com.edaviessmith.consumecontent.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        db.execSQL(YoutubeChannelORM.SQL_CREATE_TABLE);
        db.execSQL(TwitterFeedORM.SQL_CREATE_TABLE);
        db.execSQL(YoutubeFeedORM.SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(GroupORM.SQL_DROP_TABLE);
        db.execSQL(UserORM.SQL_DROP_TABLE);
        db.execSQL(GroupUserORM.SQL_DROP_TABLE);
        db.execSQL(YoutubeChannelORM.SQL_DROP_TABLE);
        db.execSQL(TwitterFeedORM.SQL_DROP_TABLE);
        db.execSQL(YoutubeFeedORM.SQL_DROP_TABLE);



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
    static final String TABLE_GROUP_USER = "groups_users"; //Many to Many
    static final String TABLE_YOUTUBE_CHANNEL = "youtube_channels";
    static final String TABLE_TWITTER_FEED = "twitter_feeds";
    static final String TABLE_YOUTUBE_FEED = "youtube_feeds";

    public static String ORDER_BY_SORT = DB.COL_SORT + " ASC";
    //// Database Columns /////

    static final String COL_ID = "id";
    static final String COL_SORT = "sort";
    static final String COL_NAME = "name";
    static final String COL_THUMBNAIL = "thumbnail";
    static final String COL_VISIBILITY = "visibility";
    static final String COL_NOTIFICATION = "notification";
    static final String COL_TYPE = "type";
    static final String COL_FEED_ID = "feed_id";
    static final String COL_DISPLAY_NAME = "display_name";

    //Foreign id keys
    static final String COL_GROUP = "group_id";
    static final String COL_USER = "user_id";
    static final String COL_YOUTBUE_CHANNEL = "youtube_channel_id";
    static final String COL_TWITTER_FEED = "twitter_feed_id";

}
