package com.example.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Calendar;

public class CalendarSharedPreferences {

    private static final String PREF_NAME = "calendar_pref";
    private static final String KEY_CALENDAR_TIME = "calendar_time";
    private static final String KEY_LAST_NOTIFICATION_HOUR = "last_notification_hour";

    // Сохраняем время календаря и последний час уведомления в SharedPreferences
    public static void saveData(Context context, Calendar calendar, int lastNotificationHour) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long timeInMillis = calendar.getTimeInMillis();
        editor.putLong(KEY_CALENDAR_TIME, timeInMillis);
        editor.putInt(KEY_LAST_NOTIFICATION_HOUR, lastNotificationHour);
        editor.apply();
    }

    // Загружаем время календаря из SharedPreferences
    public static Calendar loadCalendarTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long timeInMillis = sharedPreferences.getLong(KEY_CALENDAR_TIME, 0);
        if(timeInMillis == 0)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar;
    }

    // Загружаем последний час уведомления из SharedPreferences
    public static int loadLastNotificationHour(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_LAST_NOTIFICATION_HOUR, -1);
    }
}
