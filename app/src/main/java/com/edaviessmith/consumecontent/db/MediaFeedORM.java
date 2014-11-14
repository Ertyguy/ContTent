package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.util.Var;

import java.util.ArrayList;
import java.util.List;

public class MediaFeedORM {
    static final String TAG = "MediaFeedORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_MEDIA_FEED +" (" +
            DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DB.COL_USER             + " INTEGER, " +
            DB.COL_SORT             + " INTEGER, " +
            DB.COL_NAME   	        + " TEXT, " +
            DB.COL_THUMBNAIL 	    + " TEXT, " +
            DB.COL_CHANNEL_HANDLE	+ " TEXT ," +
            DB.COL_FEED_ID          + " TEXT, " +
            DB.COL_TYPE		        + " INTEGER, " +
            "FOREIGN KEY("+DB.COL_USER +") REFERENCES "+DB.TABLE_USER+"("+DB.COL_ID+")" +");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_MEDIA_FEED;

    
    
    public static List<MediaFeed> getMediaFeeds(SQLiteDatabase database, int userId) {
        List<MediaFeed> mediaFeeds = new ArrayList<MediaFeed>();

        Cursor cursor = database.query(false, DB.TABLE_MEDIA_FEED, null, DB.COL_USER + " = " + userId, null, null, null, DB.ORDER_BY_SORT, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mediaFeeds.add(cursorToMediaFeed(cursor));
                cursor.moveToNext();
            }
            Log.i(TAG, "MediaFeeds loaded successfully.");
        }

        return mediaFeeds;

    }

    public static MediaFeed getMediaFeed(SQLiteDatabase database, int mediaFeedId) {

        Cursor cursor = database.query(false, DB.TABLE_MEDIA_FEED, null,  DB.COL_ID + " = " + mediaFeedId, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            MediaFeed mediaFeed = cursorToMediaFeed(cursor);

            return mediaFeed;
        }

        return null;
    }


    public static void saveMediaFeeds(SQLiteDatabase database, List<MediaFeed> mediaFeeds, int youtubeChannelId) {
        for(int i=0; i< mediaFeeds.size(); i++) {
            saveMediaFeed(database, mediaFeeds.get(i), youtubeChannelId, i);
        }
    }

    public static void saveMediaFeed(SQLiteDatabase database, MediaFeed mediaFeed, int userId, int sort) {

        mediaFeed.setSort(sort);
        if(Var.isValid(mediaFeed.getId())) {
            database.update(DB.TABLE_MEDIA_FEED, mediaFeedToContentValues(mediaFeed, userId, false), DB.COL_ID + " = " + mediaFeed.getId(), null);
        } else {
            database.insert(DB.TABLE_MEDIA_FEED, null, mediaFeedToContentValues(mediaFeed, userId, false));
        }

    }

    private static ContentValues mediaFeedToContentValues(MediaFeed mediaFeed, int userId, boolean includeId) {
        ContentValues values = new ContentValues();
        if(includeId) values.put(DB.COL_ID, mediaFeed.getId());
        values.put(DB.COL_USER, userId);
        values.put(DB.COL_SORT, mediaFeed.getSort());
        values.put(DB.COL_NAME, mediaFeed.getName());
        values.put(DB.COL_CHANNEL_HANDLE, mediaFeed.getChannelHandle());
        values.put(DB.COL_THUMBNAIL, mediaFeed.getThumbnail());
        values.put(DB.COL_FEED_ID, mediaFeed.getFeedId());
        values.put(DB.COL_TYPE, mediaFeed.getType());
        return values;
    }

    private static MediaFeed cursorToMediaFeed(Cursor cursor) {
        return new MediaFeed(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                                 cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_CHANNEL_HANDLE)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_FEED_ID)),
                                 cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)) );
    }

}