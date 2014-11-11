package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.YoutubeChannel;

import java.util.ArrayList;
import java.util.List;

public class YoutubeChannelORM {
    static final String TAG = "YoutubeChannelORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_YOUTUBE_CHANNEL +" (" +
            DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DB.COL_SORT             + " INTEGER, " +
            DB.COL_NAME   	        + " TEXT, " +
            DB.COL_THUMBNAIL 	    + " TEXT, " +
            DB.COL_FEED_ID          + " TEXT, " +
            DB.COL_TYPE		        + " INTEGER " + ");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_YOUTUBE_CHANNEL;


    public static List<YoutubeChannel> getYoutubeChannels(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.query(false, DB.TABLE_YOUTUBE_CHANNEL, null, null, null, null, null, DB.ORDER_BY_SORT, null);
        List<YoutubeChannel> memberList = new ArrayList<YoutubeChannel>();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                YoutubeChannel member = cursorToYoutubeChannel(cursor);
                memberList.add(member);
                cursor.moveToNext();
            }
            Log.i("YoutubeChannelORM", "YoutubeChannels loaded successfully.");
        }

        database.close();

        return memberList;
    }

    public static List<YoutubeChannel> getVisibleYoutubeChannels(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();


        Cursor cursor = database.query(false, DB.TABLE_YOUTUBE_CHANNEL, null, DB.COL_VISIBILITY + " == ?", new String[]{String.valueOf(1)}, null, null, DB.ORDER_BY_SORT, null);

        Log.i("YoutubeChannelORM", "Loaded " + cursor.getCount() + " YoutubeChannels...");
        List<YoutubeChannel> memberList = new ArrayList<YoutubeChannel>();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                YoutubeChannel member = cursorToYoutubeChannel(cursor);
                memberList.add(member);
                cursor.moveToNext();
            }
            Log.i("YoutubeChannelORM", "YoutubeChannels loaded successfully.");
        }

        database.close();

        return memberList;
    }


    public static void updateYoutubeChannel(Context context, YoutubeChannel youtubeChannel) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.update(DB.TABLE_YOUTUBE_CHANNEL, youtubeChannelUpdateToContentValues(youtubeChannel), DB.COL_ID + " = ?", new String[]{String.valueOf(youtubeChannel.getId())});
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.close();
        }

    }

    public static void incrementYoutubeChannel(SQLiteDatabase database, int id) {
        try {
            ContentValues values = new ContentValues();
            values.put(DB.COL_ID, id+1);

            database.update(DB.TABLE_YOUTUBE_CHANNEL, values, DB.COL_ID + " = ?", new String[]{String.valueOf(id)});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void incrementSort(SQLiteDatabase database, int id) {
        try {
            ContentValues values = new ContentValues();
            values.put(DB.COL_SORT, id+1);

            database.update(DB.TABLE_YOUTUBE_CHANNEL, values, DB.COL_SORT + " = ?", new String[]{String.valueOf(id)});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateYoutubeChannels(Context context, List<YoutubeChannel> youtubeChannels) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();
            for(YoutubeChannel youtubeChannel : youtubeChannels) {
                database.update(DB.TABLE_YOUTUBE_CHANNEL, youtubeChannelUpdateToContentValues(youtubeChannel), DB.COL_ID + " = ?", new String[]{String.valueOf(youtubeChannel.getId())});
            }
            database.setTransactionSuccessful();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }
    }


    private static ContentValues youtubeChannelUpdateToContentValues(YoutubeChannel youtubeChannel) {
        return youtubeChannelToContentValues(youtubeChannel, false);
    }

    public static ContentValues youtubeChannelInsertToContentValues(YoutubeChannel youtubeChannel) {
        return youtubeChannelToContentValues(youtubeChannel, true);
    }

    private static ContentValues youtubeChannelToContentValues(YoutubeChannel youtubeChannel, boolean includeId) {
        ContentValues values = new ContentValues();
        if(includeId) values.put(DB.COL_ID, youtubeChannel.getId());
        values.put(DB.COL_SORT, youtubeChannel.getSort());
        values.put(DB.COL_NAME, youtubeChannel.getName());
        values.put(DB.COL_THUMBNAIL, youtubeChannel.getThumbnail());
        values.put(DB.COL_FEED_ID, youtubeChannel.getFeedId());
        values.put(DB.COL_TYPE, youtubeChannel.getType());
        return values;
    }

    private static YoutubeChannel cursorToYoutubeChannel(Cursor cursor) {
        return new YoutubeChannel(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                                 cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)),
                                 cursor.getString(cursor.getColumnIndex(DB.COL_FEED_ID)),
                                 cursor.getInt(cursor.getColumnIndex(DB.COL_TYPE)) );
    }

}