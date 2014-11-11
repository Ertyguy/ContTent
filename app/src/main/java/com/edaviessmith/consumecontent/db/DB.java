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
        //db.execSQL(MemberORM.SQL_CREATE_TABLE);
        //db.execSQL(YoutubeItemORM.SQL_CREATE_TABLE);
        //db.execSQL(TwitterORM.SQL_CREATE_TABLE);
        //db.execSQL(RedditORM.SQL_CREATE_TABLE);

        // Put mindcrack members into database
        //createMembers(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //db.execSQL(YoutubeItemORM.SQL_DROP_TABLE);
        //db.execSQL(TwitterORM.SQL_DROP_TABLE);
        //db.execSQL(RedditORM.SQL_DROP_TABLE);


        if(oldVersion <= 1) {
            //addNewMember(db, 3);
            //addNewMember(db, 23);
        }
        if(oldVersion <= 2) {
            //addNewMember(db, 7);
        }


        //db.execSQL(YoutubeItemORM.SQL_CREATE_TABLE);
        //db.execSQL(TwitterORM.SQL_CREATE_TABLE);
        //db.execSQL(RedditORM.SQL_CREATE_TABLE);
    }



    ///// Database Tables //////
    static final String TABLE_GROUP = "groups";
    static final String TABLE_USER = "users";

    public static String ORDER_BY_SORT = DB.COL_SORT + " ASC";
    //// Database Columns /////

    static final String COL_ID = "id";
    static final String COL_SORT = "sort";
    static final String COL_NAME = "name";
    static final String COL_THUMBNAIL = "thumbnail";
    static final String COL_VISIBILITY = "visibility";
    static final String COL_NOTIFICATION = "notification";
}
