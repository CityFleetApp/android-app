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
import com.citifleet.model.ChatFriend;
import com.citifleet.model.ChatMessage;
import com.citifleet.model.ChatMessageTypes;
import com.citifleet.model.Report;
import com.citifleet.util.Constants;
import com.citifleet.util.NewReportAddedEvent;
import com.citifleet.util.ReportDeletedEvent;
import com.citifleet.view.main.chat.ChatActivity;
import com.citifleet.view.main.chat.ChatDetailFragment;
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
    //{"author":1,"created":"2016-04-19T14:18:32","text":"Lorem ipsum dolor sit amet, in aeque ancillae incorrupte nec, altera postulant constituam cum in. Illud officiis nam et, hinc regione detraxit est ut. Qui falli labore invenire ea, et ius aliquid accusam accusata. Duo saepe prompta conclusionemque ut. His ei tritani mentitum.  Eos ei scripta inciderint, mea eu wisi error fierent. Cu duo habeo adolescens. Inani putant feugait sed ne, purto veri dicit sea cu. Eum ex impedit senserit scribentur, ex homero lobortis scribentur ius.  Ne quaeque erroribus eum, essent nonumes menandri his cu. Erat inermis sapientem mei eu,","type":"receive_message","room":6,"author_info":{"full_name":"admin","avatar_url":"","phone":"1","id":1}}

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
        } else if (messageObject.has("type")) {
            if (messageObject.get("type").getAsString().equals(ChatMessageTypes.RECEIVE_MESSAGE.getName())) {
                Gson gson = new GsonBuilder().create();
                ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);
                showNewMessageNotification(chatMessage);
            }
        }
    }

    private void showNewMessageNotification(ChatMessage chatMessage) {
        if (!(ChatDetailFragment.isFragmentActive() && ChatDetailFragment.getRoomId()==chatMessage.getRoom())) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(Constants.CHAT_ID_TAG, chatMessage.getRoom());
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ChatFriend author=null;
            for(ChatFriend friend: chatMessage.getParticipants()){
                if(friend.getId()==chatMessage.getAuthor()){
                    author = friend;
                }
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(author.getName())
                    .setContentText(chatMessage.getText())
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
}

