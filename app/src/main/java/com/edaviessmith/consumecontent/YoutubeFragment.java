package com.edaviessmith.consumecontent;

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
import com.edaviessmith.consumecontent.data.YoutubeFeed;
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
import java.util.List;
import java.util.Locale;


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

        getLocalItems();

        //handler.sendMessage(handler.obtainMessage(what, 1, 0, authUrl));

        itemAdapter = new YoutubeItemAdapter(act);
        feed_rv.setAdapter(itemAdapter);

        return view;
    }

    public void getLocalItems() {


        new Thread() {
            @Override
            public void run() {


                getFeed().setItems(YoutubeItemORM.getYoutubeItems(act, getFeed().getId()));
                itemAdapter.notifyDataSetChanged();

                new GetFeedAsyncTask().execute();


                //handler.sendMessage(handler.obtainMessage(Var.HANDLER_COMPLETE, 1, 0, ""));
            }
        }.start();
    }





    private MediaFeed getFeed() {
        Log.i(TAG, "getFeed "+pos + ", "+((MediaFeed)act.getUser().getMediaFeed().get(pos)).toString());

        return act.getUser().getMediaFeed().get(pos);//youtubeFeed;
    }

    public class YoutubeItemAdapter extends RecyclerView.Adapter<YoutubeItemAdapter.ViewHolder>{


        private Context mContext;

        public YoutubeItemAdapter( Context context) {

            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_youtube, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            YoutubeItem item = (YoutubeItem) getFeed().getItems().get(i);
            //viewHolder.title_tv.setText(items.get(i).getTitle());

            //viewHolder.countryImage.setImageDrawable(mContext.getDrawable(country.getImageResourceId(mContext)));
            imageLoader.DisplayImage(item.getImageMed(), viewHolder.thumbnail_iv);
            viewHolder.title_tv.setText(item.getTitle());



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

    boolean searchBusy;

    private class GetFeedAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            searchBusy = true;
            try {

                //searchChannel.getYoutubeFeeds().clear();
                List<YoutubeItem> youtubeItems = new ArrayList<YoutubeItem>();

                //TODO check the internet connection here
                String playlistURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=20&playlistId="+URLEncoder.encode(getFeed().getFeedId(), "UTF-8")+"&key=" + Var.DEVELOPER_KEY;
                String videos = "";
                String playlistItems = Var.HTTPGet(playlistURL);

                JSONObject res = new JSONObject(playlistItems);
                if (Var.isJsonArray(res, "items")) {
                    JSONArray items = res.getJSONArray("items");

                    for(int i = 0; i< items.length(); i++) {

                        JSONObject item = items.getJSONObject(i);
                        YoutubeItem youtubeItem = new YoutubeItem();

                        if (Var.isJsonObject(item, "snippet")) {
                            JSONObject snippet = item.getJSONObject("snippet");

                            if (Var.isJsonObject(snippet, "resourceId")) {
                                JSONObject resourceId = snippet.getJSONObject("resourceId");
                                if (Var.isJsonString(resourceId, "videoId")) {                   //Video Title
                                    youtubeItem.setVideoId(resourceId.getString("videoId"));
                                }
                            }

                            if (Var.isJsonString(snippet, "title")) {
                                youtubeItem.setTitle(snippet.getString("title"));
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
                                youtubeItem.setDate(dateInMilli/1000);
                            }
                        }

                        youtubeItems.add(youtubeItem);
                    }
                }

                getFeed().setItems(youtubeItems);
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemAdapter.notifyDataSetChanged();
                    }
                });


                if(true) return null;

                //TODO should make a second call to get more information

                String playlistUrl = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&id="+ URLEncoder.encode(videos, "UTF-8") + "&fields=items(id%2Csnippet)&key=" + Var.DEVELOPER_KEY;
                String playlist = Var.HTTPGet(playlistUrl);

                JSONObject play = new JSONObject(playlist);
                if (Var.isJsonArray(play, "items")) {
                    JSONArray items = play.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);


                        if(Var.isJsonString(item, "id")) {                                  //Feed Id
                            YoutubeFeed feed = new YoutubeFeed(item.getString("id"));
                            feed.setType(Var.TYPE_YOUTUBE_PLAYLIST);

                            if (Var.isJsonObject(item, "snippet")) {                        //Feed Name
                                JSONObject snippet = item.getJSONObject("snippet");
                                if (Var.isJsonString(snippet, "title")) {
                                    if(snippet.getString("title").startsWith("Upload")) feed.setName("Uploads");
                                    else feed.setName(snippet.getString("title"));
                                }

                                if (Var.isJsonObject(snippet, "thumbnails")) {              //Feed Thumbnail
                                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                    if (Var.isJsonObject(thumbnails, "default")) {
                                        JSONObject def = thumbnails.getJSONObject("default");
                                        if (Var.isJsonString(def, "url")) {
                                            feed.setThumbnail(def.getString("url"));
                                        }
                                    }
                                }
                            }

                           // searchChannel.getYoutubeFeeds().add(feed);

                            Log.d(TAG,"youtube feed added "+feed.getName());
                        }
                    }

                    //searchChannel.getYoutubeFeeds().add(new YoutubeFeed()); //Activity
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //searchAdapter.notifyDataSetChanged();
            itemAdapter.notifyDataSetChanged();
        }
    }
}
