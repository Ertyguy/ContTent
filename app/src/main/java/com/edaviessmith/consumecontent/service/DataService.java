package com.edaviessmith.consumecontent.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.MediaFeed;
import com.edaviessmith.consumecontent.data.NotificationList;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.db.GroupORM;
import com.edaviessmith.consumecontent.db.MediaFeedORM;
import com.edaviessmith.consumecontent.db.TwitterItemORM;
import com.edaviessmith.consumecontent.db.UserORM;
import com.edaviessmith.consumecontent.db.YoutubeItemORM;
import com.edaviessmith.consumecontent.util.App;
import com.edaviessmith.consumecontent.util.Listener;
import com.edaviessmith.consumecontent.util.TwitterUtil;
import com.edaviessmith.consumecontent.util.Var;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DataService extends Service {
    public final String TAG = ((Object) this).getClass().getSimpleName();

    ServiceBinder binder = new ServiceBinder();
    Handler handler;
    public List<ActionDispatch> actionDispatches = new ArrayList<ActionDispatch>();
    final static ExecutorService tpe = Executors.newSingleThreadExecutor();

    App app;
    //ImageLoader imageLoader;
    TwitterUtil twitter;

    public SparseArray<Group> groups;
    public LinkedHashMap<Integer, User> users; //todo possible just use current group

    private List<User> userList = new ArrayList<User>();
    private List<Group> groupList = new ArrayList<Group>();

    int selectedGroup, selectedUser;

    public NotificationList notificationList;



    @Override
    public void onCreate() {
        handler = new Handler();

        selectedGroup = Var.getIntPreference(this, Var.PREF_SELECTED_GROUP);
        selectedUser = Var.getIntPreference(this, Var.PREF_SELECTED_USER + selectedGroup);

        //selectedGroup = 1;
        //selectedUser = 1;

        //imageLoader = new ImageLoader(this);

        app = (App) getApplication();

        tpe.submit(new Runnable() {
            @Override
            public void run() {
                notificationList = new NotificationList(DataService.this);
            }
        });

        binder.fetchGroups(false);

        twitter = new TwitterUtil(this);
        twitter.setListener(new Listener() {
            @Override
            public void onError(String value) {
                Log.e(TAG, "twitter: "+value);
                twitter.resetAccessToken();
            }

            @Override
            public void onComplete(String value) {
                Log.d(TAG, "twitter listener authorized " + twitter.getUsername());
            }
        });
        //twitter.getBearerToken();

        //if (!twitter.hasAccessToken()) twitter.authorize();

        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind get notificationList "+(notificationList != null));

        return binder;
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }


    public class ServiceBinder extends Binder {

        public ServiceBinder() {
            Log.d(TAG, "serviceBinder started");
        }

        public void addListener(ActionDispatch actionDispatch) {
            if (actionDispatch != null) actionDispatches.add(actionDispatch);
        }

        public void removeListener(ActionDispatch actionDispatch) {
            actionDispatches.remove(actionDispatch);
        }


        public List<User> getUsers() {
            return userList;
        }

        public List<Group> getGroups() {
            return groupList;
        }

        public NotificationList getNotificationList() {
            return notificationList;
        }


        public User getUser() {
            return users.get(selectedUser);
        }

        public User getUser(int userId) {
            return (users != null && users.containsKey(userId)) ? users.get(userId): null;
        }

        public User getUserBySort(int sortId) {
            for(User u: users.values()) if(u.getSort() == sortId) return u;
            return null;
        }

        public boolean containsUser(int userId) {
            return users.containsKey(userId);
        }

        public Group getGroup() {
            return groups.get(selectedGroup);
        }

        private void updateUserList() {
            userList.clear();
            userList.addAll(users.values());
        }

        private void updateGroupList() {
            groupList.clear();
            for(int i=0; i< groups.size(); i++) {
                groupList.add(groups.valueAt(i));
            }
        }

        public void fetchBinder() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (ActionDispatch ad : actionDispatches) {
                        ad.binderReady();
                    }
                }
            });
        }

        public void fetchUsers() {

            Log.d(TAG, "fetchUsers "+selectedGroup);

            tpe.submit(new Runnable() {
                @Override
                public void run() {
                    users = UserORM.getUsersByGroupId(DataService.this, selectedGroup, groupList);
                    updateUserList();

                    selectedUser = Var.getIntPreference(DataService.this, Var.PREF_SELECTED_USER + selectedGroup);
                    if(!DB.isValid(selectedUser)) selectedUser = userList.get(0).getId();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(ActionDispatch ad: actionDispatches) {
                                ad.updatedUsers();
                            }
                        }
                    });

                }
            });
        }

        public void fetchGroups() {
            fetchGroups(true);
        }
        public void fetchGroups(final boolean update) {

            tpe.submit(new Runnable() {
                @Override
                public void run() {
                    groups = GroupORM.getGroups(DataService.this);
                    updateGroupList();
                    if(update) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (ActionDispatch ad : actionDispatches) {
                                    ad.updatedGroups();
                                }
                            }
                        });
                    }

                }
            });
        }


        public void fetchItemsByMediaFeedId(final int mediaFeedId) {

            final int userId = selectedUser;
            Var.setIntPreference(DataService.this, Var.PREF_SELECTED_USER + selectedGroup, selectedUser);

            tpe.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        MediaFeed mediaFeed = getUser(userId).getCastMediaFeed().get(mediaFeedId);

                        if(mediaFeed != null) {
                            if(Var.isTypeYoutube(mediaFeed.getType()))
                                mediaFeed.setItems(YoutubeItemORM.getYoutubeItems(DataService.this, mediaFeedId));
                            if(mediaFeed.getType() == Var.TYPE_TWITTER)
                                mediaFeed.setItems(TwitterItemORM.getTwitterItems(DataService.this, mediaFeedId));
                        }
                    } catch (Exception e) {e.printStackTrace(); }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (ActionDispatch ad : actionDispatches) {
                                ad.updatedMediaFeed(mediaFeedId, Var.FEED_WAITING);

                            }
                        }
                    });

                }
            });

        }


        public int getSelectedUser() {
            return selectedUser;
        }


        public void setSelectedUser(int index) {
            selectedUser = userList.get(index).getId();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (ActionDispatch ad : actionDispatches) {
                        ad.updateUserChanged();

                    }
                }
            });
        }

        public int getSelectedGroup() {
            return selectedGroup;
        }


        public void setSelectedGroup(int id) {
            selectedGroup = id;

            Var.setIntPreference(DataService.this, Var.PREF_SELECTED_GROUP, id);
            fetchUsers();
        }


        /*public ImageLoader getImageLoader() {
            return imageLoader;
        }*/

        public App getApp() {
            return app;
        }

        public TwitterUtil getTwitter() {
            return twitter;
        }

        public void saveMediaFeedItems(final int userId, final int mediaFeedId) {

            tpe.submit(new Runnable() {
                @Override
                public void run() {
                    MediaFeedORM.saveMediaFeedItems(DataService.this, ((MediaFeed) getUser(userId).getMediaFeed().get(mediaFeedId)));
                    //MediaFeedORM.saveYoutubeFeedItems(DataService.this, (YoutubeFeed) getUser(userId).getMediaFeed().get(mediaFeedId));
                }
            });



        }

        public void saveUser(final User editUser) {
            tpe.submit(new Runnable() {
                @Override
                public void run() {
                    final User user = UserORM.saveUser(DataService.this, editUser);

                    users.put(user.getId(), user);

                    for (ActionDispatch ad : actionDispatches) {
                        ad.updatedUser(user.getId());
                    }
                }
            });
        }

        public void saveGroup(final Group editGroup) {
            tpe.submit(new Runnable() {
                @Override
                public void run() {
                    final Group group = GroupORM.saveGroup(DataService.this, editGroup);

                    groups.put(group.getId(), group);
                    updateGroupList();
                    for (ActionDispatch ad : actionDispatches) {
                        ad.updatedGroup(group.getId());
                    }
                }
            });
        }

    }





}