package com.example.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("BOOT_COMPLETED", "BOOT_COMPLETED");
            Notificator.scheduleNotification(context, Calendar.getInstance());
        }
    }
}
