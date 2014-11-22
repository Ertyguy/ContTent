package com.edaviessmith.consumecontent;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.edaviessmith.consumecontent.db.MediaFeedORM;
import com.edaviessmith.consumecontent.db.YoutubeItemORM;
import com.edaviessmith.consumecontent.util.ImageLoader;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.util.YoutubeFeedAsyncTask;

import java.util.List;


public class YoutubeFragment extends Fragment{

    private static String TAG = "YoutubeFragment";
    private static YoutubeFragment youtubeFragment;
    private static ContentActivity act;
    private int pos;

    private ImageLoader imageLoader;
    private RecyclerView feed_rv;
    private YoutubeItemAdapter itemAdapter;

    private boolean isSearchBusy; //Only make a single request to API



    public static YoutubeFragment newInstance(ContentActivity activity, int pos) {
        Log.i(TAG, "newInstance");
        act = activity;

        youtubeFragment = new YoutubeFragment();

        Bundle args = new Bundle();
        args.putInt("pos", pos);
        youtubeFragment.setArguments(args);

        return youtubeFragment;
    }

    public YoutubeFragment() { }

    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        pos = getArguments() != null ? getArguments().getInt("pos") : -1;
        view.setId(pos);
        imageLoader = new ImageLoader(act);

        feed_rv = (RecyclerView) view.findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(act);
        feed_rv.setLayoutManager(linearLayoutManager);
        feed_rv.setItemAnimator(new DefaultItemAnimator());


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSearchBusy = true;
                new YoutubeFeedAsyncTask(act, getFeed(), handler).execute(getFeed().getNextPageToken());
            }
        });

        //handler.sendMessage(handler.obtainMessage(what, 1, 0, authUrl));

        itemAdapter = new YoutubeItemAdapter(act);
        feed_rv.setAdapter(itemAdapter);
        feed_rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            //@SuppressLint("NewApi")
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!isSearchBusy && linearLayoutManager.findLastVisibleItemPosition() > linearLayoutManager.getItemCount() - Var.SCROLL_OFFSET) {
                    isSearchBusy = true;
                    new YoutubeFeedAsyncTask(act, getFeed(), handler).execute(getFeed().getNextPageToken());
                    Log.d(TAG,"onScrolled getFeed called");
                }

                Log.d(TAG, "onScrolled " + dy + " : " + act.getSupportActionBar().isShowing());
                //TODO much thinking needed to hide toolbar on scroll
                //if (dy >= mActionBarHeight && act.getSupportActionBar().isShowing()) {
                if (android.os.Build.VERSION.SDK_INT >= 12) ;
                     //act.toolbar.animate().translationY(-act.toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    //act.getSupportActionBar().hide();
                //} else if (dy <= -mActionBarHeight && !act.getSupportActionBar().isShowing()) {
                    //act.getSupportActionBar().show();
                //}
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLocalItems();
    }

    public void getLocalItems() {
        if(getFeed().getItems() == null || getFeed().getItems().size() == 0) {
            new Thread() {
                @Override
                public void run() {

                    final List<YoutubeItem> localItems = YoutubeItemORM.getYoutubeItems(act, getFeed().getId());

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG,"adding local items "+localItems.size());
                            getFeed().setItems(localItems);
                            itemAdapter.notifyDataSetChanged();
                            isSearchBusy = true;
                            new YoutubeFeedAsyncTask(act, getFeed(), handler).execute(getFeed().getNextPageToken());
                        }
                    });
                }
            }.start();
        }
    }


    private YoutubeFeed getFeed() {
        return (YoutubeFeed) act.getUser().getMediaFeed().get(pos);
    }


    public class YoutubeItemAdapter extends RecyclerView.Adapter<YoutubeItemAdapter.ViewHolder> implements View.OnClickListener{

        private final int TYPE_DIV = 0;
        private final int TYPE_ITEM = 1;
        private Context mContext;

        public YoutubeItemAdapter( Context context) {

            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = null;
            if(i == TYPE_DIV) v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_youtube_divider, viewGroup, false);
            else if(i == TYPE_ITEM) v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_youtube, viewGroup, false);
            v.setOnClickListener(this);
            return new ViewHolder(v);
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0) return TYPE_DIV;
            int[] prevItemCat = Var.getTimeCategory(( getFeed().getItems().get(position - 1)).getDate());
            int[] itemCat = Var.getTimeCategory((getFeed().getItems().get(position)).getDate());
            if((prevItemCat[0] == Var.DATE_DAY && itemCat[0] == Var.DATE_DAY && prevItemCat[1] != itemCat[1])) return TYPE_DIV;
            if((prevItemCat[0] == Var.DATE_MONTH && itemCat[0] == Var.DATE_MONTH && prevItemCat[1] != itemCat[1])) return TYPE_DIV;
            if(prevItemCat[0] != itemCat[0]) return TYPE_DIV;
            //TODO account for different months as well
            return TYPE_ITEM;
        }

        @Override
        public void onClick(final View view) {
            int itemPosition = feed_rv.getChildPosition(view);
            act.startVideo(getFeed().getItems().get(itemPosition));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if(getFeed().getItems().size() > i) {
                YoutubeItem item = getFeed().getItems().get(i);

                imageLoader.DisplayImage(item.getImageMed(), viewHolder.thumbnail_iv);
                viewHolder.title_tv.setText(item.getTitle());
                viewHolder.length_tv.setText(item.getDuration());
                viewHolder.views_tv.setText(Var.displayViews(item.getViews()));
                //viewHolder.views_tv.setText(Var.simpleDate.format(new Date(item.getDate())));

                if(getItemViewType(i) == TYPE_DIV) {
                    int[] dateCats = Var.getTimeCategory((getFeed().getItems().get(i)).getDate());
                    String dividerTitle = "";
                    switch(dateCats[0]) {
                        case Var.DATE_DAY:
                            dividerTitle = Var.DAYS[dateCats[1]];
                            break;
                        case Var.DATE_THIS_WEEK: dividerTitle = "This Week"; break;
                        case Var.DATE_LAST_WEEK: dividerTitle = "Last Week"; break;
                        case Var.DATE_MONTH: dividerTitle = Var.MONTHS[dateCats[1]];
                            if(dateCats.length == 3) dividerTitle += " "+dateCats[2]; //Optional year
                            break;
                    }
                    viewHolder.div_tv.setText(dividerTitle);
                }

            }

        }

        @Override
        public int getItemCount() {
            return getFeed().getItems() == null ? 0 : getFeed().getItems().size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView thumbnail_iv;
            public TextView title_tv;
            public TextView length_tv;
            public TextView views_tv;
            public TextView status_tv;

            public TextView div_tv;

            public ViewHolder(View itemView) {
                super(itemView);
                thumbnail_iv = (ImageView) itemView.findViewById(R.id.thumbnail_iv);
                title_tv = (TextView) itemView.findViewById(R.id.title_tv);
                length_tv = (TextView) itemView.findViewById(R.id.length_tv);
                views_tv = (TextView) itemView.findViewById(R.id.views_tv);
                status_tv = (TextView) itemView.findViewById(R.id.status_tv);

                div_tv = (TextView) itemView.findViewById(R.id.text_tv);
            }

        }
    }




    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 0) {
                isSearchBusy = false;
                itemAdapter.notifyDataSetChanged();
                if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

                if(msg.arg1 == 1) {
                    new Thread() {
                        @Override
                        public void run() {
                            if (getFeed() != null && getFeed().getItems().size() > 0) MediaFeedORM.saveMediaItems(act, getFeed());
                        }
                    }.start();
                }
            }

            /*if (msg.what == 1) {
                //if (msg.arg1 == 1) listener.onError("Error getting request token");
               // else listener.onError("Error getting access token");
            } else {
                //if (msg.arg1 == 1) ;//showLoginDialog((String) msg.obj);
                //else listener.onComplete("");
                itemAdapter.notifyDataSetChanged();
                if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

            }*/
        }
    };

}
