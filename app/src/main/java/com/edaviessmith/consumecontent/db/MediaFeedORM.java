package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.data.YoutubeFeed;
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
            //TODO twitterFeed display name is not handled (design decision required)
            DB.COL_FEED_ID          + " TEXT, " +
            DB.COL_TYPE		        + " INTEGER, " +
            DB.COL_NOTIFICATION     + " INTEGER, " +
            DB.COL_LAST_UPDATE      + " INTEGER, " +
            "FOREIGN KEY("+DB.COL_USER +") REFERENCES "+DB.TABLE_USER+"("+DB.COL_ID+")," +
            "FOREIGN KEY("+DB.COL_NOTIFICATION +") REFERENCES "+DB.TABLE_NOTIFICATION+"("+DB.COL_ID+")" +");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_MEDIA_FEED;


    public static List getMediaFeeds(SQLiteDatabase database, int userId) {
        List mediaFeeds = new ArrayList();

        Cursor cursor = database.query(false, DB.TABLE_MEDIA_FEED, null, DB.COL_USER + " = " + userId, null, null, null, DB.ORDER_BY_SORT, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int type = cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE));

                if(Var.isTypeYoutube(type)) mediaFeeds.add((YoutubeFeed) cursorToMediaFeed(cursor, type));
                else mediaFeeds.add((MediaFeed) cursorToMediaFeed(cursor, type));
                Log.e(TAG, cursorToMediaFeed(cursor, type).toString());
                cursor.moveToNext();
            }
            Log.i(TAG, "MediaFeeds loaded successfully.");
        }

        return mediaFeeds;

    }

    public static List<YoutubeFeed> getMediaFeedsByNotificationId(Context context, int notificationId) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        List<YoutubeFeed> mediaFeeds = new ArrayList<YoutubeFeed>();

        database.beginTransaction();
        try {
            Cursor cursor = database.query(false, DB.TABLE_MEDIA_FEED, null, DB.COL_NOTIFICATION + " = " + notificationId, null, null, null, DB.ORDER_BY_SORT, null);

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    int type = cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE));
                    YoutubeFeed youtubeFeed = (YoutubeFeed) cursorToMediaFeed(cursor, type);

                    youtubeFeed.setItems(YoutubeItemORM.getYoutubeItems(database, youtubeFeed.getId()));

                    mediaFeeds.add(youtubeFeed);
                    cursor.moveToNext();
                }
            }

            database.setTransactionSuccessful();
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
        return mediaFeeds;

    }

    public static MediaFeed getMediaFeed(SQLiteDatabase database, int mediaFeedId) {

        Cursor cursor = database.query(false, DB.TABLE_MEDIA_FEED, null,  DB.COL_ID + " = " + mediaFeedId, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            int type = cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE));
            MediaFeed mediaFeed = (MediaFeed) cursorToMediaFeed(cursor, type);

            return mediaFeed;
        }

        return null;
    }

    public static void saveMediaItems(Context context, MediaFeed mediaFeed) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {

            //TODO update last_updated field (to prevent overtaxing requests)

            if(mediaFeed.getType() == Var.TYPE_YOUTUBE_ACTIVTY || mediaFeed.getType() == Var.TYPE_YOUTUBE_PLAYLIST) {
                YoutubeItemORM.saveYoutubeItems(database, mediaFeed.getItems(), mediaFeed.getId());
            }

            database.setTransactionSuccessful();
            Log.e(TAG, "saveMediaItems");
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    public static void saveMediaFeeds(SQLiteDatabase database, List<MediaFeed> mediaFeeds, List<MediaFeed> removedMediaFeeds, int userId) {
        if(removedMediaFeeds != null) {
            for (MediaFeed removedMediaFeed : removedMediaFeeds) {
                removeMediaFeed(database, removedMediaFeed);
            }
        }

        for(int i=0; i< mediaFeeds.size(); i++) {
            saveMediaFeed(database, mediaFeeds.get(i), userId, i);
        }
    }

    public static void saveMediaFeed(SQLiteDatabase database, MediaFeed mediaFeed, int userId, int sort) {

        mediaFeed.setSort(sort);
        if(DB.isValid(mediaFeed.getId())) {
            database.update(DB.TABLE_MEDIA_FEED, mediaFeedToContentValues(mediaFeed, userId, false), DB.COL_ID + " = " + mediaFeed.getId(), null);
        } else {
            mediaFeed.setId((int) database.insert(DB.TABLE_MEDIA_FEED, null, mediaFeedToContentValues(mediaFeed, userId, false)));
        }

        Log.d(TAG, "saveMediaFeed "+mediaFeed.getId());
    }

    public static void removeMediaFeed(SQLiteDatabase database, MediaFeed mediaFeed) {

        if(DB.isValid(mediaFeed.getId())) {
            YoutubeItemORM.removeYoutubeItems(database, mediaFeed.getId());

            database.delete(DB.TABLE_MEDIA_FEED, DB.COL_ID + " = " + mediaFeed.getId(), null);
        }

        Log.d(TAG, "deleteMediaFeed "+mediaFeed.getId());
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
        values.put(DB.COL_NOTIFICATION, DB.setForeignKey(mediaFeed.getNotificationId()));
        values.put(DB.COL_LAST_UPDATE, mediaFeed.getLastUpdate());
        return values;
    }

    private static Object cursorToMediaFeed(Cursor cursor, int type) {
        if(Var.isTypeYoutube(type)) {
            return new YoutubeFeed(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                    cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_CHANNEL_HANDLE)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_FEED_ID)),
                    cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)),
                    DB.getForeignKey(cursor, DB.COL_NOTIFICATION),
                    cursor.getLong(cursor.getColumnIndex(DB.COL_LAST_UPDATE)));
        }

        //TODO return TwitterFeed

        return new MediaFeed(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                                 cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_CHANNEL_HANDLE)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_FEED_ID)),
                                 cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)),
                                 DB.getForeignKey(cursor, DB.COL_NOTIFICATION),
                                 cursor.getLong(cursor.getColumnIndex(DB.COL_LAST_UPDATE)));
    }

}