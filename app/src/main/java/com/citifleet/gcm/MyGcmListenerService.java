package com.citifleet.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.citifleet.R;
import com.citifleet.view.main.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by vika on 21.03.16.
 */
public class MyGcmListenerService extends GcmListenerService {
    //    receive push: {"lng":28.4802189,"action":"added","id":115,"type":2,"lat":49.2389835}
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("TAG", "receive push: " + message);

        JsonParser parser = new JsonParser();
        JsonObject messageObject = parser.parse(message).getAsJsonObject();

        //TODO process or show notification
        sendNotification(message);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("GCM Message")
                .setSmallIcon(R.drawable.alert)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}

