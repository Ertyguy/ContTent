package com.edaviessmith.consumecontent;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.YoutubeFeed;
import com.edaviessmith.consumecontent.util.ImageLoader;
import com.edaviessmith.consumecontent.util.Var;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AddActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, AbsListView.OnScrollListener, SearchView.OnQueryTextListener{

    String TAG = "AddActivity";

    SearchView searchView;
    MenuItem searchItem;
    Integer social_icons[] = {R.drawable.youtube_icon, R.drawable.twitter_icon , R.drawable.ic_action_new};
    Spinner icon_sp;
    ListView search_lv;
    SearchAdapter searchAdapter;
    List<YoutubeFeed> youtubeFeeds;
    RelativeLayout search_rl;

    int spinnerInit, spinnerSelect;
    int searchMode = Var.SEARCH_NONE;

    String search, pageToken;
    boolean searchBusy;

    private ImageLoader imageLoader;
    private HttpAsyncTask task;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(spinnerSelect < spinnerInit) spinnerSelect ++; else if(position < social_icons.length - 1){

            if(searchMode == Var.SEARCH_NONE) {
                if(position == 0) toggleSearch(Var.SEARCH_YOUTUBE);
                if(position == 1) toggleSearch(Var.SEARCH_TWITTER);
            } else {
                toggleSearch(Var.SEARCH_NONE);
            }

            Log.d(TAG, "searchMode: "+searchMode + " pos:"+position);
            icon_sp.setSelection(social_icons.length - 1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //hide KB
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(view.getLastVisiblePosition() >= totalItemCount - 5 && !searchBusy){
            searchYoutube();
        }
    }

    private void toggleSearch(int searchMode) {
        this.searchMode = searchMode;

        if(searchMode == Var.SEARCH_NONE) {
            search_rl.setVisibility(View.GONE);
            searchItem.setVisible(false);
        } else {
            search_rl.setVisibility(View.VISIBLE);
            searchView.setIconified(false);
            searchItem.setVisible(true);
            searchView.requestFocusFromTouch();
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        imageLoader = new ImageLoader(this);

        search_rl = (RelativeLayout) findViewById(R.id.search_rl);

        //icon_sp = (Spinner) findViewById(R.id.media_type_sp);
        //icon_sp.setAdapter(new IconAdapter(this, R.layout.item_image, social_icons));
        //icon_sp.setSelection(social_icons.length - 1);
        //icon_sp.setOnItemSelectedListener(this);

        youtubeFeeds = new ArrayList<YoutubeFeed>();
        search_lv = (ListView) findViewById(R.id.search_lv);
        searchAdapter = new SearchAdapter(this, youtubeFeeds);
        search_lv.setAdapter(searchAdapter);
        search_lv.setOnScrollListener(this);

        spinnerInit = 1;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);

        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
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
        search = s;
        searchYoutube();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        pageToken = null;
        search = s;
        searchYoutube();
        return false;
    }



    private void searchYoutube() {
        if(search!= null && search.length() > 2) {
            if (task != null) {
                task.cancel(true);
                task = null;
            }
            task = new HttpAsyncTask();
            task.execute(search);
        } else {
            youtubeFeeds.clear();
            searchAdapter.notifyDataSetChanged();
        }
    }






    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        String response;
        boolean isNew;
        final static int maxResults = 10;

        @Override
        protected String doInBackground(String... search) {
            searchBusy = true;
            try {
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
                HttpConnectionParams.setSoTimeout(httpParams, 5000);

                // Instantiate an HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + search[0] + "&maxResults="+maxResults+"&type=channel&fields=items(id%2Csnippet)%2CnextPageToken&key=" + Var.DEVELOPER_KEY;
                if(pageToken != null && !pageToken.isEmpty()) url += "&pageToken=" + pageToken;
                else isNew = true;
                //TODO check the internet connection here

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
                    youtubeFeeds.clear();
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
                            if (Var.isJsonObject(item, "snippet")) {
                                JSONObject snippet = item.getJSONObject("snippet");
                                if (Var.isJsonString(snippet, "title")) {
                                    String title = snippet.getString("title");
                                    Log.d(TAG, "title: " + title);
                                    YoutubeFeed user = new YoutubeFeed("Feedid", title);

                                    if (Var.isJsonObject(snippet, "thumbnails")) {
                                        JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                        if (Var.isJsonObject(thumbnails, "default")) {
                                            JSONObject def = thumbnails.getJSONObject("default");
                                            if (Var.isJsonString(def, "url")) {
                                                user.setImage(def.getString("url"));
                                            }
                                        }
                                    }

                                    youtubeFeeds.add(user);


                                }

                            }

                        }
                        searchAdapter.notifyDataSetChanged();
                        if(youtubeFeeds.size() >= maxResults) searchBusy = false; //Keep busy if nothing is returned
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    public class SearchAdapter extends ArrayAdapter<YoutubeFeed> {

        public List<YoutubeFeed> feedList = new ArrayList<YoutubeFeed>();

        public SearchAdapter(Context context, List<YoutubeFeed> list) {
            super(context, R.layout.item_youtube_feed, list);
            feedList = list;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = getLayoutInflater().inflate(R.layout.item_youtube_feed, parent, false);
            ImageView image_iv = (ImageView) row.findViewById(R.id.image_iv);
            TextView name_tv = (TextView) row.findViewById(R.id.name_tv);
            TextView type_tv = (TextView) row.findViewById(R.id.type_tv);

            YoutubeFeed feed = feedList.get(position);
            if(feed.getImage() != null) imageLoader.DisplayImage(feed.getImage(), image_iv);
            else image_iv.setImageResource(R.drawable.youtube_icon);

            name_tv.setText(feed.getName());
            type_tv.setText(feed.getType_name());

            return row;

        }

        class ViewHolder {
            ImageView image_iv;
            TextView name_tv;
            TextView type_tv;
        }
    }


    public class IconAdapter extends ArrayAdapter<Integer> {

        Integer objects[];

        public IconAdapter(Context context, int textViewResourceId,   Integer[] objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
        }

        @Override
        public int getCount() {
            // don't display last item. It is used as hint.
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=getLayoutInflater();
            View row = inflater.inflate(R.layout.item_image, parent, false);
            ImageView image = (ImageView) row.findViewById(R.id.image_iv);
            image.setImageResource(R.drawable.ic_action_new);
            return row;

        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=getLayoutInflater();
            View row = inflater.inflate(R.layout.item_image, parent, false);
            ImageView image = (ImageView) row.findViewById(R.id.image_iv);
            image.setImageResource(objects[position]);

            return row;
        }
    }

}
