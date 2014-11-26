package com.edaviessmith.consumecontent.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;


abstract public class ActionFragment extends Fragment {
    public final String TAG = ((Object) this).getClass().getName();

    public ActionActivity activity;
    public ActionDispatch actionDispatch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ActionActivity) getActivity();
        activity.addActionFragment(actionDispatch);
    }


    public DataService.ServiceBinder getBinder() {
        return activity.binder;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



/*

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
                getActivity().unbindService(this);
                bindState = 0;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            bindState = 0;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binder = null;
        bindState = 0;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        Intent intent = new Intent(getActivity(), DataService.class);
        getActivity().bindService(intent, serviceConnection, 0);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (bindState == 2) {
            binder.removeListener(actionDispatch);
            getActivity().unbindService(serviceConnection);
        }
        bindState = 0;
    }
*/

    protected void onBind() {}


}

