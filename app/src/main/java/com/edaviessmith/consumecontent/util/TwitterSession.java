package com.edaviessmith.consumecontent.util;

import android.content.Context;
import android.content.SharedPreferences;

import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuth2Token;

public final class TwitterSession {

    private static final String TWEET_AUTH_KEY = "auth_key";
    private static final String TWEET_AUTH_SECRET_KEY = "auth_secret_key";
    private static final String TWEET_USER_NAME = "user_name";
    private static final String BEARER_ACCESS_TOKEN = "bearer_access_token";
    private static final String BEARER_TOKEN_TYPE = "bearer_token_type";

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

    public void storeBearerToken(OAuth2Token token) {
        editor.putString(BEARER_ACCESS_TOKEN, token.getAccessToken());
        editor.putString(BEARER_TOKEN_TYPE, token.getTokenType());
        editor.commit();
    }

    public OAuth2Token getBearerToken() {
        String accessToken = pref.getString(BEARER_ACCESS_TOKEN, "");
        String tokenType = pref.getString(BEARER_TOKEN_TYPE, "");
        if(!Var.isEmpty(accessToken) && !Var.isEmpty(tokenType))
            return new OAuth2Token(accessToken, tokenType);
        return null;
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