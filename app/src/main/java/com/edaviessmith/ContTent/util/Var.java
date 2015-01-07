package com.edaviessmith.contTent.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.edaviessmith.contTent.data.Alarm;
import com.edaviessmith.contTent.data.Group;
import com.edaviessmith.contTent.data.MediaFeed;
import com.edaviessmith.contTent.data.Notification;
import com.edaviessmith.contTent.data.NotificationList;
import com.edaviessmith.contTent.service.AlarmBroadcastReceiver;
import com.edaviessmith.contTent.service.DataService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import twitter4j.ResponseList;
import twitter4j.User;

public class Var {
    static final String TAG = "Var";

    //APIs
    static public final String DEVELOPER_KEY = "AIzaSyCfyVwQZCgFDgt-s02mPbpYgVgA_m-r7jI";
    static public final String TWITTER_OAUTH_CONSUMER_KEY = "ZyQynwwUcoU885CixQM66gpk5";
    static public final String TWITTER_OAUTH_CONSUMER_SECRET = "Vb1cTAkmOL3NY459eIBl14FweUV3Z3Y4Z4K53fiiJCPk8QVC9a";
    static public final String TWITTER_ACCESS_TOKEN = "481268817-ubSi5g1MTQQRIBm7rpux5uJL4c2PkmDjfQtOdYaH";
    static public final String TWITTER_ACCESS_TOKEN_SECRET = "02d44JbFMzV5BMQ8s7zxli93uA4TeRoe5CSRqzKjE1waC";

    //Fragment Feed Types
    public static final int TYPE_YOUTUBE_PLAYLIST = 0;
    public static final int TYPE_YOUTUBE_ACTIVTY = 1;
    public static final int TYPE_TWITTER = 2;
    //public static final int TYPE_REDDIT  = 3;

    //Youtube and Twitter item Types
    public static final int TYPE_UPLOAD = 0;
    public static final int TYPE_LIKE = 1;
    public static final int TYPE_FAVORITE = 2;
    public static final int TYPE_ADD_TO_PLAYLIST = 3;
    public static final int TYPE_TWEET = 4;
    public static final int TYPE_RETWEET = 5;


    public static final int STATUS_SEEN = 0;
    public static final int STATUS_NEW = 1;
    public static final int STATUS_FAVORITE = 2;
    public static final int STATUS_WATCH_LATER = 3;

    //Notification
    public static final int NOTIFICATION_ALARM = 0;
    public static final int NOTIFICATION_SCHEDULE = 1;
    public static final int NOTIFICATION_DISABLE = -1;
    public static final int ALARM_AT = 0;
    public static final int ALARM_EVERY = 1;
    public static final int ALARM_BETWEEN = 2; // Alarm Range

    //Fragment feed state
    public static final int FEED_WAITING = 0;
    public static final int FEED_LOADING = 1;
    public static final int FEED_WARNING = 2;
    public static final int FEED_OFFLINE = 3;
    public static final int FEED_END = 4;

    //Fragment states
    public static final int LIST_USERS  = 0;
    public static final int LIST_GROUPS = 1;
    //Group Fragment States
    public static final int GROUPS_LIST = 0;
    public static final int GROUPS_ALL  = 1;
    public static final int GROUP_EDIT  = 2;
    public static final int GROUP_EDIT_OPTIONS = 3;


    //Handler (currently not used)
    public static final int HANDLER_COMPLETE = 0;
    public static final int HANDLER_ERROR = 1;

    //Preferences
    public static final String PREFS = "preferences";
    public static final String PREF_ALL_NOTIFICATIONS = "all_notifications";
    public static final String PREF_MOBILE_NOTIFICATIONS = "mobile_notifications";
    public static final String PREF_VIBRATIONS = "vibrations";
    public static final String PREF_PLAY_SOUND = "play_sound";
    public static final String PREF_NEXT_ALARM = "next_alarm";
    public static final String PREF_HIRES_WIFI = "hires_wifi";
    public static final String PREF_HIRES_MOBILE = "hires_mobile";

    public static final String PREF_SELECTED_GROUP = "selected_group";
    public static final String PREF_SELECTED_USER = "selected_user";


