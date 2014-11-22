package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.YoutubeItem;

import java.util.ArrayList;
import java.util.List;

public class YoutubeItemORM {
    static final String TAG = "YoutubeItemORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_YOUTUBE_ITEM +" (" +
            //DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+      //I beleive this won't hinder anything (probably)
            DB.COL_MEDIA_FEED       + " INTEGER, " +
            DB.COL_TYPE             + " TEXT ," +
            DB.COL_VIDEO_ID         + " TEXT ," +
            DB.COL_DATE             + " INTEGER, " +
            DB.COL_TITLE            + " TEXT ," +
            DB.COL_DESCRIPTION      + " TEXT ," +
            DB.COL_DURATION         + " TEXT, " +
            DB.COL_IMAGE_MED        + " TEXT ," +
            DB.COL_IMAGE_HIGH       + " TEXT ," +
            DB.COL_VIEWS            + " INTEGER, " +
            DB.COL_LIKES            + " INTEGER, " +
            DB.COL_DISLIKES         + " INTEGER, " +
            DB.COL_STATUS           + " INTEGER, " +
            DB.COL_SORT             + " INTEGER, " +    //Used to minimize updates
            "FOREIGN KEY("+DB.COL_MEDIA_FEED +") REFERENCES "+DB.TABLE_MEDIA_FEED+"("+DB.COL_ID+")" +");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_YOUTUBE_ITEM;



    public static List<YoutubeItem> getYoutubeItems(Context context, int mediaFeedId) {
        List<YoutubeItem> youtubeItems = new ArrayList<YoutubeItem>();

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {
            Cursor cursor = database.query(false, DB.TABLE_YOUTUBE_ITEM, null, DB.COL_MEDIA_FEED + " = " + mediaFeedId, null, null, null, DB.ORDER_BY_DATE, null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    youtubeItems.add(cursorToYoutubeItem(cursor));
                    cursor.moveToNext();
                }
                Log.i(TAG, "YoutubeItems loaded successfully.");
            }

            database.setTransactionSuccessful();

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
        return youtubeItems;

    }

    public static List<YoutubeItem> getYoutubeItems(SQLiteDatabase db, int mediaFeedId) {
        List<YoutubeItem> youtubeItems = new ArrayList<YoutubeItem>();

        Cursor cursor = db.query(false, DB.TABLE_YOUTUBE_ITEM, null, DB.COL_MEDIA_FEED + " = " + mediaFeedId, null, null, null, DB.ORDER_BY_DATE, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                youtubeItems.add(cursorToYoutubeItem(cursor));
                cursor.moveToNext();
            }
        }

        return youtubeItems;
    }

    //Only saves the first PAGE_SIZE of items
    public static void saveYoutubeItems(SQLiteDatabase database, List<YoutubeItem> youtubeItems, int youtubeFeedId) {
        for(int i=0; i < youtubeItems.size() && i< DB.PAGE_SIZE; i++) {
            saveYoutubeItem(database, youtubeItems.get(i), youtubeFeedId, i);
        }
    }

    public static void saveYoutubeItem(SQLiteDatabase database, YoutubeItem youtubeItem, int youtubeFeedId, int sort) {
        //Try updating, otherwise insert
        int updated = database.update(DB.TABLE_YOUTUBE_ITEM, youtubeItemToContentValues(youtubeItem, youtubeFeedId, sort), DB.COL_MEDIA_FEED + " = " + youtubeFeedId + " AND "+DB.COL_SORT + " = " + sort, null);
        if(updated == 0) database.insert(DB.TABLE_YOUTUBE_ITEM, null, youtubeItemToContentValues(youtubeItem, youtubeFeedId, sort));
    }

    private static ContentValues youtubeItemToContentValues(YoutubeItem youtubeItem, int userId, int sort) {
        ContentValues values = new ContentValues();
        values.put(DB.COL_MEDIA_FEED, userId);
        values.put(DB.COL_TYPE       , youtubeItem.getType());
        values.put(DB.COL_VIDEO_ID   , youtubeItem.getVideoId());
        values.put(DB.COL_DATE       , youtubeItem.getDate());
        values.put(DB.COL_TITLE      , youtubeItem.getTitle());
        values.put(DB.COL_DESCRIPTION, youtubeItem.getDescription());
        values.put(DB.COL_DURATION   , youtubeItem.getDuration());
        values.put(DB.COL_IMAGE_MED  , youtubeItem.getImageMed());
        values.put(DB.COL_IMAGE_HIGH , youtubeItem.getImageHigh());
        values.put(DB.COL_VIEWS      , youtubeItem.getViews());
        values.put(DB.COL_LIKES      , youtubeItem.getLikes());
        values.put(DB.COL_DISLIKES   , youtubeItem.getDislikes());
        values.put(DB.COL_STATUS     , youtubeItem.getStatus());
        values.put(DB.COL_SORT, sort);
        return values;
    }

    private static YoutubeItem cursorToYoutubeItem(Cursor cursor) {
        return new YoutubeItem(cursor.getString(cursor.getColumnIndex(DB.COL_TITLE)),
                               cursor.getLong(cursor.getColumnIndex(DB.COL_DATE)),
                               cursor.getString(cursor.getColumnIndex(DB.COL_IMAGE_MED)),
                               cursor.getString(cursor.getColumnIndex(DB.COL_IMAGE_HIGH)),
                               cursor.getString(cursor.getColumnIndex(DB.COL_VIDEO_ID)),
                               cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)),
                               cursor.getString(cursor.getColumnIndex(DB.COL_DESCRIPTION)),
                               cursor.getString(cursor.getColumnIndex(DB.COL_DURATION)),
                               cursor.getInt(cursor.getColumnIndex(DB.COL_VIEWS)),
                               cursor.getInt(cursor.getColumnIndex(DB.COL_LIKES)),
                               cursor.getInt(cursor.getColumnIndex(DB.COL_DISLIKES)),
                               cursor.getInt(cursor.getColumnIndex(DB.COL_STATUS)) );
    }

}