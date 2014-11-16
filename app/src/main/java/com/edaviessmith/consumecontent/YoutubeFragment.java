package com.edaviessmith.consumecontent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
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

import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.data.YoutubeItem;
import com.edaviessmith.consumecontent.db.YoutubeItemORM;
import com.edaviessmith.consumecontent.util.ImageLoader;
import com.edaviessmith.consumecontent.util.Listener;
import com.edaviessmith.consumecontent.util.Var;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class YoutubeFragment extends Fragment {

    private static String TAG = "YoutubeFragment";
    private static YoutubeFragment youtubeFragment;
    private static ContentActivity act;
    private int pos;

    private ImageLoader imageLoader;
    private Listener listener;
    private RecyclerView feed_rv;
    private YoutubeItemAdapter itemAdapter;

    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("US"));

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
        imageLoader = new ImageLoader(act);

        feed_rv = (RecyclerView) view.findViewById(R.id.list);
        feed_rv.setLayoutManager(new LinearLayoutManager(act));
        feed_rv.setItemAnimator(new DefaultItemAnimator());



        //handler.sendMessage(handler.obtainMessage(what, 1, 0, authUrl));

        itemAdapter = new YoutubeItemAdapter(act);
        feed_rv.setAdapter(itemAdapter);

        //final TypedArray styledAttributes = act.getTheme().obtainStyledAttributes( new int[] { android.R.attr.actionBarSize });
        //mActionBarHeight = styledAttributes.getDimension(0, 0);
        //styledAttributes.recycle();
        feed_rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @SuppressLint("NewApi")
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled "+dy+" : "+act.getSupportActionBar().isShowing());
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
                            new GetFeedAsyncTask().execute();
                        }
                    });



                    //handler.sendMessage(handler.obtainMessage(Var.HANDLER_COMPLETE, 1, 0, ""));
                }
            }.start();
        }
    }





    private MediaFeed getFeed() {
        return act.getUser().getMediaFeed().get(pos);//youtubeFeed;
    }

    public class YoutubeItemAdapter extends RecyclerView.Adapter<YoutubeItemAdapter.ViewHolder>{

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

            return new ViewHolder(v);
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0) return TYPE_DIV;
            int[] prevItemCat = Var.getTimeCategory(((YoutubeItem) getFeed().getItems().get(position - 1)).getDate());
            int[] itemCat = Var.getTimeCategory(((YoutubeItem) getFeed().getItems().get(position)).getDate());
            if((prevItemCat[0] == Var.DATE_MONTH && itemCat[0] == Var.DATE_MONTH && prevItemCat[1] != itemCat[1]) || (prevItemCat[0] != itemCat[0])) return TYPE_DIV;
            //TODO account for different months as well
            return TYPE_ITEM;
        }



        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if(getFeed().getItems().size() > i) {
                YoutubeItem item = (YoutubeItem) getFeed().getItems().get(i);

                imageLoader.DisplayImage(item.getImageMed(), viewHolder.thumbnail_iv);
                viewHolder.title_tv.setText(item.getTitle());
                viewHolder.length_tv.setText(item.getDuration());
                viewHolder.views_tv.setText(item.getViews()+" Views");

                if(getItemViewType(i) == TYPE_DIV) {
                    int[] dateCats = Var.getTimeCategory(((YoutubeItem) getFeed().getItems().get(i)).getDate());
                    String dividerTitle = "";
                    switch(dateCats[0]) {
                        case Var.DATE_TODAY: dividerTitle = "Today"; break;
                        case Var.DATE_YESTERDAY: dividerTitle = "Yesterday"; break;
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

            if (msg.what == 1) {
                if (msg.arg1 == 1) listener.onError("Error getting request token");
                else listener.onError("Error getting access token");
            } else {
                if (msg.arg1 == 1) ;//showLoginDialog((String) msg.obj);
                else listener.onComplete("");
            }
        }
    };

    boolean searchBusy;

    private class GetFeedAsyncTask extends AsyncTask<Void, Void, String> {

        final List<YoutubeItem> youtubeItems = new ArrayList<YoutubeItem>();

        @Override
        protected String doInBackground(Void... params) {
            searchBusy = true;
            try {

                final Map<String, YoutubeItem> youtubeItemMap = new HashMap<String, YoutubeItem>();

                //TODO check the internet connection here

                String url = null;
                if(getFeed().getType() == Var.TYPE_YOUTUBE_PLAYLIST) url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=20&playlistId="+URLEncoder.encode(getFeed().getFeedId(), "UTF-8")+"&key=" + Var.DEVELOPER_KEY;
                if(getFeed().getType() == Var.TYPE_YOUTUBE_ACTIVTY) url = "https://www.googleapis.com/youtube/v3/activities?part=contentDetails&channelId="+URLEncoder.encode(getFeed().getChannelHandle(), "UTF-8")+"&maxResults=20&key=" + Var.DEVELOPER_KEY;
                StringBuilder videos = new StringBuilder();
                String playlistItems = Var.HTTPGet(url);

                JSONObject res = new JSONObject(playlistItems);
                if (Var.isJsonArray(res, "items")) {
                    JSONArray items = res.getJSONArray("items");

                    for(int i = 0; i< items.length(); i++) {

                        JSONObject item = items.getJSONObject(i);
                        YoutubeItem youtubeItem = new YoutubeItem();

                        //Playlist Feed
                        if (Var.isJsonObject(item, "snippet")) {
                            JSONObject snippet = item.getJSONObject("snippet");
                            if (Var.isJsonObject(snippet, "resourceId")) {
                                JSONObject resourceId = snippet.getJSONObject("resourceId");
                                if (Var.isJsonString(resourceId, "videoId")) {
                                    youtubeItem.setVideoId(resourceId.getString("videoId"));
                                    youtubeItem.setType(Var.TYPE_UPLOAD);
                                }
                            }
                        }

                        //Activity Feed
                        if (Var.isJsonObject(item, "contentDetails")) {
                            JSONObject contentDetails = item.getJSONObject("contentDetails");

                            if (Var.isJsonObject(contentDetails, "upload")) {
                                JSONObject upload = contentDetails.getJSONObject("upload");
                                if (Var.isJsonString(upload, "videoId")) {
                                    youtubeItem.setVideoId(upload.getString("videoId"));
                                    youtubeItem.setType(Var.TYPE_UPLOAD);
                                }
                            }

                            if (Var.isJsonObject(contentDetails, "like")) {
                                JSONObject like = contentDetails.getJSONObject("like");
                                if (Var.isJsonString(like, "videoId")) {
                                    youtubeItem.setVideoId(like.getString("videoId"));
                                    youtubeItem.setType(Var.TYPE_LIKE);
                                }
                            }

                            if (Var.isJsonObject(contentDetails, "favorite")) {
                                JSONObject favorite = contentDetails.getJSONObject("favorite");
                                if (Var.isJsonString(favorite, "videoId")) {
                                    youtubeItem.setVideoId(favorite.getString("videoId"));
                                    youtubeItem.setType(Var.TYPE_FAVORITE);
                                }
                            }

                            if (Var.isJsonObject(contentDetails, "playlistItem")) {
                                JSONObject playlistItem = contentDetails.getJSONObject("playlistItem");
                                if (Var.isJsonObject(playlistItem, "resourceId")) {
                                    JSONObject resourceId = playlistItem.getJSONObject("resourceId");
                                    if (Var.isJsonString(resourceId, "videoId")) {
                                        youtubeItem.setVideoId(resourceId.getString("videoId"));
                                        youtubeItem.setType(Var.TYPE_ADD_TO_PLAYLIST);
                                    }
                                    //"playlistId": string,
                                    //"playlistItemId": string
                                }
                            }

                            /* "comment": {  //Other possible activity types
                              "resourceId": {
                                "kind": string,
                                "videoId": string,
                                "channelId": string,
                              }
                            },
                            "subscription": {
                              "resourceId": {
                                "kind": string,
                                "channelId": string,
                              }
                            } */
                        }

                        youtubeItemMap.put(youtubeItem.getVideoId(), youtubeItem);
                        videos.append(((videos.length() == 0) ? "":",") + youtubeItem.getVideoId());
                    }
                }


                String videosUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2C+contentDetails%2C+statistics&id="+ URLEncoder.encode(videos.toString(), "UTF-8") + "&key=" + Var.DEVELOPER_KEY;
                String videoResponse = Var.HTTPGet(videosUrl);

                JSONObject play = new JSONObject(videoResponse);
                if (Var.isJsonArray(play, "items")) {
                    JSONArray items = play.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);


                        if(Var.isJsonString(item, "id")) {
                            YoutubeItem youtubeItem = youtubeItemMap.get(item.getString("id"));

                            if (Var.isJsonObject(item, "snippet")) {
                                JSONObject snippet = item.getJSONObject("snippet");

                                if (Var.isJsonString(snippet, "title")) {
                                    youtubeItem.setTitle(snippet.getString("title"));
                                }
                                if (Var.isJsonString(snippet, "description")) {
                                    youtubeItem.setDescription(snippet.getString("description"));
                                }


                                if (Var.isJsonObject(snippet, "thumbnails")) {                      //Video Thumbnails
                                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                    if (Var.isJsonObject(thumbnails, "medium")) {
                                        JSONObject def = thumbnails.getJSONObject("medium");
                                        if (Var.isJsonString(def, "url")) {
                                            youtubeItem.setImageMed(def.getString("url"));
                                        }
                                    }
                                    if (Var.isJsonObject(thumbnails, "high")) {
                                        JSONObject def = thumbnails.getJSONObject("high");
                                        if (Var.isJsonString(def, "url")) {
                                            youtubeItem.setImageMed(def.getString("url"));
                                        }
                                    }
                                }

                                if (Var.isJsonString(snippet, "publishedAt")) {
                                    String published = snippet.getString("publishedAt");

                                    long dateInMilli = formatter.parse(published).getTime();
                                    youtubeItem.setDate(dateInMilli / 1000);
                                }

                                if (Var.isJsonObject(item, "contentDetails")) {
                                    JSONObject contentDetails = item.getJSONObject("contentDetails");
                                    if (Var.isJsonString(contentDetails, "duration")) {
                                        youtubeItem.setLength(Var.getStringFromDuration(contentDetails.getString("duration")));
                                    }
                                }

                                if (Var.isJsonObject(item, "statistics")) {
                                    JSONObject statistics = item.getJSONObject("statistics");
                                    if (Var.isJsonString(statistics, "viewCount")) {
                                        youtubeItem.setViews(statistics.getInt("viewCount"));
                                    }
                                    if (Var.isJsonString(statistics, "likeCount")) {
                                        youtubeItem.setLikes(statistics.getInt("likeCount"));
                                    }
                                    if (Var.isJsonString(statistics, "dislikeCount")) {
                                        youtubeItem.setDislikes(statistics.getInt("dislikeCount"));
                                    }
                                    //Also Favorite and Comment count
                                }

                            }

                            youtubeItems.add(youtubeItem);
                            // searchChannel.getYoutubeFeeds().add(feed);

                            Log.d(TAG,"youtube feed added "+youtubeItem.getVideoId());
                        }
                    }

                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG,"adding youtube items "+youtubeItems.size());
            getFeed().setItems(youtubeItems);
            itemAdapter.notifyDataSetChanged();
        }
    }
}
