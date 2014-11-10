package com.edaviessmith.consumecontent.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Window;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public final class TwitterUtil {

    private final Context context;
    public final Twitter twitter;
    private final TwitterSession session;
    private final CommonsHttpOAuthConsumer httpOauthConsumer;
    private final OAuthProvider httpOauthProvider;
    private final ProgressDialog progressDialog;
    private TwitterAuthListener listener;
    private AccessToken accessToken;
    private String bearerToken;

    public static final String CALLBACK_URL = "app://connect";
    private static final String TWITTER_ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
    private static final String TWITTER_AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
    private static final String TWITTER_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
    private static final String TWITTER_BEARER_TOKEN_URL = "https://api.twitter.com/oauth2/token";

    public TwitterUtil(Context context) {
        this.context = context;

        twitter = new TwitterFactory().getInstance();
        session = new TwitterSession(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        httpOauthConsumer = new CommonsHttpOAuthConsumer(Var.TWITTER_OAUTH_CONSUMER_KEY, Var.TWITTER_OAUTH_CONSUMER_SECRET);
        httpOauthProvider = new DefaultOAuthProvider(TWITTER_REQUEST_URL, TWITTER_ACCESS_TOKEN_URL, TWITTER_AUTHORIZE_URL);
        accessToken = session.getAccessToken();

        configureToken();
    }

    public void setListener(TwitterAuthListener listener) {
        this.listener = listener;
    }

    private void configureToken() {
        if (accessToken != null) {
            twitter.setOAuthConsumer(Var.TWITTER_OAUTH_CONSUMER_KEY, Var.TWITTER_OAUTH_CONSUMER_SECRET);
            twitter.setOAuthAccessToken(accessToken);
        }
    }

    public boolean hasAccessToken() {
        return (accessToken == null) ? false : true;
    }


    public void resetAccessToken() {
        if (accessToken != null) {
            session.resetAccessToken();

            accessToken = null;
        }
    }

    public String getUsername() {
        return session.getUsername();
    }

    public void authorize() {
        progressDialog.setMessage("Initializing ...");
        progressDialog.show();

        new Thread() {
            @Override
            public void run() {
                String authUrl = "";
                int what = 1;

                try {
                    authUrl = httpOauthProvider.retrieveRequestToken(httpOauthConsumer, CALLBACK_URL);
                    what = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.sendMessage(handler.obtainMessage(what, 1, 0, authUrl));
            }
        }.start();
    }

    public void processToken(String callbackUrl) {
        progressDialog.setMessage("Finalizing ...");
        progressDialog.show();

        final String verifier = getVerifier(callbackUrl);
        new Thread() {
            @Override
            public void run() {
                int what = 1;

                try {
                    httpOauthProvider.retrieveAccessToken(httpOauthConsumer, verifier);

                    accessToken = new AccessToken(httpOauthConsumer.getToken(), httpOauthConsumer.getTokenSecret());

                    configureToken();

                    User user = twitter.verifyCredentials();

                    session.storeAccessToken(accessToken, user.getName());

                    what = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.sendMessage(handler.obtainMessage(what, 2, 0));
            }
        }.start();
    }


    private String getVerifier(String callbackUrl) {
        String verifier = "";

        try {
            callbackUrl = callbackUrl.replace("app", "http");

            URL url = new URL(callbackUrl);
            String query = url.getQuery();

            String array[] = query.split("&");

            for (String parameter : array) {
                String v[] = parameter.split("=");

                if (URLDecoder.decode(v[0]).equals(oauth.signpost.OAuth.OAUTH_VERIFIER)) {
                    verifier = URLDecoder.decode(v[1]);
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return verifier;
    }

    public void showLoginDialog(String url) {
        final TwitterAuthListener listener = new TwitterAuthListener() {
            @Override
            public void onComplete(String value) {
                processToken(value);
            }
            @Override
            public void onError(String value) {
                TwitterUtil.this.listener.onError("Failed opening authorization page");
            }
        };

        new TwitterDialog(context, url, listener).show();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();

            if (msg.what == 1) {
                if (msg.arg1 == 1)
                    listener.onError("Error getting request token");
                else
                    listener.onError("Error getting access token");
            } else {
                if (msg.arg1 == 1)
                    showLoginDialog((String) msg.obj);
                else
                    listener.onComplete("");
            }
        }
    };


    ////      Application authentication        ////

    public String getBearerToken() {

        if(bearerToken != null) return bearerToken;

        try {
            HttpPost httppost = new HttpPost(TWITTER_BEARER_TOKEN_URL);

            String authorization = "Basic " + Base64.encodeToString((Var.TWITTER_OAUTH_CONSUMER_KEY + ":" + Var.TWITTER_OAUTH_CONSUMER_SECRET).getBytes(), Base64.NO_WRAP);
            httppost.setHeader("Authorization", authorization);
            httppost.setHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            httppost.setEntity(new StringEntity("grant_type=client_credentials"));


            JSONObject root = new JSONObject(Var.HTTPPost(httppost));
            if(Var.isJsonString(root, "access_token"))
                bearerToken =root.getString("access_token");

        } catch (Exception e) {
            Log.e("loadTwitterToken", "onPost Error:" + e.getMessage());
        }

        return bearerToken;
    }

}