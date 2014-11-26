package com.edaviessmith.consumecontent.util;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.NotificationList;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.db.GroupORM;
import com.edaviessmith.consumecontent.db.UserORM;

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


    public List<Group> groups;
    public List<User> users;
    public NotificationList notificationList;



    @Override
    public void onCreate() {
        handler = new Handler();

        tpe.submit(new Runnable() {
            @Override
            public void run() {
                notificationList = new NotificationList(DataService.this);
            }
        });

        Log.d(TAG, "service get notificationList");

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
            if (actionDispatch != null)
                actionDispatches.add(actionDispatch);
        }

        public void removeListener(ActionDispatch actionDispatch) {
            actionDispatches.remove(actionDispatch);
        }


        public List<User> getUsers() {
            return users;
        }

        public List<Group> getGroups() {
            return groups;
        }

        public void fetchUsersByGroup(final int selectedGroup) {

            tpe.submit(new Runnable() {
                @Override
                public void run() {
                    users = UserORM.getUsersByGroup(DataService.this, selectedGroup);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(DispatchListener ad: actionDispatches) {
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(DispatchListener ad: actionDispatches) {
                                ad.updatedGroups();
                            }
                        }
                    });

                }
            });
        }


        public NotificationList getNotificationList() {
            return notificationList;
        }


    }





}