package com.edaviessmith.consumecontent;

import android.content.Context;
import android.os.Bundle;
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

import java.util.List;


public class YoutubeFragment extends Fragment {

    private static String TAG = "YoutubeFragment";
    private static YoutubeFragment youtubeFragment;
    private static ContentActivity act;
    private YoutubeFeed youtubeFeed;
    private int pos;

    private TextView feedId_tv;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        pos = getArguments() != null ? getArguments().getInt("pos") : -1;
        view.setId(pos);

        feed_rv = (RecyclerView) view.findViewById(R.id.list);
        feed_rv.setLayoutManager(new LinearLayoutManager(act));
        feed_rv.setItemAnimator(new DefaultItemAnimator());

        itemAdapter = new YoutubeItemAdapter(getFeed().getYoutubeItems(), R.layout.item_youtube, act);
        feed_rv.setAdapter(itemAdapter);

        return view;
    }

    private YoutubeFeed getFeed() {
        return act.getUser().getYoutubeChannel().getYoutubeFeeds().get(pos);
    }

    public static class YoutubeItemAdapter extends RecyclerView.Adapter<YoutubeItemAdapter.ViewHolder>{

        private List<YoutubeItem> items;
        private int rowLayout;
        private Context mContext;

        public YoutubeItemAdapter(List<YoutubeItem> items, int rowLayout, Context context) {
            this.items = items;
            this.rowLayout = rowLayout;
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.title_tv.setText(items.get(i).getTitle());

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
}
