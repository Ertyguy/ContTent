package com.edaviessmith.consumecontent.util;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction() != null) {

            boolean bootIntent =  intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED);
            //boolean bootEnabled = Util.notificationsOnBootEnabled();
            //If booting and boot is enabled or being called regularly
            if ((bootIntent) || intent.getAction().equals(Var.NOTIFY_ACTION)) {
                Intent i = new Intent(context, MediaFeedActivityService.class);
                context.startService(i);

            }
        }
    }

}