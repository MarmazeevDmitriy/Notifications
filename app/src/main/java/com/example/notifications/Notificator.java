package com.example.notifications;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Notificator {
    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleNotification(Context context, @Nullable Calendar newCalendar) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        Calendar calendar = CalendarSharedPreferences.loadCalendarTime(context);
        int lastNotificationHour = CalendarSharedPreferences.loadLastNotificationHour(context);

        if(calendar == null || calendar.getTimeInMillis() == 0){
            calendar = newCalendar;
            Log.d("Date", "New Calendar");
            Log.d("Date", logCurrentTime(calendar));
        } else if (newCalendar != null) {
            if(newCalendar.getTimeInMillis() > calendar.getTimeInMillis()){
                calendar = newCalendar;
                lastNotificationHour = -1;
                Log.d("Date", "New Calendar Time");
            }
            else{
                Log.d("Date", "Old Calendar");
                Log.d("Date", logCurrentTime(calendar));
                return;
            }
        }

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //long millis1 = calendar.getTimeInMillis();
        //String dateTimeString = logCurrentTime(calendar);
        //Log.d("Date", dateTimeString);

        switch (lastNotificationHour){
            case -1:
                int[] notificationHours = {6, 12, 18, 24};
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                Log.d("Date", Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)));
                for (int hour : notificationHours) {
                    if (hour > currentHour) {
                        lastNotificationHour = hour % 24;
                        break;
                    }
                }
                Log.d("Date", Integer.toString(lastNotificationHour));
                break;
            case 0:
                lastNotificationHour = 6;
                Log.d("Date", "6 hours next");
                break;
            case 6:
                lastNotificationHour = 12;
                Log.d("Date", "12 hours next");
                break;
            case 12:
                lastNotificationHour = 18;
                Log.d("Date", "18 hours next");
                break;
            case 18:
                lastNotificationHour = 0;
                Log.d("Date", "0 hours next");
                break;
        }

        if(lastNotificationHour == 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        calendar.set(Calendar.HOUR_OF_DAY, lastNotificationHour);

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //dateTimeString = logCurrentTime(calendar);
        //Log.d("Date", dateTimeString);
        //Log.d("Date", Long.toString(millis1 - calendar.getTimeInMillis()));
        CalendarSharedPreferences.saveData(context, calendar, lastNotificationHour);
        Log.d("Date", logCurrentTime(calendar));

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @SuppressLint({"MissingPermission", "NotifyDataSetChanged"})
    public static void sendNotification(Context context) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notification")
                .setContentText("This is your notification content")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(MainActivity.CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // После получения уведомления, отправляем его
            sendNotification(context);
            // Переустанавливаем следующее уведомление (если нужно)
            scheduleNotification(context, null);
        }
    }

    public static String logCurrentTime(Calendar calendar) {
        // Создаем форматтер даты и времени
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        // Получаем строковое представление текущего времени в заданном формате
        String currentTime = sdf.format(calendar.getTime());
        // Выводим текущее время в лог
        return currentTime;
    }
}
