package com.edaviessmith.consumecontent.db;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {

    static final String DB_NAME = "consumecontent.db";
    static final int DB_VERSION = 1;

    private static Context context;

    @SuppressWarnings("static-access")
    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GroupORM.SQL_CREATE_TABLE);
        db.execSQL(UserORM.SQL_CREATE_TABLE);
        db.execSQL(GroupUserORM.SQL_CREATE_TABLE);
        db.execSQL(MediaFeedORM.SQL_CREATE_TABLE);
        //db.execSQL(YoutubeChannelORM.SQL_CREATE_TABLE);
        //db.execSQL(TwitterFeedORM.SQL_CREATE_TABLE);
        //db.execSQL(YoutubeFeedORM.SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(GroupORM.SQL_DROP_TABLE);
        db.execSQL(UserORM.SQL_DROP_TABLE);
        db.execSQL(GroupUserORM.SQL_DROP_TABLE);
        db.execSQL(MediaFeedORM.SQL_DROP_TABLE);
        //db.execSQL(YoutubeChannelORM.SQL_DROP_TABLE);
        //db.execSQL(TwitterFeedORM.SQL_DROP_TABLE);
        //db.execSQL(YoutubeFeedORM.SQL_DROP_TABLE);



        if(oldVersion <= 1) {
            //addNewMember(db, 3);
            //addNewMember(db, 23);
        }
        if(oldVersion <= 2) {
            //addNewMember(db, 7);
        }



    }



    ///// Database Tables //////
    static final String TABLE_GROUP = "groups";
    static final String TABLE_USER = "users";
    static final String TABLE_GROUP_USER = "groups_users"; //Many to Many
    static final String TABLE_MEDIA_FEED = "media_feeds";


    public static String ORDER_BY_SORT = DB.COL_SORT + " ASC";
    //// Database Columns /////

    static final String COL_ID = "id";
    static final String COL_SORT = "sort";
    static final String COL_NAME = "name";
    static final String COL_THUMBNAIL = "thumbnail";
    static final String COL_VISIBILITY = "visibility";
    static final String COL_NOTIFICATION = "notification";
    static final String COL_TYPE = "type";
    static final String COL_FEED_ID = "feed_id";
    static final String COL_CHANNEL_HANDLE = "channel_handle";  //ChannelId or TwitterHandle

    //Foreign id keys
    static final String COL_GROUP = "group_id";
    static final String COL_USER = "user_id";




    ///Helper class to view database for debugging

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

}
