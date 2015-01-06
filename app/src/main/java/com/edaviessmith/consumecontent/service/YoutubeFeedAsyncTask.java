package com.edaviessmith.consumecontent.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.edaviessmith.consumecontent.data.YoutubeFeed;
import com.edaviessmith.consumecontent.data.YoutubeItem;
import com.edaviessmith.consumecontent.util.Var;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class YoutubeFeedAsyncTask extends AsyncTask<String, Void, String> {

    private final static String TAG = "YoutubeFeedAsyncTask";
    final List<YoutubeItem> youtubeItems = new ArrayList<YoutubeItem>();

    private Context context;
    private YoutubeFeed youtubeFeed;
    //private Handler handler;
    private ActionDispatch actionDispatch;
    private int userId;

    private boolean cancel;
    
    public YoutubeFeedAsyncTask(Context context, YoutubeFeed youtubeFeed, int userId, ActionDispatch actionDispatch) {
        this.context = context;
        this.youtubeFeed = youtubeFeed;
        this.actionDispatch = actionDispatch;
        this.userId = userId;
    }
    
    @Override
    protected String doInBackground(String... params) {
        try {

            final Map<String, YoutubeItem> youtubeItemMap = new HashMap<String, YoutubeItem>();

            //TODO make this a beautiful toast like Chrome (use Handler)
            if (!Var.isNetworkAvailable(context)) {
                //handler.sendMessage(handler.obtainMessage(1, Var.FEED_OFFLINE, youtubeFeed.getId(), null));
                actionDispatch.updatedMediaFeed(youtubeFeed.getId(), Var.FEED_OFFLINE);
                cancel = true;
                return null;
            }

            String url = null;

            if (youtubeFeed.getType() == Var.TYPE_YOUTUBE_PLAYLIST) {
                String fields = "&fields=items%2Fsnippet%2CnextPageToken";
                url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet" + fields + "&maxResults=20&playlistId=" + URLEncoder.encode(youtubeFeed.getFeedId(), "UTF-8") + "&key=" + Var.DEVELOPER_KEY;
            }
            if (youtubeFeed.getType() == Var.TYPE_YOUTUBE_ACTIVTY) {
                String fields = "&fields=items(contentDetails%2Csnippet)%2CnextPageToken";
                url = "https://www.googleapis.com/youtube/v3/activities?part=snippet%2C+contentDetails"+fields+"&channelId=" + URLEncoder.encode(youtubeFeed.getChannelHandle(), "UTF-8") + "&maxResults=20&key=" + Var.DEVELOPER_KEY;
            }
            if(!Var.isEmpty(params[0])) url+= "&pageToken="+ youtubeFeed.getNextPageToken();
            StringBuilder videos = new StringBuilder();
            String playlistItems = Var.HTTPGet(url);

            JSONObject res = new JSONObject(playlistItems);
            if (Var.isJsonString(res, "nextPageToken")) {
                youtubeFeed.setNextPageToken(res.getString("nextPageToken"));
            } else {
                youtubeFeed.setNextPageToken("");
            }

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
                        //Other possible activity types
                        /* "comment": { "resourceId": { "kind": string, "videoId": string, "channelId": string, } },
                        "subscription": { "resourceId": {  "kind": string, "channelId": string, }  } */
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

                                long dateInMilli = Var.stringDate.parse(published).getTime();
                                youtubeItem.setDate(dateInMilli);
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

                        youtubeItem.setStatus(Var.STATUS_SEEN);
                        youtubeItems.add(youtubeItem);
                        //Log.d(TAG,"youtube feed added "+youtubeItem.getVideoId());
                    }
                }

            }

        } catch (Throwable t) {
            Log.e(TAG, "getFeed failed");
            t.printStackTrace();
            actionDispatch.updatedMediaFeed(youtubeFeed.getId(), Var.FEED_WARNING);
            cancel = true;
            return null;
        }

        Collections.sort(youtubeItems, new Comparator<YoutubeItem>() {
            public int compare(YoutubeItem m1, YoutubeItem m2) {
                return (m1.getDate() > m2.getDate() ? -1 : (m1.getDate() == m2.getDate() ? 0 : 1));
            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG,"adding youtube items "+youtubeItems.size());
        if(!cancel) {
            if (youtubeFeed.addItems(youtubeItems) && youtubeFeed.getItems().get(0).getStatus() == Var.STATUS_NEW) {
                actionDispatch.updateMediaFeedDatabase(userId, youtubeFeed.getId());
            }

            int feed = Var.isEmpty(youtubeFeed.getNextPageToken()) ? Var.FEED_END: Var.FEED_WAITING;

            actionDispatch.updatedMediaFeed(youtubeFeed.getId(), feed);
        }
    }


}

