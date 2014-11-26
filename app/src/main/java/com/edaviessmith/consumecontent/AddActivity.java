package com.edaviessmith.consumecontent;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.data.NotificationList;
import com.edaviessmith.consumecontent.data.TwitterFeed;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.data.YoutubeChannel;
import com.edaviessmith.consumecontent.data.YoutubeFeed;
import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.db.GroupORM;
import com.edaviessmith.consumecontent.db.UserORM;
import com.edaviessmith.consumecontent.util.App;
import com.edaviessmith.consumecontent.util.ImageLoader;
import com.edaviessmith.consumecontent.util.Listener;
import com.edaviessmith.consumecontent.util.TwitterUtil;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.view.Fab;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;


public class AddActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AbsListView.OnScrollListener {
    String TAG = "AddActivity";

    //Searching users
    public static final int SEARCH_NONE = 0;
    public static final int SEARCH_OPTIONS = 1;
    public static final int SEARCH_YOUTUBE = 2;
    public static final int SEARCH_YT_CHANNEL = 3;
    public static final int SEARCH_TWITTER = 4;
    private static int maxResults = 10;

    private App app;
    public  ImageLoader imageLoader;
    private SearchYoutubeTask searchTask;
    private SearchTwitterTask searchTwitterTask;
    private GetFeedAsyncTask feedTask;
    private TwitterUtil twitter;
    private Toolbar toolbar;


    SearchAdapter searchAdapter;
    FeedAdapter feedAdapter;
    private DragSortController dragSortController;
    //GroupAdapter groupAdapter;
    List<YoutubeChannel> youtubeChannelSearch;
    List<TwitterFeed> twitterFeedSearch;
    List<Group> groups;
    List<MediaFeed> mediaFeeds;
    List<MediaFeed> selectedFeeds;
    NotificationList notificationList;
    User editUser;
    YoutubeChannel searchChannel;
    List<String> userPictureThumbnails;


    ListView search_lv;
    DragSortListView feed_lv;
    View search_v, searchTwitter_v, searchDiv_v, channel_v, footer, search_rl, youtube_ll, twitter_ll, groups_v, userThumbnail_v,  actionNotification, actionDelete;
    TextView searchMessage_tv, name_tv, searchTwitterLogin_tv;
    ImageView channelThumbnail_iv, clearSearch_iv, userThumbnail_iv;
    EditText userName_edt, search_edt;

    Fab search_fab, youtube_fab, twitter_fab, action_fab;
    ProgressBar userThumbnail_pb;

    int twitterPage, searchMode = SEARCH_NONE;
    String search, pageToken;
    boolean searchBusy;


    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = false;
    public int removeMode = DragSortController.FLING_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        app = (App) getApplication();
        imageLoader = new ImageLoader(this);
        youtubeChannelSearch = new ArrayList<YoutubeChannel>();
        twitterFeedSearch = new ArrayList<TwitterFeed>();
        userPictureThumbnails = new ArrayList<String>();

        groups = GroupORM.getVisibleGroups(this);
        notificationList = new NotificationList(this);

        int userId = getIntent().getIntExtra(Var.INTENT_USER_ID, -1);
        if(DB.isValid(userId)) editUser = UserORM.getUser(this, userId, groups);
        else editUser = new User();

        selectedFeeds = new ArrayList<MediaFeed>();
        mediaFeeds = new ArrayList<MediaFeed>();

