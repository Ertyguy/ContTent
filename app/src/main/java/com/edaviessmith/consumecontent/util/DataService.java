package com.edaviessmith.consumecontent.util;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.NotificationList;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.db.GroupORM;
import com.edaviessmith.consumecontent.db.UserORM;
import com.edaviessmith.consumecontent.db.YoutubeItemORM;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DataService extends Service {
    public final String TAG = ((Object) this).getClass().getSimpleName();

    ServiceBinder binder = new ServiceBinder();
    Handler handler;
    public List<ActionDispatch> actionDispatches = new ArrayList<ActionDispatch>();
    final static ExecutorService tpe = Executors.newSingleThreadExecutor();


    public SparseArray<Group> groups;
    public SparseArray<User> users; //todo possible just use current group

    private List<User> userList;
    private List<Group> groupList;

    int selectedGroup, selectedUser;

    public NotificationList notificationList;



    @Override
    public void onCreate() {
        handler = new Handler();

        selectedGroup = Var.getIntPreference(this, Var.PREF_SELECTED_GROUP);
        selectedUser = Var.getIntPreference(this, Var.PREF_SELECTED_USER);

        selectedGroup = 1;
        selectedUser = 1;

        groupList = new ArrayList<Group>();
        userList = new ArrayList<User>();

        tpe.submit(new Runnable() {
            @Override
            public void run() {
                notificationList = new NotificationList(DataService.this);
            }
        });

        super.onCreate();
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

        public User getUser(int pos) {
            return users.get(pos);
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

        public void fetchUsersByGroup(final int selectedGroup) {

            tpe.submit(new Runnable() {
                @Override
                public void run() {
                    users = UserORM.getUsersByGroupId(DataService.this, selectedGroup);
                    userList.clear();
                    for(int i=0; i< users.size(); i++) {
                        userList.add(users.valueAt(i));
                    }
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

            tpe.submit(new Runnable() {
                @Override
                public void run() {
                    groups = GroupORM.getGroups(DataService.this);
                    groupList.clear();
                    for(int i=0; i< groups.size(); i++) {
                        groupList.add(groups.valueAt(i));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(ActionDispatch ad: actionDispatches) {
                                ad.updatedGroups();
                            }
                        }
                    });

                }
            });
        }


        public void fetchYoutubeItemsByMediaFeedId(final int mediaFeedId) {

            final int userId = selectedUser;
            tpe.submit(new Runnable() {
                @Override
                public void run() {

                    getUsers().get(userId).getCastMediaFeed().get(mediaFeedId).setItems(YoutubeItemORM.getYoutubeItems(DataService.this, mediaFeedId));

                    Log.d(TAG, "fetchYoutubeItemsByMediaFeedId");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (DispatchListener ad : actionDispatches) {
                                ad.updatedUserMediaFeed(userId, mediaFeedId);
                            }
                        }
                    });

                }
            });

        }


        public int getSelectedUser() {
            return selectedUser;
        }

        public void setSelectedUser(int id) {
            selectedUser = users.valueAt(id).getId();
        }
    }





}