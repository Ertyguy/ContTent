package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.YoutubeFeed;
import com.edaviessmith.consumecontent.util.Var;

import java.util.ArrayList;
import java.util.List;

public class YoutubeFeedORM {
    static final String TAG = "YoutubeFeedORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_YOUTUBE_FEED +" (" +
            DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DB.COL_SORT             + " INTEGER, " +
            DB.COL_NAME   	        + " TEXT, " +
            DB.COL_THUMBNAIL 	    + " TEXT, " +
            DB.COL_FEED_ID          + " TEXT, " +
            DB.COL_TYPE		        + " INTEGER, " +
            DB.COL_YOUTBUE_CHANNEL	+ " INTEGER, " +
            DB.COL_VISIBILITY		+ " INTEGER, " +
            "FOREIGN KEY("+DB.COL_YOUTBUE_CHANNEL  +") REFERENCES "+DB.TABLE_YOUTUBE_CHANNEL +"("+DB.COL_ID+")"  + ");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_YOUTUBE_FEED;


    public static List<YoutubeFeed> getYoutubeFeeds(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.query(false, DB.TABLE_YOUTUBE_FEED, null, null, null, null, null, DB.ORDER_BY_SORT, null);
        List<YoutubeFeed> memberList = new ArrayList<YoutubeFeed>();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                YoutubeFeed member = cursorToYoutubeFeed(cursor);
                memberList.add(member);
                cursor.moveToNext();
            }
            Log.i("YoutubeFeedORM", "YoutubeFeeds loaded successfully.");
        }

        database.close();

        return memberList;
    }

    public static List<YoutubeFeed> getVisibleYoutubeFeeds(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();


        Cursor cursor = database.query(false, DB.TABLE_YOUTUBE_FEED, null, DB.COL_VISIBILITY + " == ?", new String[]{String.valueOf(1)}, null, null, DB.ORDER_BY_SORT, null);

        Log.i("YoutubeFeedORM", "Loaded " + cursor.getCount() + " YoutubeFeeds...");
        List<YoutubeFeed> memberList = new ArrayList<YoutubeFeed>();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                YoutubeFeed member = cursorToYoutubeFeed(cursor);
                memberList.add(member);
                cursor.moveToNext();
            }
            Log.i("YoutubeFeedORM", "YoutubeFeeds loaded successfully.");
        }

        database.close();

        return memberList;
    }

/*
    public static void updateYoutubeFeed(Context context, YoutubeFeed youtubeFeed) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.update(DB.TABLE_YOUTUBE_FEED, youtubeFeedToContentValues(youtubeFeed, false), DB.COL_ID + " = ?", new String[]{String.valueOf(youtubeFeed.getId())});
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.close();
        }

    }

    public static void incrementYoutubeFeed(SQLiteDatabase database, int id) {
        try {
            ContentValues values = new ContentValues();
            values.put(DB.COL_ID, id+1);

            database.update(DB.TABLE_YOUTUBE_FEED, values, DB.COL_ID + " = ?", new String[]{String.valueOf(id)});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void incrementSort(SQLiteDatabase database, int id) {
        try {
            ContentValues values = new ContentValues();
            values.put(DB.COL_SORT, id+1);

            database.update(DB.TABLE_YOUTUBE_FEED, values, DB.COL_SORT + " = ?", new String[]{String.valueOf(id)});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateYoutubeFeeds(Context context, List<YoutubeFeed> youtubeFeeds) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();
            for(YoutubeFeed youtubeFeed : youtubeFeeds) {
                database.update(DB.TABLE_YOUTUBE_FEED, youtubeFeedToContentValues(youtubeFeed, false), DB.COL_ID + " = ?", new String[]{String.valueOf(youtubeFeed.getId())});
            }
            database.setTransactionSuccessful();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }
    }*/

    public static List<YoutubeFeed> getYoutubeFeeds(SQLiteDatabase database, int youtubeChannelId) {
        List<YoutubeFeed> youtubeFeeds = new ArrayList<YoutubeFeed>();

        Cursor cursor = database.query(false, DB.TABLE_YOUTUBE_FEED, null, DB.COL_YOUTBUE_CHANNEL + " == " + youtubeChannelId, null, null, null, DB.ORDER_BY_SORT, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                YoutubeFeed youtubeFeed = cursorToYoutubeFeed(cursor);
                youtubeFeeds.add(youtubeFeed);
                cursor.moveToNext();
            }
            Log.i(TAG, "YoutubeFeeds loaded successfully.");
        }

        return youtubeFeeds;

    }

    public static void saveYoutubeFeeds(SQLiteDatabase database, List<YoutubeFeed> youtubeFeeds, int youtubeChannelId) {

        for(int i=0; i< youtubeFeeds.size(); i++) {
            saveYoutubeFeed(database, youtubeFeeds.get(i), youtubeChannelId, i);
        }
    }

    public static void saveYoutubeFeed(SQLiteDatabase database, YoutubeFeed youtubeFeed, int youtubeChannelId, int sort) {

        youtubeFeed.setSort(sort);
        if(Var.isValid(youtubeFeed.getId())) {
            database.update(DB.TABLE_YOUTUBE_FEED, youtubeFeedToContentValues(youtubeFeed, youtubeChannelId, false), DB.COL_ID + " = " + youtubeFeed.getId(), null);
        } else {
            database.insert(DB.TABLE_YOUTUBE_FEED, null, youtubeFeedToContentValues(youtubeFeed, youtubeChannelId, false));
        }

    }



    private static ContentValues youtubeFeedToContentValues(YoutubeFeed youtubeFeed, int youtubeChannelId, boolean includeId) {
        ContentValues values = new ContentValues();
        if(includeId) values.put(DB.COL_ID, youtubeFeed.getId());
        values.put(DB.COL_SORT, youtubeFeed.getSort());
        values.put(DB.COL_NAME, youtubeFeed.getName());
        values.put(DB.COL_THUMBNAIL, youtubeFeed.getThumbnail());
        values.put(DB.COL_FEED_ID, youtubeFeed.getFeedId());
        values.put(DB.COL_TYPE, youtubeFeed.getType());
        values.put(DB.COL_YOUTBUE_CHANNEL, youtubeChannelId);
        values.put(DB.COL_VISIBILITY, youtubeFeed.isVisible() ? 1 : 0);
        return values;
    }

    private static YoutubeFeed cursorToYoutubeFeed(Cursor cursor) {
        return new YoutubeFeed(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                                 cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_FEED_ID)),
                                 cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)),
                                 cursor.getInt(cursor.getColumnIndex(DB.COL_VISIBILITY)) == 1);
    }


}