package com.edaviessmith.consumecontent.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;


abstract public class ActionFragment extends Fragment {
    public final String TAG = ((Object) this).getClass().getName();

    public ActionActivity activity;
    public ActionDispatch actionDispatch;
    public DataService.ServiceBinder binder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ActionActivity) getActivity();
        binder = activity.binder;
    }


    @Override
    public void onResume() {
        super.onResume();
        activity.binder.addListener(actionDispatch);
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.binder.removeListener(actionDispatch);
    }

}

