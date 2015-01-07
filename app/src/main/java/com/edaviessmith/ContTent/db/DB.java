package com.edaviessmith.contTent.db;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

import com.edaviessmith.contTent.data.Alarm;
import com.edaviessmith.contTent.data.Group;
import com.edaviessmith.contTent.data.MediaFeed;
import com.edaviessmith.contTent.data.Notification;
import com.edaviessmith.contTent.data.TwitterFeed;
import com.edaviessmith.contTent.data.User;
import com.edaviessmith.contTent.data.YoutubeFeed;
import com.edaviessmith.contTent.util.Var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class DB extends SQLiteOpenHelper {

    static final String DB_NAME = "contTent.db";
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
        db.execSQL(YoutubeItemORM.SQL_CREATE_TABLE);
        db.execSQL(TwitterItemORM.SQL_CREATE_TABLE);
        db.execSQL(NotificationORM.SQL_CREATE_TABLE);
        db.execSQL(AlarmORM.SQL_CREATE_TABLE);

        addDefaultNotifications(db);
        addDefaultGroupsUsersMediaFeeds(db);
    }

    private void addDefaultGroupsUsersMediaFeeds(SQLiteDatabase db) {

        final Group mindcrackGroup = new Group(0, "Mindcrack", "https://yt3.ggpht.com/-9QlCRBfW1nI/AAAAAAAAAAI/AAAAAAAAAAA/O3hR1mPFSDw/s100-c-k-no/photo.jpg", true);
        final Group roosterTeethGroup = new Group(1, "Rooster Teeth", "https://lh6.googleusercontent.com/-hddEYyXVeZM/AAAAAAAAAAI/AAAAAAAAAAA/ghwEL1-FHdE/photo.jpg", true);

        mindcrackGroup.setUsers(new LinkedHashMap<Integer, User>() {{
                put(0, new User("Adlingtont", "https://lh4.googleusercontent.com/-6DIByMHfFbw/AAAAAAAAAAI/AAAAAAAAAAA/GMmF_KtKbtQ/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/ecGJWrXgBGM/default.jpg", "UCfhM3yJ8a_sAJj0qOKb40gw", "UUfhM3yJ8a_sAJj0qOKb40gw", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/xc4J9kUPdk8/default.jpg", "UCfhM3yJ8a_sAJj0qOKb40gw", "LLfhM3yJ8a_sAJj0qOKb40gw", 0));
                    put(2, new YoutubeFeed("Favorites", "https://i.ytimg.com/vi/J5Vr9Rs8fBs/default.jpg", "UCfhM3yJ8a_sAJj0qOKb40gw", "FLfhM3yJ8a_sAJj0qOKb40gw", 0));
                    put(3, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/UNf-hhGP6rQ/default.jpg", "UCfhM3yJ8a_sAJj0qOKb40gw", null, 1));
                    put(4, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/442366472455610369/dlwVd39Y_bigger.png", "@adlingtont", "16713126", 2, "adlingtont"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(1, new User("AnderZEL", "https://lh3.googleusercontent.com/-3PhwKIB2SZQ/AAAAAAAAAAI/AAAAAAAAAAA/aUoAYV_j-iI/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/LHY08mBxps8/default.jpg", "UC-_VTaWqRsZ1nzZLHQIGwQA", "UU-_VTaWqRsZ1nzZLHQIGwQA", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/EMCrZdNM2tM/default.jpg", "UC-_VTaWqRsZ1nzZLHQIGwQA", "LL-_VTaWqRsZ1nzZLHQIGwQA", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/piKoWnhGxSs/default.jpg", "UC-_VTaWqRsZ1nzZLHQIGwQA", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/515621731638448128/8N9NTN_x_bigger.jpeg", "@FansOfAnderzel", "968758723", 2, "null"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(2, new User("Arkas", "https://lh6.googleusercontent.com/-Tef2i7GdFqQ/AAAAAAAAAAI/AAAAAAAAAAA/7MuQK1LEUq8/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/RmdNI4gzA8k/default.jpg", "UCStPXwuYhdUu-B6fkVgi3vQ", "UUStPXwuYhdUu-B6fkVgi3vQ", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/PSZxmZmBfnU/default.jpg", "UCStPXwuYhdUu-B6fkVgi3vQ", "LLStPXwuYhdUu-B6fkVgi3vQ", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/RmdNI4gzA8k/default.jpg", "UCStPXwuYhdUu-B6fkVgi3vQ", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/470678634835894272/vBwfLIDw_bigger.jpeg", "@MCArkas", "361982728", 2, "Arkas"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(3, new User("Aureylian", "https://lh6.googleusercontent.com/-M0gttE6PTjI/AAAAAAAAAAI/AAAAAAAAAAA/7mbiZA77004/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/pNBkFXF-b44/default.jpg", "UCM2FHDmMP92caH9aK7RwEdw", "UUM2FHDmMP92caH9aK7RwEdw", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/lkL6X4I2BnY/default.jpg", "UCM2FHDmMP92caH9aK7RwEdw", "LLM2FHDmMP92caH9aK7RwEdw", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/Z7qJWJOL-Ro/default.jpg", "UCM2FHDmMP92caH9aK7RwEdw", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/540263382360678400/ptoE2tXr_bigger.png", "@aureylian", "206263351", 2, "AurELFian"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(4, new User("AvidyaZen", "https://lh4.googleusercontent.com/-b4Bsg4yjL-E/AAAAAAAAAAI/AAAAAAAAAAA/6bNDEdr1ftk/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/qB5uE5hTLjc/default.jpg", "UCDREKsabG-MOTPxmJOHctEw", "UUDREKsabG-MOTPxmJOHctEw", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/YKvNreOw0v4/default.jpg", "UCDREKsabG-MOTPxmJOHctEw", "LLDREKsabG-MOTPxmJOHctEw", 0));
                    put(2, new YoutubeFeed("Favorites", "https://i.ytimg.com/vi/nGeKSiCQkPw/default.jpg", "UCDREKsabG-MOTPxmJOHctEw", "FLDREKsabG-MOTPxmJOHctEw", 0));
                    put(3, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/HZBrZ8yjmB4/default.jpg", "UCDREKsabG-MOTPxmJOHctEw", null, 1));
                    put(4, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/486919200808521728/HODL3aDI_bigger.png", "@AvidyaZEN", "194725449", 2, "AvidyaZEN"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(5, new User("BdoubleO100", "https://lh3.googleusercontent.com/-N9bzILzOWE8/AAAAAAAAAAI/AAAAAAAAAAA/tE8mBnO9E-M/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/a5TMGHUiurY/default.jpg", "UClu2e7S8atp6tG2galK9hgg", "UUlu2e7S8atp6tG2galK9hgg", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/Yvy5FMebvXM/default.jpg", "UClu2e7S8atp6tG2galK9hgg", "LLlu2e7S8atp6tG2galK9hgg", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/NUmyKZ0hvnE/default.jpg", "UClu2e7S8atp6tG2galK9hgg", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/423583343284744192/TFc8UqcH_bigger.png", "@BdoubleO100", "316643326", 2, "BdoubleO100"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(6, new User("BlameTheController", "https://lh3.googleusercontent.com/-MHIPpVzLK7U/AAAAAAAAAAI/AAAAAAAAAAA/XHj6X59K2ko/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/mpPUQesvTxk/default.jpg", "UCmSwqv2aPbuOGiuii2TeaLQ", "UUmSwqv2aPbuOGiuii2TeaLQ", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/cZWsW1a0aNc/default.jpg", "UCmSwqv2aPbuOGiuii2TeaLQ", "LLmSwqv2aPbuOGiuii2TeaLQ", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/ZDxpLtIKcAo/default.jpg", "UCmSwqv2aPbuOGiuii2TeaLQ", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/378800000663562362/193d566e333715edcb2fdddf72bef843_bigger.png", "@BlameTC", "335520695", 2, "Blame the Controller"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(7, new User("Coestar", "https://lh6.googleusercontent.com/-3KtWXObWKRs/AAAAAAAAAAI/AAAAAAAAAAA/XYDY6AF-Yb8/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/Hfzds05yO1Q/default.jpg", "UCf-p8DeUzTqmsGW1tFOArTg", "UUf-p8DeUzTqmsGW1tFOArTg", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/pNBkFXF-b44/default.jpg", "UCf-p8DeUzTqmsGW1tFOArTg", "LLf-p8DeUzTqmsGW1tFOArTg", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/Wv_dvHSAL7A/default.jpg", "UCf-p8DeUzTqmsGW1tFOArTg", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/539256314057076736/KuMwKj9o_bigger.jpeg", "@Coestar", "199122609", 2, "Coestar"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(8, new User("Docm77", "https://lh5.googleusercontent.com/-OaEQxtIwH7M/AAAAAAAAAAI/AAAAAAAAAAA/cS5JjuIepvQ/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/dwlHo0rYyQg/default.jpg", "UC4O9HKe9Jt5yAhKuNv3LXpQ", "UU4O9HKe9Jt5yAhKuNv3LXpQ", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/C_oztQO7agA/default.jpg", "UC4O9HKe9Jt5yAhKuNv3LXpQ", "LL4O9HKe9Jt5yAhKuNv3LXpQ", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/dwlHo0rYyQg/default.jpg", "UC4O9HKe9Jt5yAhKuNv3LXpQ", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/442026641565294592/jn1GADtR_bigger.png", "@docm77", "272137584", 2, "Docm77´s Minecraft"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(9, new User("EthosLab", "https://lh4.googleusercontent.com/-3aReJeKgmEc/AAAAAAAAAAI/AAAAAAAAAAA/eiDEfktVcxI/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/jjJdk1rmuFs/default.jpg", "UCFKDEp9si4RmHFWJW1vYsMA", "UUFKDEp9si4RmHFWJW1vYsMA", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/pImNwPI5NXk/default.jpg", "UCFKDEp9si4RmHFWJW1vYsMA", "LLFKDEp9si4RmHFWJW1vYsMA", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/jjJdk1rmuFs/default.jpg", "UCFKDEp9si4RmHFWJW1vYsMA", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/1653150513/EthoSmall_bigger.png", "@EthoLP", "419232978", 2, "Etho"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(10, new User("Generikb", "https://lh6.googleusercontent.com/-YzwncYuIejo/AAAAAAAAAAI/AAAAAAAAAAA/lmWTWrVXTbA/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/R-pEWp8F_ic/default.jpg", "UCJTWU5K7kl9EE109HBeoldA", "UUJTWU5K7kl9EE109HBeoldA", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/dzcSs9CON60/default.jpg", "UCJTWU5K7kl9EE109HBeoldA", "LLJTWU5K7kl9EE109HBeoldA", 0));
                    put(2, new YoutubeFeed("Favorites", "https://i.ytimg.com/vi/XDjWd5HFdtI/default.jpg", "UCJTWU5K7kl9EE109HBeoldA", "FLJTWU5K7kl9EE109HBeoldA", 0));
                    put(3, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/R-pEWp8F_ic/default.jpg", "UCJTWU5K7kl9EE109HBeoldA", null, 1));
                    put(4, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/1670119045/generikb_bigger.jpg", "@generikb", "30881244", 2, "Generikb"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(11, new User("GuudeBoulderfist", "https://lh3.googleusercontent.com/-POcLZo9ZxFw/AAAAAAAAAAI/AAAAAAAAAAA/EQO9wfLCCMw/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/VBxvxQXpXB4/default.jpg", "UCAxBpbVbSXT2wsCwZfrIIVg", "UUAxBpbVbSXT2wsCwZfrIIVg", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/aXJD_7xtVLk/default.jpg", "UCAxBpbVbSXT2wsCwZfrIIVg", "LLAxBpbVbSXT2wsCwZfrIIVg", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/VBxvxQXpXB4/default.jpg", "UCAxBpbVbSXT2wsCwZfrIIVg", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/476553119854120960/qSGzue8H_bigger.png", "@GuudeLP", "216762382", 2, "Jason"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(12, new User("JSano19", "https://lh5.googleusercontent.com/-Wguby1sxRo8/AAAAAAAAAAI/AAAAAAAAAAA/uHivXgmQzgQ/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/Jigy_CcHnGM/default.jpg", "UCJbgutTlUYZyUpfjTyYLWdw", "UUJbgutTlUYZyUpfjTyYLWdw", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/SKtdFFKVTPs/default.jpg", "UCJbgutTlUYZyUpfjTyYLWdw", "LLJbgutTlUYZyUpfjTyYLWdw", 0));
                    put(2, new YoutubeFeed("Favorites", "https://i.ytimg.com/vi/GvmeQ5f3c5I/default.jpg", "UCJbgutTlUYZyUpfjTyYLWdw", "FLJbgutTlUYZyUpfjTyYLWdw", 0));
                    put(3, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/Jigy_CcHnGM/default.jpg", "UCJbgutTlUYZyUpfjTyYLWdw", null, 1));
                    put(4, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/378800000396224639/b9955a7d145861696ad542bf1eb0b7c6_bigger.jpeg", "@jsano19", "257485803", 2, "JSano19"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(13, new User("kurtjmac", "https://i.ytimg.com/i/1Un5592U9mFx5n6j2HyXow/1.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/_2urINKZ-TM/default.jpg", "UC1Un5592U9mFx5n6j2HyXow", "UU1Un5592U9mFx5n6j2HyXow", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/YAYtLadGSv8/default.jpg", "UC1Un5592U9mFx5n6j2HyXow", "LL1Un5592U9mFx5n6j2HyXow", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/_2urINKZ-TM/default.jpg", "UC1Un5592U9mFx5n6j2HyXow", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/519949353440391168/yVvuCM-h_bigger.png", "@kurtjmac", "21139474", 2, "KurtJMac"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(14, new User("SuperMCGamer", "https://lh4.googleusercontent.com/-h0IjVsxoBxI/AAAAAAAAAAI/AAAAAAAAAAA/m6WIj5tVyoA/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/CJ3Qc3ktIKY/default.jpg", "UC6MqXe9o-xBQHzE-DmebTRw", "UU6MqXe9o-xBQHzE-DmebTRw", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/ixXF9U9BU7E/default.jpg", "UC6MqXe9o-xBQHzE-DmebTRw", "LL6MqXe9o-xBQHzE-DmebTRw", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/CJ3Qc3ktIKY/default.jpg", "UC6MqXe9o-xBQHzE-DmebTRw", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/527520112752857088/JOQ8n4V1_bigger.png", "@SuperMCGamer", "15510977", 2, "MC"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(15, new User("Mhykol", "https://lh4.googleusercontent.com/-ooPP3-oCLz0/AAAAAAAAAAI/AAAAAAAAAAA/SpHy0x_F82U/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/4zOxjVlwhrU/default.jpg", "UCD2JcdggW1j72WBgbuwrt-g", "UUD2JcdggW1j72WBgbuwrt-g", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/PmJQZgfB9cs/default.jpg", "UCD2JcdggW1j72WBgbuwrt-g", "LLD2JcdggW1j72WBgbuwrt-g", 0));
                    put(2, new YoutubeFeed("Favorites", "https://i.ytimg.com/vi/W3hxYYj5gl0/default.jpg", "UCD2JcdggW1j72WBgbuwrt-g", "FLD2JcdggW1j72WBgbuwrt-g", 0));
                    put(3, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/4zOxjVlwhrU/default.jpg", "UCD2JcdggW1j72WBgbuwrt-g", null, 1));
                    put(4, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/510069499328856064/_unu0wRH_bigger.jpeg", "@mhykol", "9929402", 2, "Mhykol"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(16, new User("MillBeeful", "https://lh3.googleusercontent.com/-cf0Msq7uSxs/AAAAAAAAAAI/AAAAAAAAAAA/IrQj2h_7CP4/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/HPzaKbS3tAU/default.jpg", "UCIsp57CkuqoPQyHP2B2Y5NA", "UUIsp57CkuqoPQyHP2B2Y5NA", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/Jtw43KKEOzY/default.jpg", "UCIsp57CkuqoPQyHP2B2Y5NA", "LLIsp57CkuqoPQyHP2B2Y5NA", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/HPzaKbS3tAU/default.jpg", "UCIsp57CkuqoPQyHP2B2Y5NA", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/497566171420299264/MesF6mdy_bigger.jpeg", "@Millbeeful", "420887788", 2, "Millbee"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(17, new User("nebris88", "https://lh6.googleusercontent.com/-E0aFr6lwmL8/AAAAAAAAAAI/AAAAAAAAAAA/I2RkTWpTfx4/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/BRBPNDOXP1E/default.jpg", "UCChaPGDM0d6YQv2yyVoVfGg", "UUChaPGDM0d6YQv2yyVoVfGg", 0));
                    put(1, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/BRBPNDOXP1E/default.jpg", "UCChaPGDM0d6YQv2yyVoVfGg", null, 1));
                    put(2, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/378800000029573995/3d446834faf022631129a3ebae4575dc_bigger.jpeg", "@nebris88", "1538812074", 2, "cheatynebris"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(18, new User("OMGchad", "https://lh5.googleusercontent.com/-azPRf6DLryY/AAAAAAAAAAI/AAAAAAAAAAA/CrYU362BQuQ/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/Kzeah4_tHx0/default.jpg", "UCaEj4agLSPPKuAS9DJX9Ffw", "UUaEj4agLSPPKuAS9DJX9Ffw", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/VAJ-j64ZxrQ/default.jpg", "UCaEj4agLSPPKuAS9DJX9Ffw", "LLaEj4agLSPPKuAS9DJX9Ffw", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/Kzeah4_tHx0/default.jpg", "UCaEj4agLSPPKuAS9DJX9Ffw", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/520093059543859200/Jkf2MJJ-_bigger.png", "@OMGchad", "14807093", 2, "Chad Johnson"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(19, new User("pakratt13", "https://lh3.googleusercontent.com/-0OKLzzz0Vss/AAAAAAAAAAI/AAAAAAAAAAA/6n99MlwE6iw/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/dt0fYT0uA18/default.jpg", "UCEpnkm5LLXPyDo8mw8_w3MA", "UUEpnkm5LLXPyDo8mw8_w3MA", 0));
                    put(1, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/dt0fYT0uA18/default.jpg", "UCEpnkm5LLXPyDo8mw8_w3MA", null, 1));
                    put(2, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/1764476666/Itpp_bigger.JPG", "@pakratt0013", "216115244", 2, "Pakratt0013"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(20, new User("paulsoaresjr", "https://lh5.googleusercontent.com/-lHQGaB6kiOg/AAAAAAAAAAI/AAAAAAAAAAA/W7sE98_H3o0/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/0oVX-Z1UpkY/default.jpg", "UCP6f9x4iXk3LH8Q1sqJmYPQ", "UUP6f9x4iXk3LH8Q1sqJmYPQ", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/QJP8wFqB10M/default.jpg", "UCP6f9x4iXk3LH8Q1sqJmYPQ", "LLP6f9x4iXk3LH8Q1sqJmYPQ", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/0oVX-Z1UpkY/default.jpg", "UCP6f9x4iXk3LH8Q1sqJmYPQ", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/531476889521516544/ToeMCgBa_bigger.png", "@paulsoaresjr", "19905877", 2, "Paul Soares Jr"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(21, new User("PauseUnpause", "https://lh4.googleusercontent.com/-OTSWHHhwxD4/AAAAAAAAAAI/AAAAAAAAAAA/a6ROK57bpf4/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/h9eSCWXWeEE/default.jpg", "UCcoMCX6scirNav1fEMbjaPA", "UUcoMCX6scirNav1fEMbjaPA", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/gNDvpPsYDNM/default.jpg", "UCcoMCX6scirNav1fEMbjaPA", "LLcoMCX6scirNav1fEMbjaPA", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/h9eSCWXWeEE/default.jpg", "UCcoMCX6scirNav1fEMbjaPA", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/1590099359/pauseicon_bigger.png", "@PauseUnpauses", "316769147", 2, "Alex"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(22, new User("Pyropuncher", "https://lh6.googleusercontent.com/-MffsLabyqh4/AAAAAAAAAAI/AAAAAAAAAAA/LI3CrqqHePY/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/ZGImt8nd6V8/default.jpg", "UC6pSdcEaOjeUyVl-Vivp-dQ", "UU6pSdcEaOjeUyVl-Vivp-dQ", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/Dpkz3W1Qzcs/default.jpg", "UC6pSdcEaOjeUyVl-Vivp-dQ", "LL6pSdcEaOjeUyVl-Vivp-dQ", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/ZGImt8nd6V8/default.jpg", "UC6pSdcEaOjeUyVl-Vivp-dQ", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/1791131047/312744_10150371697001285_278053126284_10198063_1069405278_n_bigger.jpg", "@Pyrao", "71640497", 2, "Pyro"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(23, new User("SethBling", "https://lh3.googleusercontent.com/-cQbNeIk8Nqc/AAAAAAAAAAI/AAAAAAAAAAA/Ec-1lYSgtw8/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/mybB1v19HSE/default.jpg", "UC8aG3LDTDwNR1UQhSn9uVrw", "UU8aG3LDTDwNR1UQhSn9uVrw", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/ksmuaZfByjA/default.jpg", "UC8aG3LDTDwNR1UQhSn9uVrw", "LL8aG3LDTDwNR1UQhSn9uVrw", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/mybB1v19HSE/default.jpg", "UC8aG3LDTDwNR1UQhSn9uVrw", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/3059054992/bed4e6b413a20b9388a7a1368278c265_bigger.png", "@SethBling", "371921181", 2, "SethBling"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(24, new User("sevadus", "https://lh6.googleusercontent.com/-ORYc48HFBvE/AAAAAAAAAAI/AAAAAAAAAAA/Dj4P0Heki-g/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/hivVGtm5Avc/default.jpg", "UCfiMdLmi7v8tz9Kc5QKUCJg", "UUfiMdLmi7v8tz9Kc5QKUCJg", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/AdfFnTt2UT0/default.jpg", "UCfiMdLmi7v8tz9Kc5QKUCJg", "LLfiMdLmi7v8tz9Kc5QKUCJg", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/hivVGtm5Avc/default.jpg", "UCfiMdLmi7v8tz9Kc5QKUCJg", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/412221113796546560/KNDYkw0y_bigger.png", "@Sevadus", "398826957", 2, "Matt Zagursky"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(25, new User("thejims", "https://lh4.googleusercontent.com/-ZRwVe5txQEg/AAAAAAAAAAI/AAAAAAAAAAA/xHnSN-pAtWU/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/IBZq73qSOUE/default.jpg", "UCjyZBSE0KfXinkrlOG9uGbQ", "UUjyZBSE0KfXinkrlOG9uGbQ", 0));
                    put(1, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/IBZq73qSOUE/default.jpg", "UCjyZBSE0KfXinkrlOG9uGbQ", null, 1));
                    put(2, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/2383934359/hdg48x7dnj6hqt2q2acd_bigger.jpeg", "@thejimsLP", "631635460", 2, "thejims"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(26, new User("Vechs", "https://lh3.googleusercontent.com/-uqa8pdR_2DE/AAAAAAAAAAI/AAAAAAAAAAA/74l9PTPDhUo/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/fllWB4EjhpQ/default.jpg", "UCOy5LOrYA5wVg5F_jAWtwfw", "UUOy5LOrYA5wVg5F_jAWtwfw", 0));
                    put(1, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/fllWB4EjhpQ/default.jpg", "UCOy5LOrYA5wVg5F_jAWtwfw", null, 1));
                    put(2, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/473728115038900224/CgNKDIcs_bigger.jpeg", "@Vechs", "391797352", 2, "Vechs"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(27, new User("VintageBeef", "https://lh4.googleusercontent.com/-CG6foIgF79M/AAAAAAAAAAI/AAAAAAAAAAA/fhgkswkgvH8/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/C6ugmR2_cPQ/default.jpg", "UCu17Sme-KE87ca9OTzP0p7g", "UUu17Sme-KE87ca9OTzP0p7g", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/r_iTf0Z1hNs/default.jpg", "UCu17Sme-KE87ca9OTzP0p7g", "LLu17Sme-KE87ca9OTzP0p7g", 0));
                    put(2, new YoutubeFeed("Favorites", "https://i.ytimg.com/vi/kGQxu4STarA/default.jpg", "UCu17Sme-KE87ca9OTzP0p7g", "FLu17Sme-KE87ca9OTzP0p7g", 0));
                    put(3, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/C6ugmR2_cPQ/default.jpg", "UCu17Sme-KE87ca9OTzP0p7g", null, 1));
                    put(4, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/3066173662/c5ee5dd17d9beae8b3449ddb19e55078_bigger.png", "@VintageBeefLP", "340656318", 2, "VintageBeef"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(28, new User("W92Baj", "https://lh4.googleusercontent.com/-heZJiap7qVo/AAAAAAAAAAI/AAAAAAAAAAA/Swd4LZyRbfw/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/b1RyEBKQbOg/default.jpg", "UCB3hoa-iGe3FVLBw1PmRRug", "UUB3hoa-iGe3FVLBw1PmRRug", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/-RLTFvJ-_xg/default.jpg", "UCB3hoa-iGe3FVLBw1PmRRug", "LLB3hoa-iGe3FVLBw1PmRRug", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/b1RyEBKQbOg/default.jpg", "UCB3hoa-iGe3FVLBw1PmRRug", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/378800000817908993/dcffb54329dfe1df518262fa96f27bcc_bigger.png", "@W92Baj", "397260132", 2, "Baj Fawkes"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(29, new User("Zisteau", "https://lh4.googleusercontent.com/-aU4pDSgcvcs/AAAAAAAAAAI/AAAAAAAAAAA/37-1QoArNUc/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/4UVGzyVBF7g/default.jpg", "UCewxof_QqDdqVdXY1BaDtqQ", "UUewxof_QqDdqVdXY1BaDtqQ", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/J7jLYJ-J_Ng/default.jpg", "UCewxof_QqDdqVdXY1BaDtqQ", "LLewxof_QqDdqVdXY1BaDtqQ", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/4UVGzyVBF7g/default.jpg", "UCewxof_QqDdqVdXY1BaDtqQ", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/3618573645/1800ff11331376f2239de0ec4aa9f3b0_bigger.jpeg", "@Zisteau", "365133030", 2, "Zisteau"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));
                put(30, new User("MindCrackNetwork", "https://lh5.googleusercontent.com/-9QlCRBfW1nI/AAAAAAAAAAI/AAAAAAAAAAA/O3hR1mPFSDw/photo.jpg", new SparseArray<MediaFeed>() {{
                    put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/9wQKf7b5gSI/default.jpg", "UCAWQEAjn8udSFKN6D4NlqWQ", "UUAWQEAjn8udSFKN6D4NlqWQ", 0));
                    put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/DD28PDDHHx4/default.jpg", "UCAWQEAjn8udSFKN6D4NlqWQ", "LLAWQEAjn8udSFKN6D4NlqWQ", 0));
                    put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/9wQKf7b5gSI/default.jpg", "UCAWQEAjn8udSFKN6D4NlqWQ", null, 1));
                    put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/511728820731121665/UcRP_29M_bigger.jpeg", "@MindCrackLP", "612905817", 2, "MindCrack Community"));
                }}, new ArrayList<Group>() {{
                    add(mindcrackGroup);
                }}));

            }});

            roosterTeethGroup.setUsers(new LinkedHashMap<Integer, User>() {{
                    put(0, new User("Rooster Teeth", "https://lh6.googleusercontent.com/-hddEYyXVeZM/AAAAAAAAAAI/AAAAAAAAAAA/ghwEL1-FHdE/photo.jpg", new SparseArray<MediaFeed>() {{
                        put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/x3DQhsELdG0/default.jpg", "UCzH3iADRIq1IJlIXjfNgTpA", "UUzH3iADRIq1IJlIXjfNgTpA", 0));
                        put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/5W2f2MumEng/default.jpg", "UCzH3iADRIq1IJlIXjfNgTpA", "LLzH3iADRIq1IJlIXjfNgTpA", 0));
                        put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/YNNcaJsEm6M/default.jpg", "UCzH3iADRIq1IJlIXjfNgTpA", null, 1));
                        put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/411644253257752576/hApHgKr0_bigger.png", "@RoosterTeeth", "14903310", 2, "Rooster Teeth"));
                        put(4, new TwitterFeed("RT Store", "http://pbs.twimg.com/profile_images/532942374540828673/6n6bcfYL_bigger.png", "@TheRTStore", "2897918329", 2, "Rooster Teeth Store"));
                    }}, new ArrayList<Group>() {{
                        add(roosterTeethGroup);
                    }}));
                    put(1, new User("LetsPlay", "https://lh5.googleusercontent.com/-f70aQff_iKE/AAAAAAAAAAI/AAAAAAAAAAA/-R40EvoOKf0/photo.jpg", new SparseArray<MediaFeed>() {{
                        put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/r69opIlqTI8/default.jpg", "UCkxctb0jr8vwa4Do6c6su0Q", "UUkxctb0jr8vwa4Do6c6su0Q", 0));
                        put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/VT6VrznzqMY/default.jpg", "UCkxctb0jr8vwa4Do6c6su0Q", "LLkxctb0jr8vwa4Do6c6su0Q", 0));
                        put(2, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/ry3ljkA7-F0/default.jpg", "UCkxctb0jr8vwa4Do6c6su0Q", null, 1));
                        put(3, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/1834741417/283025_10150250994985698_268895495697_7664630_357027_n_bigger.jpg", "@AchievementHunt", "16297193", 2, "Achievement Hunter"));
                    }}, new ArrayList<Group>() {{
                        add(roosterTeethGroup);
                    }}));
                    put(2, new User("The Know", "https://lh5.googleusercontent.com/-0lKHa9Wjo54/AAAAAAAAAAI/AAAAAAAAAAA/fVDFDWwii7A/photo.jpg", new SparseArray<MediaFeed>() {{
                        put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/iVsIPFIcrsw/default.jpg", "UC4w_tMnHl6sw5VD93tVymGw", "UU4w_tMnHl6sw5VD93tVymGw", 0));
                        put(1, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/620MGHaRXb0/default.jpg", "UC4w_tMnHl6sw5VD93tVymGw", null, 1));
                        put(2, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/492008086756810752/r9Ny-l4M_bigger.jpeg", "@RT_TheKnow", "2674690663", 2, "The Know"));
                    }}, new ArrayList<Group>() {{
                        add(roosterTeethGroup);
                    }}));
                    put(3, new User("The Slow Mo Guys", "https://lh3.googleusercontent.com/-hdZED2lNuKE/AAAAAAAAAAI/AAAAAAAAAAA/ppDB-or2f7I/photo.jpg", new SparseArray<MediaFeed>() {{
                        put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/RkLn2gR7SyE/default.jpg", "UCUK0HBIBWgM2c4vsPhkYY4w", "UUUK0HBIBWgM2c4vsPhkYY4w", 0));
                        put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/RkLn2gR7SyE/default.jpg", "UCUK0HBIBWgM2c4vsPhkYY4w", "LLUK0HBIBWgM2c4vsPhkYY4w", 0));
                        put(2, new YoutubeFeed("Favorites", "https://i.ytimg.com/vi/tmwvt-bNLY0/default.jpg", "UCUK0HBIBWgM2c4vsPhkYY4w", "FLUK0HBIBWgM2c4vsPhkYY4w", 0));
                        put(3, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/oUJLgKkmllA/default.jpg", "UCUK0HBIBWgM2c4vsPhkYY4w", null, 1));
                        put(4, new TwitterFeed("Gavin Free", "http://pbs.twimg.com/profile_images/378800000107482565/0ad70f276469aaf65de8826322559079_bigger.jpeg", "@GavinFree", "57841792", 2, "Gavin Free"));
                        put(5, new TwitterFeed("Daniel Gruchy", "http://pbs.twimg.com/profile_images/3122150668/fc01cdcace9d70c9ed06725d7e78ca54_bigger.png", "@DanielGruchy", "466876723", 2, "Daniel Gruchy"));
                    }}, new ArrayList<Group>() {{
                        add(roosterTeethGroup);
                    }}));
                    put(4, new User("GameFails", "https://lh5.googleusercontent.com/-ODNeOeprR_M/AAAAAAAAAAI/AAAAAAAAAAA/JUe-0kwPkQs/photo.jpg", new SparseArray<MediaFeed>() {{
                        put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/Ewo7poPoGWQ/default.jpg", "UCVyr0QjyHKZQP4ej0YI1Apg", "UUVyr0QjyHKZQP4ej0YI1Apg", 0));
                        put(1, new YoutubeFeed("Liked videos", "https://i.ytimg.com/vi/pRYUx8RsAKg/default.jpg", "UCVyr0QjyHKZQP4ej0YI1Apg", "LLVyr0QjyHKZQP4ej0YI1Apg", 0));
                        put(2, new YoutubeFeed("Favorites", "https://i.ytimg.com/vi/wzs-mIsCIQk/default.jpg", "UCVyr0QjyHKZQP4ej0YI1Apg", "FLVyr0QjyHKZQP4ej0YI1Apg", 0));
                        put(3, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/Ewo7poPoGWQ/default.jpg", "UCVyr0QjyHKZQP4ej0YI1Apg", null, 1));
                    }}, new ArrayList<Group>() {{
                        add(roosterTeethGroup);
                    }}));
                    put(5, new User("AH Community", "https://lh4.googleusercontent.com/-ryusidJvXpU/AAAAAAAAAAI/AAAAAAAAAAA/p4qAh-_kcpQ/photo.jpg", new SparseArray<MediaFeed>() {
                        {
                            put(0, new YoutubeFeed("Uploads", "https://i.ytimg.com/vi/HyZix9xwMp4/default.jpg", "UCwm59NePcctHtfYanzfyQEQ", "UUwm59NePcctHtfYanzfyQEQ", 0));
                            put(1, new YoutubeFeed("Activity", "https://i.ytimg.com/vi/HyZix9xwMp4/default.jpg", "UCwm59NePcctHtfYanzfyQEQ", null, 1));
                            put(2, new TwitterFeed("Twitter", "http://pbs.twimg.com/profile_images/1834741417/283025_10150250994985698_268895495697_7664630_357027_n_bigger.jpg", "@AchievementHunt", "16297193", 2, "Achievement Hunter"));
                        }}, new ArrayList<Group>() {{
                        add(roosterTeethGroup);
                    }}));
                }});

        List<Group> groups = new ArrayList<Group>() {{
            add(mindcrackGroup);
            add(roosterTeethGroup);
        }};

        GroupORM.saveGroups(db, groups);
    }

    private void addDefaultNotifications(SQLiteDatabase db) {


        final List<Integer> weekdays = new ArrayList<Integer>() {{
            add(0);
            add(1);
            add(1);
            add(1);
            add(1);
            add(1);
            add(0);
        }};
        final List<Integer> weekends = new ArrayList<Integer>() {{
            add(1);
            add(0);
            add(0);
            add(0);
            add(0);
            add(0);
            add(1);
        }};
        final List<Integer> everyday = new ArrayList<Integer>() {{
            add(1);
            add(1);
            add(1);
            add(1);
            add(1);
            add(1);
            add(1);
        }};

        final List<Alarm> scheduleAlarms = new ArrayList<Alarm>() {{
            add(new Alarm(true, Var.ALARM_BETWEEN, 28800000, 84900000, false, weekdays));
            add(new Alarm(true, Var.ALARM_BETWEEN, 25200000, 86400000, false, weekends));
        }};

        final List<Alarm> hourlyAlarms = new ArrayList<Alarm>() {{
            add(new Alarm(true, Var.ALARM_EVERY, 3600000, true, everyday));
            add(new Alarm(true, Var.ALARM_EVERY, 10800000, false, everyday));
        }};

        final List<Alarm> fewHoursAlarms = new ArrayList<Alarm>() {{
            add(new Alarm(true, Var.ALARM_EVERY, 7200000, true, everyday));
            add(new Alarm(true, Var.ALARM_EVERY, 14400000, false, everyday));
        }};

        final List<Alarm> busyWeekAlarms = new ArrayList<Alarm>() {{
            add(new Alarm(true, Var.ALARM_EVERY, 3600000, true, weekends));
            add(new Alarm(true, Var.ALARM_EVERY, 7200000, false, weekends));
            add(new Alarm(true, Var.ALARM_AT, 44400000, false, weekdays));
            add(new Alarm(true, Var.ALARM_AT, 71100000, false, weekdays));
        }};

        List<Notification> notifications = new ArrayList<Notification>() {{
            add(new Notification("Schedule Time", Var.NOTIFICATION_SCHEDULE, scheduleAlarms));
            add(new Notification("Hourly on Wifi", Var.NOTIFICATION_ALARM, hourlyAlarms));
            add(new Notification("Every few hours", Var.NOTIFICATION_ALARM, fewHoursAlarms));
            add(new Notification("Busy week, free weekends", Var.NOTIFICATION_ALARM, busyWeekAlarms));
        }};

        NotificationORM.saveNotifications(db, notifications);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(GroupORM.SQL_DROP_TABLE);
        db.execSQL(UserORM.SQL_DROP_TABLE);
        db.execSQL(GroupUserORM.SQL_DROP_TABLE);
        db.execSQL(MediaFeedORM.SQL_DROP_TABLE);
        db.execSQL(YoutubeItemORM.SQL_DROP_TABLE);
        db.execSQL(TwitterItemORM.SQL_DROP_TABLE);

        if (oldVersion <= 1) {
            //addNewMember(db, 3);
            //addNewMember(db, 23);
        }
        if (oldVersion <= 2) {
            //addNewMember(db, 7);
        }


    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        if (android.os.Build.VERSION.SDK_INT >= 16)
            db.setForeignKeyConstraintsEnabled(true);
    }


    ///// Database Tables //////
    static final String TABLE_GROUP = "groups";
    static final String TABLE_USER = "users";
    static final String TABLE_GROUP_USER = "groups_users";  //Many to Many
    static final String TABLE_MEDIA_FEED = "media_feeds";
    static final String TABLE_YOUTUBE_ITEM = "youtube_items";
    static final String TABLE_TWITTER_ITEM = "twitter_items";
    static final String TABLE_NOTIFICATION = "notifications";
    static final String TABLE_ALARM = "alarms";


    public static String ORDER_BY_SORT = DB.COL_SORT + " ASC";
    public static String ORDER_BY_DATE = DB.COL_DATE + " DESC";
    public static String ORDER_BY_ID = DB.COL_ID + " ASC";

    //// Database Columns /////
    static final String COL_ID = "id";
    static final String COL_SORT = "sort";
    static final String COL_NAME = "name";
    static final String COL_THUMBNAIL = "thumbnail";
    static final String COL_THUMB = "thumb";
    static final String COL_THUMBNAILS = "thumbnails";
    static final String COL_VISIBILITY = "visibility";

    static final String COL_TYPE = "type";
    static final String COL_FEED_ID = "feed_id";
    static final String COL_CHANNEL_HANDLE = "channel_handle";  //ChannelId or TwitterHandle
    static final String COL_DISPLAY_NAME = "display_name";
    static final String COL_LAST_UPDATE = "last_update";

    static final String COL_ENABLED = "enabled";
    static final String COL_TIME = "time";
    static final String COL_TIME_BETWEEN = "time_between";
    static final String COL_DAYS = "days";
    static final String COL_ONLY_WIFI = "only_wifi";

    //Foreign id keys
    static final String COL_GROUP = "group_id";
    static final String COL_USER = "user_id";
    static final String COL_MEDIA_FEED = "media_feed_id";
    static final String COL_NOTIFICATION = "notification_id";

    static final String COL_TITLE = "title";
    static final String COL_DATE = "date";
    static final String COL_IMAGE_MED = "image_med";
    static final String COL_IMAGE_HIGH = "image_high";

    static final String COL_VIDEO_ID = "video_id";
    static final String COL_DESCRIPTION = "description";
    static final String COL_DURATION = "duration";
    static final String COL_VIEWS = "views";
    static final String COL_LIKES = "likes";
    static final String COL_DISLIKES = "dislikes";
    static final String COL_STATUS = "status";

    static final String COL_TWEET_ID = "tweet_id";
    static final String COL_TWEET_THUMBNAIL = "tweet_thumbnail";

    public static final int PAGE_SIZE = 20; //Number of items to save
    public static final String STRIP = "DB_STRIP"; //Tag when reading db


    //Util methods

    public static boolean isValid(int i) { //Check if integer has been set
        return i != -1;
    }


    public static String strSeparator = ",";

    public static String integerListToString(List<Integer> integerList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < integerList.size(); i++)
            sb.append((i == 0 ? "" : strSeparator)).append(integerList.get(i));

        return sb.toString();
    }

    public static List<Integer> stringToIntegerList(String str) {
        String[] arr = str.split(strSeparator);
        List<Integer> integerList = new ArrayList<Integer>();
        for (String a : arr) integerList.add(Integer.decode(a));
        return integerList;
    }

    public static String stringListToString(List<String> integerList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < integerList.size(); i++)
            sb.append((i == 0 ? "" : strSeparator)).append(integerList.get(i));

        return sb.toString();
    }

    public static ArrayList<String> stringToStringList(String str) {
        return new ArrayList<String>(Arrays.asList(str.split(strSeparator)));
    }


    ///Helper class to view database for debugging

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }


    }

    public static String setForeignKey(int foreignKeyId) {
        return DB.isValid(foreignKeyId) ? String.valueOf(foreignKeyId) : null;
    }

    public static int getForeignKey(Cursor c, String columnName) {
        if (c.isNull(c.getColumnIndex(columnName))) return -1;
        return c.getInt(c.getColumnIndex(columnName));
    }
}
