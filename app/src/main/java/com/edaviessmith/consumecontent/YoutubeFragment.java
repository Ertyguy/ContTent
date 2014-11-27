package com.edaviessmith.consumecontent;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.YoutubeFeed;
import com.edaviessmith.consumecontent.data.YoutubeItem;
import com.edaviessmith.consumecontent.db.MediaFeedORM;
import com.edaviessmith.consumecontent.util.ActionDispatch;
import com.edaviessmith.consumecontent.util.ActionFragment;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.util.YoutubeFeedAsyncTask;


public class YoutubeFragment extends ActionFragment {

    private ContentActivity act;
    private int pos, tab;
    private Handler handler;

    //private boolean isSearchBusy; //Only make a single request to API
    private int feedState = Var.FEED_WAITING;

    private RecyclerView feed_rv;
    private YoutubeItemAdapter itemAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;



    public static YoutubeFragment newInstance(int pos, int tab) {
        Bundle args = new Bundle();
        args.putInt("pos", pos);
        args.putInt("tab", tab);
        YoutubeFragment youtubeFragment = new YoutubeFragment();
        youtubeFragment.setArguments(args);
        return youtubeFragment;
    }

    public YoutubeFragment() {
        actionDispatch = new ActionDispatch() {

            @Override
            public void binderReady() {
                super.binderReady();

                Log.d(TAG, "binderReady");
            }

            @Override
            public void updatedUserMediaFeed(int userId, int mediaFeedId) {
                super.updatedUserMediaFeed(userId, mediaFeedId);

                Log.d(TAG, "updatedUserMediaFeed "+userId + mediaFeedId);

                if(pos == userId && tab == mediaFeedId) {
                    itemAdapter.notifyDataSetChanged();

                    if(Var.isEmpty(getFeed().getNextPageToken())) {//getFeed().getItems().size() == 0) {  //No local items so
                        setFeedState(Var.FEED_LOADING);
                        new YoutubeFeedAsyncTask(act, getFeed(), handler).execute(getFeed().getNextPageToken());
                    }
                }
            }
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = (ContentActivity) getActivity();
        pos = getArguments() != null ? getArguments().getInt("pos") : -1;
        tab = getArguments() != null ? getArguments().getInt("tab") : -1;

        View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        view.setId(pos);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    setFeedState(Var.FEED_WAITING);
                    //itemAdapter.notifyDataSetChanged();
                    if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

                    if (getFeed() != null && msg.arg1 == 1 && getFeed().getId() == msg.arg2) {
                        new Thread() {
                            @Override
                            public void run() {
                                Log.d(TAG, "check" + (getFeed().getItems() != null));
                                if (getFeed().getItems() != null && getFeed().getItems().size() > 0) {
                                    MediaFeedORM.saveMediaItems(act, getFeed());
                                }
                            }
                        }.start();
                    }
                } else {
                    if (getFeed().getId() == msg.arg2) setFeedState(msg.arg1);
                }
            }
        };

        feed_rv = (RecyclerView) view.findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(act);
        feed_rv.setLayoutManager(linearLayoutManager);
        feed_rv.setItemAnimator(new DefaultItemAnimator());


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(feedState != Var.FEED_LOADING) {
                    setFeedState(Var.FEED_LOADING);
                    new YoutubeFeedAsyncTask(act, getFeed(), handler).execute("");
                }
            }
        });



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

                if(feedState == Var.FEED_WAITING && linearLayoutManager.findLastVisibleItemPosition() > linearLayoutManager.getItemCount() - Var.SCROLL_OFFSET) {
                    setFeedState(Var.FEED_LOADING);
                    new YoutubeFeedAsyncTask(act, getFeed(), handler).execute(getFeed().getNextPageToken());
                    Log.d(TAG,"onScrolled getFeed called");
                }

                Log.d(TAG, "onScrolled " + dy + " : " + act.getSupportActionBar().isShowing());
                //TODO much thinking needed to hide toolbar on scroll
                //if (dy >= mActionBarHeight && act.getSupportActionBar().isShowing()) {
                //if (android.os.Build.VERSION.SDK_INT >= 12) ;
                     //act.toolbar.animate().translationY(-act.toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    //act.getSupportActionBar().hide();
                //} else if (dy <= -mActionBarHeight && !act.getSupportActionBar().isShowing()) {
                    //act.getSupportActionBar().show();
                //}
            }
        });

        if(getBinder() != null) {
            getBinder().fetchYoutubeItemsByMediaFeedId(getFeed().getId());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getBinder().fetchYoutubeItemsByMediaFeedId(getFeed().getId());
    }



    private YoutubeFeed getFeed() {
        return (YoutubeFeed) getBinder().getUser(pos).getCastMediaFeed().get(tab);
    }


    public void setFeedState(int feedState) {
        boolean change = (this.feedState != feedState);
        this.feedState = feedState;
        if(change) itemAdapter.notifyDataSetChanged();
        //itemAdapter.notifyItemChanged(itemAdapter.getItemCount() - 1);
    }

    public class YoutubeItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

        private static final int TYPE_DIV = 0;
        private static final int TYPE_ITEM = 1;
        private static final int TYPE_FOOTER = 2;
        private Context context;



        public YoutubeItemAdapter( Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = null;
            if(i == TYPE_DIV) v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_youtube_divider, viewGroup, false);
            else if(i == TYPE_ITEM) v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_youtube, viewGroup, false);
            else if(i == TYPE_FOOTER) v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading_footer, viewGroup, false);

            if(v != null) v.setOnClickListener(this);
            if(i == TYPE_DIV || i == TYPE_ITEM)  return new ViewHolderItem(v);
            if(i == TYPE_FOOTER) return new ViewHolderFooter(v);
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            if(position >= getItemCount() - 1) return TYPE_FOOTER;
            if(position == 0) return TYPE_DIV;

            int[] prevItemCat = Var.getTimeCategory(( getFeed().getItems().get(position - 1)).getDate());
            int[] itemCat = Var.getTimeCategory((getFeed().getItems().get(position)).getDate());
            if((prevItemCat[0] == Var.DATE_DAY && itemCat[0] == Var.DATE_DAY && prevItemCat[1] != itemCat[1])) return TYPE_DIV;
            if((prevItemCat[0] == Var.DATE_MONTH && itemCat[0] == Var.DATE_MONTH && prevItemCat[1] != itemCat[1])) return TYPE_DIV;
            if(prevItemCat[0] != itemCat[0]) return TYPE_DIV;
            return TYPE_ITEM;
        }

        @Override
        public void onClick(final View view) {
            int itemPosition = feed_rv.getChildPosition(view);
            if(itemPosition < getItemCount() - 1)  act.startVideo(getFeed().getItems().get(itemPosition));
            else if(feedState == Var.FEED_WARNING || feedState == Var.FEED_OFFLINE) {
                setFeedState(Var.FEED_LOADING);
                new YoutubeFeedAsyncTask(act, getFeed(), handler).execute(getFeed().getNextPageToken());

            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof ViewHolderItem) {
                //if (getFeed().getItems().size() > i) {
                ViewHolderItem holder = (ViewHolderItem) viewHolder;
                YoutubeItem item = getFeed().getItems().get(i);

                getBinder().getImageLoader().DisplayImage(item.getImageMed(), holder.thumbnail_iv, holder.thumbnail_pb);
                holder.title_tv.setText(item.getTitle());
                holder.length_tv.setText(item.getDuration());

                if (getFeed().getType() == Var.TYPE_YOUTUBE_PLAYLIST) {
                    holder.views_tv.setText(Var.displayViews(item.getViews()));
                }
                if (getFeed().getType() == Var.TYPE_YOUTUBE_ACTIVTY) {
                    holder.views_tv.setText(Var.displayActivity(item.getType()));
                }
                //holder.views_tv.setText(Var.simpleDate.format(new Date(item.getDate())));

                if (getItemViewType(i) == TYPE_DIV) {
                    int[] dateCats = Var.getTimeCategory((getFeed().getItems().get(i)).getDate());
                    String dividerTitle = "";
                    switch (dateCats[0]) {
                        case Var.DATE_DAY:
                            dividerTitle = Var.DAYS[dateCats[1]];
                            break;
                        case Var.DATE_THIS_WEEK:
                            dividerTitle = "This Week";
                            break;
                        case Var.DATE_LAST_WEEK:
                            dividerTitle = "Last Week";
                            break;
                        case Var.DATE_MONTH:
                            dividerTitle = Var.MONTHS[dateCats[1]];
                            if (dateCats.length == 3)
                                dividerTitle += " " + dateCats[2]; //Optional year
                            break;
                    }
                    holder.div_tv.setText(dividerTitle);
                }
                //}
            }
            if (viewHolder instanceof ViewHolderFooter) {
                ViewHolderFooter footer = (ViewHolderFooter) viewHolder;

                boolean loading = (feedState == Var.FEED_WAITING || feedState == Var.FEED_LOADING);

                footer.loading_v.setVisibility (loading? View.VISIBLE: View.GONE);
                footer.warning_iv.setVisibility(loading? View.GONE: View.VISIBLE);
                footer.warning_tv.setVisibility(loading? View.INVISIBLE: View.VISIBLE);

                if(feedState != Var.FEED_LOADING) {
                    footer.warning_iv.setImageResource(feedState == Var.FEED_WARNING ?
                                                        R.drawable.ic_warning_amber_36dp :
                                                        R.drawable.ic_error_red_36dp);

                    if(feedState == Var.FEED_WARNING) footer.warning_tv.setText("Could not connect to Youtube");
                    if(feedState == Var.FEED_OFFLINE) footer.warning_tv.setText("No Internet connection");
                }

            }
        }

        @Override
        public int getItemCount() {
            return (getBinder() == null || getFeed().getItems() == null) ? 1 : getFeed().getItems().size() + 1;
        }


        public class ViewHolderItem extends RecyclerView.ViewHolder {
            public ImageView thumbnail_iv;
            public ProgressBar thumbnail_pb;
            public TextView title_tv;
            public TextView length_tv;
            public TextView views_tv;
            public TextView status_tv;

            public TextView div_tv;

            public ViewHolderItem(View itemView) {
                super(itemView);
                thumbnail_iv = (ImageView) itemView.findViewById(R.id.thumbnail_iv);
                thumbnail_pb = (ProgressBar) itemView.findViewById(R.id.thumbnail_pb);
                title_tv = (TextView) itemView.findViewById(R.id.title_tv);
                length_tv = (TextView) itemView.findViewById(R.id.length_tv);
                views_tv = (TextView) itemView.findViewById(R.id.views_tv);
                status_tv = (TextView) itemView.findViewById(R.id.status_tv);
                div_tv = (TextView) itemView.findViewById(R.id.text_tv);
            }
        }

        class ViewHolderFooter extends RecyclerView.ViewHolder {
            public View loading_v;
            public ImageView warning_iv;
            public TextView warning_tv;

            public ViewHolderFooter(View itemView) {
                super(itemView);
                loading_v = itemView.findViewById(R.id.loading_pb);
                warning_iv = (ImageView) itemView.findViewById(R.id.warning_iv);
                warning_tv = (TextView) itemView.findViewById(R.id.warning_tv);
            }
        }
    }


}
