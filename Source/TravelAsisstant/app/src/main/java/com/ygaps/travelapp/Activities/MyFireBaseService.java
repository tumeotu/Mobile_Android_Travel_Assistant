package com.ygaps.travelapp.Activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;
import com.ygaps.travelapp.Retrofit.RequestFireBase;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyFireBaseService extends FirebaseMessagingService
{
    private static final String TAG = "MyFireBaseService";
    public static String UserToken;
    public static String DeviceId;

    public static void sendRegistrationToServer(String token)
    {
        // TODO: Implement this method to send token to your app server.
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        MyAPIClient user = retrofit.create(MyAPIClient.class);

        RequestFireBase firebase = new RequestFireBase();
        firebase.FcmToken = token;
        firebase.AppVersion = "1.0";
        firebase.DeviceId = DeviceId;
        firebase.Platform = 1;
        Call<ResponseBody> call = user.registerFireBase(UserToken, firebase);
        call.enqueue(new Callback<ResponseBody>()
        {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                if (response.isSuccessful())
                {
                    try
                    {
                        Log.i(TAG, response.body().string());
                    }
                    catch (IOException ignored)
                    {
                    }
                } else
                {
                    try
                    {
                        assert response.errorBody() != null;
                        Log.e(TAG, String.format("%s: %s", response.message(), response.errorBody().string()));
                    }
                    catch (IOException ignored)
                    {
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Log.e(TAG, String.format("Failed to register firebase: %s", t.getMessage()));
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        sendNotification(remoteMessage.getData());
    }

    @Override
    public void onNewToken(String token)
    {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendNotification(Map<String, String> messageBody)
    {
        //cái intent này dẫn đến cái tab notifications nếu nhấn vào thông báo
        Intent intent1 = new Intent(this, ActivityNotify.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.project_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //tui mới chỉ làm thông báo cho lời mời, nên cái nội dung chứa trong cái
        //chuỗi content như bên dưới
        String content = messageBody.get("hostId") + " has invited you to tour " + messageBody.get("name");

        //tạo notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                        .setContentTitle("Tour Invitation")
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent1)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(0)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
