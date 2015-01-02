package com.edaviessmith.consumecontent.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class UserORM {
    static final String TAG = "UserORM";

    public static String SQL_CREATE_TABLE = "CREATE TABLE "+ DB.TABLE_USER +" (" +
            DB.COL_ID 	 + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DB.COL_SORT             + " INTEGER, " +
            DB.COL_NAME   	        + " TEXT, " +
            DB.COL_THUMB 	        + " INTEGER, " +
            DB.COL_THUMBNAILS 	    + " TEXT " +");";

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

                    Log.e(TAG, user.toString());
                    user.setGroups(GroupORM.getUserGroups(database, user.getId()));
                    user.setMediaFeed(MediaFeedORM.getMediaFeeds(database, user.getId()));

                    users.add(user);
                    cursor.moveToNext();
                }

            }
            Log.i(TAG, "Users loaded successfully :"+users.size());
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }

        return users;
    }

    public static User getUser(Context context, int userId, List<Group> groups) {
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

                    user.setGroups(GroupORM.getUserGroups(database, user.getId(), groups));
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

    //Moving main structures to map for faster access
    @Deprecated
    public static List<User> getUsersByGroup(Context context, int groupId) {
        DB databaseHelper = new DB(context);
        List<User> users = new ArrayList<User>();
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {

            Cursor cursor = database.rawQuery("SELECT U.* FROM "+DB.TABLE_USER+" U INNER JOIN "+DB.TABLE_GROUP_USER+" GU " +
                    "ON U."+DB.COL_ID+" = GU."+DB.COL_USER+" WHERE GU."+DB.COL_GROUP+" = "+groupId, null);

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


    public static LinkedHashMap<Integer, User> getUsersByGroupId(Context context, int groupId, List<Group> groups) {
        DB databaseHelper = new DB(context);
        LinkedHashMap<Integer, User> users = new LinkedHashMap<Integer, User>();
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {

            Cursor cursor = database.rawQuery("SELECT U.* FROM "+DB.TABLE_USER+" U INNER JOIN "+DB.TABLE_GROUP_USER+" GU " +
                    "ON U."+DB.COL_ID+" = GU."+DB.COL_USER+" WHERE GU."+DB.COL_GROUP+" = "+groupId +" ORDER BY U."+ DB.ORDER_BY_SORT, null);

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    User user = cursorToUser(cursor);
                    //Log.e(DB.STRIP, user.toString());
                    if(groups != null) user.setGroups(GroupORM.getUserGroups(database, user.getId(), groups));
                    else user.setGroups(GroupORM.getUserGroups(database, user.getId()));
                    user.setMediaFeed(MediaFeedORM.getMediaFeeds(database, user.getId()));

                    users.put(user.getId(), user);
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


    public static List<User> getUsersByMediaFeeds(Context context, List<Integer> mediaFeedIds) {
        DB databaseHelper = new DB(context);
        List<User> users = new ArrayList<User>();
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {

            Cursor cursor = database.rawQuery("SELECT U.* FROM "+DB.TABLE_USER+" AS U INNER JOIN "+DB.TABLE_MEDIA_FEED+" MF " +
                                              "ON (U."+DB.COL_ID+" = MF."+DB.COL_USER+") " +
                                              "WHERE MF."+DB.COL_ID+" IN ("+DB.integerListToString(mediaFeedIds)+")", null) ;

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
            Log.i(TAG, "Users loaded successfully :"+users.size());
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();
            database.close();
        }

        return users;
    }

    public static User saveUser(Context context, User user) {
        DB databaseHelper = new DB(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.beginTransaction();
        try {

            if(DB.isValid(user.getId())) {
                database.update(DB.TABLE_USER, userToContentValues(user, false), DB.COL_ID + " = " + user.getId(), null);
            } else {
                user.setId((int) database.insert(DB.TABLE_USER, null, userToContentValues(user, false)));
            }

            user.setMediaFeed(MediaFeedORM.saveMediaFeeds(database, user.getCastMediaFeed(), user.getRemoved(), user.getId()));
            GroupUserORM.saveUserGroups(database, user.getGroups(), user.getId());

            database.setTransactionSuccessful();

            Log.d(TAG, "User saved with id:" + user.getId());
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }

        user.clearRemoved();

        return user;
    }

    public static  void saveUsers(SQLiteDatabase database, LinkedHashMap<Integer, User> users, List<User> removed, int groupId) {
        if(removed != null) {
            for(User removedUser: removed) {
                if(GroupUserORM.romoveUserFromGroup(database, removedUser.getId(), groupId)) {
                    removeUser(database, removedUser); //User has no groups so remove the user
                }
            }
        }
        saveUsers(database, users);
    }

    private static void removeUser(SQLiteDatabase database, User user) {
        if(DB.isValid(user.getId())) {
            for(int i=0; i< user.getCastMediaFeed().size(); i++) {
                MediaFeedORM.removeMediaFeed(database, user.getCastMediaFeed().valueAt(i));
            }
            database.delete(DB.TABLE_USER, DB.COL_ID + " = "+user.getId(), null);
            Log.d(TAG, "removeUser "+user.getId());
        }
    }


    public static void saveUsers(SQLiteDatabase database, LinkedHashMap<Integer, User> users) {
        //for(int i=0; i< users.size(); i++) {
        for(User user: users.values()) {
            saveUser(database, user);
        }
    }

    public static User saveUser(SQLiteDatabase database, User user) {

        if(DB.isValid(user.getId())) {
            database.update(DB.TABLE_USER, userToContentValues(user, false), DB.COL_ID + " = " + user.getId(), null);
        } else {
            user.setId((int) database.insert(DB.TABLE_USER, null, userToContentValues(user, false)));
        }


        MediaFeedORM.saveMediaFeeds(database, user.getCastMediaFeed(), user.getRemoved(), user.getId());
        GroupUserORM.saveUserGroups(database, user.getGroups(), user.getId());

        return user;
    }

    private static ContentValues userToContentValues(User user, boolean includeId) {
        ContentValues values = new ContentValues();
        if(includeId) values.put(DB.COL_ID, user.getId());
        values.put(DB.COL_SORT, user.getSort());
        values.put(DB.COL_NAME, user.getName());
        values.put(DB.COL_THUMB, user.getThumb());
        values.put(DB.COL_THUMBNAILS, DB.stringListToString(user.getThumbnails()));
        return values;
    }

    private static User cursorToUser(Cursor cursor) {
        return new User(cursor.getInt(cursor.getColumnIndex(DB.COL_ID)),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_SORT)),
                         cursor.getString(cursor.getColumnIndex(DB.COL_NAME)),
                         cursor.getInt(cursor.getColumnIndex(DB.COL_THUMB)),
                         cursor.getString(cursor.getColumnIndex(DB.COL_THUMBNAILS)));
    }


}