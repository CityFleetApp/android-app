package com.cityfleet.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.cityfleet.R;
import com.cityfleet.model.ChatFriend;
import com.cityfleet.model.ChatMessage;
import com.cityfleet.model.ChatMessageTypes;
import com.cityfleet.model.JobOfferCompletedPushNotification;
import com.cityfleet.model.PushNotification;
import com.cityfleet.model.Report;
import com.cityfleet.util.CircleTransform;
import com.cityfleet.util.Constants;
import com.cityfleet.util.NewReportAddedEvent;
import com.cityfleet.util.ReportDeletedEvent;
import com.cityfleet.view.main.MainActivity;
import com.cityfleet.view.main.chat.ChatActivity;
import com.cityfleet.view.main.chat.ChatDetailFragment;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

/**
 * Created by vika on 21.03.16.
 */
public class MyGcmListenerService extends GcmListenerService {
    //    receive push: {"lng":28.4802189,"action":"added","id":115,"type":2,"lat":49.2389835}
    //receive push: {"lng":28.4802699,"action":"removed","id":126,"report_type":4,"lat":49.2389275}
    //   {"id":13,"title":"Job Offer Created","type":"offer_created"}
    //{"author":1,"created":"2016-04-19T14:18:32","text":"Lorem ipsum dolor sit amet, in aeque ancillae incorrupte nec, altera postulant constituam cum in. Illud officiis nam et, hinc regione detraxit est ut. Qui falli labore invenire ea, et ius aliquid accusam accusata. Duo saepe prompta conclusionemque ut. His ei tritani mentitum.  Eos ei scripta inciderint, mea eu wisi error fierent. Cu duo habeo adolescens. Inani putant feugait sed ne, purto veri dicit sea cu. Eum ex impedit senserit scribentur, ex homero lobortis scribentur ius.  Ne quaeque erroribus eum, essent nonumes menandri his cu. Erat inermis sapientem mei eu,","type":"receive_message","room":6,"author_info":{"full_name":"admin","avatar_url":"","phone":"1","id":1}}
//{'id': offer.id, 'type': 'offer_covered', 'title': 'Your job offer accepted'}
//   /'type': 'rate_driver', 'id': offer.id, 'title': offer.title
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
                final Report report = gson.fromJson(message, Report.class);
                Handler mainHandler = new Handler(getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new NewReportAddedEvent(report));
                    }
                };
                mainHandler.post(myRunnable);
            } else if (action.equals("removed")) {
                Gson gson = new GsonBuilder().create();
                final Report report = gson.fromJson(message, Report.class);
                Handler mainHandler = new Handler(getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new ReportDeletedEvent(report));
                    }
                };
                mainHandler.post(myRunnable);
            }
        } else if (messageObject.has("type")) {
            String type = messageObject.get("type").getAsString();
            Gson gson = new GsonBuilder().create();
            if (type.equals(ChatMessageTypes.RECEIVE_MESSAGE.getName())) {
                ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);
                showNewMessageNotification(chatMessage);
            } else if (type.equals("offer_covered") || type.equals("offer_created")) {
                PushNotification offerCoveredNotification = gson.fromJson(message, PushNotification.class);
                showJobOfferAwardedNotification(offerCoveredNotification, type.equals("offer_covered"));
            } else if (type.equals("new_notification")) {
                PushNotification newNotification = gson.fromJson(message, PushNotification.class);
                showNewNotification(newNotification);
            } else if (type.equals("rate_driver")) {
                JobOfferCompletedPushNotification notification = gson.fromJson(message, JobOfferCompletedPushNotification.class);
                showJobOfferCompletedNotification(notification);
            }
        }
    }

    private void showNewMessageNotification(ChatMessage chatMessage) {
        if (!(ChatDetailFragment.isFragmentActive() && ChatDetailFragment.getRoomId() == chatMessage.getRoom())) {
            Intent intent = new Intent(this, ChatActivity.class);
            List<ChatFriend> chatFriends = chatMessage.getParticipants();
            int[] participants = new int[chatFriends.size()];
            for(int i=0; i<participants.length; i++){
                participants[i] = chatFriends.get(i).getId();
            }
            intent.putExtra(Constants.CHAT_ID_TAG, chatMessage.getRoom());
            intent.putExtra(Constants.CHAT_PARTICIPANTS_TAG, participants);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ChatFriend author = null;
            for (ChatFriend friend : chatMessage.getParticipants()) {
                if (friend.getId() == chatMessage.getAuthor()) {
                    author = friend;
                }
            }
            Bitmap authorImage = null;
            if (!TextUtils.isEmpty(author.getPhoto())) {
                try {
                    int imageSize = getResources().getDimensionPixelSize(R.dimen.large_image_notification_size);
                    authorImage = Picasso.with(getApplicationContext()).load(author.getPhoto()).transform(new CircleTransform()).resize(imageSize, imageSize).centerCrop().get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(author.getName())
                    .setContentText(chatMessage.getText())
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH);
            if (authorImage != null) {
                notificationBuilder.setLargeIcon(authorImage);
            }
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Constants.NEW_MESSAGE_NOTIF_ID, notificationBuilder.build());

        }
    }

    private void showJobOfferAwardedNotification(PushNotification offerCoveredNotification, boolean isCovered) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.JOB_OFFER_ID_TAG, offerCoveredNotification.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(offerCoveredNotification.getTitle())
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(isCovered ? Constants.JOB_OFFER_COVERED_NOTIF_ID : Constants.NEW_JOB_OFFER_NOTIF_ID, notificationBuilder.build());
    }

    private void showJobOfferCompletedNotification(JobOfferCompletedPushNotification pushNotification) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.JOB_OFFER_ID_TAG, pushNotification.getId());
        intent.putExtra(Constants.JOB_OFFER_TITLE_TAG, pushNotification.getOfferTitle());
        intent.putExtra(Constants.JOB_OFFER_EXECUTOR_TAG, pushNotification.getDriverName());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(pushNotification.getTitle())
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.JOB_OFFER_COMPLETED_NOTIF_ID, notificationBuilder.build());
    }

    private void showNewNotification(PushNotification newNotification) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.NOTIFICATION_ID_TAG, newNotification.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(newNotification.getTitle())
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Constants.ADMIN_NOTIF_ID, notificationBuilder.build());
    }
}

