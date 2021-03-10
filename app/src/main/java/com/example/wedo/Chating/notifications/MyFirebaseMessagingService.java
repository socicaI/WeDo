package com.example.wedo.Chating.notifications;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.wedo.Chating.InviteesChating;
import com.example.wedo.Group.MainCategoryActivity;
import com.example.wedo.Login.LoginActivity;
import com.example.wedo.R;
import com.example.wedo.Schedule.ResultActivity;
import com.example.wedo.ScheduleHttp.DeviceInfoUpdate;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        //build our notification, and than show it
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(getApplicationContext(), InviteesChating.class);     //채팅방

        SharedPreferences pref = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        String userId = pref.getString("userID", "0");
        String userPass = pref.getString("userPass", "0");
        String userNick = pref.getString("userNick", "0");
        String userEmail = pref.getString("userEmail", "0");
        String profilePath = pref.getString("profilePath", "0");

        intent.putExtra("id", remoteMessage.getData().get("group"));
        intent.putExtra("nick", userNick);
        intent.putExtra("orderNick", remoteMessage.getData().get("orderNick"));
        intent.putExtra("profilePath", profilePath);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("userID", userId);
        intent.putExtra("userPass", userPass);
        intent.putExtra("TitleProfile", remoteMessage.getData().get("TitleProfile"));
        intent.putExtra("people", remoteMessage.getData().get("people"));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        };
        DeviceInfoUpdate DeviceInfoUpdate = new DeviceInfoUpdate(userNick, remoteMessage.getData().get("orderNick"), remoteMessage.getData().get("group"), responseListener);
        RequestQueue queue = Volley.newRequestQueue(MyFirebaseMessagingService.this);
        queue.add(DeviceInfoUpdate);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "101")
                .setSmallIcon(R.drawable.wedo)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}

