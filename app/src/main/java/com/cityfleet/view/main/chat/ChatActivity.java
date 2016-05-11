package com.cityfleet.view.main.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.ChatMessage;
import com.cityfleet.model.ChatMessageTypes;
import com.cityfleet.model.ChatRoom;
import com.cityfleet.model.MarkRoomAsRead;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;
import com.cityfleet.util.Constants;
import com.cityfleet.util.MarkMessageSeenEvent;
import com.cityfleet.util.NewMessageEvent;
import com.cityfleet.util.PostMessageEvent;
import com.cityfleet.util.PrefUtil;
import com.cityfleet.util.RoomInvitationEvent;
import com.cityfleet.view.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 15.04.16.
 */
public class ChatActivity extends BaseActivity {
    private static final String SERVER = "ws://104.236.223.160/?token=";
    private WebSocket webSocket;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        try {
            webSocket = connectToWebSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        int roomId = getIntent().getIntExtra(Constants.CHAT_ID_TAG, 0);
        if (roomId != 0) {
            changeFragment(ChatDetailFragment.getInstance(roomId), false);
        } else {
            changeFragment(new FriendsListTabbedFragment(), false);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int roomId = intent.getIntExtra(Constants.CHAT_ID_TAG, 0);
        if (roomId != 0) {
            changeFragment(ChatDetailFragment.getInstance(roomId), false);
            EventBus.getDefault().post(new MarkMessageSeenEvent(new MarkRoomAsRead(roomId)));
        }
    }

    @Subscribe
    public void onEventMainThread(PostMessageEvent event) {
        Gson gson = new Gson();
        String message = gson.toJson(event.getChatMessageToSend());
        webSocket.sendText(message);
    }

    @Subscribe
    public void onEventMainThread(MarkMessageSeenEvent event) {
        Gson gson = new Gson();
        String message = gson.toJson(event.getMarkRoomAsRead());
        webSocket.sendText(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.disconnect();
        }
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected Fragment getInitFragment() {
        return null;
    }

    private WebSocket connectToWebSocket() throws IOException, WebSocketException {
        return new WebSocketFactory()
                .createSocket(SERVER + PrefUtil.getToken(this))
                .addListener(webSocketListener)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connectAsynchronously();
    }

    public void createChatRoomAndOpen(Set<Integer> userIds, boolean addToBackStack) {
        NetworkManager networkManager = CityFleetApp.getInstance().getNetworkManager();
        if (networkManager.isConnectedOrConnecting()) {
            progressBar.setVisibility(View.VISIBLE);
            StringBuilder chatName = new StringBuilder();
            int[] participants = new int[userIds.size()];
            int i = 0;
            for (Integer userId : userIds) {
                chatName.append(userId).append(", ");
                participants[i] = userId;
                i++;
            }
            String chatNameString = chatName.substring(0, chatName.lastIndexOf(","));
            Call<ChatRoom> call = networkManager.getNetworkClient().createChatRoom(chatNameString, participants);
            CreateChatRoomCallback callback = new CreateChatRoomCallback();
            callback.setAddToBackStack(addToBackStack);
            call.enqueue(callback);
        } else {
            onNetworkError();
        }
    }

    public void onNetworkError() {
        Toast.makeText(this, getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }

    private void onMessageReceived(String message) {
        Gson gson = new Gson();
        JsonObject object = new JsonParser().parse(message).getAsJsonObject();
        if (object.has("type")) {
            String method = object.get("type").getAsString();
            if (method.equals(ChatMessageTypes.RECEIVE_MESSAGE.getName())) {
                ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);
                EventBus.getDefault().post(new NewMessageEvent(chatMessage));
            } else if (method.equals(ChatMessageTypes.ROOM_INVITATION.getName())) {
                ChatRoom chatRoom = gson.fromJson(message, ChatRoom.class);
                EventBus.getDefault().post(new RoomInvitationEvent(chatRoom));
            }
        }
    }

    private WebSocketAdapter webSocketListener = new WebSocketAdapter() {
        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            Log.d("TAG", "connected");
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
            super.onConnectError(websocket, exception);
            Log.d("TAG", "connection error: " + exception.getMessage());
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            Log.d("TAG", "on disconnected");
        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            onMessageReceived(text);
        }

        @Override
        public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
            super.onBinaryMessage(websocket, binary);
            Log.d("TAG", "binary message");
        }

        @Override
        public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            super.onBinaryFrame(websocket, frame);
            Log.d("TAG", "binary frame");

        }

        @Override
        public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {
            super.onMessageDecompressionError(websocket, cause, compressed);
            Log.d("TAG", "error" + cause.getMessage());
        }

        @Override
        public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
            super.onTextMessageError(websocket, cause, data);
            Log.d("TAG", "error" + cause.getMessage());
        }

    };

    public void onFailure(String error) {
        if (error == null) {
            error = getString(R.string.default_error_mes);
        }
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private class CreateChatRoomCallback implements Callback<ChatRoom> {

        @Override
        public void onResponse(Call<ChatRoom> call, Response<ChatRoom> response) {
            progressBar.setVisibility(View.GONE);
            if (response.isSuccessful()) {
                changeFragment(ChatDetailFragment.getInstance(response.body().getId()), addToBackStack);
            } else {
                ChatActivity.this.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<ChatRoom> call, Throwable t) {
            Log.e(FriendsListPresenter.class.getName(), t.getMessage());
            progressBar.setVisibility(View.GONE);
            ChatActivity.this.onFailure(t.getMessage());
        }

        private boolean addToBackStack = false;

        public void setAddToBackStack(boolean addToBackStack) {
            this.addToBackStack = addToBackStack;
        }
    }

}
