package com.edaviessmith.consumecontent.util;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


abstract public class ActionActivity extends ActionBarActivity {
    public final String TAG = ((Object) this).getClass().getSimpleName();

    protected int bindState = 0;
    public ActionDispatch actionDispatch;
    public DataService.ServiceBinder binder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            if (bindState == 1) {
                binder = (DataService.ServiceBinder)service;
                binder.addListener(actionDispatch);
                onBind();
                bindState = 2;
            } else {
                unbindService(this);
                bindState = 0;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bindState = 0;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binder = null;
        bindState = 0;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        bindState = 1;
        Intent intent = new Intent(this, DataService.class);
        startService(intent);
        bindService(intent, serviceConnection, 0);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (bindState == 2) {
            binder.removeListener(actionDispatch);
            unbindService(serviceConnection);
        }
        bindState = 0;
    }

    protected void onBind() {}

}

