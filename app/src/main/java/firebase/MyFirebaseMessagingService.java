package firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.example.lustre.R;
import com.example.lustre.activities.OrderDetailActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "order_updates";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Message Notification Title: " + title);
            Log.d(TAG, "Message Notification Body: " + body);

            Map<String, String> data = remoteMessage.getData();
            String orderId = data.get("orderId");
            String status = data.get("status");
            String type = data.get("type");

            showNotification(title, body, orderId, status);

            if ("order_status_update".equals(type) && orderId != null) {
                handleOrderStatusUpdate(orderId, status);
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userId = getCurrentUserId();

        if (userId != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("fcmToken", token);

            db.collection("users").document(userId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "FCM Token updated successfully"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating FCM token", e));
        }
    }

    private void showNotification(String title, String body, String orderId, String status) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("status", status);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // Get appropriate icon based on status
        int iconRes = getNotificationIcon(status);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setSmallIcon(iconRes)
                        .setColor(getResources().getColor(R.color.primary))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(orderId.hashCode(), notificationBuilder.build());
        }
    }

    private int getNotificationIcon(String status) {
        switch (status) {
            case "ORDER_PLACED":
                return R.drawable.ic_order_placed;
            case "IN_PROCESS":
                return R.drawable.ic_processing;
            case "SHIPPED":
                return R.drawable.ic_shipped;
            case "CANCELED":
                return R.drawable.ic_canceled;
            default:
                return R.drawable.ic_notification;
        }
    }

    private void handleOrderStatusUpdate(String orderId, String status) {
        Intent broadcastIntent = new Intent("ORDER_STATUS_UPDATED");
        broadcastIntent.putExtra("orderId", orderId);
        broadcastIntent.putExtra("status", status);
        sendBroadcast(broadcastIntent);
        Log.d(TAG, "Order " + orderId + " status updated to: " + status);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Order Updates";
            String description = "Notifications for order status updates";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(getResources().getColor(R.color.primary));
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("user_id", null);
    }
}

