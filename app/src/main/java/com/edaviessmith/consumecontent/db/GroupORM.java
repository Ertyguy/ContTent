package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupORM {
    static final String TAG = "GroupORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_GROUP +" (" +
            DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DB.COL_SORT             + " INTEGER, " +
            DB.COL_NAME   	        + " TEXT, " +
            DB.COL_THUMBNAIL 	    + " TEXT, " +
            DB.COL_VISIBILITY		+ " INTEGER " + ");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_GROUP;


    public static List<Group> getUserGroups(SQLiteDatabase database, int userId) {

        Cursor cursor = database.rawQuery("SELECT G.* FROM "+DB.TABLE_GROUP+" AS G INNER JOIN "+DB.TABLE_GROUP_USER+" GU on (G."+DB.COL_ID+" = GU."+DB.COL_GROUP+")", null) ;
        List<Group> groups = new ArrayList<Group>();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                groups.add(cursorToGroup(cursor));
                cursor.moveToNext();
            }
            Log.i("UserORM", "Groups loaded successfully for user "+userId);
        }

        return groups;
    }


    public static List<Group> getGroups(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.query(false, DB.TABLE_GROUP, null, null, null, null, null, DB.ORDER_BY_SORT, null);
        List<Group> groupList = new ArrayList<Group>();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Group group = cursorToGroup(cursor);
                groupList.add(group);
                cursor.moveToNext();
            }
            Log.i("GroupORM", "Groups loaded successfully.");
        }

        database.close();

        return groupList;
    }

    public static List<Group> getVisibleGroups(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();


        Cursor cursor = database.query(false, DB.TABLE_GROUP, null, DB.COL_VISIBILITY + " == ?", new String[]{String.valueOf(1)}, null, null, DB.ORDER_BY_SORT, null);

        Log.i("GroupORM", "Loaded " + cursor.getCount() + " Groups...");
        List<Group> groupList = new ArrayList<Group>();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Group group = cursorToGroup(cursor);
                groupList.add(group);
                cursor.moveToNext();
            }
            Log.i("GroupORM", "Groups loaded successfully.");
        }

        database.close();

        return groupList;
    }


    public static void insertGroup(Context context, Group group) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        try {
            database.insert(DB.TABLE_GROUP, null, groupToContentValues(group, false));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateGroup(Context context, Group group) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.update(DB.TABLE_GROUP, groupUpdateToContentValues(group), DB.COL_ID + " = ?", new String[]{String.valueOf(group.getId())});
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.close();
        }

    }

    public static void incrementGroup(SQLiteDatabase database, int id) {
        try {
            ContentValues values = new ContentValues();
            values.put(DB.COL_ID, id+1);

            database.update(DB.TABLE_GROUP, values, DB.COL_ID + " = ?", new String[]{String.valueOf(id)});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void incrementSort(SQLiteDatabase database, int id) {
        try {
            ContentValues values = new ContentValues();
            values.put(DB.COL_SORT, id+1);

            database.update(DB.TABLE_GROUP, values, DB.COL_SORT + " = ?", new String[]{String.valueOf(id)});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateGroups(Context context, List<Group> groups) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();
            for(Group group : groups) {
                database.update(DB.TABLE_GROUP, groupUpdateToContentValues(group), DB.COL_ID + " = ?", new String[]{String.valueOf(group.getId())});
            }
            database.setTransactionSuccessful();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }
    }


    private static ContentValues groupUpdateToContentValues(Group group) {
        return groupToContentValues(group, false);
    }

    public static ContentValues groupInsertToContentValues(Group group) {
        return groupToContentValues(group, true);
    }

    private static ContentValues groupToContentValues(Group group, boolean includeId) {
        ContentValues values = new ContentValues();
        if(includeId) values.put(DB.COL_ID, group.getId());
        values.put(DB.COL_SORT, group.getSort());
        values.put(DB.COL_NAME, group.getName());
        values.put(DB.COL_THUMBNAIL, group.getThumbnail());
        values.put(DB.COL_VISIBILITY, group.isVisible());
        return values;
    }

    private static Group cursorToGroup(Cursor cursor) {
        return new Group(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                         cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                         cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAIL)),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_VISIBILITY)) == 1);
    }

}