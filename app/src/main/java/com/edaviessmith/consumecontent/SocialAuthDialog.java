package com.edaviessmith.consumecontent;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SocialAuthDialog extends Dialog {

    // Variables
    public static final int BLUE = 0xFF6D84B4;
    public static final int MARGIN = 4;
    public static final int PADDING = 2;

    public static float width = 40;
    public static float height = 60;

    public static final float[] DIMENSIONS_DIFF_LANDSCAPE = { width, height };
    public static final float[] DIMENSIONS_DIFF_PORTRAIT = { width, height };

    public static boolean titleStatus = false;
    public static final String DISPLAY_STRING = "touch";

    private final String mUrl;
    private String newUrl;
    private int count;

    // Android Components
    private TextView mTitle;
    private final DialogListener mListener;
    private ProgressDialog mSpinner;
    private CustomWebView mWebView;
    private LinearLayout mContent;
    private Drawable icon;
    private Handler handler;
    static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
            ViewGroup.LayoutParams.FILL_PARENT);

    static final FrameLayout.LayoutParams WRAP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);

    // SocialAuth Components
    //private final SocialAuthManager mSocialAuthManager;
    //private final Provider mProviderName;

    /**
     * Constructor for the dialog
     *
     * @param context  Parent component that opened this dialog
     * @param url  URL that will be used for authenticating
     * @param listener  Listener object to handle events
     */
    public SocialAuthDialog(Context context, String url, DialogListener listener) {
        super(context);
        //mProviderName = providerName;
        mUrl = url;
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        //Util.getDisplayDpi(getContext());

        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");
        mSpinner.setCancelable(true);

        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);
        setUpTitle();
        setUpWebView();

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int orientation = getContext().getResources().getConfiguration().orientation;
        float[] dimensions = (orientation == Configuration.ORIENTATION_LANDSCAPE) ? DIMENSIONS_DIFF_LANDSCAPE  : DIMENSIONS_DIFF_PORTRAIT;

        addContentView(mContent, new LinearLayout.LayoutParams(display.getWidth() - ((int) (dimensions[0] * scale + 0.5f)), display.getHeight() - ((int) (dimensions[1] * scale + 0.5f))));

        mSpinner.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mWebView.stopLoading();
                mListener.onBack();
                SocialAuthDialog.this.dismiss();
            }
        });

        this.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    mWebView.stopLoading();
                    dismiss();
                    mListener.onBack();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Sets title and icon of provider
     *
     */

    private void setUpTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mTitle = new TextView(getContext());
        //int res = getContext().getResources().getIdentifier(mProviderName.toString(), "drawable",   getContext().getPackageName());
        icon = getContext().getResources().getDrawable(R.drawable.ic_twitter_icon);

        mTitle.setText("Twitter / Authorize Application");
        mTitle.setGravity(Gravity.CENTER_VERTICAL);
        mTitle.setTextColor(Color.WHITE);
        mTitle.setTypeface(Typeface.DEFAULT_BOLD);
        mTitle.setBackgroundColor(BLUE);
        mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
        mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
        mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

        if (!titleStatus)
            mContent.addView(mTitle);
    }

    /**
     * Set up WebView to load the provider URL
     *
     */
    private void setUpWebView() {
        mWebView = new CustomWebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new SocialAuthDialog.SocialAuthWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);
        mWebView.setLayoutParams(FILL);
        mContent.addView(mWebView);
    }

    /**
     * WebView Client
     */

    private class SocialAuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            Log.d("SocialAuth-WebView", "Override url: " + url);



            try {

                if (url.toString().equalsIgnoreCase("twitter")) {
                    if (url.startsWith("cancel")) { //TODO get cancel uri
                        // Handles Twitter and Facebook Cancel
                        mListener.onCancel();
                    } else { // for Facebook and Twitter
                        //URL u = new URL(url);
                        //final Map<String, String> params = Var.decodeUrl(u.getQuery());
                        //params.putAll(Var.decodeUrl(u.getRef()));
                        view.loadUrl(url);

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                try {


                                    //AuthProvider auth = mSocialAuthManager.connect(params);
                                    //TODO need to authenticate i think

                                    //writeToken(auth);


                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mSpinner != null && mSpinner.isShowing()) mSpinner.dismiss();

                                            Bundle bundle = new Bundle();
                                            bundle.putString("Twitter", "twitter");
                                            mListener.onComplete(bundle);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mListener.onError(new Error("Unknown Error", e));
                                }
                            }
                        };
                        new Thread(runnable).start();
                    }
                    //SocialAuthDialog.this.dismiss();
                    return true;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }


            if (url.startsWith("cancel")) {
                // Handles MySpace and Linkedin Cancel
                mListener.onCancel();
                SocialAuthDialog.this.dismiss();
                return true;
            } else if (url.contains(DISPLAY_STRING)) {
                return false;
            }

            return false;

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d("SocialAuth-WebView", "Inside OnReceived Error");
            Log.d("SocialAuth-WebView", String.valueOf(errorCode));
            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onError(new Error(description, new Exception(failingUrl)));
            SocialAuthDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            Log.d("SocialAuth-WebView", "onPageStart:" + url);
        }

        @Override
        public void onPageFinished(WebView view, final String url) {

            super.onPageFinished(view, url);


            String title = mWebView.getTitle();
            if (title != null && title.length() > 0) {
                mTitle.setText(title);
            }

            mSpinner.dismiss();
        }
    }

    /**
     * Internal Method to create new File in internal memory for each provider
     * and save accessGrant
     *
     * @param auth AuthProvider
     *//*

    private void writeToken(AuthProvider auth) {

        AccessGrant accessGrant = auth.getAccessGrant();
        String key = accessGrant.getKey();
        String secret = accessGrant.getSecret();

        String providerid = accessGrant.getProviderId();

        Map<String, Object> attributes = accessGrant.getAttributes();

        Editor edit = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

        edit.putString(mProviderName.toString() + " key", key);
        edit.putString(mProviderName.toString() + " secret", secret);
        edit.putString(mProviderName.toString() + " providerid", providerid);

        if (attributes != null) {
            for (Map.Entry entry : attributes.entrySet()) {
                System.out.println(entry.getKey() + ", " + entry.getValue());
            }

            for (String s : attributes.keySet()) {
                edit.putString(mProviderName.toString() + "attribute " + s, String.valueOf(attributes.get(s)));
            }

        }

        edit.commit();

    }*/



    /**
     * Workaround for Null pointer exception in WebView.onWindowFocusChanged in
     * droid phones and emulator with android 2.2 os. It prevents first time
     * WebView crash.
     */

    public class CustomWebView extends WebView {

        public CustomWebView(Context context) {
            super(context);
        }

        public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public CustomWebView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            try {
                super.onWindowFocusChanged(hasWindowFocus);
            } catch (NullPointerException e) {
                // Catch null pointer exception
            }
        }
    }
}