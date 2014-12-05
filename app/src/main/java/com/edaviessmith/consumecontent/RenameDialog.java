package com.edaviessmith.consumecontent;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.util.Var;


public class RenameDialog extends Dialog implements View.OnClickListener {

    private final static String TAG = "RenameDialog";

    AddActivity act;
    EditText name_edt, rename_edt;
    ImageView thumbnail_iv;
    View cancel_tv, set_tv;
    String thumbnail;
    MediaFeed mediaFeed;



    public RenameDialog(AddActivity activity, MediaFeed mediaFeed, EditText name_edt, String thumbnail) {
        super(activity);
        this.act = activity;
        this.mediaFeed = mediaFeed;
        this.name_edt = name_edt;
        this.thumbnail = thumbnail;

        init();
    }

    private void init() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_rename);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * (Var.isDeviceLandscape(act) ? 0.65 : 0.95));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        getWindow().setAttributes(params);

        rename_edt = (EditText) findViewById(R.id.name_edt);
        rename_edt.setText(name_edt.getText().toString());
        act.binder.getApp().postFocusTextEnd(rename_edt);

        thumbnail_iv = (ImageView) findViewById(R.id.thumbnail_iv);
        thumbnail_iv.setVisibility(Var.isEmpty(thumbnail)? View.GONE: View.VISIBLE);
        if(!Var.isEmpty(thumbnail)) {
            act.binder.getImageLoader().DisplayImage(thumbnail, thumbnail_iv);
        }

        set_tv = findViewById(R.id.set_tv);
        set_tv.setOnClickListener(this);

        cancel_tv = findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(this);

        show();
    }


    @Override
    public void onClick(View v) {

        if(set_tv == v) {

            act.rename(mediaFeed, rename_edt.getText().toString());
            dismiss();
        }

        if(cancel_tv == v) {
            dismiss();
        }



    }

}
