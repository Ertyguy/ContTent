package com.edaviessmith.consumecontent.util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
    static public final String PREF_TW_AUTH = "twitter_auth";
    static public final String PREF_TW_ACCESS_TOKEN = "twitter_beareraccesstoken";
    static public final String PREF_TW_TOKEN_TYPE = "twitter_bearertokentype";
    static public final String BUGSENSE_KEY = "4a749a4a";



    //Fragment types
    public static final int TYPE_YOUTUBE = 0;
    public static final int TYPE_TWITTER = 1;
    public static final int TYPE_REDDIT  = 2;

    //Searching users
    public static final int SEARCH_NONE = 0;
    public static final int SEARCH_YOUTUBE = 1;
    public static final int SEARCH_TWITTER = 2;


    //Util functions

    public static String HTTPGet(String url)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null)  sb.append(line + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        instream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

}
