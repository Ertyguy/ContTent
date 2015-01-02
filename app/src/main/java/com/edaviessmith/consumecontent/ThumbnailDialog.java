package com.edaviessmith.consumecontent;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.edaviessmith.consumecontent.util.Var;

import java.util.List;


public class ThumbnailDialog extends Dialog implements View.OnClickListener {

    private final static String TAG = "ThumbnailDialog";

    AddActivity act;

    List<String> thumbnails;

    RecyclerView thumbnail_rv;
    ThumbnailAdapter notificationAdapter;
    View cancel_tv;

    public ThumbnailDialog(AddActivity act,  List<String> thumbnails) {
        super(act);
        this.act = act;
        this.thumbnails = thumbnails;
        init();
    }


    private void init() {


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_thumbnails);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * (Var.isDeviceLandscape(act) ? 0.65 : 0.95));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        getWindow().setAttributes(params);


        thumbnail_rv = (RecyclerView) findViewById(R.id.list);
        thumbnail_rv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        thumbnail_rv.setItemAnimator(new DefaultItemAnimator());
        //Recycler view can't WRAP_CONTENT
        int height = (Math.min((thumbnails.size() / 4 + (thumbnails.size() % 4 > 0? 1: 0)), 3) * Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 80)) + Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 40);
        thumbnail_rv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));


        notificationAdapter = new ThumbnailAdapter(act);
        thumbnail_rv.setAdapter(notificationAdapter);

        cancel_tv = findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(this);

        show();
    }


    @Override
    public void onClick(View v) {

        if(cancel_tv == v) {
            dismiss();
        }

    }


    public class ThumbnailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{


        private Context context;

        public ThumbnailAdapter( Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_thumbnail, viewGroup, false);
            v.setOnClickListener(this);
            return new ViewHolderItem(v);
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public void onClick(final View view) {
            int pos = thumbnail_rv.getChildPosition(view);

            act.setThumbnail(thumbnails.get(pos));
            dismiss();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof ViewHolderItem) {

                final ViewHolderItem holder = (ViewHolderItem) viewHolder;

                act.binder.getImageLoader().DisplayImage(thumbnails.get(i), holder.thumbnail_iv, holder.thumbnail_pb);

            }

        }

        @Override
        public int getItemCount() {
            return thumbnails.size();
        }


        public class ViewHolderItem extends RecyclerView.ViewHolder {
            public ImageView thumbnail_iv;
            public ProgressBar thumbnail_pb;

            public ViewHolderItem(View itemView) {
                super(itemView);
                thumbnail_iv = (ImageView) itemView.findViewById(R.id.thumbnail_iv);
                thumbnail_pb = (ProgressBar) itemView.findViewById(R.id.thumbnail_pb);
            }
        }

    }




}
