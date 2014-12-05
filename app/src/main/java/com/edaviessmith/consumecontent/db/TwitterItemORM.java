package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.TwitterItem;

import java.util.ArrayList;
import java.util.List;

public class TwitterItemORM {
    static final String TAG = "TwitterItemORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_TWITTER_ITEM +" (" +
            //DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+      //I beleive this won't hinder anything (probably)
            DB.COL_MEDIA_FEED       + " INTEGER, " +
            DB.COL_TYPE             + " INTEGER ," +
            DB.COL_TWEET_ID         + " INTEGER ," +
            DB.COL_DATE             + " INTEGER, " +
            DB.COL_TITLE            + " TEXT ," +
            DB.COL_DESCRIPTION      + " TEXT ," +
            DB.COL_IMAGE_MED        + " TEXT ," +
            DB.COL_IMAGE_HIGH       + " TEXT ," +
            DB.COL_TWEET_THUMBNAIL  + " TEXT ," +
            DB.COL_STATUS           + " INTEGER, " +
            DB.COL_SORT             + " INTEGER, " +     //Used to minimize updates
            "FOREIGN KEY("+DB.COL_MEDIA_FEED +") REFERENCES "+DB.TABLE_MEDIA_FEED+"("+DB.COL_ID+")" +");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_TWITTER_ITEM;



    public static List<TwitterItem> getTwitterItems(Context context, int mediaFeedId) {
        List<TwitterItem> twitterItems = new ArrayList<TwitterItem>();

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {
            Cursor cursor = database.query(false, DB.TABLE_TWITTER_ITEM, null, DB.COL_MEDIA_FEED + " = " + mediaFeedId, null, null, null, DB.ORDER_BY_DATE, null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    twitterItems.add(cursorToTwitterItem(cursor));
                    cursor.moveToNext();
                }
            }
            Log.i(TAG, "TwitterItems loaded successfully " +twitterItems.size());
            database.setTransactionSuccessful();

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
        return twitterItems;

    }
    
    public static List<TwitterItem> getTwitterItems(SQLiteDatabase database, int mediaFeedId) {
        List<TwitterItem> twitterItems = new ArrayList<TwitterItem>();

        Cursor cursor = database.query(false, DB.TABLE_TWITTER_ITEM, null, DB.COL_MEDIA_FEED + " = " + mediaFeedId, null, null, null, DB.ORDER_BY_DATE, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                twitterItems.add(cursorToTwitterItem(cursor));
                cursor.moveToNext();
            }
            Log.i(TAG, "TwitterItems loaded successfully.");
        }

        return twitterItems;

    }

    //Only saves the first PAGE_SIZE of items
    public static void saveTwitterItems(SQLiteDatabase database, List<TwitterItem> twitterItems, int youtubeFeedId) {
        for(int i=0; i < twitterItems.size() && i< DB.PAGE_SIZE; i++) {
            saveTwitterItem(database, twitterItems.get(i), youtubeFeedId, i);
        }
    }

    public static void saveTwitterItem(SQLiteDatabase database, TwitterItem twitterItem, int youtubeFeedId, int sort) {
        //Try updating, otherwise insert
        int updated = database.update(DB.TABLE_TWITTER_ITEM, twitterItemToContentValues(twitterItem, youtubeFeedId, sort), DB.COL_MEDIA_FEED + " = " + youtubeFeedId + " AND " + DB.COL_SORT + " = " + sort, null);
        if(updated == 0) database.insert(DB.TABLE_TWITTER_ITEM, null, twitterItemToContentValues(twitterItem, youtubeFeedId, sort));
    }

    private static ContentValues twitterItemToContentValues(TwitterItem twitterItem, int mediaFeedId, int sort) {
        ContentValues values = new ContentValues();
        values.put(DB.COL_MEDIA_FEED        , mediaFeedId);
        values.put(DB.COL_TYPE              , twitterItem.getType());
        values.put(DB.COL_TITLE             , twitterItem.getTitle());
        values.put(DB.COL_DESCRIPTION       , twitterItem.getDescription());
        values.put(DB.COL_DATE              , twitterItem.getDate());
        values.put(DB.COL_IMAGE_MED         , twitterItem.getImageMed());
        values.put(DB.COL_IMAGE_HIGH        , twitterItem.getImageHigh());
        values.put(DB.COL_TWEET_ID          , twitterItem.getTweetId());
        values.put(DB.COL_TWEET_THUMBNAIL   , twitterItem.getTweetThumbnail());
        values.put(DB.COL_STATUS            , twitterItem.getStatus());
        values.put(DB.COL_SORT              , sort);
        return values;
    }

    private static TwitterItem cursorToTwitterItem(Cursor cursor) {
        return new TwitterItem(cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)),
                cursor.getString(cursor.getColumnIndex(DB.COL_TITLE)),
                cursor.getString(cursor.getColumnIndex(DB.COL_DESCRIPTION)),
                cursor.getLong(cursor.getColumnIndex(DB.COL_DATE)),
                cursor.getString(cursor.getColumnIndex(DB.COL_IMAGE_MED)),
                cursor.getString(cursor.getColumnIndex(DB.COL_IMAGE_HIGH)),
                cursor.getInt(cursor.getColumnIndex(DB.COL_STATUS)),
                cursor.getLong(cursor.getColumnIndex(DB.COL_TWEET_ID)),
                cursor.getString(cursor.getColumnIndex(DB.COL_TWEET_THUMBNAIL)) );

    }

}