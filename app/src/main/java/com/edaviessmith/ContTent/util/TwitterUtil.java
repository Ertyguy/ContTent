package com.edaviessmith.contTent.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

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
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public final class TwitterUtil {
    private static final String TAG = "TwitterUtil";

    private final Context context;
    public final Twitter userTwitter, appTwitter;
    private final TwitterSession session;
    private final CommonsHttpOAuthConsumer httpOauthConsumer;
    private final OAuthProvider httpOauthProvider;
    private ProgressDialog progressDialog;
    private Listener listener;
    public AccessToken accessToken;
    private Context actContext;
    //private String bearerToken;

    public  static final String CALLBACK_URL = "app://connect";
    private static final String TWITTER_ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
    private static final String TWITTER_AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
    private static final String TWITTER_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
    private static final String TWITTER_BEARER_TOKEN_URL = "https://api.twitter.com/oauth2/token";

    static public final String PREF_TW_ACCESS_TOKEN = "twitter_beareraccesstoken";
    static public final String PREF_TW_TOKEN_TYPE = "twitter_bearertokentype";

    public TwitterUtil(Context context) {
        this.context = context;


        userTwitter = new TwitterFactory().getInstance();
        session = new TwitterSession(context);

        httpOauthConsumer = new CommonsHttpOAuthConsumer(Var.TWITTER_OAUTH_CONSUMER_KEY, Var.TWITTER_OAUTH_CONSUMER_SECRET);
        httpOauthProvider = new DefaultOAuthProvider(TWITTER_REQUEST_URL, TWITTER_ACCESS_TOKEN_URL, TWITTER_AUTHORIZE_URL);
        //httpOauthConsumer.setTokenWithSecret(Var.TWITTER_ACCESS_TOKEN, Var.TWITTER_ACCESS_TOKEN_SECRET);
        accessToken = session.getAccessToken();
        configureToken();



        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(Var.TWITTER_OAUTH_CONSUMER_KEY)
               .setOAuthConsumerSecret(Var.TWITTER_OAUTH_CONSUMER_SECRET)
               .setOAuthAccessToken(Var.TWITTER_ACCESS_TOKEN)
               .setOAuthAccessTokenSecret(Var.TWITTER_ACCESS_TOKEN_SECRET)
               .setApplicationOnlyAuthEnabled(true)
               .setHttpConnectionTimeout(100000);
        OAuth2Token token = null;//session.getBearerToken();
        if(token != null) {
            Log.d("TwitterUtil", "session "+token.getAccessToken() + ", " + token.getTokenType());
            builder.setOAuth2TokenType(token.getTokenType());
            builder.setOAuth2AccessToken(token.getAccessToken());
        }

        appTwitter = new TwitterFactory(builder.build()).getInstance();
        //appTwitter = factory.getInstance();
        //if(token == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OAuth2Token token = appTwitter.getOAuth2Token();
                        //appTwitter.setOAuth2Token(token);
                        session.storeBearerToken(token);
                        Log.d("TwitterUtil", "runnable "+token.getAccessToken() + ", " + token.getTokenType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        /*} else {
            appTwitter.setOAuth2Token(session.getBearerToken());
        }*/



        //twitter.setOAuth2Token(new OAuth2Token(Var.TWITTER_ACCESS_TOKEN, Var.TWITTER_ACCESS_TOKEN_SECRET));
        //if(accessToken != null) twitter.setOAuthAccessToken(accessToken);

    }

    public Twitter getUserTwitter() {
        return userTwitter;
    }

    public Twitter getAppTwitter() {
        return appTwitter;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void configureToken() {
        if (accessToken != null) {
            userTwitter.setOAuthConsumer(Var.TWITTER_OAUTH_CONSUMER_KEY, Var.TWITTER_OAUTH_CONSUMER_SECRET);
            userTwitter.setOAuthAccessToken(accessToken);
        }

    }

    public boolean hasAccessToken() {
        return accessToken != null;
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

    public void authorize(Context context) {
        actContext = context;
        progressDialog = new ProgressDialog(actContext);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

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

                    User user = userTwitter.verifyCredentials();
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
        final Listener listener = new Listener() {
            @Override
            public void onComplete(String value) {
                processToken(value);
            }
            @Override
            public void onError(String value) {
                TwitterUtil.this.listener.onError("Failed opening authorization page");
            }
        };

        new TwitterDialog(actContext, url, listener).show();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();

            if (msg.what == 1) {
                if (msg.arg1 == 1) listener.onError("Error getting request token");
                else listener.onError("Error getting access token");
            } else {
                if (msg.arg1 == 1) showLoginDialog((String) msg.obj);
                else listener.onComplete("");
            }
        }
    };
}