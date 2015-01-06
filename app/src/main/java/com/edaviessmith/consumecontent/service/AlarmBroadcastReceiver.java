package com.edaviessmith.consumecontent.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.edaviessmith.consumecontent.util.Var;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction() != null) {

            int notificationId =  intent.getIntExtra(Var.NOTIFY_NOTIFICATION_ID, -1);
            //boolean bootEnabled = Util.notificationsOnBootEnabled();
            //If booting and boot is enabled or being called regularly
            if (intent.getAction().equals(Var.NOTIFY_ACTION)) {
                Intent i = new Intent(context, AlarmIntentService.class);
                i.putExtra(Var.NOTIFY_NOTIFICATION_ID, notificationId);
                context.startService(i);

            }
        }
    }

}