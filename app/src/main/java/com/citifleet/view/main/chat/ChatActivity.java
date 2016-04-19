package com.citifleet.view.main.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.citifleet.R;
import com.citifleet.model.ChatMessage;
import com.citifleet.model.ChatMessageTypes;
import com.citifleet.util.Constants;
import com.citifleet.util.NewMessageEvent;
import com.citifleet.util.PostMessageEvent;
import com.citifleet.util.PrefUtil;
import com.citifleet.view.BaseActivity;
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

/**
 * Created by vika on 15.04.16.
 */
public class ChatActivity extends BaseActivity {
    private static final String SERVER = "ws://104.236.223.160/?token=";
    private WebSocket webSocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
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

    @Subscribe
    public void onEvent(PostMessageEvent event) {
        Gson gson = new Gson();
        String message = gson.toJson(event.getChatMessageToSend());
        webSocket.sendText(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.disconnect();
        }
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

    private void onMessageReceived(String message) {
        Gson gson = new Gson();
        JsonObject object = new JsonParser().parse(message).getAsJsonObject();
        if (object.has("type")) {
            String method = object.get("type").getAsString();
            if (method.equals(ChatMessageTypes.RECEIVE_MESSAGE.getName())) {
                ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);
                EventBus.getDefault().post(new NewMessageEvent(chatMessage));
            } else {
                //TODO
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
}
