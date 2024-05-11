package com.example.notifications;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "";
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView profileImageView;
    ImageView profileImageView2;
    Bitmap imageBitmap;
    Button button;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
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
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notificator.sendNotification(getApplicationContext());
            }
        });

        //startNotificationService();

        ActivityResultLauncher<String[]> multiPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                        callback -> {
                            Log.d("PermissionGrant", "Permission is" + callback.toString());
                        });

        multiPermissionLauncher.launch(new String[]{android.Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.SCHEDULE_EXACT_ALARM, Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.READ_EXTERNAL_STORAGE});

        Notificator.scheduleNotification(this, Calendar.getInstance());

























        profileImageView = findViewById(R.id.imageView);
        profileImageView2 = findViewById(R.id.imageView2);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Теперь у вас есть URI выбранного изображения. Вы можете использовать его для отображения или сохранения.
            try {
                imageBitmap = getBitmapFromUri(imageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Bitmap roundedBitmap = getRoundedBitmap(imageBitmap);
            profileImageView.setImageBitmap(roundedBitmap);

            Bitmap roundedBitmap2 = getRoundedSquareBitmap(imageBitmap, 256, 40);
            profileImageView2.setImageBitmap(roundedBitmap2);
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        Objects.requireNonNull(inputStream).close();
        return bitmap;
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = Math.min(width, height);

        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, (width - diameter) / 2, (height - diameter) / 2, diameter, diameter);
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final android.graphics.Rect rect = new android.graphics.Rect(0, 0, diameter, diameter);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(croppedBitmap, rect, rect, paint);

        return output;
    }

    private Bitmap getRoundedSquareBitmap(Bitmap bitmap, int targetSize, int cornerRadius) {
        // Масштабируем изображение до квадратного размера
        bitmap = scaleBitmapToSquare(bitmap, targetSize);

        // Создаем квадратное изображение
        Bitmap output = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));

        RectF rect = new RectF(0, 0, targetSize, targetSize);
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);

        return output;
    }

    private Bitmap scaleBitmapToSquare(Bitmap bitmap, int targetSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Определяем минимальную сторону для масштабирования
        int newSize = Math.min(width, height);

        // Масштабируем изображение до минимальной стороны
        float scale = (float) targetSize / newSize;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Вычисляем смещение для центрирования изображения
        int deltaX = Math.abs(width - newSize) / 2;
        int deltaY = Math.abs(height - newSize) / 2;

        // Обрезаем изображение до квадрата с центрированием

        return Bitmap.createBitmap(bitmap, deltaX, deltaY, newSize, newSize, matrix, true);
    }

    //private void startNotificationService() {
    //    // Создание интента для запуска службы
    //    Intent serviceIntent = new Intent(this, NotificationSchedulerService.class);
    //    // Запуск службы
    //    startService(serviceIntent);
    //}
}