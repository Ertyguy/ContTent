package com.edaviessmith.consumecontent.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

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

    //Searching users
    public static final int SEARCH_NONE = 0;
    public static final int SEARCH_OPTIONS = 1;
    public static final int SEARCH_YOUTUBE = 2;
    public static final int SEARCH_YT_CHANNEL = 3;
    public static final int SEARCH_TWITTER = 4;

    //Notification
    public static final int NOTIFICATION_ALARM = 0;
    public static final int NOTIFICATION_SLEEP = 1;
    public static final int ALARM_AT = 0;
    public static final int ALARM_EVERY = 1;

    //Handler (currently not used)
    public static final int HANDLER_COMPLETE = 0;
    public static final int HANDLER_ERROR = 1;

    //Format Youtube Video length into (00:00)
    static SimpleDateFormat length =  new SimpleDateFormat("mm:ss", Locale.getDefault());
    static SimpleDateFormat lengthHour =  new SimpleDateFormat("k:mm:ss", Locale.getDefault());
    public static DateFormat stringDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("US"));
    public static DateFormat simpleDate = new SimpleDateFormat("MMM dd k:mm a", Locale.getDefault());

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
            //date = AppInstance.getContext().getResources().getString(R.string.loading_date);
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

            /*if(cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
               return new int[] {Var.DATE_DAY, 0};
            } else {
                if(cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && ((cal.get(Calendar.DAY_OF_YEAR))+1) == now.get(Calendar.DAY_OF_YEAR)) {
                    return new int[] {Var.DATE_DAY, 1};
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
            }*/
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
}
