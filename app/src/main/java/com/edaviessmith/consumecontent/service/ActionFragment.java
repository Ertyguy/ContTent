package com.edaviessmith.consumecontent.service;

import android.os.Bundle;
import android.support.v4.app.Fragment;


abstract public class ActionFragment extends Fragment {
    public final String TAG = ((Object) this).getClass().getSimpleName();

    public ActionActivity activity;
    public ActionDispatch actionDispatch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ActionActivity) getActivity();
    }


    public DataService.ServiceBinder getBinder() {
        return (activity != null && activity.bindState == 2)? activity.binder: null;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.addActionFragment(actionDispatch);
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.removeActionFragment(actionDispatch);
    }

    public void cleanFragment() {

    }

    @Override
    public void onDestroy() {
        activity = null;
        actionDispatch = null;
        super.onDestroy();
    }

    protected void onBind() {}


}

