package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.util.Var;

import java.util.ArrayList;
import java.util.List;

public class UserORM {
    static final String TAG = "UserORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_USER +" (" +
            DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DB.COL_SORT             + " INTEGER, " +
            DB.COL_NAME   	        + " TEXT, " +
            DB.COL_THUMBNAIL 	    + " TEXT, " +
            DB.COL_YOUTBUE_CHANNEL	+ " INTEGER, " +
            DB.COL_TWITTER_FEED		+ " INTEGER, " +
            DB.COL_NOTIFICATION		+ " INTEGER " + ");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_USER;


    public static List<User> getUsers(Context context) {
        DB databaseHelper = new DB(context);
        List<User> users = new ArrayList<User>();
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {
            Cursor cursor = database.query(false, DB.TABLE_USER, null, null, null, null, null, DB.ORDER_BY_SORT, null);

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    User user = cursorToUser(cursor);

                    user.setGroups(GroupORM.getUserGroups(database, user.getId()));

                    user.setYoutubeChannel((YoutubeChannelORM.getYoutubeChannel(database, user.getYoutubeChannel().getId())));
                    user.setTwitterFeed(TwitterFeedORM.getTwitterFeed(database, user.getTwitterFeed().getId()));

                    users.add(user);
                    cursor.moveToNext();
                }

            }
            Log.i("UserORM", "Users loaded successfully :"+users.size());
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }

        return users;
    }

    public static List<User> getNotificationUsers(Context context) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {

            Cursor cursor = database.query(false, DB.TABLE_USER, null, DB.COL_NOTIFICATION + " == ?", new String[]{String.valueOf(1)}, null, null, DB.ORDER_BY_SORT, null);

            Log.i("UserORM", "Loaded " + cursor.getCount() + " Users...");
            List<User> memberList = new ArrayList<User>();

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    User member = cursorToUser(cursor);
                    memberList.add(member);
                    cursor.moveToNext();
                }
                Log.i("UserORM", "Users loaded successfully.");
            }
        }catch (Exception e) {
            database.endTransaction();
            e.printStackTrace();
        }

        return null;
    }


    public static void updateUser(Context context, User user) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.update(DB.TABLE_USER, userToContentValues(user, false), DB.COL_ID + " = ?", new String[]{String.valueOf(user.getId())});
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.close();
        }

    }

    public static void incrementUser(SQLiteDatabase database, int id) {
        try {
            ContentValues values = new ContentValues();
            values.put(DB.COL_ID, id+1);

            database.update(DB.TABLE_USER, values, DB.COL_ID + " = ?", new String[]{String.valueOf(id)});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void incrementSort(SQLiteDatabase database, int id) {
        try {
            ContentValues values = new ContentValues();
            values.put(DB.COL_SORT, id+1);

            database.update(DB.TABLE_USER, values, DB.COL_SORT + " = ?", new String[]{String.valueOf(id)});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUsers(Context context, List<User> users) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();
            for(User user : users) {
                database.update(DB.TABLE_USER, userToContentValues(user, false), DB.COL_ID + " = ?", new String[]{String.valueOf(user.getId())});
            }
            database.setTransactionSuccessful();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }
    }


    public static void saveUser(Context context, User user) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {

            user.getYoutubeChannel().setId(YoutubeChannelORM.saveYoutubeChannel(database, user.getYoutubeChannel()));
            user.getTwitterFeed().setId(TwitterFeedORM.saveTwitterFeed(database, user.getTwitterFeed()));

            if(Var.isValid(user.getId())) {
                database.update(DB.TABLE_USER, userToContentValues(user, false), DB.COL_ID + " = " + user.getId(), null);
            } else {
                user.setId((int) database.insert(DB.TABLE_USER, null, userToContentValues(user, false)));
            }

            GroupUserORM.saveUserGroups(database, user.getGroups(), user.getId());

            database.setTransactionSuccessful();

            Log.d(TAG, "User saved with id:" + user.getId());
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }

    }


    private static ContentValues userToContentValues(User user, boolean includeId) {
        ContentValues values = new ContentValues();
        if(includeId) values.put(DB.COL_ID, user.getId());
        values.put(DB.COL_SORT, user.getSort());
        values.put(DB.COL_NAME, user.getName());
        values.put(DB.COL_THUMBNAIL, user.getThumbnail());
        values.put(DB.COL_YOUTBUE_CHANNEL, user.getYoutubeChannel().getId());
        values.put(DB.COL_TWITTER_FEED, user.getTwitterFeed().getId());
        values.put(DB.COL_NOTIFICATION, user.isNotification());
        return values;
    }

    private static User cursorToUser(Cursor cursor) {
        return new User(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                         cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                         cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_YOUTBUE_CHANNEL)),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_TWITTER_FEED)),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_NOTIFICATION)) == 1);
    }

}