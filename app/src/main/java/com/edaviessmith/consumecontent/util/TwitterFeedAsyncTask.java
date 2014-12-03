package com.edaviessmith.consumecontent.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.edaviessmith.consumecontent.data.TwitterFeed;
import com.edaviessmith.consumecontent.data.TwitterItem;
import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.service.ActionDispatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;


public class TwitterFeedAsyncTask extends AsyncTask<Integer, Void, String> {

    private final static String TAG = "TwitterFeedAsyncTask";
    final List<TwitterItem> twitterItems = new ArrayList<TwitterItem>();

    private Context context;
    private TwitterFeed twitterFeed;
    private ActionDispatch actionDispatch;
    private TwitterUtil twitter;
    private int userId;

    private boolean cancel;

    public TwitterFeedAsyncTask(Context context, TwitterFeed twitterFeed, TwitterUtil twitter, int userId, ActionDispatch actionDispatch) {
        this.context = context;
        this.twitterFeed = twitterFeed;
        this.actionDispatch = actionDispatch;
        this.twitter = twitter;
        this.userId = userId;
    }
    
    @Override
    protected String doInBackground(Integer... params) {
        try {

            //final Map<String, TwitterItem> twitterItemMap = new HashMap<String, TwitterItem>();

            //TODO make this a beautiful toast like Chrome (use Handler)
            if (!Var.isNetworkAvailable(context)) {
                //handler.sendMessage(handler.obtainMessage(1, Var.FEED_OFFLINE, youtubeFeed.getId(), null));
                actionDispatch.updatedMediaFeed(twitterFeed.getId(), Var.FEED_OFFLINE);
                cancel = true;
                return null;
            }


            Log.d(TAG, "feed_id: "+twitterFeed.getFeedId() + "- "+twitterFeed.getNextPageToken());
            long userId = Long.parseLong(twitterFeed.getFeedId());

            ResponseList<twitter4j.Status> response = (DB.isValid(params[0]) && twitterFeed.getNextPageToken() > 0) ?
                    (twitter.twitter.getUserTimeline(userId, new Paging(twitterFeed.getNextPageToken(), 20))):
                    twitter.twitter.getUserTimeline(userId);

            if(response == null || response.size() == 0)
                twitterFeed.setNextPageToken(0);
            else {
                twitterFeed.setNextPageToken((twitterFeed.getNextPageToken()+1));

                //int index = 20 * page;
                for (twitter4j.Status tweet : response) {
                    if (tweet != null) {
                        TwitterItem item = new TwitterItem();
                        //item.setId(index++);
                        item.setTweetId(tweet.getId());
                        if(tweet.getMediaEntities().length > 0) {
                            item.setImageHigh(tweet.getMediaEntities()[0].getMediaURL());
                            //tweet.getMediaEntities()[0].getType()
                        } else if(tweet.getExtendedMediaEntities().length > 0) {
                            item.setImageHigh(tweet.getExtendedMediaEntities()[0].getMediaURL());
                        }
                        item.setTitle(tweet.getText());
                        item.setDate(tweet.getCreatedAt().getTime());

                        twitterItems.add(item);
                    }
                }
            }

               /* //Only save the first page of YoutubeItems
                if(beginningOfList) {
                    act.updateTwitterFeed(tweets);
                    beginningOfList = false;
                    Log.d(TAG, "adding items to updateYoutubeItems");
                } else {
                    for(Tweet item : tweets) {
                        adapter.add(item);
                    }
                }


                // If the records in the database are not up to date
                if(!act.isTwitterFeedUpToDate) {
                    adapter.clearItems(); //Clear all items currently in the adapter

                    for(Tweet item : tweets) {
                        adapter.add(item);
                    }
                    act.isTwitterFeedUpToDate = true;
                }


                searchBusy = false;

                //Used if you scroll to the end of the list before the first result is returned
                if(waitingToSearch) {

                    if(act.twitterPageToken != 0) {
                        new TwitterFeed(TwitterFragment.this, act.getCurrentTwitterMemberId()).execute(act.getMember().getTwitterId());
                        endOfList = false;
                    }
                    waitingToSearch = false;
                }
    */


            /*String url = null;

            if (twitterFeed.getType() == Var.TYPE_YOUTUBE_PLAYLIST) {
                String fields = "&fields=items%2Fsnippet%2CnextPageToken";
                url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet" + fields + "&maxResults=20&playlistId=" + URLEncoder.encode(twitterFeed.getFeedId(), "UTF-8") + "&key=" + Var.DEVELOPER_KEY;
            }
            if (twitterFeed.getType() == Var.TYPE_YOUTUBE_ACTIVTY) {
                String fields = "&fields=items(contentDetails%2Csnippet)%2CnextPageToken";
                url = "https://www.googleapis.com/youtube/v3/activities?part=snippet%2C+contentDetails"+fields+"&channelId=" + URLEncoder.encode(twitterFeed.getChannelHandle(), "UTF-8") + "&maxResults=20&key=" + Var.DEVELOPER_KEY;
            }
            if(!Var.isEmpty(params[0])) url+= "&pageToken="+ twitterFeed.getNextPageToken();
            StringBuilder videos = new StringBuilder();
            String playlistItems = Var.HTTPGet(url);

            JSONObject res = new JSONObject(playlistItems);
            if (Var.isJsonString(res, "nextPageToken")) {
                twitterFeed.setNextPageToken(res.getString("nextPageToken"));
            } else {
                twitterFeed.setNextPageToken("");
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
                        *//* "comment": { "resourceId": { "kind": string, "videoId": string, "channelId": string, } },
                        "subscription": { "resourceId": {  "kind": string, "channelId": string, }  } *//*
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

                        twitterItems.add(youtubeItem);
                        //Log.d(TAG,"youtube feed added "+youtubeItem.getVideoId());
                    }
                }

            }*/

        } catch (Throwable t) {
            Log.e(TAG, "getFeed failed");
            t.printStackTrace();
            actionDispatch.updatedMediaFeed(twitterFeed.getId(), Var.FEED_WARNING);
            cancel = true;
            return null;
        }

        Collections.sort(twitterItems, new Comparator<TwitterItem>() {
            public int compare(TwitterItem m1, TwitterItem m2) {
                return (m1.getDate() > m2.getDate() ? -1 : (m1.getDate() == m2.getDate() ? 0 : 1));
            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG,"adding twitter items "+twitterItems.size());
        if(!cancel) {
            if (twitterFeed.addItems(twitterItems)) {
                actionDispatch.updateMediaFeedDatabase(userId, twitterFeed.getId());
            }

            //int feed = twitterFeed.getNextPageToken() ? Var.FEED_END: Var.FEED_WAITING;

            actionDispatch.updatedMediaFeed(twitterFeed.getId(), Var.FEED_WAITING);
        }
    }


}

