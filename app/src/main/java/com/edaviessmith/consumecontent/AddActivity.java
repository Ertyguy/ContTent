package com.edaviessmith.consumecontent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.TwitterFeed;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.data.YoutubeChannel;
import com.edaviessmith.consumecontent.data.YoutubeFeed;
import com.edaviessmith.consumecontent.db.GroupORM;
import com.edaviessmith.consumecontent.db.UserORM;
import com.edaviessmith.consumecontent.util.ImageLoader;
import com.edaviessmith.consumecontent.util.TwitterAuthListener;
import com.edaviessmith.consumecontent.util.TwitterUtil;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.view.SpinnerTrigger;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;


public class AddActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener,
                                                              AbsListView.OnScrollListener, SearchView.OnQueryTextListener{
    String TAG = "AddActivity";

    SearchView searchView;
    MenuItem searchItem, saveItem;
    ListView search_lv, feed_lv;
    SearchAdapter searchAdapter;
    FeedAdapter feedAdapter;
    GroupAdapter groupAdapter;
    List<YoutubeChannel> youtubeChannelSearch;
    List<TwitterFeed> twitterFeedSearch;
    List<Group> groups;
    List<String> userPictureThumbnails;
    LinearLayout search_ll;
    View searchYoutube_v, searchTwitter_v, searchDiv_v;

    final static int maxResults = 10;

    View youtube_v, twitter_v;
    TextView youtubeName_tv, twitterHandle_tv, searchMessage_tv;
    EditText userName_edt;
    Spinner userPicture_sp;
    SpinnerTrigger group_sp;
    SwitchCompat notification_sw;
    IconAdapter iconAdapter;
    int spinnerInit, spinnerSelect;
    int searchMode = Var.SEARCH_NONE;
    SearchView.SearchAutoComplete search_edt;
    String search, pageToken;
    int twitterPage;
    boolean searchBusy;

    User editUser;


    //SocialAuthAdapter adapter;
    private TextView searchTwitterLogin_tv;

    private ImageLoader imageLoader;
    private SearchYoutubeTask searchTask;
    private SearchTwitterTask searchTwitterTask;
    private GetFeedAsyncTask feedTask;

    TwitterUtil twitter;


    private void toggleSearch(int searchMode) {
        this.searchMode = searchMode;

        if(searchMode == Var.SEARCH_NONE) {
            dismissSearch();
            search_ll.setVisibility(View.GONE);
            search_edt.setText("");
            searchItem.setVisible(false);
            saveItem.setVisible(true);

            youtubeChannelSearch.clear();
            twitterFeedSearch.clear();
            searchAdapter.notifyDataSetChanged();
        } else {
            search_ll.setVisibility(View.VISIBLE);
            searchView.setIconified(false);
            saveItem.setVisible(false);
            searchItem.setVisible(true);
            searchView.requestFocusFromTouch();

            searchTwitter_v.setVisibility((searchMode == Var.SEARCH_TWITTER && !twitter.hasAccessToken()) ? View.VISIBLE: View.GONE);
            searchDiv_v.setVisibility((searchMode == Var.SEARCH_TWITTER && twitter.hasAccessToken()) ? View.GONE: View.VISIBLE);
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
        userPictureThumbnails = new ArrayList<String>();

        groups = GroupORM.getVisibleGroups(this);

        View header = getLayoutInflater().inflate(R.layout.header_add, null, false);
        View searchHeader = getLayoutInflater().inflate(R.layout.header_search_user, null, false);

        feed_lv = (ListView) findViewById(R.id.feed_lv);
        feed_lv.addHeaderView(header, null, false);


        feedAdapter = new FeedAdapter(this);
        feed_lv.setAdapter(feedAdapter);

        //Header
        userPicture_sp = (Spinner) header.findViewById(R.id.user_picture_sp);
        userName_edt = (EditText) header.findViewById(R.id.user_name_edt);
        group_sp = (SpinnerTrigger) header.findViewById(R.id.group_sp);
        notification_sw = (SwitchCompat) header.findViewById(R.id.notification_sw);
        youtube_v = header.findViewById(R.id.youtube_v);
        twitter_v = header.findViewById(R.id.twitter_v);
        youtube_v.setOnClickListener(this);
        twitter_v.setOnClickListener(this);
        youtubeName_tv = (TextView) header.findViewById(R.id.youtube_name_tv);
        twitterHandle_tv = (TextView) header.findViewById(R.id.twitter_handle_tv);

        groupAdapter = new GroupAdapter(this, groups);
        group_sp.setAdapter(groupAdapter);
        group_sp.setOnItemSelectedListener(this);

        //Search
        search_ll = (LinearLayout) findViewById(R.id.search_ll);
        searchYoutube_v = searchHeader.findViewById(R.id.youtube_v);
        searchTwitter_v = searchHeader.findViewById(R.id.twitter_v);
        searchDiv_v = searchHeader.findViewById(R.id.div_v);
        searchMessage_tv = (TextView) searchHeader.findViewById(R.id.search_message_tv);
        searchTwitterLogin_tv = (TextView) searchHeader.findViewById(R.id.twitter_login_tv);
        searchTwitterLogin_tv.setOnClickListener(this);

        search_lv = (ListView) findViewById(R.id.search_lv);
        search_lv.addHeaderView(searchHeader, null, false);
        searchAdapter = new SearchAdapter(this);
        search_lv.setAdapter(searchAdapter);
        search_lv.setOnScrollListener(this);
        search_lv.setOnItemClickListener(this);


        iconAdapter = new IconAdapter(this, userPictureThumbnails, imageLoader);
        userPicture_sp.setAdapter(iconAdapter);
        //userPicture_sp.setOnItemSelectedListener(this);

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

        spinnerInit = 1;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Add User");
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
            search_edt = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

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
        searchMessage_tv.setVisibility(View.GONE);
        searchTimer(search);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        pageToken = null;
        twitterPage = 0;
        search = s;
        searchMessage_tv.setVisibility(View.GONE);
        searchTimer(search);
        return false;
    }


    Handler handler = new Handler();
    private void searchTimer(final String newText) {
        if(handler!= null) handler.removeCallbacksAndMessages(null);
        handler.postDelayed(runnable(newText), 800);
    }

    private Runnable runnable(final String newText) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                if(searchMode == Var.SEARCH_YOUTUBE) searchYoutube();
                if(searchMode == Var.SEARCH_TWITTER) searchTwitter();
            }

        };
        return runnable;
    }


    private void searchYoutube() {
        if(search != null && search.length() > 2) {
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
        if(search != null && search.length() > 2) {
            if (searchTwitterTask != null) {
                searchTwitterTask.cancel(true);
                searchTwitterTask = null;
            }
            searchTwitter_v.setVisibility(twitter.hasAccessToken() ? View.GONE: View.VISIBLE);
            searchDiv_v.setVisibility(twitter.hasAccessToken() ? View.GONE: View.VISIBLE);

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
            editUser.setYoutubeChannel(youtubeChannelSearch.get(position - 1));
            updateThumbnails();
            if (!Var.isEmpty(editUser.getThumbnail()) && !Var.isEmpty(editUser.getYoutubeChannel().getThumbnail())) {
                editUser.setThumbnail(editUser.getYoutubeChannel().getThumbnail());
                userPicture_sp.setSelection(0);
            }
            if(Var.isEmpty(userName_edt.getText().toString())) userName_edt.setText(editUser.getYoutubeChannel().getName());

            youtubeName_tv.setText(getResources().getString(R.string.youtube_channel) + " ("+editUser.getYoutubeChannel().getName()+")");
        }

        if(searchMode == Var.SEARCH_TWITTER) {
            editUser.setTwitterFeed(twitterFeedSearch.get(position - 1));
            updateThumbnails();
            if (!Var.isEmpty(editUser.getThumbnail()) && !Var.isEmpty(editUser.getTwitterFeed().getThumbnail())) {
                editUser.setThumbnail(editUser.getTwitterFeed().getThumbnail());
                userPicture_sp.setSelection(userPictureThumbnails.size() - 1);
            }

            if(Var.isEmpty(userName_edt.getText().toString())) userName_edt.setText(editUser.getTwitterFeed().getDisplayName());
            twitterHandle_tv.setText(getResources().getString(R.string.twitter_handle) + " ("+editUser.getTwitterFeed().getName()+")");
        }

        toggleSearch(Var.SEARCH_NONE);
        updateFeeds();
    }

    private void updateThumbnails() {
        userPictureThumbnails.clear();

        if(!Var.isEmpty(editUser.getYoutubeChannel().getThumbnail())) userPictureThumbnails.add(editUser.getYoutubeChannel().getThumbnail());
        if(!Var.isEmpty(editUser.getTwitterFeed().getThumbnail())) userPictureThumbnails.add(editUser.getTwitterFeed().getThumbnail());

        iconAdapter.notifyDataSetChanged();
    }

    private void updateFeeds() {
        if (feedTask != null) {
            feedTask.cancel(true);
            feedTask  = null;
        }
        feedTask  = new GetFeedAsyncTask();
        feedTask.execute();
    }

    @Override
    public void onClick(View v) {
        if(v == youtube_v) toggleSearch(Var.SEARCH_YOUTUBE);
        if(v == twitter_v) toggleSearch(Var.SEARCH_TWITTER);
        if(v == searchTwitterLogin_tv) {
            twitter.resetAccessToken();
            if (!twitter.hasAccessToken()) twitter.authorize();
            //Hide the signin
            searchTwitter_v.setVisibility(View.GONE);
            searchDiv_v.setVisibility(View.GONE);
        }
    }



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
            //if(searchMode == Var.SEARCH_TWITTER) searchTwitter();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_save) {
            editUser.setName(userName_edt.getText().toString().trim());
            editUser.setNotification(notification_sw.isChecked());
            editUser.setThumbnail((String) userPicture_sp.getSelectedItem());


            UserORM.saveUser(this, editUser);

            finish();
        }

        if(id == android.R.id.home) {
            if(searchMode != Var.SEARCH_NONE) {
                toggleSearch(Var.SEARCH_NONE);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(searchMode != Var.SEARCH_NONE) {
            toggleSearch(Var.SEARCH_NONE);
            return;
        }

        super.onBackPressed();
    }



    private class SearchYoutubeTask extends AsyncTask<String, Void, String> {

        String response;
        boolean isNew;


        @Override
        protected String doInBackground(String... search) {
            searchBusy = true;
            try {

                //TODO check the internet connection here

                String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + URLEncoder.encode(search[0], "UTF-8") + "&maxResults=" + maxResults
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
                                    channel.setFeedId(id.getString("channelId"));
                                }
                            }

                            if (Var.isJsonObject(item, "snippet")) {                        //Channel Name
                                JSONObject snippet = item.getJSONObject("snippet");
                                if (Var.isJsonString(snippet, "title")) {
                                    channel.setName(snippet.getString("title"));
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

    private class GetFeedAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            searchBusy = true;
            try {

                editUser.getYoutubeChannel().getYoutubeFeeds().clear();

                //TODO check the internet connection here
                String channelUrl = "https://www.googleapis.com/youtube/v3/channels?part=contentDetails&id="+editUser.getYoutubeChannel().getFeedId()+"&fields=items%2FcontentDetails&key=" + Var.DEVELOPER_KEY;
                String playlists = "";
                String channel = Var.HTTPGet(channelUrl);

                JSONObject res = new JSONObject(channel);
                if (Var.isJsonArray(res, "items")) {
                    JSONArray items = res.getJSONArray("items");
                    JSONObject item = items.getJSONObject(0);
                    if(Var.isJsonObject(item, "contentDetails")) {
                        JSONObject contentDetails = item.getJSONObject(("contentDetails"));
                        if (Var.isJsonObject(contentDetails, "relatedPlaylists")) {                        //Channel Name
                            JSONObject relatedPlaylists = contentDetails.getJSONObject("relatedPlaylists");
                            if (Var.isJsonString(relatedPlaylists, "uploads"))
                                playlists += (Var.isEmpty(playlists)? "" : ",") + relatedPlaylists.getString("uploads");
                            if (Var.isJsonString(relatedPlaylists, "likes"))
                                playlists += (Var.isEmpty(playlists)? "" : ",") + relatedPlaylists.getString("likes");
                            if (Var.isJsonString(relatedPlaylists, "favorites"))
                                playlists += (Var.isEmpty(playlists)? "" : ",") + relatedPlaylists.getString("favorites");
                        }
                    }
                }

                String playlistUrl = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&id="+ URLEncoder.encode(playlists, "UTF-8") + "&fields=items(id%2Csnippet)&key=" + Var.DEVELOPER_KEY;
                String playlist = Var.HTTPGet(playlistUrl);

                JSONObject play = new JSONObject(playlist);
                if (Var.isJsonArray(play, "items")) {
                    JSONArray items = play.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);


                        if(Var.isJsonString(item, "id")) {                                  //Feed Id
                            YoutubeFeed feed = new YoutubeFeed(item.getString("id"));

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

                            editUser.getYoutubeChannel().getYoutubeFeeds().add(feed);

                            Log.d(TAG,"youtube feed added "+feed.getName());
                        }
                    }

                    editUser.getYoutubeChannel().getYoutubeFeeds().add(new YoutubeFeed()); //Activity
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            feedAdapter.notifyDataSetChanged();
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
            return editUser.getYoutubeChannel().getYoutubeFeeds().size();
        }

        @Override
        public YoutubeFeed getItem(int position) {
            return editUser.getYoutubeChannel().getYoutubeFeeds().get(position);
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
                holder.image_iv = (ImageView) convertView.findViewById(R.id.thumbnail_iv);
                holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.visible_sw = (SwitchCompat) convertView.findViewById(R.id.visible_sw);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            YoutubeFeed feed = getItem(position);

            holder.image_iv.setImageResource(R.drawable.ic_youtube_icon);
            if(feed.getThumbnail() != null) imageLoader.DisplayImage(feed.getThumbnail(), holder.image_iv);
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
        public int getItemViewType(int position) {
            return (searchMode == Var.SEARCH_YOUTUBE)? 0: 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
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
                convertView = inflater.inflate((getItemViewType(position) == 0 ? R.layout.item_youtube_search: R.layout.item_twitter_search), parent, false);
                holder = new ViewHolder();
                holder.image_iv = (ImageView) convertView.findViewById(R.id.thumbnail_iv);
                holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.screenName_tv = (TextView) convertView.findViewById(R.id.screen_name_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(searchMode == Var.SEARCH_YOUTUBE) {
                YoutubeChannel feed = (YoutubeChannel) getItem(position);
                holder.image_iv.setImageResource(R.drawable.ic_youtube_icon);
                if (feed.getThumbnail() != null)  imageLoader.DisplayImage(feed.getThumbnail(), holder.image_iv);

                holder.name_tv.setText(feed.getName());
            }

            if(searchMode == Var.SEARCH_TWITTER) {
                TwitterFeed feed = (TwitterFeed) getItem(position);
                holder.image_iv.setImageResource(R.drawable.ic_twitter_icon);
                if (feed.getThumbnail() != null)  imageLoader.DisplayImage(feed.getThumbnail(), holder.image_iv);

                holder.name_tv.setText(feed.getDisplayName());
                holder.screenName_tv.setText(feed.getName());
            }

            return convertView;
        }

        class ViewHolder {
            ImageView image_iv;
            TextView name_tv;
            TextView screenName_tv;
        }
    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,"item selected "+spinnerSelect+", "+spinnerInit + "pos: "+position);
        if(spinnerSelect < spinnerInit) spinnerSelect ++; else { //if(position < social_icons.length - 1){


            if(position < groups.size()) {
                groupAdapter.selected = position;
                if(spinnerSelect == 1 && position == 0) {
                    Log.d(TAG, "trying to refresh");
                    groupAdapter.notifyDataSetChanged();
                }

            } else {    //Do nothing because an option outside the list was selected
                spinnerSelect --;


                final EditText input = new EditText(this);
                new AlertDialog.Builder(this)
                        .setTitle("Update Status")
                        .setMessage("Create a new Group")
                        .setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //TODO most of this is hardcoded ugliness
                                Group group = new Group(input.getText().toString(), true);
                                GroupORM.insertGroup(AddActivity.this, group);

                                groups.clear();
                                groups.addAll(GroupORM.getVisibleGroups(AddActivity.this));
                                groupAdapter.notifyDataSetChanged();
                                group_sp.setSelection(groups.size() - 1);


                                editUser.getGroups().clear();
                                editUser.getGroups().add(group);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        group_sp.setSelection(groupAdapter.selected);
                    }
                }).show();
            }

            //icon_sp.setSelection(social_icons.length - 1);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    public class GroupAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        Context context;
        List<Group> groups;
        int selected;

        public GroupAdapter(Context context, List<Group> groups) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.groups = groups;
            selected = -1;
        }


        @Override
        public int getCount() {
            return groups.size() + 1;
        }

        @Override
        public Group getItem(int position) {
            return groups.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_group, parent, false);
                holder = new ViewHolder();
                //holder.image_iv = (ImageView) convertView.findViewById(R.id.image_iv);
                holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            if(position - 1 < 0 && selected == -1) {
                holder.name_tv.setText("Select Group");
                holder.name_tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }else if (position < groups.size()) {
                Group group = getItem(position);

                holder.name_tv.setText(group.getName());
                holder.name_tv.setTextColor(getResources().getColor(android.R.color.black));
            } else {
                holder.name_tv.setText("Add new group");
                holder.name_tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            return convertView;
        }


        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            ViewHolder holder;

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_group, parent, false);
                holder = new ViewHolder();
                //holder.image_iv = (ImageView) convertView.findViewById(R.id.image_iv);
                holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position < groups.size()) {
                Group group = getItem(position);

                holder.name_tv.setText(group.getName());
                holder.name_tv.setTextColor(getResources().getColor(android.R.color.black));
            } else {
                holder.name_tv.setText("Add new group");
                holder.name_tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            return convertView;
        }



        class ViewHolder {
            //ImageView image_iv;
            TextView name_tv;
        }
    }

    protected class SearchTwitterTask extends AsyncTask<Void, Void, Integer> {
        String jsonTokenStream;
        ResponseList<twitter4j.User> users;

        @Override
        protected Integer doInBackground(Void... params) {
            searchBusy = true;
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

                    if(users != null) {
                        for (twitter4j.User u : users) {
                            TwitterFeed feed = new TwitterFeed(String.valueOf(u.getId()));
                            feed.setDisplayName(u.getName());
                            feed.setName("@" + u.getScreenName());
                            feed.setThumbnail(u.getProfileImageURL());

                            twitterFeedSearch.add(feed);
                        }

                        searchBusy = false;
                    } else if(jsonTokenStream != null) {

                        //  Log error and clear the adapter
                        Object json = new JSONTokener(jsonTokenStream).nextValue();
                        if (json instanceof JSONObject) {
                            if (Var.isJsonArray(((JSONObject) json), "errors")) {
                                JSONArray errors = ((JSONObject) json).getJSONArray("errors");
                                if (errors.length() > 0) {
                                    Log.d(TAG, jsonTokenStream);

                                    if (Var.isJsonString(errors.getJSONObject(0), "message")) {
                                        String message = errors.getJSONObject(0).getString("message");
                                        int code = errors.getJSONObject(0).getInt("code");

                                        searchMessage_tv.setVisibility(View.VISIBLE);
                                        if (code == 88)
                                            searchMessage_tv.setText(R.string.rate_limit_error);
                                        if (code == 34)
                                            searchMessage_tv.setText(R.string.page_does_not_exist_error);
                                        else searchMessage_tv.setText(message);


                                    }
                                }
                            }
                        } else if (json instanceof JSONArray) {

                            if (!twitter.hasAccessToken()) {

                                JSONArray results = new JSONArray(jsonTokenStream);
                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject item = results.getJSONObject(i);

                                    if (Var.isJsonString(item, "id")) {

                                        TwitterFeed feed = new TwitterFeed(item.getString("id"));

                                        if (Var.isJsonString(item, "name"))
                                            feed.setDisplayName(item.getString("name"));
                                        if (Var.isJsonString(item, "screen_name"))
                                            feed.setName("@" + item.getString("screen_name"));
                                        if (Var.isJsonString(item, "profile_image_url"))
                                            feed.setThumbnail(item.getString("profile_image_url").replace("_normal", "_bigger"));

                                        twitterFeedSearch.add(feed);
                                    }
                                }
                            }
                        }
                    }
                    searchAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("Tweet", "Error retrieving JSON stream" + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
    }



}
