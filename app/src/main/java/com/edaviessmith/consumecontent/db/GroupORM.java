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

        Cursor cursor = database.rawQuery("SELECT G.* FROM "+DB.TABLE_GROUP+" AS G INNER JOIN "+DB.TABLE_GROUP_USER+" GU " +
                "on (G."+DB.COL_ID+" = GU."+DB.COL_GROUP+") " +
                "WHERE GU."+DB.COL_USER+" = "+userId, null) ;
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


    //Add user groups (using all groups as reference for comparison purposes

    public static List<Group> getUserGroups(SQLiteDatabase database, int userId, List<Group> groups) {

        Cursor cursor = database.query(false, DB.TABLE_GROUP_USER, new String[]{DB.COL_GROUP}, DB.COL_USER + " == "+userId, null, null, null, null, null);

        List<Group> userGroups = new ArrayList<Group>();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(DB.COL_GROUP));

                for(Group group: groups){
                    if(id == group.getId()){
                        userGroups.add(group);
                        break;
                    }
                }

                cursor.moveToNext();
            }
            Log.i("UserORM", "Groups loaded successfully for user "+userId);
        }

        return userGroups;

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

                group.setUsers(UserORM.getUsersByGroup(context, group.getId()));

                groupList.add(group);
                cursor.moveToNext();
            }
            Log.i("GroupORM", "Groups loaded "+groupList.size()+" successfully.");
        }

        database.close();

        return groupList;
    }

    public static List<Group> getVisibleGroups(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();


        Cursor cursor = database.query(false, DB.TABLE_GROUP, null, DB.COL_VISIBILITY + " == 1", null, null, null, DB.ORDER_BY_SORT, null);

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

    public static void saveGroups(Context context, List<Group> groups) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        try {
            database.beginTransaction();
            for(Group group : groups) {
                database.update(DB.TABLE_GROUP, groupToContentValues(group, false), DB.COL_ID + " = ?", new String[]{String.valueOf(group.getId())});
            }
            database.setTransactionSuccessful();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }
    }

    public static void saveGroups(SQLiteDatabase database, List<Group> groups) {

        try {
            for(Group group: groups) {

                if (DB.isValid(group.getId())) {
                    database.update(DB.TABLE_GROUP, groupToContentValues(group, false), DB.COL_ID + " = " + group.getId(), null);
                } else {
                    group.setId((int) database.insert(DB.TABLE_GROUP, null, groupToContentValues(group, false)));
                }


                UserORM.saveUsers(database, group.getUsers());

                //AlarmORM.saveAlarms(database, group.getAlarms(), group.getId());

                Log.d(TAG, "Notification saved with id:" + group.getId());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

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