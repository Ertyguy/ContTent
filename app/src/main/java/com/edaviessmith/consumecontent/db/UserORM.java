package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.User;

import java.util.ArrayList;
import java.util.List;

public class UserORM {
    static final String TAG = "UserORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_USER +" (" +
            DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DB.COL_SORT             + " INTEGER, " +
            DB.COL_NAME   	        + " TEXT, " +
            DB.COL_THUMBNAIL 	    + " TEXT " + ");";

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
                    user.setMediaFeed(MediaFeedORM.getMediaFeeds(database, user.getId()));

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

    public static User getUser(Context context, int userId) {
        DB databaseHelper = new DB(context);
        User user = null;
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {
            Cursor cursor = database.query(false, DB.TABLE_USER, null, DB.COL_ID+" = "+userId, null, null, null, DB.ORDER_BY_SORT, null);

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    user = cursorToUser(cursor);

                    user.setGroups(GroupORM.getUserGroups(database, user.getId()));
                    user.setMediaFeed(MediaFeedORM.getMediaFeeds(database, user.getId()));
                }

            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }

        return user;
    }


    public static List<User> getUsersByMediaFeeds(Context context, List<Integer> mediaFeedIds) {
        DB databaseHelper = new DB(context);
        List<User> users = new ArrayList<User>();
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {

            Cursor cursor = database.rawQuery("SELECT U.* FROM "+DB.TABLE_USER+" AS U INNER JOIN "+DB.TABLE_MEDIA_FEED+" MF " +
                                              "ON (U."+DB.COL_ID+" = MF."+DB.COL_USER+") " +
                                              "WHERE MF."+DB.COL_ID+" IN ("+DB.integerListToString(mediaFeedIds)+")", null) ;

            //Cursor cursor = database.query(false, DB.TABLE_USER, null, null, null, null, null, DB.ORDER_BY_SORT, null);

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    User user = cursorToUser(cursor);

                    user.setGroups(GroupORM.getUserGroups(database, user.getId()));
                    //user.setMediaFeed(MediaFeedORM.getMediaFeeds(database, user.getId())); //Not needed

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

    public static void saveUser(Context context, User user) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {


            if(DB.isValid(user.getId())) {
                database.update(DB.TABLE_USER, userToContentValues(user, false), DB.COL_ID + " = " + user.getId(), null);
            } else {
                user.setId((int) database.insert(DB.TABLE_USER, null, userToContentValues(user, false)));
            }

            MediaFeedORM.saveMediaFeeds(database, user.getMediaFeed(), user.getId());
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
        return values;
    }

    private static User cursorToUser(Cursor cursor) {
        return new User(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                         cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                         cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)));
    }

}