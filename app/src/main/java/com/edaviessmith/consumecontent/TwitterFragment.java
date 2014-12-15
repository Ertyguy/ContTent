package com.edaviessmith.consumecontent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.TwitterFeed;
import com.edaviessmith.consumecontent.data.TwitterItem;
import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.service.ActionDispatch;
import com.edaviessmith.consumecontent.service.ActionFragment;
import com.edaviessmith.consumecontent.util.TwitterFeedAsyncTask;
import com.edaviessmith.consumecontent.util.Var;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TwitterFragment extends ActionFragment {

    private ContentActivity act;
    private int userId, mediaFeedId;
    private int feedState = Var.FEED_WAITING;

    private RecyclerView feed_rv;
    private TwitterItemAdapter itemAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;



    public static TwitterFragment newInstance(int userId, int mediaFeedId) {
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        args.putInt("mediaFeedId", mediaFeedId);
        TwitterFragment youtubeFragment = new TwitterFragment();
        youtubeFragment.setArguments(args);
        return youtubeFragment;
    }

    public TwitterFragment() {
        actionDispatch = new ActionDispatch() {

            @Override
            public void binderReady() {
                super.binderReady();

                if (isFragmentOpen(userId, mediaFeedId)) {
                    getBinder().fetchItemsByMediaFeedId(getFeed().getId());
                }
                Log.d(TAG, "binderReady");
            }

            @Override
            public void updatedMediaFeed(int mediaFeedId, int feedState) {
                super.updatedMediaFeed(mediaFeedId, feedState);

                Log.d(TAG, "updatedUserMediaFeed "+ mediaFeedId + "-"+feedState);
                if (isFragmentOpen(userId, mediaFeedId)) {

                    if(feedState == Var.FEED_WAITING || feedState == Var.FEED_END) {
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);

                        if (!Var.isRecent(getFeed().getLastUpdate()) && feedState != Var.FEED_END && !DB.isValid(getFeed().getNextPageToken())) {//getFeed().getItems().size() == 0) {  //No local items so
                            Log.d(TAG, "getNextPage AsyncTask "+ getFeed().getNextPageToken());
                            feedState = Var.FEED_LOADING;
                            new TwitterFeedAsyncTask(act, getFeed(), getBinder().getTwitter(), userId,  actionDispatch).execute(getFeed().getNextPageToken());
                        }
                    }

                    setFeedState(feedState);

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            itemAdapter.notifyDataSetChanged();
                        }
                    });


                }
            }

            @Override
            public void updateMediaFeedDatabase(int userId, int mediaFeedId) {
                super.updateMediaFeedDatabase(userId, mediaFeedId);

                if(getBinder() != null) getBinder().saveMediaFeedItems(userId, mediaFeedId);

            }


        };
    }

    private boolean isFragmentOpen(int userId, int mediaFeedId) {

        return (getBinder() != null && getBinder().containsUser(userId) &&
                getBinder().getUser(userId).getCastMediaFeed().get(mediaFeedId) != null && this.mediaFeedId == mediaFeedId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = (ContentActivity) getActivity();
        userId = getArguments() != null ? getArguments().getInt("userId") : -1;
        mediaFeedId = getArguments() != null ? getArguments().getInt("mediaFeedId") : -1;

        View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        view.setId(userId);

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
                    new TwitterFeedAsyncTask(act, getFeed(), getBinder().getTwitter(), userId, actionDispatch).execute(-1);
                }
            }
        });

        itemAdapter = new TwitterItemAdapter(act);
        feed_rv.setAdapter(itemAdapter);
        feed_rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(feedState == Var.FEED_WAITING && linearLayoutManager.findLastVisibleItemPosition() > linearLayoutManager.getItemCount() - Var.SCROLL_OFFSET) {
                    setFeedState(Var.FEED_LOADING);
                    new TwitterFeedAsyncTask(act, getFeed(), getBinder().getTwitter(), userId, actionDispatch).execute(getFeed().getNextPageToken());
                    Log.d(TAG,"onScrolled getFeed called");
                }
            }

        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(getBinder() != null) getBinder().fetchItemsByMediaFeedId(getFeed().getId());
    }



    private TwitterFeed getFeed() {
        return (TwitterFeed) getBinder().getUser(userId).getCastMediaFeed().get(mediaFeedId);
    }


    public void setFeedState(int feedState) {
        this.feedState = feedState;
    }

    public class TwitterItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

        private static final int TYPE_DIV = 0;
        private static final int TYPE_ITEM = 1;
        private static final int TYPE_FOOTER = 2;
        private Context context;

        public TwitterItemAdapter( Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = null;
            if(i == TYPE_DIV) v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_twitter_divider, viewGroup, false);
            else if(i == TYPE_ITEM) v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_twitter, viewGroup, false);
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
            /*
            int itemPosition = feed_rv.getChildPosition(view);
            if(itemPosition < getItemCount() - 1)  act.startVideo(getFeed().getItems().get(itemPosition));
            else */
            if(feedState == Var.FEED_WARNING || feedState == Var.FEED_OFFLINE || feedState == Var.FEED_END) {
                setFeedState(Var.FEED_LOADING);
                new TwitterFeedAsyncTask(act, getFeed(), getBinder().getTwitter(), userId, actionDispatch).execute(getFeed().getNextPageToken());
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof ViewHolderItem) {

                final ViewHolderItem holder = (ViewHolderItem) viewHolder;
                TwitterItem item = getFeed().getItems().get(i);

                holder.thumbnail_v.setVisibility(!Var.isEmpty(item.getImageHigh())? View.VISIBLE: View.GONE);
                if(!Var.isEmpty(item.getImageHigh())) {
                    getBinder().getImageLoader().DisplayImage(item.getImageHigh(), holder.thumbnail_iv, holder.thumbnail_pb);
                }
                //Regex and highlight @
                String text = item.getDescription();
                if(!Var.isEmpty(text)) {
                    ArrayList<Integer> tags = new ArrayList<Integer>();
                    char tag = '@';
                    for (int index = text.indexOf(tag); index >= 0; index = text.indexOf(tag, index + 1)) {
                        tags.add(index);
                    }

                    Spannable wordToSpan = new SpannableString(text);
                    for (int tagIndex : tags) {
                        Pattern p = Pattern.compile("@[a-zA-Z0-9]+");
                        Matcher m = p.matcher(text.substring(tagIndex));
                        if (m.find()) {
                            int tagEnd = m.end();
                            if (tagEnd > 0) {
                                wordToSpan.setSpan(new ForegroundColorSpan(R.color.blue_twitter), tagIndex, tagIndex + tagEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }
                    holder.description_tv.setText(wordToSpan);
                }

                holder.date_tv.setText(Var.getShortTime(item.getDate()));

                holder.status_v.setVisibility(item.getType() != Var.TYPE_TWEET? View.VISIBLE: View.GONE);
                //holder.user_iv.setVisibility(item.getType() != Var.TYPE_TWEET? View.VISIBLE: View.GONE);
                if(item.getType() == Var.TYPE_TWEET) {

                }
                if(item.getType() == Var.TYPE_RETWEET) {
                    holder.title_tv.setText(item.getTitle());

                }

                if (!Var.isEmpty(item.getTweetThumbnail())) {
                    getBinder().getImageLoader().DisplayImage(item.getTweetThumbnail(), holder.user_iv);
                }

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
            }

            if (viewHolder instanceof ViewHolderFooter) {
                ViewHolderFooter footer = (ViewHolderFooter) viewHolder;

                boolean loading = (feedState == Var.FEED_WAITING || feedState == Var.FEED_LOADING);

                footer.loading_v.setVisibility (loading? View.VISIBLE: View.GONE);
                footer.warning_iv.setVisibility(loading? View.GONE: View.VISIBLE);
                footer.warning_tv.setVisibility(loading? View.INVISIBLE: View.VISIBLE);

                if(feedState != Var.FEED_LOADING) {
                    footer.warning_iv.setImageResource(feedState == Var.FEED_OFFLINE ?
                            R.drawable.ic_error_red_36dp:
                            R.drawable.ic_warning_amber_36dp);

                    if(feedState == Var.FEED_END)   footer.warning_tv.setText("No more videos found");
                    if(feedState == Var.FEED_WARNING) footer.warning_tv.setText("Could not connect to Youtube");
                    if(feedState == Var.FEED_OFFLINE) footer.warning_tv.setText("No Internet connection");
                }

            }
        }

        @Override
        public int getItemCount() {
            return (getBinder() == null  || getFeed() == null || getFeed().getItems() == null) ? 1 : getFeed().getItems().size() + 1;
        }


        public class ViewHolderItem extends RecyclerView.ViewHolder {
            public ImageView user_iv;
            public View status_v;
            public View thumbnail_v;
            public ImageView thumbnail_iv;
            public ProgressBar thumbnail_pb;
            public TextView title_tv;
            public TextView description_tv;
            public TextView status_tv;
            public TextView date_tv;
            public TextView div_tv;

            public ViewHolderItem(View itemView) {
                super(itemView);
                status_v = itemView.findViewById(R.id.status_v);
                thumbnail_v = itemView.findViewById(R.id.thumbnail_v);
                user_iv = (ImageView) itemView.findViewById(R.id.user_iv);
                thumbnail_iv = (ImageView) itemView.findViewById(R.id.thumbnail_iv);
                thumbnail_pb = (ProgressBar) itemView.findViewById(R.id.thumbnail_pb);
                title_tv = (TextView) itemView.findViewById(R.id.title_tv);
                description_tv = (TextView) itemView.findViewById(R.id.description_tv);
                status_tv = (TextView) itemView.findViewById(R.id.status_tv);
                date_tv = (TextView) itemView.findViewById(R.id.date_tv);
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
