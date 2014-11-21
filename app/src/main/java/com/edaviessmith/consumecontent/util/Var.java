package com.edaviessmith.consumecontent.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.edaviessmith.consumecontent.data.Alarm;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Var {
    static final String TAG = "Var";

    //APIs
    static public final String DEVELOPER_KEY = "AIzaSyCfyVwQZCgFDgt-s02mPbpYgVgA_m-r7jI";
    static public final String TWITTER_OAUTH_CONSUMER_KEY = "ZyQynwwUcoU885CixQM66gpk5";
    static public final String TWITTER_OAUTH_CONSUMER_SECRET = "Vb1cTAkmOL3NY459eIBl14FweUV3Z3Y4Z4K53fiiJCPk8QVC9a";


    //Fragment Feed Types
    public static final int TYPE_YOUTUBE_PLAYLIST = 0;
    public static final int TYPE_YOUTUBE_ACTIVTY = 1;
    public static final int TYPE_TWITTER = 2;
    //public static final int TYPE_REDDIT  = 3;

    //Youtube Item Types
    public static final int TYPE_UPLOAD = 0;
    public static final int TYPE_LIKE = 1;
    public static final int TYPE_FAVORITE = 2;
    public static final int TYPE_ADD_TO_PLAYLIST = 3;

    //Notification
    public static final int NOTIFICATION_ALARM = 0;
    public static final int NOTIFICATION_SLEEP = 1;
    public static final int ALARM_AT = 0;
    public static final int ALARM_EVERY = 1;
    public static final int ALARM_BEFORE = 2; //Sleep alarms
    public static final int ALARM_AFTER = 3;

    //Handler (currently not used)
    public static final int HANDLER_COMPLETE = 0;
    public static final int HANDLER_ERROR = 1;

    //Preferences
    public static final String PREFS = "preferences";
    public static final String PREF_ALL_NOTIFICATIONS = "all_notifications";
    public static final String PREF_MOBILE_NOTIFICATIONS = "mobile_notifications";
    public static final String PREF_VIBRATIONS = "vibrations";
    public static final String PREF_PLAY_SOUND = "play_sound";


    static public final String NOTIFY_ACTION = "notify_action";

    //Time Variables
    static SimpleDateFormat length =  new SimpleDateFormat("mm:ss", Locale.getDefault());
    static SimpleDateFormat lengthHour =  new SimpleDateFormat("k:mm:ss", Locale.getDefault());
    public static DateFormat stringDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("US"));
    public static DateFormat simpleDate = new SimpleDateFormat("MMM dd k:mm a", Locale.getDefault());

    public static final Long HOUR_MILLI = 3600000L ;
    public static final Long MINUTE_MILLI = 60000L ;

    public static final int DATE_DAY = 0;
    public static final int DATE_THIS_WEEK = 1;
    public static final int DATE_LAST_WEEK = 2;
    public static final int DATE_MONTH = 3; //Divide by individual month
    public static final String[] DAYS = {"Today", "Yesterday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    public static final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};


    public static int SCROLL_OFFSET = 5; //Number of items before next request


    //Util functions
    public static int getPixels(int unit, float size) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(unit, size, metrics);
    }
    public static int getDp(int px)  {
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
                InputStream instream = entity.getContent();
                reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null)  sb.append(line + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try { instream.close(); }
                    catch (IOException e) { e.printStackTrace(); }
                }
                return sb.toString().trim();
            }


        } catch (Exception e) { e.printStackTrace();  Log.e("GetData", "error in http Request"); }
        finally{
            if (reader != null)
                try { reader.close(); }
                catch (IOException e) { e.printStackTrace(); }
        }
        return null;
    }


    public static String HTTPPost(String url) {
        return HTTPPost(new HttpPost(url));
    }

    public static String HTTPPost(HttpPost httpPost) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());

            InputStream inputStream;
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null)  sb.append(line + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try { instream.close(); }
                    catch (IOException e) { e.printStackTrace(); }
                }
                return sb.toString().trim();
            }
        } catch (Exception e) { e.printStackTrace();  Log.e("GetData", "error in http Request"); }
        return null;
    }

    // Validate JSON when parsing
    public static boolean isJsonString(JSONObject jObj, String jObjKey) {
        boolean isValid = false;
        try { if (jObj.has(jObjKey) && !jObj.isNull(jObjKey)  && (jObj.getString(jObjKey) instanceof String)) isValid = true; }
        catch (JSONException e) { e.printStackTrace(); Log.e("isJsonString", "JSONException jObjKey:" + jObjKey); }
        catch (Exception e) { e.printStackTrace(); Log.e("isJsonString", "Exception jObjKey:" + jObjKey); }
        return isValid;
    }

    public static boolean isJsonObject(JSONObject jObj, String jObjKey) {
        boolean isValid = false;
        try { if (jObj.has(jObjKey) && !jObj.isNull(jObjKey)  && (jObj.getJSONObject(jObjKey) instanceof JSONObject)) isValid = true; }
        catch (JSONException e) { e.printStackTrace(); Log.e("isJSONObject", "JSONException jObjKey:" + jObjKey); }
        catch (Exception e) { e.printStackTrace(); Log.e("isJsonObject", "Exception jObjKey:" + jObjKey); }
        return isValid;
    }

    public static boolean isJsonArray(JSONObject jObj, String jObjKey) {
        boolean isValid = false;
        try { if (jObj.has(jObjKey) && !jObj.isNull(jObjKey)  && (jObj.getJSONArray(jObjKey) instanceof JSONArray)) isValid = true; }
        catch (JSONException e) {  e.printStackTrace(); Log.e("isJSONArray", "JSONException jObjKey:" + jObjKey); }
        catch (Exception e) {  e.printStackTrace(); Log.e("isJsonArray", "Exception jObjKey:" + jObjKey); }
        return isValid;
    }


    public static boolean isEmpty(String s) {
        return (s == null || (s.toString().trim().isEmpty()));
    }

    public static String getTimeSince(long publishedDate) {
        String date = "";

        if(publishedDate <= 0) {
            //date = context.getResources().getString(R.string.loading_date);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(publishedDate);

            Calendar now = Calendar.getInstance();
            SimpleDateFormat s;
            if(cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
                date = "Today at ";
                s =  new SimpleDateFormat("h:mm a", Locale.getDefault());
            } else {
                if(cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && ((cal.get(Calendar.DAY_OF_YEAR))+1) == now.get(Calendar.DAY_OF_YEAR)) {
                    return "Yesterday";
                }
                s = new SimpleDateFormat("MMMM d", Locale.getDefault());

            }
            date += s.format(publishedDate*1000);
        }

        return date;
    }

    public static void setNextAlarm(Context context) {

        if(getBoolPreference(context, Var.PREF_ALL_NOTIFICATIONS)) {
            Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
            intent.setAction(Var.NOTIFY_ACTION);
            boolean alarmActive = (PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null);

            //If alarm isn't already active create a new alarm
            if(!alarmActive) {
                try {
                    long minutes = 0L;//getMinutes();
                    //BugSenseHandler.addCrashExtraData("Members", "Alarm Minutes set to "+minutes);
                    if(minutes != 0L) {
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        //BugSenseHandler.addCrashExtraData("Members", "Alarm Pending");
                        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + minutes, pendingIntent);
                        Log.d("Util", "AlarmManager update was set to run in "+ (minutes / 60000) + " minutes");
                    } else {
                        Log.d("Util", "Alarm time set to never");
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace(); // in case you want to see the stacktrace in your log cat output
                    //Toast.makeText(context, "Unable to start Notifications", Toast.LENGTH_SHORT).show();
                    //BugSenseHandler.addCrashExtraData("Exception", "Alarm exception");
                    //BugSenseHandler.sendException(ex);
                }
            } else {
                Log.d("Util", "AlarmManager already active");
            }
        }
    }



    //Used to divide media list by time segments (today, yesterday, this week, last week this month)
    //Second return integer is for month value
    //Third return integer is for year value
    public static int[] getTimeCategory(long publishedDate) {

        if(publishedDate > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(publishedDate);

            Calendar now = Calendar.getInstance();
            if(cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && (now.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR) <= 4)) {
                int days = now.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
                if(days <0) days = 0;
                return new int[] {Var.DATE_DAY, (days < 2) ? days: (cal.get(Calendar.DAY_OF_WEEK) + 1)};
            } else {
                if(cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && (cal.get(Calendar.WEEK_OF_YEAR)) == now.get(Calendar.WEEK_OF_YEAR)) {
                    return new int[] {Var.DATE_THIS_WEEK};
                } else {
                    if(cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && ((cal.get(Calendar.WEEK_OF_YEAR)+1)) == now.get(Calendar.WEEK_OF_YEAR)) {
                        return new int[] {Var.DATE_LAST_WEEK};
                    } else {
                        if(cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) ) {
                            return new int[] {Var.DATE_MONTH, cal.get(Calendar.MONTH)};
                        } else {
                            return new int[]{Var.DATE_MONTH, cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)};
                        }
                    }
                }
            }
        }

        return new int[] {Var.DATE_DAY, 0};
    }


    public static String getStringFromDuration(String youtubeDuration) {
        String formatDate = "'PT'";
        if(youtubeDuration.contains("H")) formatDate += "h'H'";
        if(youtubeDuration.contains("M")) formatDate += "mm'M'";
        if(youtubeDuration.contains("S")) formatDate += "ss'S'";
        DateFormat df = new SimpleDateFormat(formatDate);
        try {
            Date d = df.parse(youtubeDuration);
            if((d.getTime() + TimeZone.getDefault().getRawOffset()) < 3600000) return length.format(d); //Only show hour if that long  //Remove stupid default local (+5 for me)
            else return lengthHour.format(d);
        }
        catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static String displayViews(int views) {

        if(views > 1000000) {
            return (views / 1000000) + "M views";
        } else if(views > 1000) {
            return (views / 1000) + "K views";
        } else {
            return (views == 301? views+"+": views) + " views";
        }

    }

    public static boolean getBoolPreference(Context context, String pref) {
        SharedPreferences settings = context.getSharedPreferences(Var.PREFS, 0);
        return settings.getBoolean(pref, getPrefDefault(pref));
    }

    @SuppressLint("NewApi")
    public static void setBoolPreference(Context context, String pref, boolean state) {
        SharedPreferences.Editor settings = context.getSharedPreferences(Var.PREFS, 0).edit();
        settings.putBoolean(pref, state);
        if (android.os.Build.VERSION.SDK_INT >= 9) {
            settings.apply();
        } else {
            settings.commit();
        }
    }

    private static boolean getPrefDefault(String pref) {
        if(pref.equals(Var.PREF_PLAY_SOUND)) return false;
        return true;
    }


    public static String getAlarmText(Alarm alarm) {

        if(alarm.getType() == Var.ALARM_AT || alarm.getType() == Var.ALARM_BEFORE || alarm.getType() == Var.ALARM_AFTER) {
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            c.setTimeInMillis(alarm.getTime());
            int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

            String ret = "At ";
            if(alarm.getType() == Var.ALARM_BEFORE)   ret = "Before ";
            if(alarm.getType() == Var.ALARM_AFTER)   ret = "After ";
            return ret +(hourOfDay <= 12? hourOfDay: hourOfDay - 12)+":"+String.format("%02d",  c.get(Calendar.MINUTE)) + " " + (hourOfDay >= 12? "pm": "am");
        }

        if(alarm.getType() == Var.ALARM_EVERY) {
            int hour = (int) (alarm.getTime() / Var.HOUR_MILLI);
            return "Every " + hour + " hour" + (hour == 1 ? "" : "s");
        }

        return "";
    }


    //TODO doesn't account for sleep time or day of the week
    public static String getNextAlarmTime(Alarm alarm) {

        Calendar now = Calendar.getInstance(Locale.getDefault());
        Calendar when = Calendar.getInstance(Locale.getDefault());

        if(alarm.getType() == Var.ALARM_AT || alarm.getType() == Var.ALARM_BEFORE || alarm.getType() == Var.ALARM_AFTER) {
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            c.setTimeInMillis(alarm.getTime());

            when = (Calendar) now.clone();
            when.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
            when.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
        }

        if(alarm.getType() == Var.ALARM_EVERY) {
            Calendar today = (Calendar) now.clone();
            today.set(Calendar.MINUTE, 0);

            int next = (int) (today.get(Calendar.HOUR_OF_DAY) % (alarm.getTime() / Var.HOUR_MILLI));

            when = (Calendar) now.clone();
            when.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY) + (next > 0? next: (int) (alarm.getTime() / Var.HOUR_MILLI)));
            when.set(Calendar.MINUTE, 0);
        }

        int today = now.get(Calendar.DAY_OF_WEEK) - 1;
        Log.d(TAG, getAlarmText(alarm)+" today: "+ Var.DAYS[today+2]);
        for(int day = now.before(when)? 0: 1; day < 7; day++) {
            if(alarm.getDays().get((day + today) % 7) == 1) { //Next day alarm will go off
                when.add(Calendar.DAY_OF_YEAR, (day));
                Log.d(TAG, "when: "+ Var.DAYS[((day + today) % 7)+2] + " days: "+ (when.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR)));
                break;
            }
        }

        /*int today = now.get(Calendar.DAY_OF_WEEK) - 1;
        for(int day = today; day < today + 7; day++) {
            if(alarm.getDays().get(day % 7) == 1) { //Next day alarm will go off
                when.add(Calendar.DAY_OF_YEAR, (day - today) + (now.before(when)? 0: 1));
                break;
            }
        }*/

        /*int days = when.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);
        int hours = (when.get(Calendar.HOUR_OF_DAY)) *//*+ (now.before(when)? 0: 24))*//* - now.get(Calendar.HOUR_OF_DAY);
        int minutes = when.get(Calendar.MINUTE) - now.get(Calendar.MINUTE);
        if(minutes < 0) {
            hours --;
            //minutes = 60 - minutes;
        }
        if(minutes > 59) {
            hours ++;
            //minutes -= 60;
        }

        String time = "in ";
        if(days > 0) time += days+" day"+(days == 1? "":"s");
        if(days > 0 && (hours > 0 || minutes > 0)) time += " and ";
        if(hours > 0) time += hours+" hour"+(hours == 1? "":"s");
        if(hours > 0 && minutes > 0) time += " and ";
        if(minutes > 0) time += minutes+" minute"+(minutes == 1? "":"s");
*/

        Calendar nextAlarm = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        nextAlarm.setTimeInMillis(when.getTimeInMillis() - now.getTimeInMillis());

        int days = nextAlarm.get(Calendar.DAY_OF_YEAR) - 1;
        int hours = nextAlarm.get(Calendar.HOUR_OF_DAY);
        int minutes = nextAlarm.get(Calendar.MINUTE);

        String time = "in ";
        if(days > 0) time += days+" day"+(days == 1? "":"s");
        if(days > 0 && hours > 0 && minutes > 0) time += ", ";
        else if(days > 0 && hours > 0 && minutes == 0 || days > 0 && minutes > 0) time += " and ";
        if(hours > 0) time += hours+" hour"+(hours == 1? "":"s");
        if(hours > 0 && minutes > 0) time += " and ";
        if(minutes > 0) time += minutes+" minute"+(minutes == 1? "":"s");


        return time;
    }
}
