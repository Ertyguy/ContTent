package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.TwitterFeed;
import com.edaviessmith.consumecontent.util.Var;

import java.util.ArrayList;
import java.util.List;

public class TwitterFeedORM {
    static final String TAG = "TwitterFeedORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_TWITTER_FEED +" (" +
            DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DB.COL_SORT             + " INTEGER, " +
            DB.COL_NAME   	        + " TEXT, " +
            DB.COL_DISPLAY_NAME		+ " TEXT ," +
            DB.COL_THUMBNAIL 	    + " TEXT, " +
            DB.COL_FEED_ID          + " TEXT, " +
            DB.COL_TYPE		        + " INTEGER " + ");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_TWITTER_FEED;


    public static List<TwitterFeed> getTwitterFeeds(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.query(false, DB.TABLE_TWITTER_FEED, null, null, null, null, null, DB.ORDER_BY_SORT, null);
        List<TwitterFeed> memberList = new ArrayList<TwitterFeed>();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TwitterFeed member = cursorToTwitterFeed(cursor);
                memberList.add(member);
                cursor.moveToNext();
            }
            Log.i("TwitterFeedORM", "TwitterFeeds loaded successfully.");
        }

        database.close();

        return memberList;
    }

    public static List<TwitterFeed> getVisibleTwitterFeeds(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();


        Cursor cursor = database.query(false, DB.TABLE_TWITTER_FEED, null, DB.COL_VISIBILITY + " == ?", new String[]{String.valueOf(1)}, null, null, DB.ORDER_BY_SORT, null);

        Log.i("TwitterFeedORM", "Loaded " + cursor.getCount() + " TwitterFeeds...");
        List<TwitterFeed> memberList = new ArrayList<TwitterFeed>();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TwitterFeed member = cursorToTwitterFeed(cursor);
                memberList.add(member);
                cursor.moveToNext();
            }
            Log.i("TwitterFeedORM", "TwitterFeeds loaded successfully.");
        }

        database.close();

        return memberList;
    }


    public static void updateTwitterFeed(Context context, TwitterFeed twitterFeed) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.update(DB.TABLE_TWITTER_FEED, twitterFeedToContentValues(twitterFeed, false), DB.COL_ID + " = ?", new String[]{String.valueOf(twitterFeed.getId())});
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.close();
        }

    }

    public static void incrementTwitterFeed(SQLiteDatabase database, int id) {
        try {
            ContentValues values = new ContentValues();
            values.put(DB.COL_ID, id+1);

            database.update(DB.TABLE_TWITTER_FEED, values, DB.COL_ID + " = ?", new String[]{String.valueOf(id)});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void incrementSort(SQLiteDatabase database, int id) {
        try {
            ContentValues values = new ContentValues();
            values.put(DB.COL_SORT, id+1);

            database.update(DB.TABLE_TWITTER_FEED, values, DB.COL_SORT + " = ?", new String[]{String.valueOf(id)});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTwitterFeeds(Context context, List<TwitterFeed> twitterFeeds) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();
            for(TwitterFeed twitterFeed : twitterFeeds) {
                database.update(DB.TABLE_TWITTER_FEED, twitterFeedToContentValues(twitterFeed, false), DB.COL_ID + " = ?", new String[]{String.valueOf(twitterFeed.getId())});
            }
            database.setTransactionSuccessful();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }
    }


    public static TwitterFeed getTwitterFeed(SQLiteDatabase database, int twitterFeedId) {

        Cursor cursor = database.query(false, DB.TABLE_TWITTER_FEED, null,  DB.COL_ID + " = " + twitterFeedId, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            TwitterFeed twitterFeed = cursorToTwitterFeed(cursor);

            return twitterFeed;
        }

        return null;
    }

    public static int saveTwitterFeed(SQLiteDatabase database, TwitterFeed twitterFeed) {

        if(Var.isValid(twitterFeed.getId())) {
            database.update(DB.TABLE_TWITTER_FEED, twitterFeedToContentValues(twitterFeed, false), DB.COL_ID + " = " + twitterFeed.getId(), null);
            return twitterFeed.getId();
        } else {
            return (int) database.insert(DB.TABLE_TWITTER_FEED, null, twitterFeedToContentValues(twitterFeed, false));
        }

    }

    private static ContentValues twitterFeedToContentValues(TwitterFeed twitterFeed, boolean includeId) {
        ContentValues values = new ContentValues();
        if(includeId) values.put(DB.COL_ID, twitterFeed.getId());
        values.put(DB.COL_SORT, twitterFeed.getSort());
        values.put(DB.COL_NAME, twitterFeed.getName());
        values.put(DB.COL_DISPLAY_NAME, twitterFeed.getDisplayName());
        values.put(DB.COL_THUMBNAIL, twitterFeed.getThumbnail());
        values.put(DB.COL_FEED_ID, twitterFeed.getFeedId());
        values.put(DB.COL_TYPE, twitterFeed.getType());
        return values;
    }

    private static TwitterFeed cursorToTwitterFeed(Cursor cursor) {
        return new TwitterFeed(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                                 cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_DISPLAY_NAME)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_FEED_ID)),
                                 cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)) );
    }

}