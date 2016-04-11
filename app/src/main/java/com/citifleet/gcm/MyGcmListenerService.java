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
import com.citifleet.model.Report;
import com.citifleet.util.NewReportAddedEvent;
import com.citifleet.util.ReportDeletedEvent;
import com.citifleet.view.main.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by vika on 21.03.16.
 */
public class MyGcmListenerService extends GcmListenerService {
    //    receive push: {"lng":28.4802189,"action":"added","id":115,"type":2,"lat":49.2389835}
    //receive push: {"lng":28.4802699,"action":"removed","id":126,"report_type":4,"lat":49.2389275}
    //   {"id":13,"title":"Job Offer Created","type":"offer_created"}
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("TAG", "receive push: " + message);
        JsonParser parser = new JsonParser();
        JsonObject messageObject = parser.parse(message).getAsJsonObject();
        if (messageObject.has("action")) {
            String action = messageObject.get("action").getAsString();
            if (action.equals("added")) {
                Gson gson = new GsonBuilder().create();
                Report report = gson.fromJson(message, Report.class);
                EventBus.getDefault().post(new NewReportAddedEvent(report));
            } else if (action.equals("removed")) {
                Gson gson = new GsonBuilder().create();
                Report report = gson.fromJson(message, Report.class);
                EventBus.getDefault().post(new ReportDeletedEvent(report));
            }
        }
        //TODO process or show notification
        // sendNotification(message);
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

