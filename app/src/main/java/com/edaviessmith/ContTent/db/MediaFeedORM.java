package com.edaviessmith.contTent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import com.edaviessmith.contTent.data.MediaFeed;
import com.edaviessmith.contTent.data.TwitterFeed;
import com.edaviessmith.contTent.data.YoutubeFeed;
import com.edaviessmith.contTent.util.Var;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MediaFeedORM {
    static final String TAG = "MediaFeedORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_MEDIA_FEED +" (" +
            DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DB.COL_USER             + " INTEGER, " +
            DB.COL_SORT             + " INTEGER, " +
            DB.COL_NAME   	        + " TEXT, " +
            DB.COL_THUMBNAIL 	    + " TEXT, " +
            DB.COL_CHANNEL_HANDLE	+ " TEXT ," +
            DB.COL_DISPLAY_NAME	    + " TEXT ," +  //TODO twitterFeed display name is not handled efficiently (design decision required)
            DB.COL_FEED_ID          + " TEXT, " +
            DB.COL_TYPE		        + " INTEGER, " +
            DB.COL_NOTIFICATION     + " INTEGER, " +
            DB.COL_LAST_UPDATE      + " INTEGER, " +
            "FOREIGN KEY("+DB.COL_USER +") REFERENCES "+DB.TABLE_USER+"("+DB.COL_ID+")," +
            "FOREIGN KEY("+DB.COL_NOTIFICATION +") REFERENCES "+DB.TABLE_NOTIFICATION+"("+DB.COL_ID+")" +");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_MEDIA_FEED;


    public static SparseArray getMediaFeeds(SQLiteDatabase database, int userId) {
        SparseArray mediaFeeds = new SparseArray();

        Cursor cursor = database.query(false, DB.TABLE_MEDIA_FEED, null, DB.COL_USER + " = " + userId, null, null, null, DB.ORDER_BY_SORT, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(DB.COL_ID));
                int type = cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE));

                if(Var.isTypeYoutube(type)) mediaFeeds.put(id, (YoutubeFeed) cursorToMediaFeed(cursor, type, false));
                if(type == Var.TYPE_TWITTER) mediaFeeds.put(id, (TwitterFeed) cursorToMediaFeed(cursor, type, false));
                else mediaFeeds.put(id, (MediaFeed) cursorToMediaFeed(cursor, type, false));
                //Log.e(DB.STRIP, mediaFeeds.get(id).toString());
                cursor.moveToNext();
            }
            Log.i(TAG, "MediaFeeds loaded successfully "+mediaFeeds.size());
        }

        return mediaFeeds;

    }

    public static List<YoutubeFeed> getYoutubeFeedsByNotificationId(Context context, int notificationId) {
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
                    YoutubeFeed youtubeFeed = (YoutubeFeed) cursorToMediaFeed(cursor, type, true);

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
            MediaFeed mediaFeed = (MediaFeed) cursorToMediaFeed(cursor, type, false);

            return mediaFeed;
        }

        return null;
    }

    public static void saveMediaFeedItems(Context context, MediaFeed mediaFeed) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {

            database.update(DB.TABLE_MEDIA_FEED,lastUpdateToContentValues(Calendar.getInstance(Locale.getDefault()).getTimeInMillis()), DB.COL_ID + " = " + mediaFeed.getId(), null);

            if(mediaFeed.getType() == Var.TYPE_YOUTUBE_ACTIVTY || mediaFeed.getType() == Var.TYPE_YOUTUBE_PLAYLIST) {
                YoutubeItemORM.saveYoutubeItems(database, mediaFeed.getItems(), mediaFeed.getId());
            }
            if(mediaFeed.getType() == Var.TYPE_TWITTER) {
                TwitterItemORM.saveTwitterItems(database, mediaFeed.getItems(), mediaFeed.getId());
            }

            database.setTransactionSuccessful();
            Log.e(TAG, "saveMediaFeedItems");
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
    }


    public static void saveYoutubeFeedItems(Context context, YoutubeFeed mediaFeed) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {

            database.update(DB.TABLE_MEDIA_FEED,lastUpdateToContentValues(Calendar.getInstance(Locale.getDefault()).getTimeInMillis()), DB.COL_ID + " = " + mediaFeed.getId(), null);

            if(mediaFeed.getType() == Var.TYPE_YOUTUBE_ACTIVTY || mediaFeed.getType() == Var.TYPE_YOUTUBE_PLAYLIST) {
                YoutubeItemORM.saveYoutubeItems(database, mediaFeed.getItems(), mediaFeed.getId());
            }

            database.setTransactionSuccessful();
            Log.e(TAG, "saveYoutubeFeedItems");
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    public static SparseArray<MediaFeed> saveMediaFeeds(SQLiteDatabase database, SparseArray<MediaFeed> mediaFeeds, SparseArray<MediaFeed> removedMediaFeeds, int userId) {
        if(removedMediaFeeds != null) {
            //for (MediaFeed removedMediaFeed : removedMediaFeeds) {
            for(int i=0; i< removedMediaFeeds.size(); i++) {
                removeMediaFeed(database, removedMediaFeeds.valueAt(i));
            }
        }
        SparseArray<MediaFeed> mediaFeedSparseArray = new SparseArray<MediaFeed>();
        for(int i=0; i< mediaFeeds.size(); i++) {
            MediaFeed mediaFeed = saveMediaFeed(database, mediaFeeds.valueAt(i), userId, i);
            mediaFeedSparseArray.put(mediaFeed.getId(), mediaFeed);
        }
        return mediaFeedSparseArray;
    }

    public static MediaFeed saveMediaFeed(SQLiteDatabase database, MediaFeed mediaFeed, int userId, int sort) {

        mediaFeed.setSort(sort);
        if(DB.isValid(mediaFeed.getId())) {
            database.update(DB.TABLE_MEDIA_FEED, mediaFeedToContentValues(mediaFeed, userId, false), DB.COL_ID + " = " + mediaFeed.getId(), null);
        } else {
            mediaFeed.setId((int) database.insert(DB.TABLE_MEDIA_FEED, null, mediaFeedToContentValues(mediaFeed, userId, false)));
        }

       // Log.d(TAG, "saveMediaFeed "+mediaFeed.getId() + ", "+mediaFeed.getSort());
        return mediaFeed;
    }

    public static MediaFeed saveMediaFeed(Context context, MediaFeed mediaFeed, int userId) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {

            if(DB.isValid(mediaFeed.getId())) {
                database.update(DB.TABLE_MEDIA_FEED, mediaFeedToContentValues(mediaFeed, userId, false), DB.COL_ID + " = " + mediaFeed.getId(), null);
            } else {
                mediaFeed.setId((int) database.insert(DB.TABLE_MEDIA_FEED, null, mediaFeedToContentValues(mediaFeed, userId, false)));
            }

            database.setTransactionSuccessful();
            Log.e(TAG, "saveMediaFeed");
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }

        return mediaFeed;
    }


    public static void removeMediaFeed(SQLiteDatabase database, MediaFeed mediaFeed) {

        if(DB.isValid(mediaFeed.getId())) {
            YoutubeItemORM.removeYoutubeItems(database, mediaFeed.getId());

            database.delete(DB.TABLE_MEDIA_FEED, DB.COL_ID + " = " + mediaFeed.getId(), null);
        }

        Log.d(TAG, "deleteMediaFeed "+mediaFeed.getId());
    }



    private static ContentValues lastUpdateToContentValues(long lastUpdate) {
        ContentValues values = new ContentValues();
        values.put(DB.COL_LAST_UPDATE, lastUpdate);
        return values;
    }

    private static ContentValues mediaFeedToContentValues(MediaFeed mediaFeed, int userId, boolean includeId) {
        ContentValues values = new ContentValues();
        if(includeId) values.put(DB.COL_ID, mediaFeed.getId());
        values.put(DB.COL_USER, userId);
        values.put(DB.COL_SORT, mediaFeed.getSort());
        values.put(DB.COL_NAME, mediaFeed.getName());
        values.put(DB.COL_CHANNEL_HANDLE, mediaFeed.getChannelHandle());
        if(!Var.isEmpty(mediaFeed.getDisplayName())) values.put(DB.COL_DISPLAY_NAME, mediaFeed.getDisplayName());
        values.put(DB.COL_THUMBNAIL, mediaFeed.getThumbnail());
        values.put(DB.COL_FEED_ID, mediaFeed.getFeedId());
        values.put(DB.COL_TYPE, mediaFeed.getType());
        values.put(DB.COL_NOTIFICATION, DB.setForeignKey(mediaFeed.getNotificationId()));
        values.put(DB.COL_LAST_UPDATE, mediaFeed.getLastUpdate());
        return values;
    }

    private static Object cursorToMediaFeed(Cursor cursor, int type, boolean includeUserId) {
        if(Var.isTypeYoutube(type)) {
            YoutubeFeed youtubeFeed = new YoutubeFeed(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                    cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_CHANNEL_HANDLE)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_FEED_ID)),
                    cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)),
                    DB.getForeignKey(cursor, DB.COL_NOTIFICATION),
                    cursor.getLong(cursor.getColumnIndex(DB.COL_LAST_UPDATE)));
            if(includeUserId) youtubeFeed.setUserId(cursor.getInt(cursor.getColumnIndex(DB.COL_USER)));
            return youtubeFeed;

        }

        if(type == Var.TYPE_TWITTER) {
            TwitterFeed twitterFeed = new TwitterFeed(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                    cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_CHANNEL_HANDLE)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(DB.COL_FEED_ID)),
                    cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)),
                    DB.getForeignKey(cursor, DB.COL_NOTIFICATION),
                    cursor.getLong(cursor.getColumnIndex(DB.COL_LAST_UPDATE)));
            if(includeUserId) twitterFeed.setUserId(cursor.getInt(cursor.getColumnIndex(DB.COL_USER)));
            return twitterFeed;

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