package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.GroupUser;

import java.util.List;

public class GroupUserORM {
    static final String TAG = "GroupUserORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_GROUP_USER +" (" +
            DB.COL_GROUP 	        + " INTEGER, "+
            DB.COL_USER             + " INTEGER, " +
            "FOREIGN KEY("+DB.COL_GROUP +") REFERENCES "+DB.TABLE_GROUP+"("+DB.COL_ID+")," +
            "FOREIGN KEY("+DB.COL_USER  +") REFERENCES "+DB.TABLE_USER +"("+DB.COL_ID+")"  + ");";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB.TABLE_GROUP_USER;


    public static void saveUserGroups(SQLiteDatabase database, List<Group> groups, int userId) {
        //clear all groups for user
        database.delete(DB.TABLE_GROUP_USER,  DB.COL_USER + " = "+ userId, null);
        //Log.i(TAG, "removed groupUser "+userId);
        for(Group group : groups) {
            database.insert(DB.TABLE_GROUP_USER, null, groupUserToContentValues(new GroupUser(group.getId(), userId)));
            //Log.i(TAG, "insert groupUser "+group.getId()+" - "+userId);
        }
    }



    //Return true if user no longer has a group
    public static boolean romoveUserFromGroup(SQLiteDatabase database, int userId, int groupId) {
        //remove user from group
        database.delete(DB.TABLE_GROUP_USER,  DB.COL_USER + " = "+ userId + " AND " + DB.COL_GROUP + " = "+groupId, null);
        //Log.i(TAG, "removed groupUser "+userId);

        Cursor cursor = database.query(false, DB.TABLE_GROUP_USER, null, DB.COL_USER+" = "+userId, null, null, null, null, null);
        return cursor.getCount() > 0;


    }

    private static ContentValues groupUserToContentValues(GroupUser group) {
        ContentValues values = new ContentValues();
        values.put(DB.COL_GROUP, group.getGroupId());
        values.put(DB.COL_USER, group.getUserId());
        return values;
    }

    private static GroupUser cursorToGroupUser(Cursor cursor) {
        return new GroupUser(cursor.getInt(cursor.getColumnIndex(DB.COL_GROUP)),
                cursor.getInt(cursor.getColumnIndex(DB.COL_USER)));
    }





    /*public static List<Group> getGroups(Context context) {

        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.query(false, DB.TABLE_GROUP_USER, null, null, null, null, null, null, null);
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


        Cursor cursor = database.query(false, DB.TABLE_GROUP, null, DB.COL_VISIBILITY + " == ?", new String[]{String.valueOf(1)}, null, null, null, null);

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
*/


}