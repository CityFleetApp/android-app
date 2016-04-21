package com.citifleet.view.main.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.ChatMessage;
import com.citifleet.model.ChatMessageToSend;
import com.citifleet.model.ChatMessageTypes;
import com.citifleet.model.MarkRoomAsRead;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.util.Constants;
import com.citifleet.util.MarkMessageSeenEvent;
import com.citifleet.util.NewMessageEvent;
import com.citifleet.util.PostMessageEvent;
import com.citifleet.view.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 18.04.16.
 */
public class ChatDetailFragment extends BaseFragment {
    private int chatId;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.chatList)
    RecyclerView chatList;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.newMessageEt)
    EditText newMessageEt;
    private ChatMessagesAdapter adapter;
    private static boolean isFragmentActive = false;
    private static int roomId = Constants.DEFAULT_UNSELECTED_POSITION;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_view, container, false);
        ButterKnife.bind(this, view);
        chatId = getArguments().getInt(Constants.CHAT_ID_TAG);
        title.setText(R.string.chatting);
        adapter = new ChatMessagesAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        chatList.setLayoutManager(linearLayoutManager);
        chatList.setAdapter(adapter);
        loadChatHistory();
        return view;
    }

    public static ChatDetailFragment getInstance(int roomId) {
        ChatDetailFragment chatDetailFragment = new ChatDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHAT_ID_TAG, roomId);
        chatDetailFragment.setArguments(bundle);
        return chatDetailFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentActive = true;
        roomId = chatId;
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentActive = false;
        roomId = Constants.DEFAULT_UNSELECTED_POSITION;
    }

    public static boolean isFragmentActive() {
        return isFragmentActive;
    }

    public static int getRoomId() {
        return roomId;
    }

    @Subscribe
    public void onEvent(final NewMessageEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(event.getChatMessage().getRoom()==chatId) {
                    adapter.addMessage(event.getChatMessage());
                    chatList.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
                    EventBus.getDefault().post(new MarkMessageSeenEvent(new MarkRoomAsRead(chatId)));
                }
            }
        });
    }

    @OnClick(R.id.okBtn)
    void onOkBtnClicked() {
        if (newMessageEt.getText().length() > 0) {
            ChatMessageToSend chatMessageToSend = new ChatMessageToSend();
            chatMessageToSend.setMethod(ChatMessageTypes.POST_MESSAGE.getName());
            chatMessageToSend.setText(newMessageEt.getText().toString());
            chatMessageToSend.setRoom(chatId);
            EventBus.getDefault().post(new PostMessageEvent(chatMessageToSend));
            newMessageEt.getText().clear();
        }
    }

    private void loadChatHistory() {
        NetworkManager networkManager = CitiFleetApp.getInstance().getNetworkManager();
        if (networkManager.isConnectedOrConnecting()) {
            startLoading();
            Call<List<ChatMessage>> call = networkManager.getNetworkClient().getChatRoomHistory(chatId);
            call.enqueue(chatHistoryCallback);
        } else {
            onNetworkError();
        }
    }

    private Callback<List<ChatMessage>> chatHistoryCallback = new Callback<List<ChatMessage>>() {
        @Override
        public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
            stopLoading();
            if (response.isSuccess()) {
                adapter.setList(response.body());
            } else {
                onFailureMessage(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
            Log.e(ChatDetailFragment.class.getName(), t.getMessage());
            stopLoading();
            onFailureMessage(t.getMessage());
        }
    };


    private void startLoading() {
        if (isAdded()) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }


    private void stopLoading() {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    private void onFailureMessage(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = getString(R.string.default_error_mes);
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }


    private void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
