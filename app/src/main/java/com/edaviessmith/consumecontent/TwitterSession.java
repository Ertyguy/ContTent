package com.edaviessmith.consumecontent;

import android.content.Context;
import android.content.SharedPreferences;

import twitter4j.auth.AccessToken;

public final class TwitterSession {

    private static final String TWEET_AUTH_KEY = "auth_key";
    private static final String TWEET_AUTH_SECRET_KEY = "auth_secret_key";
    private static final String TWEET_USER_NAME = "user_name";
    private static final String SHARED = "Twitter_Preferences";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public TwitterSession(Context context) {
        pref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void storeAccessToken(AccessToken accessToken, String username) {
        editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
        editor.putString(TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret());
        editor.putString(TWEET_USER_NAME, username);
        editor.commit();
    }

    public void resetAccessToken() {
        editor.putString(TWEET_AUTH_KEY, null);
        editor.putString(TWEET_AUTH_SECRET_KEY, null);
        editor.putString(TWEET_USER_NAME, null);
        editor.commit();
    }

    public String getUsername() {
        return pref.getString(TWEET_USER_NAME, "");
    }

    public AccessToken getAccessToken() {
        String token = pref.getString(TWEET_AUTH_KEY, null);
        String tokenSecret = pref.getString(TWEET_AUTH_SECRET_KEY, null);

        if (token != null && tokenSecret != null) {
            return new AccessToken(token, tokenSecret);
        } else {
            return null;
        }
    }
}