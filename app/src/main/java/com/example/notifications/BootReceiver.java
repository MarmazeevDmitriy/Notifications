package com.example.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
            Log.d("BOOT", "Boot complited");
            Notificator.scheduleNotification(context.getApplicationContext(), Calendar.getInstance());
        }
    }
}