        for(int i=0; i< editUser.getCastMediaFeed().size(); i++) {
            mediaFeeds.add( editUser.getCastMediaFeed().valueAt(i));
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        actionDelete = findViewById(R.id.action_delete);
        actionDelete.setOnClickListener(this);
        actionNotification = findViewById(R.id.action_notification);
        actionNotification.setOnClickListener(this);

        View header = getLayoutInflater().inflate(R.layout.header_add, null, false);
        View searchHeader = getLayoutInflater().inflate(R.layout.header_search_user, null, false);
        footer = getLayoutInflater().inflate(R.layout.item_list_divider, null, false);
        footer.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 48)));
        footer.setVisibility(View.GONE);

        feedAdapter = new FeedAdapter(this, mediaFeeds);
        feed_lv = (DragSortListView) findViewById(R.id.feed_lv);
        feed_lv.addHeaderView(header, null, false);
        feed_lv.addFooterView(footer, null, false);
        feed_lv.setAdapter(feedAdapter);

        dragSortController = buildController(feed_lv);

        feed_lv.setDropListener(onDrop);
        //group_lv.setRemoveListener(onRemove);
        feed_lv.setFloatViewManager(dragSortController);
        feed_lv.setOnTouchListener(dragSortController);
        feed_lv.setDragEnabled(dragEnabled);
        feed_lv.setOnItemClickListener(this);



        //Header
        userThumbnail_v = header.findViewById(R.id.user_thumbnail_v);
        userThumbnail_iv = (ImageView) header.findViewById(R.id.user_thumbnail_iv);
        userThumbnail_pb = (ProgressBar) header.findViewById(R.id.user_thumbnail_pb);
        userName_edt = (EditText) header.findViewById(R.id.user_name_edt);
        groups_v = header.findViewById(R.id.groups_v);

        search_fab = (Fab) header.findViewById(R.id.search_fab);
        youtube_fab = (Fab) header.findViewById(R.id.youtube_fab);
        twitter_fab = (Fab) header.findViewById(R.id.twitter_fab);

        youtube_ll = header.findViewById(R.id.youtube_ll);
        twitter_ll = header.findViewById(R.id.twitter_ll);

        //groupAdapter = new GroupAdapter(this, groups);
        //group_sp.setAdapter(groupAdapter);
        //group_sp.setOnItemSelectedListener(this);

        //Search
        search_rl = findViewById(R.id.search_rl);
        search_edt = (EditText) findViewById(R.id.search_edt);
        search_edt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                pageToken = null;
                twitterPage = 0;
                search = s.toString();
                searchMessage_tv.setVisibility(View.GONE);
                searchTimer();
            }
        });
        clearSearch_iv = (ImageView) findViewById(R.id.clear_iv);
        clearSearch_iv.setOnClickListener(this);


        search_v = findViewById(R.id.search_v);
        search_v.setOnClickListener(this);
        channel_v = searchHeader.findViewById(R.id.channel_v);

        channelThumbnail_iv = (ImageView) channel_v.findViewById(R.id.thumbnail_iv);
        name_tv = (TextView) channel_v.findViewById(R.id.name_tv);

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

        action_fab = (Fab) findViewById(R.id.action_fab);

        userThumbnail_v.setOnClickListener(this);
        groups_v.setOnClickListener(this);
        action_fab.setOnClickListener(this);
        search_fab.setOnClickListener(this);
        youtube_ll.setOnClickListener(this);
        twitter_ll.setOnClickListener(this);

        twitter = new TwitterUtil(this);
        twitter.setListener(new Listener() {
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



        userName_edt.setText(editUser.getName());
        getSupportActionBar().setTitle((!Var.isEmpty(editUser.getName())? editUser.getName(): "Add User"));

        if(DB.isValid(editUser.getId())) {
            action_fab.setDrawable(getResources().getDrawable(R.drawable.ic_action_accept));

            imageLoader.DisplayImage(editUser.getThumbnail(), userThumbnail_iv, userThumbnail_pb, false);
            addThumbnail(editUser.getThumbnail());
            for(int i=0; i<editUser.getMediaFeed().size(); i++)
                addThumbnail(((MediaFeed) editUser.getMediaFeed().valueAt(i)).getThumbnail());

            toggleSearch(SEARCH_NONE);
        } else {
            toggleSearch(SEARCH_OPTIONS);
        }


    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                MediaFeed item = feedAdapter.getItem(from);
                feedAdapter.remove(item);
                feedAdapter.insert(item, to);
                feed_lv.moveCheckState(from, to);
            }
        }
    };

    public DragSortController buildController(DragSortListView dslv) {
        // defaults are
        //   dragStartMode = onDown
        //   removeMode = flingRight
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        controller.setRemoveMode(removeMode);
        return controller;
    }



    private void toggleSearch(int searchMode) {
        this.searchMode = searchMode;

        search_v.setVisibility(searchModePreSearch() ? View.GONE: View.VISIBLE);
        searchTwitter_v.setVisibility((searchMode == SEARCH_TWITTER && !twitter.hasAccessToken()) ? View.VISIBLE: View.GONE);
        searchDiv_v.setVisibility((searchMode == SEARCH_TWITTER && twitter.hasAccessToken()) ? View.GONE: View.VISIBLE);
        youtube_ll.setVisibility((searchMode == SEARCH_OPTIONS) ? View.VISIBLE: View.GONE);
        twitter_ll.setVisibility((searchMode == SEARCH_OPTIONS) ? View.VISIBLE: View.GONE);
        channel_v.setVisibility((searchMode == SEARCH_YT_CHANNEL) ? View.VISIBLE: View.GONE);
        search_fab.setVisibility((searchModePreSearch() || searchMode == SEARCH_YT_CHANNEL) ? View.VISIBLE : View.GONE);
        search_fab.setDrawable(getResources().getDrawable(searchMode == SEARCH_YT_CHANNEL
                ? R.drawable.ic_add_white_18dp
                : R.drawable.ic_search_white_24dp));


        search_lv.setChoiceMode((searchMode == SEARCH_YT_CHANNEL) ? ListView.CHOICE_MODE_MULTIPLE : ListView.CHOICE_MODE_SINGLE);

        if(searchMode == SEARCH_NONE || searchMode == SEARCH_OPTIONS) {
            dismissSearch();
            search_rl.setVisibility(View.GONE);
            search_edt.getText().clear();

            action_fab.setVisibility((editUser.getMediaFeed().size() > 0) ? View.VISIBLE: View.GONE);
            search_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            youtubeChannelSearch.clear();
            twitterFeedSearch.clear();
            searchAdapter.notifyDataSetChanged();

        } if(searchMode == SEARCH_YT_CHANNEL) {
            dismissSearch();
            action_fab.setVisibility(View.VISIBLE);
            search_lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            clearSearchOptions(1);
        } else if(searchMode == SEARCH_YOUTUBE || searchMode == SEARCH_TWITTER) {
            search_edt.setHint((searchMode == SEARCH_YOUTUBE) ? R.string.search_youtube : R.string.search_twitter);
            app.postFocusText(search_edt);
            search_rl.setVisibility(View.VISIBLE);
            search_rl.requestFocus();
        }
        if (searchModePreSearch()) {
            search_fab.setDrawable(getResources().getDrawable(searchMode == SEARCH_NONE
                    ? R.drawable.ic_search_white_24dp
                    : R.drawable.ic_close_white_36dp));
        }
    }

    private boolean searchModePreSearch() {
        return (searchMode == SEARCH_NONE || searchMode == SEARCH_OPTIONS);
    }

    private void clearSearchOptions(int selected) {
        for (int i = 0; i < search_lv.getCount(); i++) search_lv.setItemChecked(i, (i == selected)); //Unselect all options
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    //TODO handler sometimes fails when typing fast (haven't seen lately)
    Handler handler = new Handler();
    private void searchTimer() {
        if(handler != null) handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(searchMode == SEARCH_YOUTUBE) searchYoutube();
                if(searchMode == SEARCH_TWITTER) searchTwitter();
            }
        }, 800);
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



    private void toggleEditActions(boolean show) {
        actionNotification.setVisibility(show ? View.VISIBLE: View.GONE);
        actionDelete.setVisibility(show ? View.VISIBLE: View.GONE);
    }


    public void clearFeedSelection(int selected) {
        for (int i = 0; i < feed_lv.getCount(); i++) feed_lv.setItemChecked(i, (i == selected)); //Unselect all options
        if(selected < 0) toggleEditActions(false);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "onItemClick "+parent +" = "+feed_lv);

        if(search_lv == parent) {
            if (searchMode == SEARCH_YOUTUBE) {
                searchChannel = youtubeChannelSearch.get(position - 1);
                Log.d(TAG, searchChannel.toString());

                addThumbnail(searchChannel.getThumbnail());
                if (Var.isEmpty(userName_edt.getText().toString()))
                    userName_edt.setText(searchChannel.getName());
                name_tv.setText(searchChannel.getName());
                imageLoader.DisplayImage(searchChannel.getThumbnail(), channelThumbnail_iv);

                toggleSearch(SEARCH_YT_CHANNEL);
                searchChannel();
            }

            if (searchMode == SEARCH_TWITTER) {
                mediaFeeds.add(twitterFeedSearch.get(position - 1));
                addThumbnail(twitterFeedSearch.get(position - 1).getThumbnail());

                if (Var.isEmpty(userName_edt.getText().toString()))
                    userName_edt.setText(twitterFeedSearch.get(position - 1).getDisplayName());

                toggleSearch(SEARCH_NONE);
            }
        }
        if(feed_lv == parent) {
            selectedFeeds.clear();
            SparseBooleanArray checked = feed_lv.getCheckedItemPositions();
            for (int i = 0; i < checked.size(); i++) {
                if(checked.valueAt(i)) {
                    selectedFeeds.add((MediaFeed) feed_lv.getItemAtPosition(checked.keyAt(i)));
                }
            }

            toggleEditActions(selectedFeeds.size() > 0);
        }
    }

    private void addThumbnail(String thumbnail) {
        if(!Var.isEmpty(thumbnail)) {
            userPictureThumbnails.add(thumbnail);

            if (Var.isEmpty(editUser.getThumbnail())) {
                editUser.setThumbnail(userPictureThumbnails.get(0));
                imageLoader.DisplayImage(userPictureThumbnails.get(0), userThumbnail_iv);

            }

            //iconAdapter.notifyDataSetChanged();
        }
    }

    private void searchChannel() {
        if (feedTask != null) {
            feedTask.cancel(true);
            feedTask  = null;
        }
        feedTask  = new GetFeedAsyncTask();
        feedTask.execute();


    }

    @Override
    public void onClick(View v) {

        if(actionNotification == v) {
            new NotificationDialog(this, notificationList, selectedFeeds);
        }

        if(actionDelete == v) {

            for(MediaFeed mediaFeed: selectedFeeds) {
                mediaFeeds.remove(mediaFeed);
            }

            clearFeedSelection(-1);

        }

        if(groups_v == v) {
            new GroupDialog(this, groups, editUser.getGroups());
        }
        if(userThumbnail_v == v) {

        }

        if(v == search_fab) {
            if(searchMode == SEARCH_NONE) toggleSearch(SEARCH_OPTIONS);
            else if(searchMode == SEARCH_OPTIONS) toggleSearch(SEARCH_NONE);
        }

        if(v == action_fab) {
            if(searchMode == SEARCH_YT_CHANNEL) {
                SparseBooleanArray checked = search_lv.getCheckedItemPositions();
                for (int i = 0; i < checked.size(); i++) {
                    if(checked.valueAt(i)) {
                        YoutubeFeed youtubeFeed = (YoutubeFeed) search_lv.getItemAtPosition(checked.keyAt(i));
                        youtubeFeed.setChannelHandle(searchChannel.getFeedId());
                        addThumbnail(youtubeFeed.getThumbnail());
                        mediaFeeds.add(youtubeFeed);
                    }
                }

                feedAdapter.notifyDataSetChanged();
                clearSearchOptions(-1);

                toggleSearch(SEARCH_NONE);
            }
            else if(searchMode == SEARCH_NONE) {

                //TODO make sure save is working
                editUser.setName(userName_edt.getText().toString().trim());



                editUser.addMediaFeed(mediaFeeds);


                //editUser.setThumbnail((String) userPicture_sp.getSelectedItem());//TODO set thumbnail

                UserORM.saveUser(this, editUser);

                finish();
            }
        }
        if(v == youtube_ll) toggleSearch(SEARCH_YOUTUBE);
        if(v == twitter_ll) toggleSearch(SEARCH_TWITTER);
        if(v == search_v) toggleSearch(SEARCH_NONE);
        if(v == searchTwitterLogin_tv) {
            twitter.resetAccessToken();
            if (!twitter.hasAccessToken()) twitter.authorize();
            //Hide the signin
            searchTwitter_v.setVisibility(View.GONE);
            searchDiv_v.setVisibility(View.GONE);
        }
        if(v == clearSearch_iv) search_edt.getText().clear();
    }



    public void onScrollStateChanged(AbsListView view, int scrollState) {
       dismissSearch();
    }

    private void dismissSearch() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(search_edt.getWindowToken(), 0);

    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(view.getLastVisiblePosition() >= totalItemCount - 5 && !searchBusy){
            if(searchMode == SEARCH_YOUTUBE) searchYoutube();
            //if(searchMode == SEARCH_TWITTER) searchTwitter();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            if(searchMode != SEARCH_NONE) {
                toggleSearch(SEARCH_NONE);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(searchMode != SEARCH_NONE) {
            toggleSearch(SEARCH_NONE);
            return;
        }

        super.onBackPressed();
    }

    public void setGroups(List<Group> groups) {
        editUser.setGroups(groups);
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

                searchChannel.getYoutubeFeeds().clear();

                //TODO check the internet connection here
                String channelUrl = "https://www.googleapis.com/youtube/v3/channels?part=contentDetails&id="+searchChannel.getFeedId()+"&fields=items%2FcontentDetails&key=" + Var.DEVELOPER_KEY;
                String playlists = "";
                String channel = Var.HTTPGet(channelUrl);

                JSONObject res = new JSONObject(channel);
                if (Var.isJsonArray(res, "items")) {
                    JSONArray items = res.getJSONArray("items");
                    if (items.length() > 0) {
                        JSONObject item = items.getJSONObject(0);
                        if (Var.isJsonObject(item, "contentDetails")) {
                            JSONObject contentDetails = item.getJSONObject(("contentDetails"));
                            if (Var.isJsonObject(contentDetails, "relatedPlaylists")) {                        //Channel Name
                                JSONObject relatedPlaylists = contentDetails.getJSONObject("relatedPlaylists");
                                if (Var.isJsonString(relatedPlaylists, "uploads"))
                                    playlists += (Var.isEmpty(playlists) ? "" : ",") + relatedPlaylists.getString("uploads");
                                if (Var.isJsonString(relatedPlaylists, "likes"))
                                    playlists += (Var.isEmpty(playlists) ? "" : ",") + relatedPlaylists.getString("likes");
                                if (Var.isJsonString(relatedPlaylists, "favorites"))
                                    playlists += (Var.isEmpty(playlists) ? "" : ",") + relatedPlaylists.getString("favorites");
                            }
                        }
                    }
                }

                String playlistUrl = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&id="+ URLEncoder.encode(playlists, "UTF-8") + "&fields=items(id%2Csnippet)&key=" + Var.DEVELOPER_KEY;
                String playlist = Var.HTTPGet(playlistUrl);

                JSONObject play = new JSONObject(playlist);
                if (Var.isJsonArray(play, "items")) {
                    JSONArray items = play.getJSONArray("items");

                    YoutubeFeed activityFeed = new YoutubeFeed();
                    activityFeed.setChannelHandle(searchChannel.getFeedId());

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);


                        if(Var.isJsonString(item, "id")) {                                  //Feed Id
                            YoutubeFeed feed = new YoutubeFeed(item.getString("id"));
                            feed.setType(Var.TYPE_YOUTUBE_PLAYLIST);
                            feed.setChannelHandle(searchChannel.getFeedId());

                            if (Var.isJsonObject(item, "snippet")) {                        //Feed Name
                                JSONObject snippet = item.getJSONObject("snippet");
                                if (Var.isJsonString(snippet, "title")) {
                                    if(snippet.getString("title").startsWith("Upload")) {
                                        feed.setName("Uploads");

                                    }
                                    else feed.setName(snippet.getString("title"));
                                }

                                if (Var.isJsonObject(snippet, "thumbnails")) {              //Feed Thumbnail
                                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                    if (Var.isJsonObject(thumbnails, "default")) {
                                        JSONObject def = thumbnails.getJSONObject("default");
                                        if (Var.isJsonString(def, "url")) {
                                            feed.setThumbnail(def.getString("url"));
                                            if(feed.getName().contentEquals("Uploads")) activityFeed.setThumbnail(def.getString("url"));
                                        }
                                    }
                                }
                            }

                            searchChannel.getYoutubeFeeds().add(feed);

                            Log.d(TAG,"youtube feed added "+feed.getName());
                        }
                    }


                    searchChannel.getYoutubeFeeds().add(activityFeed); //Activity
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            searchAdapter.notifyDataSetChanged();
        }
    }


    public class FeedAdapter extends ArrayAdapter<MediaFeed> {

        private LayoutInflater inflater;
        Context context;
        List<MediaFeed> mediaFeeds;

        public FeedAdapter(Context context, List<MediaFeed> mediaFeeds) {
            super(context, R.layout.item_group_user, mediaFeeds);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mediaFeeds = mediaFeeds;
        }


        @Override
        public int getCount() {
            return mediaFeeds.size();
        }

        @Override
        public MediaFeed getItem(int position) {
            return mediaFeeds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            clearFeedSelection(-1);
            footer.setVisibility(getCount() == 0? View.GONE: View.VISIBLE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_youtube_feed, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final MediaFeed feed = getItem(position);

            holder.image_iv.setImageResource(R.drawable.ic_youtube_icon);
            if(feed.getThumbnail() != null) imageLoader.DisplayImage(feed.getThumbnail(), holder.image_iv);
            holder.name_edt.setText(feed.getName());

            holder.notification_v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new NotificationDialog(AddActivity.this, notificationList, new ArrayList<MediaFeed>() {{ add(feed); }});
                }
            });

            if(DB.isValid(feed.getNotificationId())) {
                holder.notification_tv.setText(Var.getNextNotificationAlarm(notificationList.getNotification(feed.getNotificationId()), notificationList.getScheduleNotification()));
            }

            return convertView;

        }

        class ViewHolder implements View.OnTouchListener {
            View row;
            ImageView image_iv;
            EditText name_edt;
            View notification_v;
            TextView notification_tv;

            public ViewHolder(View view) {
                row = view;
                row.setOnTouchListener(this);
                image_iv = (ImageView) view.findViewById(R.id.thumbnail_iv);
                name_edt = (EditText) view.findViewById(R.id.name_edt);
                name_edt.setOnTouchListener(this);
                notification_v = view.findViewById(R.id.notification_v);
                notification_tv = (TextView) view.findViewById(R.id.notification_tv);
            }

            //Allow focusable view and onItemClick
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                } else {
                    FeedAdapter.ViewHolder holder = (FeedAdapter.ViewHolder) view.getTag();
                    holder.name_edt.setFocusable(false);
                    holder.name_edt.setFocusableInTouchMode(false);
                }
                return false;
            }
        }
    }


    public class SearchAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public SearchAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getItemViewType(int position) {
            return (searchMode == SEARCH_YOUTUBE || searchMode == SEARCH_YT_CHANNEL)? 0: 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            if(searchMode == SEARCH_YOUTUBE) return youtubeChannelSearch.size();
            if(searchMode == SEARCH_TWITTER) return twitterFeedSearch.size();
            if(searchMode == SEARCH_YT_CHANNEL) return searchChannel.getYoutubeFeeds().size();
                return 0;
        }

        @Override
        public Object getItem(int position) {
            if(searchMode == SEARCH_YOUTUBE) return youtubeChannelSearch.get(position);
            if(searchMode == SEARCH_TWITTER) return twitterFeedSearch.get(position);
            if(searchMode == SEARCH_YT_CHANNEL) return searchChannel.getYoutubeFeeds().get(position);
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

            if(searchMode == SEARCH_YOUTUBE) {
                YoutubeChannel feed = (YoutubeChannel) getItem(position);
                holder.image_iv.setImageResource(R.drawable.ic_youtube_icon);
                if (feed.getThumbnail() != null)  imageLoader.DisplayImage(feed.getThumbnail(), holder.image_iv);

                holder.name_tv.setText(feed.getName());
            }

            if(searchMode == SEARCH_TWITTER) {
                TwitterFeed feed = (TwitterFeed) getItem(position);
                holder.image_iv.setImageResource(R.drawable.ic_twitter_icon);
                if (feed.getThumbnail() != null)  imageLoader.DisplayImage(feed.getThumbnail(), holder.image_iv);

                holder.name_tv.setText(feed.getDisplayName());
                holder.screenName_tv.setText(feed.getName());
            }

            if(searchMode == SEARCH_YT_CHANNEL) {
                YoutubeFeed feed = (YoutubeFeed) getItem(position);
                holder.image_iv.setImageResource(R.drawable.ic_youtube_icon);
                if (feed.getThumbnail() != null)  imageLoader.DisplayImage(feed.getThumbnail(), holder.image_iv);

                holder.name_tv.setText(feed.getName());
            }

            return convertView;
        }

        class ViewHolder {
            ImageView image_iv;
            TextView name_tv;
            TextView screenName_tv;
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
                            feed.setChannelHandle("@" + u.getScreenName());
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
                                            feed.setChannelHandle("@" + item.getString("screen_name"));
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
