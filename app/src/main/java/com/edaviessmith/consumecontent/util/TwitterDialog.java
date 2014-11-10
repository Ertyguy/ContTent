package com.edaviessmith.consumecontent.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edaviessmith.consumecontent.R;

public final class TwitterDialog extends Dialog {

    private static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private final String url;
    private final TwitterAuthListener listener;
    private ProgressDialog spinner;
    private WebView webView;
    private TextView title;
    private boolean progressDialogRunning = false;
    private Context context;

    public TwitterDialog(Context context, String url, TwitterAuthListener listener) {
        super(context);
        this.context = context;
        this.url = url;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spinner = new ProgressDialog(getContext());

        spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spinner.setMessage("Loading...");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        setUpTitle(layout);
        setUpWebView(layout);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        float[] dimensions = (width < height) ?
            new float[]{ width < Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 280)? width :Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 280), height < Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 420) ? height : Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 420) }:
            new float[]{ width < Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 460)? width :Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 460), height < Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 260) ? height : Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 260) };

        addContentView(layout, new FrameLayout.LayoutParams((int) (dimensions[0]), (int) (dimensions[1])));
    }

    private void setUpTitle(LinearLayout layout) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Drawable icon = getContext().getResources().getDrawable(R.drawable.ic_twitter_white);

        int MARGIN = Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 5);
        int PADDING = Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 5);

        title = new TextView(getContext());
        title.setText("Twitter / Authorize an application");
        title.setTextColor(Color.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setBackgroundColor(getContext().getResources().getColor(R.color.blue_twitter));
        title.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
        title.setCompoundDrawablePadding(MARGIN + PADDING);
        title.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

        layout.addView(title);
    }

    private void setUpWebView(LinearLayout layout) {
        webView = new WebView(getContext());

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new TwitterWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setLayoutParams(FILL);

        layout.addView(webView);
    }

    private class TwitterWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(TwitterUtil.CALLBACK_URL)) {
                listener.onComplete(url);

                TwitterDialog.this.dismiss();

                return true;
            } else if (url.startsWith("authorize")) {
                return false;
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            listener.onError(description);
            TwitterDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            spinner.show();
            progressDialogRunning = true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String titleText = webView.getTitle();
            if (titleText != null && titleText.length() > 0) {
                title.setText(titleText);
            }
            progressDialogRunning = false;
            spinner.dismiss();
        }
    }

    @Override
    protected void onStop() {
        progressDialogRunning = false;
        super.onStop();
    }

    public void onBackPressed() {
        if (!progressDialogRunning) {
            TwitterDialog.this.dismiss();
        }
    }
}