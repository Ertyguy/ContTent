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
            DB.COL_THUMBNAIL 	    + " TEXT, " +
            DB.COL_NOTIFICATION		+ " INTEGER " + ");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_USER;


    public static List<User> getUsers(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.query(false, DB.TABLE_USER, null, null, null, null, null, DB.ORDER_BY_SORT, null);
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

        database.close();

        return memberList;
    }

    public static List<User> getNotificationUsers(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();


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

        database.close();

        return memberList;
    }


    public static void updateUser(Context context, User user) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.update(DB.TABLE_USER, userUpdateToContentValues(user), DB.COL_ID + " = ?", new String[]{String.valueOf(user.getId())});
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
                database.update(DB.TABLE_USER, userUpdateToContentValues(user), DB.COL_ID + " = ?", new String[]{String.valueOf(user.getId())});
            }
            database.setTransactionSuccessful();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }
    }


    private static ContentValues userUpdateToContentValues(User user) {
        return userToContentValues(user, false);
    }

    public static ContentValues userInsertToContentValues(User user) {
        return userToContentValues(user, true);
    }

    private static ContentValues userToContentValues(User user, boolean includeId) {
        ContentValues values = new ContentValues();
        if(includeId) values.put(DB.COL_ID, user.getId());
        values.put(DB.COL_SORT, user.getSort());
        values.put(DB.COL_NAME, user.getName());
        values.put(DB.COL_THUMBNAIL, user.getThumbnail());
        values.put(DB.COL_NOTIFICATION, user.isNotification());
        return values;
    }

    private static User cursorToUser(Cursor cursor) {
        return new User(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                         cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                         cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_NOTIFICATION)) == 1);
    }

}