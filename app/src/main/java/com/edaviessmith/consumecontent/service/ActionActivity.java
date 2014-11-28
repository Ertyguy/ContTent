package com.edaviessmith.consumecontent.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


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
                for(ActionDispatch fragActionDispatch: fragActionDispatches) binder.addListener(fragActionDispatch);
                onBind();
                bindState = 2;
            } else {
                unbindService(this);
                bindState = 0;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
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
        bindState = 1;
        Intent intent = new Intent(this, DataService.class);
        startService(intent);
        bindService(intent, serviceConnection, 0);

        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (bindState == 2) {
            binder.removeListener(actionDispatch);
            for(ActionDispatch fragActionDispatch: fragActionDispatches) binder.removeListener(fragActionDispatch);
            unbindService(serviceConnection);
        }
        bindState = 0;
    }

    protected void onBind() {
        binder.fetchBinder();

    }

    List<ActionDispatch> fragActionDispatches = new ArrayList<ActionDispatch>();

    public void addActionFragment(ActionDispatch fragActionDispatch) {
        fragActionDispatches.add(fragActionDispatch);
        if(bindState == 2) {
            binder.addListener(fragActionDispatch);
        }
    }

    public void removeActionFragment(ActionDispatch fragActionDispatch) {
        fragActionDispatches.remove(fragActionDispatch);
        if(bindState == 2) {
            binder.removeListener(fragActionDispatch);
        }
    }
}

