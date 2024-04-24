package com.example.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "";
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //    CharSequence name = getString(R.string.channel_name);
        //    String description = getString(R.string.channel_description);
        //    int importance = NotificationManager.IMPORTANCE_DEFAULT;
        //    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        //    channel.setDescription(description);
        //
        //    NotificationManager notificationManager = getSystemService(NotificationManager.class);
        //    notificationManager.createNotificationChannel(channel);
        //}
        //
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
        //        .setSmallIcon(R.drawable.ic_launcher_foreground)
        //        .setContentTitle("NITIFICATION!!!")
        //        .setContentText("BLA BLA BLA")
        //        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //
        //button = findViewById(R.id.button);
        //button.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //        notificationManager.notify(0, builder.build());
        //    }
        //});

        startNotificationService();
    }

    private void startNotificationService() {
        // Создание интента для запуска службы
        Intent serviceIntent = new Intent(this, NotificationSchedulerService.class);
        // Запуск службы
        startService(serviceIntent);
    }
}