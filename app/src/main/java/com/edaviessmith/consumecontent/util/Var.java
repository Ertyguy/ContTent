package com.edaviessmith.consumecontent.util;

import android.content.res.Resources;
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

public class Var {

    //APIs
    static public final String DEVELOPER_KEY = "AIzaSyCfyVwQZCgFDgt-s02mPbpYgVgA_m-r7jI";
    static public final String TWITTER_OAUTH_CONSUMER_KEY = "ZyQynwwUcoU885CixQM66gpk5";
    static public final String TWITTER_OAUTH_CONSUMER_SECRET = "Vb1cTAkmOL3NY459eIBl14FweUV3Z3Y4Z4K53fiiJCPk8QVC9a";


    //Fragment Feed Types
    public static final int TYPE_YOUTUBE_PLAYLIST = 0;
    public static final int TYPE_YOUTUBE_ACTIVTY = 1;
    public static final int TYPE_TWITTER = 2;
    //public static final int TYPE_REDDIT  = 3;

    //Searching users
    public static final int SEARCH_NONE = 0;
    public static final int SEARCH_YOUTUBE = 1;
    public static final int SEARCH_TWITTER = 2;


    //Util functions
    public static int getPixels(int unit, float size) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(unit, size, metrics);
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
        return (s != null && (s.toString().trim().isEmpty()));
    }
}