    static public final String NOTIFY_ACTION = "notify_action";
    static public final String NOTIFY_NOTIFICATION_ID = "notification_id";
    public static final String INTENT_USER_ID = "user_id";
    public static final String INTENT_GROUP_ID = "group_id";
    public static final String INTENT_GROUP_NAME = "group_name";

    //Time Variables
    static SimpleDateFormat length = new SimpleDateFormat("mm:ss", Locale.getDefault());
    static SimpleDateFormat lengthHour = new SimpleDateFormat("k:mm:ss", Locale.getDefault());
    public static DateFormat stringDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("US"));
    public static DateFormat simpleDate = new SimpleDateFormat("MMM dd h:mm a", Locale.getDefault());

    public static final Long HOUR_MILLI = 3600000L;
    public static final Long MINUTE_MILLI = 60000L;

    public static final int DATE_DAY = 0;
    public static final int DATE_THIS_WEEK = 1;
    public static final int DATE_LAST_WEEK = 2;
    public static final int DATE_MONTH = 3; //Divide by individual month
    public static final String[] DAYS = {"Today", "Yesterday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    public static final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public static final String[] MONTHSSHORT = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static int SCROLL_OFFSET = 5; //Number of items before next request

    //URLS
    //TODO: add commonly used urls here to cleanup the code

    //Util functions
    public static int getPixels(int unit, float size) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(unit, size, metrics);
    }

    public static int getDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static boolean isTypeYoutube(int type) {
        return type == Var.TYPE_YOUTUBE_PLAYLIST || type == Var.TYPE_YOUTUBE_ACTIVTY;
    }

    public static String HTTPGet(String url) {
        return HTTPGet(new HttpGet(url));
    }

    public static String HTTPGet(HttpGet httpget) {
        HttpClient httpclient = new DefaultHttpClient();

        HttpResponse response;
        BufferedReader reader = null;

        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream inStream = entity.getContent();
                reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) sb.append(line + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return sb.toString().trim();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("GetData", "error in http Request");
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }


    public static String HTTPPost(String url) {
        return HTTPPost(new HttpPost(url));
    }

    public static String HTTPPost(HttpPost httpPost) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());

            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream insStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(insStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) sb.append(line + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        insStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return sb.toString().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("GetData", "error in http Request");
        }
        return null;
    }

    // Validate JSON when parsing
    public static boolean isJsonString(JSONObject jObj, String jObjKey) {
        boolean isValid = false;
        try {
            if (jObj.has(jObjKey) && !jObj.isNull(jObjKey) && (jObj.getString(jObjKey) instanceof String))
                isValid = true;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("isJsonString", "JSONException jObjKey:" + jObjKey);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("isJsonString", "Exception jObjKey:" + jObjKey);
        }
        return isValid;
    }

    public static boolean isJsonObject(JSONObject jObj, String jObjKey) {
        boolean isValid = false;
        try {
            if (jObj.has(jObjKey) && !jObj.isNull(jObjKey) && (jObj.getJSONObject(jObjKey) instanceof JSONObject))
                isValid = true;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("isJSONObject", "JSONException jObjKey:" + jObjKey);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("isJsonObject", "Exception jObjKey:" + jObjKey);
        }
        return isValid;
    }

    public static boolean isJsonArray(JSONObject jObj, String jObjKey) {
        boolean isValid = false;
        try {
            if (jObj.has(jObjKey) && !jObj.isNull(jObjKey) && (jObj.getJSONArray(jObjKey) instanceof JSONArray))
                isValid = true;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("isJSONArray", "JSONException jObjKey:" + jObjKey);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("isJsonArray", "Exception jObjKey:" + jObjKey);
        }
        return isValid;
    }


    public static boolean isEmpty(String s) {
        return (s == null || (s.trim().isEmpty()));
    }


    public static String getTimeSince(long publishedDate) {
        String date = "";

        if (publishedDate <= 0) {
            //date = context.getResources().getString(R.string.loading_date);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(publishedDate);

            Calendar now = Calendar.getInstance();
            SimpleDateFormat s;
            if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
                date = "Today at ";
                s = new SimpleDateFormat("h:mm a", Locale.getDefault());
            } else {
                if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && ((cal.get(Calendar.DAY_OF_YEAR)) + 1) == now.get(Calendar.DAY_OF_YEAR)) {
                    return "Yesterday";
                }
                s = new SimpleDateFormat("MMMM d", Locale.getDefault());

            }
            date += s.format(publishedDate * 1000);
        }

        return date;
    }


    public static void setNextAlarm(Context context, NotificationList notificationList) {

        if (getBoolPreference(context, Var.PREF_ALL_NOTIFICATIONS)) {
            Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
            intent.setAction(Var.NOTIFY_ACTION);

            Calendar today = Calendar.getInstance(Locale.getDefault());
            long now = today.getTimeInMillis();
            long nextAlarmPref = Var.getLongPreference(context, Var.PREF_NEXT_ALARM);


            try {
                int notificationId = -1; //Send id in intent to know which notification to update
                long nextAlarm = 0L;

                for (Notification notification : notificationList.getNotifications()) {
                    for (Alarm alarm : notification.getAlarms()) {
                        long alarmCal = Var.getNextAlarmTime(alarm, notificationList.getScheduleNotification()).getTimeInMillis();
                        if (nextAlarm == 0L || alarmCal < nextAlarm) {
                            notificationId = notification.getId();
                            nextAlarm = alarmCal;
                        }
                    }
                }

                //TODO debug to hardcode alarm in x minutes
                //nextAlarm = 5 * Var.MINUTE_MILLI;

                Log.d(TAG, "AlarmManager current Alarm: " + (nextAlarmPref / Var.MINUTE_MILLI) + ", next: " + ((nextAlarm + now) / Var.MINUTE_MILLI));
                if (nextAlarm > 0L && (((nextAlarm + now) / Var.MINUTE_MILLI) != (nextAlarmPref / Var.MINUTE_MILLI))) {
                    intent.putExtra(Var.NOTIFY_NOTIFICATION_ID, notificationId);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

                    Var.setLongPreference(context, Var.PREF_NEXT_ALARM, (nextAlarm + now));


                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC, (nextAlarm + now), pendingIntent);
                    Log.d(TAG, "AlarmManager update was set to run in " + nextAlarm);
                } else {
                    Log.d(TAG, "Alarm not set");
                }


            } catch (Exception e) {
                e.printStackTrace(); // in case you want to see the stacktrace in your log cat output
            }
        }
    }



    public static String getNextNotificationTime(List<Notification> notifications, Notification scheduleNotification) {

        Alarm nextAlarm = null;
        Calendar nextAlarmTime = null;

        for(Notification notification: notifications) {
            for (Alarm alarm : notification.getAlarms()) {
                Calendar alarmTime = Var.getNextAlarmTime(alarm, scheduleNotification);
                if (nextAlarmTime == null || alarmTime.before(nextAlarmTime)) {
                    nextAlarmTime = alarmTime;
                    nextAlarm = alarm;
                }
            }
        }
        String check = Var.getNextAlarmTimeText(nextAlarm, scheduleNotification);
        return Var.isEmpty(check) ? "Not watching" : "Next check in "+check;
    }




    public static String getNextNotificationAlarm(Notification notification, Notification scheduleNotification) {

        Alarm nextAlarm = null;
        Calendar nextAlarmTime = null;

        for(Alarm alarm: notification.getAlarms()){
            Calendar alarmTime = Var.getNextAlarmTime(alarm, scheduleNotification);
            if(nextAlarmTime == null || alarmTime.before(nextAlarmTime)) {
                nextAlarmTime = alarmTime;
                nextAlarm = alarm;
            }
        }

        return "Next check: " + Var.getNextAlarmTimeText(nextAlarm, scheduleNotification);
    }

    //Used to divide media list by time segments (today, yesterday, this week, last week this month)
    //Second return integer is for month value
    //Third return integer is for year value
    public static int[] getTimeCategory(long publishedDate) {

        if (publishedDate > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(publishedDate);

            Calendar now = Calendar.getInstance();
            if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && (now.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR) <= 4)) {
                int days = now.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
                if (days < 0) days = 0;
                return new int[]{Var.DATE_DAY, (days < 2) ? days : (cal.get(Calendar.DAY_OF_WEEK) + 1)};
            } else {
                if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && (cal.get(Calendar.WEEK_OF_YEAR)) == now.get(Calendar.WEEK_OF_YEAR)) {
                    return new int[]{Var.DATE_THIS_WEEK};
                } else {
                    if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && ((cal.get(Calendar.WEEK_OF_YEAR) + 1)) == now.get(Calendar.WEEK_OF_YEAR)) {
                        return new int[]{Var.DATE_LAST_WEEK};
                    } else {
                        if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                            return new int[]{Var.DATE_MONTH, cal.get(Calendar.MONTH)};
                        } else {
                            return new int[]{Var.DATE_MONTH, cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)};
                        }
                    }
                }
            }
        }

        return new int[]{Var.DATE_DAY, 0};
    }


    public static String getStringFromDuration(String youtubeDuration) {
        String formatDate = "'PT'";
        if (youtubeDuration.contains("H")) formatDate += "h'H'";
        if (youtubeDuration.contains("M")) formatDate += "mm'M'";
        if (youtubeDuration.contains("S")) formatDate += "ss'S'";
        DateFormat df = new SimpleDateFormat(formatDate);
        try {
            Date d = df.parse(youtubeDuration);
            if ((d.getTime() + TimeZone.getDefault().getRawOffset()) < 3600000)
                return length.format(d); //Only show hour if that long  //Remove stupid default local (+5 for me)
            else return lengthHour.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static String displayViews(int views) {
        if (views > 1000000) {
            return (views / 1000000) + "M views";
        } else if (views > 1000) {
            return (views / 1000) + "K views";
        } else {
            return (views == 301 ? views + "+" : views) + " views";
        }
    }


    public static String displayActivity(int type) {
        switch (type) {
            case Var.TYPE_UPLOAD: return "Uploaded";
            case Var.TYPE_LIKE: return "Liked";
            case Var.TYPE_FAVORITE: return "Favorited";
            case Var.TYPE_ADD_TO_PLAYLIST: return "Added to Playlist";
        }
        return "";
    }

    public static boolean getBoolPreference(Context context, String pref) {
        SharedPreferences settings = context.getSharedPreferences(Var.PREFS, 0);
        return settings.getBoolean(pref, getPrefDefault(pref));
    }

    public static void setBoolPreference(Context context, String pref, boolean state) {
        SharedPreferences.Editor settings = context.getSharedPreferences(Var.PREFS, 0).edit();
        settings.putBoolean(pref, state);
            settings.apply();
    }

    public static int getIntPreference(Context context, String pref) {
        SharedPreferences settings = context.getSharedPreferences(Var.PREFS, 0);
        return settings.getInt(pref, -1);
    }

    public static void setIntPreference(Context context, String pref, int state) {
        SharedPreferences.Editor settings = context.getSharedPreferences(Var.PREFS, 0).edit();
        settings.putInt(pref, state);
        settings.apply();
    }


    public static long getLongPreference(Context context, String pref) {
        SharedPreferences settings = context.getSharedPreferences(Var.PREFS, 0);
        return settings.getLong(pref, 0L);
    }

    public static void setLongPreference(Context context, String pref, long l) {
        SharedPreferences.Editor settings = context.getSharedPreferences(Var.PREFS, 0).edit();
        settings.putLong(pref, l);
        settings.apply();
    }

    private static boolean getPrefDefault(String pref) {
        if (pref.equals(Var.PREF_PLAY_SOUND)) return false;
        return true;
    }

    public static boolean isDeviceTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isDeviceLandscape(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }



    public static String getAlarmText(Alarm alarm) {

        if (alarm.getType() == Var.ALARM_AT) {
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            c.setTimeInMillis(alarm.getTime());

            return "At " + Var.getTimeText(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        }

        if (alarm.getType() == Var.ALARM_EVERY) {
            int hour = (int) (alarm.getTime() / Var.HOUR_MILLI);
            return "Every " + hour + " hour" + (hour == 1 ? "" : "s");
        }

        if (alarm.getType() == Var.ALARM_BETWEEN) {
            Calendar from = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            from.setTimeInMillis(alarm.getTime());

            Calendar to = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            to.setTimeInMillis(alarm.getTimeBetween());

            return "From " + Var.getTimeText(from.get(Calendar.HOUR_OF_DAY), from.get(Calendar.MINUTE))
                    + " to " + Var.getTimeText(to.get(Calendar.HOUR_OF_DAY), to.get(Calendar.MINUTE));

        }

        return "";
    }

    public static String getTimeText(int hour, int minute) {
        return (hour < 13 ? (hour == 0 ? 12 : hour) : hour - 12) + ":" + String.format("%02d", minute) + " " + (hour >= 12 ? "pm" : "am");
    }

    public static String getNextAlarmTimeText(Alarm alarm, Notification notificationSchedule) {

        Calendar nextAlarm = Var.getNextAlarmTime(alarm, notificationSchedule);

        int days = nextAlarm.get(Calendar.DAY_OF_YEAR) - 1;
        int hours = nextAlarm.get(Calendar.HOUR_OF_DAY);
        int minutes = nextAlarm.get(Calendar.MINUTE);

        String time = "in ";
        if (days > 0) time += days + "d";
        if (days > 0 && hours > 0 && minutes > 0) time += ", ";
        else if (days > 0 && hours > 0 && minutes == 0 || days > 0 && minutes > 0) time += " and ";
        if (hours > 0) time += hours + "h" ;
        if (hours > 0 && minutes > 0) time += " and ";
        if (minutes > 0) time += minutes + "m";

        if (days > 0 || hours > 0 || minutes > 0) return time;
        return "never";
    }

    public static String getShortTime(long date) {
        Calendar time  = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        time.setTimeInMillis(time.getTimeInMillis() - date);

        int days = time.get(Calendar.DAY_OF_YEAR) - 1;
        int hours = time.get(Calendar.HOUR_OF_DAY);
        int minutes = time.get(Calendar.MINUTE);

        if(days > 2) {
            Calendar day  = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            day.setTimeInMillis(date);
            return MONTHSSHORT[day.get(Calendar.MONTH)] + " "+day.get(Calendar.DAY_OF_MONTH);
        }

        if (days > 0) return days + "d";
        if (hours > 0) return hours + "h" ;
        if (minutes > 0) return minutes + "m";

        return "?";

    }

    public static Calendar getNextAlarmTime(Alarm alarm, Notification scheduleNotification) {

        Calendar now = Calendar.getInstance(Locale.getDefault());
        Calendar when = Calendar.getInstance(Locale.getDefault());

        int today = now.get(Calendar.DAY_OF_WEEK) - 1;

        if (alarm.getType() == Var.ALARM_AT) {
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            c.setTimeInMillis(alarm.getTime());

            when.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
            when.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
        }

        if (alarm.getType() == Var.ALARM_EVERY) {
            Calendar calToday = (Calendar) now.clone(); //Get absolute day and add relative time increment for next alarm
            Calendar nextAlarm = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            nextAlarm.clear();

            int next = (int) (calToday.get(Calendar.HOUR_OF_DAY) % (alarm.getTime() / Var.HOUR_MILLI));
            nextAlarm.set(Calendar.HOUR_OF_DAY, calToday.get(Calendar.HOUR_OF_DAY) + (next > 0 ? next : (int) (alarm.getTime() / Var.HOUR_MILLI)));

            calToday.set(Calendar.HOUR_OF_DAY, 0);
            calToday.set(Calendar.MINUTE, 0);

            int daysUntilNextAlarm = alarm.getDaysUntilNextAlarm(today);

            int daysUntilNextSchedule = 0;
            Alarm scheduleAlarm = scheduleNotification.getAlarmForDay((today + daysUntilNextAlarm) % 7);   //Get today's schedule
            if (scheduleAlarm == null || nextAlarm.getTimeInMillis() > (scheduleAlarm.getTimeBetween() == 0 ? Var.HOUR_MILLI * 24 : scheduleAlarm.getTimeBetween())) { //No alarm or we pass it's end time
                daysUntilNextSchedule = scheduleNotification.getDaysUntilNextAlarm((today + daysUntilNextAlarm) % 7);
                scheduleAlarm = scheduleNotification.getAlarmForDay((today + daysUntilNextAlarm + daysUntilNextSchedule) % 7);    //Get next alarm in schedule
            }
            if ((nextAlarm.getTimeInMillis() < scheduleAlarm.getTime()) || ((daysUntilNextAlarm + daysUntilNextSchedule) > 0)) { // Alarm is before next schedule or the schedule is not today
                when.setTimeInMillis(calToday.getTimeInMillis() + scheduleAlarm.getTime() + (alarm.getTime() - (scheduleAlarm.getTime() % alarm.getTime()))); //Start of next alarm
                when.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + daysUntilNextSchedule);
            } else {
                when.setTimeInMillis(calToday.getTimeInMillis() + nextAlarm.getTimeInMillis()); //Today + in how many hours
                //Log.d(TAG, "nextAlarm : "+Var.getTimeText(nextAlarm.get(Calendar.HOUR_OF_DAY), nextAlarm.get(Calendar.MINUTE)));
            }
            //Log.d(TAG, "day alarm set with"+daysUntilNextSchedule+" - "+ Var.getTimeText(when.get(Calendar.HOUR_OF_DAY), when.get(Calendar.MINUTE)));

        }


        //Log.d(TAG, getAlarmText(alarm)+" today: "+ Var.DAYS[today+2]);
        for (int day = now.before(when) ? 0 : 1; day < 7; day++) {
            if (alarm.getDays().get((day + today) % 7) == 1) { //Next day alarm will go off
                when.add(Calendar.DAY_OF_YEAR, (day));
                //Log.d(TAG, "when: " + Var.DAYS[((day + today) % 7) + 2] + " days: " + (when.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR)));
                break;
            }
        }

        Calendar nextAlarm = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        nextAlarm.setTimeInMillis(when.getTimeInMillis() - now.getTimeInMillis());

        return nextAlarm;
    }

    //The last 20 minutes
    public static boolean isRecent(long lastUpdate) {
        Calendar now = Calendar.getInstance(Locale.getDefault());

        return (now.getTimeInMillis() - (Var.MINUTE_MILLI * 20) < lastUpdate);
    }


    //Listener to update a feed thumbnail reference if while making the request the image is not found
    public static Listener getThumbnailListener(final DataService.ServiceBinder binder, final MediaFeed feed, final ImageView image_iv, final int userId) {
        return new Listener() {
            @Override
            public void onComplete(String value) {

            }

            @Override
            public void onError(String value) {
                updateMediaFeedThumbnail(binder, feed, image_iv, userId);
            }
        };
    }

    //Listener to update a user thumbnail reference if while making the request the image is not found
    public static Listener getUserThumbnailListener(final DataService.ServiceBinder binder, final com.edaviessmith.contTent.data.User user, final ImageView image_iv) {
        return new Listener() {
            @Override
            public void onComplete(String value) {

            }

            @Override
            public void onError(String value) {
                updateUserThumbnail(binder, user, image_iv);
            }
        };
    }

    //Listener to update a group thumbnail reference if while making the request the image is not found
    public static Listener getGroupThumbnailListener(final DataService.ServiceBinder binder, final Group group, final ImageView image_iv) {
        return new Listener() {
            @Override
            public void onComplete(String value) {

            }

            @Override
            public void onError(String value) {
                boolean thumbSet = false;
                String channelId = null;
                for(com.edaviessmith.contTent.data.User user: group.getUsers().values()) {

                    for(String thumbnail: user.getThumbnails()) {
                        if(thumbnail.equals(group.getThumbnail())) {
                            String thumb = updateUserThumbnail(binder, user, image_iv);

                            group.setThumbnail(thumb);
                            binder.saveGroup(group);
                        }
                    }
                }
            }
        };
    }


    private static String updateUserThumbnail(final DataService.ServiceBinder binder, final com.edaviessmith.contTent.data.User user, final ImageView image_iv) {
        String channelId = null;
        for(int i=0; i< user.getCastMediaFeed().size(); i++) {
            if(channelId == null && Var.isTypeYoutube(user.getCastMediaFeed().valueAt(i).getType())) channelId = user.getCastMediaFeed().valueAt(i).getChannelHandle();
            if(user.getThumbnail().equals(user.getCastMediaFeed().valueAt(i).getThumbnail())) {
                String thumbnail = updateMediaFeedThumbnail(binder, user.getCastMediaFeed().valueAt(i), image_iv, user.getId());

                if(!user.getThumbnails().contains(user.getCastMediaFeed().valueAt(i).getThumbnail())) {
                    user.getThumbnails().add(thumbnail);
                    user.setThumb(user.getThumbnails().indexOf(thumbnail));
                } else {
                    int index = user.getThumbnails().indexOf(user.getCastMediaFeed().valueAt(i).getThumbnail());
                    user.getThumbnails().set(index, thumbnail);
                    user.setThumb(index);
                }

                binder.saveUser(user);
                return thumbnail;
            }
        }
        //Grab the channel from the first youtube playlist
        if(!Var.isEmpty(channelId)) {
            String channelUrl = "https://www.googleapis.com/youtube/v3/channels?part=snippet&id=" + channelId + "&&fields=items&key=" + Var.DEVELOPER_KEY;
            String req = Var.HTTPGet(channelUrl);

            try {
                JSONObject play = new JSONObject(req);
                if (Var.isJsonArray(play, "items")) {
                    JSONArray items = play.getJSONArray("items");
                    if (items.length() > 0) {
                        JSONObject item = items.getJSONObject(0);

                        if (Var.isJsonObject(item, "snippet")) {
                            JSONObject snippet = item.getJSONObject("snippet");
                            if (Var.isJsonObject(snippet, "thumbnails")) {              //Feed Thumbnail
                                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                if (Var.isJsonObject(thumbnails, "default")) {
                                    JSONObject def = thumbnails.getJSONObject("default");
                                    if (Var.isJsonString(def, "url")) {
                                        String thumb = def.getString("url");

                                        user.getThumbnails().set(user.getThumb(), thumb);
                                        binder.getImageLoader().DisplayImage(thumb, image_iv);
                                        binder.saveUser(user);
                                        return thumb;
                                    }
                                }
                            }
                        }

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }


    private static String updateMediaFeedThumbnail(final DataService.ServiceBinder binder, final MediaFeed feed, final ImageView image_iv, final int userId) {
        try {
            if(Var.isTypeYoutube(feed.getType())) {

                String req = null;
                if(feed.getType() == Var.TYPE_YOUTUBE_PLAYLIST) {
                    String playlistUrl = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&id=" + URLEncoder.encode(feed.getFeedId(), "UTF-8") + "&fields=items(id%2Csnippet)&key=" + Var.DEVELOPER_KEY;
                    req = Var.HTTPGet(playlistUrl);
                } else if(feed.getType() == Var.TYPE_YOUTUBE_ACTIVTY) {
                    //TODO this call has not been tested (need to manually corrupt some YT thumbnails to test)
                    String activityUrl = "https://www.googleapis.com/youtube/v3/activities?part=snippet&fields=items(id%2Csnippet)&channelId=" + URLEncoder.encode(feed.getChannelHandle(), "UTF-8") + "&maxResults=20&key=" + Var.DEVELOPER_KEY;
                    req = Var.HTTPGet(activityUrl);
                }

                JSONObject play = new JSONObject(req);
                if (Var.isJsonArray(play, "items")) {
                    JSONArray items = play.getJSONArray("items");
                    if(items.length() > 0) {
                        JSONObject item = items.getJSONObject(0);

                        if (Var.isJsonObject(item, "snippet")) {
                            JSONObject snippet = item.getJSONObject("snippet");
                            if (Var.isJsonObject(snippet, "thumbnails")) {              //Feed Thumbnail
                                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                if (Var.isJsonObject(thumbnails, "default")) {
                                    JSONObject def = thumbnails.getJSONObject("default");
                                    if (Var.isJsonString(def, "url")) {
                                        feed.setThumbnail(def.getString("url"));
                                        binder.getImageLoader().DisplayImage(feed.getThumbnail(), image_iv);
                                        binder.saveMediaFeed(userId, feed.getId());
                                        return def.getString("url");
                                    }
                                }
                            }
                        }

                    }
                }
            }

            if(feed.getType() == Var.TYPE_TWITTER) {
                ResponseList<User> users = binder.getTwitter().getAppTwitter().lookupUsers(new long[]{Long.parseLong(feed.getFeedId())});
                if(users != null) {
                    twitter4j.User u = users.get(0);
                    String thumb = u.getProfileImageURL().replace("_normal", "_bigger");
                    if(!thumb.equals(feed.getThumbnail())) {
                        feed.setThumbnail(thumb);
                        binder.getImageLoader().DisplayImage(feed.getThumbnail(), image_iv);
                        binder.saveMediaFeed(userId, feed.getId());
                        return thumb;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}