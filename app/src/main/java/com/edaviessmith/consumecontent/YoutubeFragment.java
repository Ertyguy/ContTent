package com.edaviessmith.consumecontent;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.YoutubeFeed;
import com.edaviessmith.consumecontent.data.YoutubeItem;
import com.edaviessmith.consumecontent.db.YoutubeItemORM;
import com.edaviessmith.consumecontent.util.Listener;

import java.util.List;


public class YoutubeFragment extends Fragment {

    private static String TAG = "YoutubeFragment";
    private static YoutubeFragment youtubeFragment;
    private static ContentActivity act;
    private int pos;


    private Listener listener;
    private RecyclerView feed_rv;
    private YoutubeItemAdapter itemAdapter;

    public static YoutubeFragment newInstance(ContentActivity activity, int pos) {
        Log.i(TAG, "newInstance");
        act = activity;

        youtubeFragment = new YoutubeFragment();

        Bundle args = new Bundle();
        args.putInt("pos", pos);
        youtubeFragment.setArguments(args);

        return youtubeFragment;
    }

    public YoutubeFragment() {

        listener = new Listener() {
            @Override
            public void onComplete(String value) {
                /* complete action*/
            }
            @Override
            public void onError(String value) {
                Log.e(TAG, "listener error "+value);
            }
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        pos = getArguments() != null ? getArguments().getInt("pos") : -1;
        view.setId(pos);

        feed_rv = (RecyclerView) view.findViewById(R.id.list);
        feed_rv.setLayoutManager(new LinearLayoutManager(act));
        feed_rv.setItemAnimator(new DefaultItemAnimator());

        getLocalItems();

        //handler.sendMessage(handler.obtainMessage(what, 1, 0, authUrl));

        itemAdapter = new YoutubeItemAdapter(getFeed().getItems(), act);
        feed_rv.setAdapter(itemAdapter);

        return view;
    }

    public void getLocalItems() {


        new Thread() {
            @Override
            public void run() {

                getFeed().setItems(YoutubeItemORM.getYoutubeItems(act, getFeed().getId()));

                //handler.sendMessage(handler.obtainMessage(Var.HANDLER_COMPLETE, 1, 0, ""));
            }
        }.start();
    }



    private YoutubeFeed getFeed() {
        return (YoutubeFeed) act.getUser().getMediaFeed().get(pos);//youtubeFeed;
    }

    public static class YoutubeItemAdapter extends RecyclerView.Adapter<YoutubeItemAdapter.ViewHolder>{

        private List<YoutubeItem> items;
        private Context mContext;

        public YoutubeItemAdapter(List<YoutubeItem> items,  Context context) {
            this.items = items;
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_youtube, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            //viewHolder.title_tv.setText(items.get(i).getTitle());

            //viewHolder.countryImage.setImageDrawable(mContext.getDrawable(country.getImageResourceId(mContext)));
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView thumbnail_iv;
            public TextView title_tv;
            public TextView length_tv;
            public TextView views_tv;
            public TextView date_tv;
            public TextView status_tv;

            public ViewHolder(View itemView) {
                super(itemView);
                thumbnail_iv = (ImageView) itemView.findViewById(R.id.thumbnail_iv);
                title_tv = (TextView) itemView.findViewById(R.id.title_tv);
                length_tv = (TextView) itemView.findViewById(R.id.length_tv);
                views_tv = (TextView) itemView.findViewById(R.id.views_tv);
                date_tv = (TextView) itemView.findViewById(R.id.date_tv);
                status_tv = (TextView) itemView.findViewById(R.id.status_tv);
            }

        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                if (msg.arg1 == 1) listener.onError("Error getting request token");
                else listener.onError("Error getting access token");
            } else {
                if (msg.arg1 == 1) ;//showLoginDialog((String) msg.obj);
                else listener.onComplete("");
            }
        }
    };
}
