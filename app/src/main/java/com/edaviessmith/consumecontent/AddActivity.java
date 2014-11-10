package com.edaviessmith.consumecontent;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.edaviessmith.consumecontent.data.TwitterFeed;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.data.YoutubeChannel;
import com.edaviessmith.consumecontent.data.YoutubeFeed;
import com.edaviessmith.consumecontent.util.ImageLoader;
import com.edaviessmith.consumecontent.util.TwitterAuthListener;
import com.edaviessmith.consumecontent.util.TwitterUtil;
import com.edaviessmith.consumecontent.util.Var;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;


public class AddActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener,
                                                              AbsListView.OnScrollListener, SearchView.OnQueryTextListener{
    String TAG = "AddActivity";

    SearchView searchView;
    MenuItem searchItem, saveItem;
    Integer social_icons[] = {
            R.drawable.ic_youtube_icon,
            R.drawable.ic_twitter_icon ,
            R.drawable.ic_action_new };
    Spinner icon_sp;
    ListView search_lv, feed_lv;
    SearchAdapter searchAdapter;
    FeedAdapter feedAdapter;
    List<YoutubeChannel> youtubeChannelSearch;
    List<TwitterFeed> twitterFeedSearch;
    LinearLayout search_ll;
    View searchYoutube_v, searchTwitter_v;

    ImageButton userPicture_ib, youtube_ib, twitter_ib;
    EditText userName_edt;

    int spinnerInit, spinnerSelect;
    int searchMode = Var.SEARCH_NONE;

    String search, pageToken;
    int twitterPage;
    boolean searchBusy;

    User editUser;


    //SocialAuthAdapter adapter;
    private TextView searchTwitterLogin_tv;

    private ImageLoader imageLoader;
    private SearchYoutubeTask searchTask;
    private SearchTwitterTask searchTwitterTask;
    private HttpFeedAsyncTask feedTask;

    TwitterUtil twitter;



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(spinnerSelect < spinnerInit) spinnerSelect ++; else if(position < social_icons.length - 1){

            if(searchMode == Var.SEARCH_NONE) {
                if(position == 0) toggleSearch(Var.SEARCH_YOUTUBE);
                if(position == 1) toggleSearch(Var.SEARCH_TWITTER);
            } else {
                toggleSearch(Var.SEARCH_NONE);
            }

            icon_sp.setSelection(social_icons.length - 1);
        }
    }


    private void toggleSearch(int searchMode) {
        this.searchMode = searchMode;

        if(searchMode == Var.SEARCH_NONE) {
            dismissSearch();
            search_ll.setVisibility(View.GONE);
            searchItem.setVisible(false);
            saveItem.setVisible(true);

        } else {
            search_ll.setVisibility(View.VISIBLE);
            searchView.setIconified(false);
            saveItem.setVisible(false);
            searchItem.setVisible(true);
            searchView.requestFocusFromTouch();

            searchTwitter_v.setVisibility((searchMode == Var.SEARCH_TWITTER && !twitter.hasAccessToken()) ? View.VISIBLE: View.GONE);
            searchYoutube_v.setVisibility((searchMode == Var.SEARCH_YOUTUBE) ? View.VISIBLE: View.GONE);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        imageLoader = new ImageLoader(this);
        editUser = new User();
        youtubeChannelSearch = new ArrayList<YoutubeChannel>();
        twitterFeedSearch = new ArrayList<TwitterFeed>();


        View header = getLayoutInflater().inflate(R.layout.header_add, null, false);
        View searchHeader = getLayoutInflater().inflate(R.layout.header_search_user, null, false);

        feed_lv = (ListView) findViewById(R.id.feed_lv);
        feed_lv.addHeaderView(header);


        feedAdapter = new FeedAdapter(this);
        feed_lv.setAdapter(feedAdapter);

        //Header
        userPicture_ib = (ImageButton) header.findViewById(R.id.user_picture_ib);
        userName_edt = (EditText) header.findViewById(R.id.user_name_edt);
        youtube_ib = (ImageButton) header.findViewById(R.id.youtube_ib);
        twitter_ib = (ImageButton) header.findViewById(R.id.twitter_ib);
        youtube_ib.setOnClickListener(this);
        twitter_ib.setOnClickListener(this);

        //Search
        search_ll = (LinearLayout) findViewById(R.id.search_ll);
        searchYoutube_v = searchHeader.findViewById(R.id.youtube_v);
        searchTwitter_v = searchHeader.findViewById(R.id.twitter_v);
        searchTwitterLogin_tv = (TextView) searchHeader.findViewById(R.id.twitter_login_tv);
        searchTwitterLogin_tv.setOnClickListener(this);

        search_lv = (ListView) findViewById(R.id.search_lv);
        search_lv.addHeaderView(searchHeader, null, false);
        searchAdapter = new SearchAdapter(this);
        search_lv.setAdapter(searchAdapter);
        search_lv.setOnScrollListener(this);
        search_lv.setOnItemClickListener(this);


        //Selecting image TODO: use for the profile image picker
        //icon_sp = (Spinner) findViewById(R.id.media_type_sp);
        //icon_sp.setAdapter(new IconAdapter(this, R.layout.item_image, social_icons));
        //icon_sp.setSelection(social_icons.length - 1);
        //icon_sp.setOnItemSelectedListener(this);

        twitter = new TwitterUtil(this);
        twitter.setListener(new TwitterAuthListener() {
            @Override
            public void onError(String value) {
                Toast.makeText(AddActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                Log.e("TWITTER", value);
                twitter.resetAccessToken();
            }

            @Override
            public void onComplete(String value) {
                Log.d(TAG, "twitter listener authorized " + twitter.getUsername());
            }
        });

        //if (!twitter.hasAccessToken()) twitter.authorize();

        spinnerInit = 1;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);

        saveItem = menu.findItem(R.id.action_save);
        searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);

        }

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                toggleSearch(Var.SEARCH_NONE);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        pageToken = null;
        twitterPage = 0;
        search = s;
        if(searchMode == Var.SEARCH_YOUTUBE) searchYoutube();
        if(searchMode == Var.SEARCH_TWITTER) searchTwitter();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        pageToken = null;
        twitterPage = 0;
        search = s;
        if(searchMode == Var.SEARCH_YOUTUBE) searchYoutube();
        if(searchMode == Var.SEARCH_TWITTER) searchTwitter();
        return false;
    }



    private void searchYoutube() {
        if(search!= null && search.length() > 2) {
            if (searchTask != null) {
                searchTask.cancel(true);
                searchTask = null;
            }
            searchTask = new SearchYoutubeTask();
            searchTask.execute(search);
        } else {
            youtubeChannelSearch.clear();
            searchAdapter.notifyDataSetChanged();
        }
    }

    private void searchTwitter() {
        if(search!= null && search.length() > 2) {
            if (searchTwitterTask != null) {
                searchTwitterTask.cancel(true);
                searchTwitterTask = null;
            }
            searchTwitterTask = new SearchTwitterTask();
            searchTwitterTask.execute();
        } else {
            twitterFeedSearch.clear();
            searchAdapter.notifyDataSetChanged();
        }
    }





    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(searchMode == Var.SEARCH_YOUTUBE) {
            editUser.youtubeChannel = youtubeChannelSearch.get(position - 1);
            if (editUser.youtubeChannel.getThumbnail() != null)
                imageLoader.DisplayImage(editUser.youtubeChannel.getThumbnail(), userPicture_ib);
            userName_edt.setText(editUser.youtubeChannel.getDisplayName());
        }

        toggleSearch(Var.SEARCH_NONE);
        updateFeeds();
    }

    private void updateFeeds() {
        if (feedTask != null) {
            feedTask.cancel(true);
            feedTask  = null;
        }
        feedTask  = new HttpFeedAsyncTask();
        feedTask.execute();
    }

    @Override
    public void onClick(View v) {
        if(v == youtube_ib) toggleSearch(Var.SEARCH_YOUTUBE);
        if(v == twitter_ib) toggleSearch(Var.SEARCH_TWITTER);
        if(v == searchTwitterLogin_tv) {
            twitter.resetAccessToken();
            if (!twitter.hasAccessToken()) twitter.authorize();
        }
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
       dismissSearch();
    }

    private void dismissSearch() {
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(view.getLastVisiblePosition() >= totalItemCount - 5 && !searchBusy){
            if(searchMode == Var.SEARCH_YOUTUBE) searchYoutube();
            if(searchMode == Var.SEARCH_TWITTER) searchTwitter();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    private class SearchYoutubeTask extends AsyncTask<String, Void, String> {

        String response;
        boolean isNew;
        final static int maxResults = 10;

        @Override
        protected String doInBackground(String... search) {
            searchBusy = true;
            try {

                //TODO check the internet connection here

                String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + search[0] + "&maxResults=" + maxResults
                        + "&type=channel&fields=items(id%2Csnippet)%2CnextPageToken&key=" + Var.DEVELOPER_KEY;
                if(pageToken != null && !pageToken.isEmpty()) url += "&pageToken=" + pageToken;
                else isNew = true;

                response = Var.HTTPGet(url);

            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (isCancelled()) return;
            else {
                if(isNew) {
                    youtubeChannelSearch.clear();
                    searchAdapter.notifyDataSetChanged();
                }
                try {
                    JSONObject res = new JSONObject(response);

                    if(Var.isJsonString(res, "nextPageToken"))
                        pageToken = res.getString("nextPageToken");

                    if (Var.isJsonArray(res, "items")) {
                        JSONArray items = res.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);

                            YoutubeChannel channel = new YoutubeChannel();
                            if(Var.isJsonObject(item, "id")) {                              //Channel Id
                                JSONObject id = item.getJSONObject(("id"));
                                if(Var.isJsonString(id, "channelId")) {
                                    channel.setChannelId(id.getString("channelId"));
                                }
                            }

                            if (Var.isJsonObject(item, "snippet")) {                        //Channel Name
                                JSONObject snippet = item.getJSONObject("snippet");
                                if (Var.isJsonString(snippet, "title")) {
                                    channel.setDisplayName(snippet.getString("title"));
                                }

                                if (Var.isJsonObject(snippet, "thumbnails")) {              //Channel Thumbnail
                                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                    if (Var.isJsonObject(thumbnails, "default")) {
                                        JSONObject def = thumbnails.getJSONObject("default");
                                        if (Var.isJsonString(def, "url")) {
                                            channel.setThumbnail(def.getString("url"));
                                        }
                                    }
                                }

                                youtubeChannelSearch.add(channel);
                            }

                        }
                        searchAdapter.notifyDataSetChanged();
                        if(youtubeChannelSearch.size() >= maxResults) searchBusy = false; //Keep busy if nothing is returned
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    private class HttpFeedAsyncTask extends AsyncTask<Void, Void, String> {

        String feeds;
        boolean isNew;
        final static int maxResults = 10;

        @Override
        protected String doInBackground(Void... params) {
            searchBusy = true;
            try {

                //TODO check the internet connection here

                String url = "https://www.googleapis.com/youtube/v3/channels?part=contentDetails&id="+editUser.youtubeChannel.getChannelId()+"&fields=items%2FcontentDetails&key=" + Var.DEVELOPER_KEY;
                feeds = Var.HTTPGet(url);

            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (isCancelled()) return;
            else {

                editUser.youtubeChannel.getYoutubeFeeds().clear();

                try {
                    JSONObject res = new JSONObject(feeds);
                    if (Var.isJsonArray(res, "items")) {
                        JSONArray items = res.getJSONArray("items");
                        JSONObject item = items.getJSONObject(0);
                        if(Var.isJsonObject(item, "contentDetails")) {
                            JSONObject contentDetails = item.getJSONObject(("contentDetails"));
                            if (Var.isJsonObject(contentDetails, "relatedPlaylists")) {                        //Channel Name
                                JSONObject relatedPlaylists = contentDetails.getJSONObject("relatedPlaylists");
                                if (Var.isJsonString(relatedPlaylists, "uploads"))
                                    editUser.youtubeChannel.getYoutubeFeeds().add(new YoutubeFeed(relatedPlaylists.getString("uploads"), "Youtube Uploads"));
                                if (Var.isJsonString(relatedPlaylists, "likes"))
                                    editUser.youtubeChannel.getYoutubeFeeds().add(new YoutubeFeed(relatedPlaylists.getString("likes"), "Liked Videos"));
                                if (Var.isJsonString(relatedPlaylists, "favorites"))
                                    editUser.youtubeChannel.getYoutubeFeeds().add(new YoutubeFeed(relatedPlaylists.getString("favorites"), "Favorite Videos"));
                            }
                            editUser.youtubeChannel.getYoutubeFeeds().add(new YoutubeFeed()); //Activity
                        }
                        //if(youtubeChannelSearch.size() >= maxResults) searchBusy = false; //Keep busy if nothing is returned
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }

                Log.d(TAG,"feed sync working "+editUser.youtubeChannel.getYoutubeFeeds().size());
                feedAdapter.notifyDataSetChanged();
            }
        }
    }


    public class FeedAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        Context context;

        public FeedAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return editUser.youtubeChannel.getYoutubeFeeds().size();
        }

        @Override
        public YoutubeFeed getItem(int position) {
            return editUser.youtubeChannel.getYoutubeFeeds().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_youtube_feed, parent, false);
                holder = new ViewHolder();
                holder.image_iv = (ImageView) convertView.findViewById(R.id.image_iv);
                holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.visible_sw = (SwitchCompat) convertView.findViewById(R.id.visible_sw);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            YoutubeFeed feed = getItem(position);

            holder.image_iv.setImageResource(R.drawable.ic_youtube_icon);
            if(feed.getImage() != null) imageLoader.DisplayImage(feed.getImage(), holder.image_iv);

            holder.name_tv.setText(feed.getName());
            holder.visible_sw.setChecked(feed.isVisible());


            return convertView;

        }

        class ViewHolder {
            ImageView image_iv;
            TextView name_tv;
            SwitchCompat visible_sw;
        }
    }


    public class SearchAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public SearchAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            if(searchMode == Var.SEARCH_YOUTUBE) return youtubeChannelSearch.size();
            if(searchMode == Var.SEARCH_TWITTER) return twitterFeedSearch.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(searchMode == Var.SEARCH_YOUTUBE) return youtubeChannelSearch.get(position);
            if(searchMode == Var.SEARCH_TWITTER) return twitterFeedSearch.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_youtube_search, parent, false);
                holder = new ViewHolder();
                holder.image_iv = (ImageView) convertView.findViewById(R.id.image_iv);
                holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(searchMode == Var.SEARCH_YOUTUBE) {
                YoutubeChannel feed = (YoutubeChannel) getItem(position);

                holder.image_iv.setImageResource(R.drawable.ic_youtube_icon);
                if (feed.getThumbnail() != null)  imageLoader.DisplayImage(feed.getThumbnail(), holder.image_iv);

                holder.name_tv.setText(feed.getDisplayName());
            }

            if(searchMode == Var.SEARCH_TWITTER) {
                TwitterFeed feed = (TwitterFeed) getItem(position);

                holder.image_iv.setImageResource(R.drawable.ic_twitter_icon);
                if (feed.getThumbnail() != null)  imageLoader.DisplayImage(feed.getThumbnail(), holder.image_iv);

                holder.name_tv.setText(feed.getDisplayName());
            }


            return convertView;

        }

        class ViewHolder {
            ImageView image_iv;
            TextView name_tv;
        }
    }


    protected class SearchTwitterTask extends AsyncTask<Void, Void, Integer> {
        String jsonTokenStream;
        ResponseList<twitter4j.User> users;

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                if(!twitter.hasAccessToken()) {
                    String tweeterURL = "https://api.twitter.com/1.1/users/lookup.json?screen_name=" + search;
                    HttpGet httpget = new HttpGet(tweeterURL);
                    httpget.setHeader("Authorization", "Bearer " + twitter.getBearerToken());
                    httpget.setHeader("Content-type", "application/json");

                    jsonTokenStream = Var.HTTPGet(httpget);
                } else {
                    users = twitter.twitter.searchUsers(search, twitterPage++);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 1;
        }

        protected void onPostExecute(Integer result) {
            if (isCancelled()) return;
            else {


                if(users == null || twitterPage == 1) {
                    twitterFeedSearch.clear();
                }

                try {
                    if(!twitter.hasAccessToken()) {


                        JSONArray results = new JSONArray(jsonTokenStream);

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject item = results.getJSONObject(i);

                            if (Var.isJsonString(item, "id")) {

                                TwitterFeed feed = new TwitterFeed(item.getString("id"));

                                if (Var.isJsonString(item, "name") && Var.isJsonString(item, "screen_name"))
                                    feed.setDisplayName(item.getString("name") + " @" + item.get("screen_name"));
                                if (Var.isJsonString(item, "profile_image_url"))
                                    feed.setThumbnail(item.getString("profile_image_url"));

                                twitterFeedSearch.add(feed);
                            }
                        }
                    } else {
                        Log.e("loadTwitterToken", "user search " + users.size());
                        for(twitter4j.User u : users) {
                            TwitterFeed feed = new TwitterFeed(String.valueOf(u.getId()));
                            feed.setDisplayName(u.getName() + " @" + u.getScreenName());
                            feed.setThumbnail(u.getProfileImageURL());

                            twitterFeedSearch.add(feed);
                        }
                    }
                    searchAdapter.notifyDataSetChanged();
                    Log.e("loadTwitterToken", "all worked");
                } catch (Exception e) {
                    Log.e("Tweet", "Error retrieving JSON stream" + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
    }



}
