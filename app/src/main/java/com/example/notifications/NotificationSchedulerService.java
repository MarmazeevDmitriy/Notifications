package com.example.notifications;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

public class NotificationSchedulerService extends Service {
    private static final int HOUR_TO_TRIGGER = 14; // Час, когда нужно запускать уведомления

    private Handler handler;
    private Runnable runnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Создаем и запускаем обработчик событий
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Получаем текущее время
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);

                // Вычисляем разницу во времени до ближайшего нужного времени
                int minutesDifference = calculateTimeDifference(currentHour, currentMinute);

                // Проверяем, сколько времени осталось до нужного момента
                if (minutesDifference < 60 && minutesDifference > 2) {
                    // Если менее часа, устанавливаем интервал до следующей проверки в минутах
                    handler.postDelayed(this, minutesDifference * 60 * 1000);
                } else if(minutesDifference >= 60){
                    // Если более часа, устанавливаем интервал до следующей проверки в часах
                    handler.postDelayed(this, 60 * 60 * 1000);
                } else {
                    handler.postDelayed(this, (long) minutesDifference * 60 * 1000 / 2);
                }

                // Запускаем уведомления, если текущий час соответствует HOUR_TO_TRIGGER
                if (currentHour == HOUR_TO_TRIGGER) {
                    Log.d("NotificationScheduler", "Launching notifications...");
                    Notificator.scheduleNotification(getApplicationContext());
                }
            }
        };
        handler.post(runnable);

        // Уведомляем систему, что служба должна быть перезапущена, если она прекратила свою работу
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Отменяем запланированные задачи при уничтожении службы
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Метод для вычисления разницы во времени до ближайшего нужного времени
    private int calculateTimeDifference(int currentHour, int currentMinute) {
        int timeToTrigger;
        if (currentHour < HOUR_TO_TRIGGER) {
            // Если текущий час меньше, чем HOUR_TO_TRIGGER, разница - до следующего HOUR_TO_TRIGGER
            timeToTrigger = (HOUR_TO_TRIGGER - currentHour) * 60 - currentMinute;
        } else if (currentHour > HOUR_TO_TRIGGER) {
            // Если текущий час больше, чем HOUR_TO_TRIGGER, разница - до следующего HOUR_TO_TRIGGER следующего дня
            timeToTrigger = (24 - currentHour + HOUR_TO_TRIGGER) * 60 - currentMinute;
        } else {
            // Если текущий час равен HOUR_TO_TRIGGER, разница - до следующего часа
            timeToTrigger = 60 - currentMinute;
        }
        return timeToTrigger;
    }
}
