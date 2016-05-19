package com.cityfleet.view.main.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.ChatMessage;
import com.cityfleet.model.ChatMessageToSend;
import com.cityfleet.model.ChatMessageTypes;
import com.cityfleet.model.ChatRoom;
import com.cityfleet.model.MarkRoomAsRead;
import com.cityfleet.model.PagesResult;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;
import com.cityfleet.util.AddFriendsToChatEvent;
import com.cityfleet.util.Constants;
import com.cityfleet.util.EndlessRecyclerOnScrollListener;
import com.cityfleet.util.ImagePickerUtil;
import com.cityfleet.util.MarkMessageSeenEvent;
import com.cityfleet.util.NewMessageEvent;
import com.cityfleet.util.OpenChatImageEvent;
import com.cityfleet.util.PostMessageEvent;
import com.cityfleet.util.ScaleImageHelper;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
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
public class ChatDetailFragment extends BaseFragment implements ImagePickerUtil.ImageResultListener {
    private int chatId;
    private int[] chatParticipants;
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
    private EndlessRecyclerOnScrollListener scrollListener;
    private int offset = 0;
    private int totalMessagesCount;
    private ImagePickerUtil imagePickerUtil;
    private ImageResultHandler imageResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_view, container, false);
        ButterKnife.bind(this, view);
        chatId = getArguments().getInt(Constants.CHAT_ID_TAG);
        chatParticipants = getArguments().getIntArray(Constants.CHAT_PARTICIPANTS_TAG);
        title.setText(R.string.chatting);
        if (adapter == null) {
            adapter = new ChatMessagesAdapter(getContext());
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        chatList.setLayoutManager(linearLayoutManager);
        chatList.setAdapter(adapter);
        setScrollListener(linearLayoutManager);
        loadChatHistory();
        imagePickerUtil = new ImagePickerUtil(this, this);
        return view;
    }

    public static ChatDetailFragment getInstance(int roomId, int[] chatParticipants) {
        ChatDetailFragment chatDetailFragment = new ChatDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHAT_ID_TAG, roomId);
        bundle.putIntArray(Constants.CHAT_PARTICIPANTS_TAG, chatParticipants);
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

    @Subscribe
    void onOpenChatImageEvent(OpenChatImageEvent event) {
        ((ChatActivity) getActivity()).changeFragment(ChatImageDetailFragment.getInstance(event.getImageUrl()), true);
    }

    public void setScrollListener() {
        setScrollListener((LinearLayoutManager) chatList.getLayoutManager());
    }

    public void removeScrollListener() {
        chatList.removeOnScrollListener(scrollListener);
    }

    private void setScrollListener(LinearLayoutManager llm) {
        scrollListener = new EndlessRecyclerOnScrollListener(llm) {
            @Override
            public void onLoadMore(int current_page) {
                if (adapter.getItemCount() < totalMessagesCount) {
                    loadChatHistory();
                }
            }
        };
        chatList.addOnScrollListener(scrollListener);
    }

    public static boolean isFragmentActive() {
        return isFragmentActive;
    }

    public static int getRoomId() {
        return roomId;
    }

    @Subscribe
    public void onEventMainThread(final NewMessageEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event.getChatMessage().getRoom() == chatId) {
                    offset++;
                    totalMessagesCount++;
                    adapter.addMessage(event.getChatMessage());
                    EventBus.getDefault().post(new MarkMessageSeenEvent(new MarkRoomAsRead(chatId)));
                }
            }
        });
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(AddFriendsToChatEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        addFriendsToChatRoom(event.getAllFriends());
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

    @OnClick(R.id.cameraBtn)
    void onCameraBtnClicked() {
        imagePickerUtil.onImageClick();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (imagePickerUtil.isImagePickerPermissionResultCode(requestCode)) {
            imagePickerUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePickerUtil.isImagePickerRequestResultCode(requestCode)) {
            imagePickerUtil.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void addFriendsToChatRoom(int[] allParticipantsIds) {
        NetworkManager networkManager = CityFleetApp.getInstance().getNetworkManager();
        if (networkManager.isConnectedOrConnecting()) {
            startLoading();
            Call<ChatRoom> call = networkManager.getNetworkClient().editChatRoom(chatId, allParticipantsIds);
            call.enqueue(addFriendsToChatRoomCallback);
        } else {
            onNetworkError();
        }
    }

    private void loadChatHistory() {
        NetworkManager networkManager = CityFleetApp.getInstance().getNetworkManager();
        if (networkManager.isConnectedOrConnecting()) {
            startLoading();
            Call<PagesResult<ChatMessage>> call = networkManager.getNetworkClient().getChatRoomHistory(chatId, offset);
            call.enqueue(chatHistoryCallback);
        } else {
            onNetworkError();
        }
    }

    private Callback<ChatRoom> addFriendsToChatRoomCallback = new Callback<ChatRoom>() {
        @Override
        public void onResponse(Call<ChatRoom> call, Response<ChatRoom> response) {
            stopLoading();
            if (response.isSuccessful()) {
                Toast.makeText(getContext(), R.string.add_friends_chat_room_message, Toast.LENGTH_SHORT).show();
            } else {
                onFailureMessage(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<ChatRoom> call, Throwable t) {
            stopLoading();
            if (t.getMessage() != null) {
                Log.e(ChatDetailFragment.class.getName(), t.getMessage());
                onFailureMessage(t.getMessage());
            }
        }
    };

    private Callback<PagesResult<ChatMessage>> chatHistoryCallback = new Callback<PagesResult<ChatMessage>>() {
        @Override
        public void onResponse(Call<PagesResult<ChatMessage>> call, Response<PagesResult<ChatMessage>> response) {
            stopLoading();
            if (response.isSuccessful()) {
                List<ChatMessage> list = response.body().getResults();
//                Collections.reverse(list);
                totalMessagesCount = response.body().getCount();
                adapter.addItems(list);
                offset = offset + Constants.PAGE_SIZE;
            } else {
                onFailureMessage(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<PagesResult<ChatMessage>> call, Throwable t) {
            stopLoading();
            if (t.getMessage() != null) {
                Log.e(ChatDetailFragment.class.getName(), t.getMessage());
                onFailureMessage(t.getMessage());
            }
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

    @OnClick(R.id.addBtn)
    void onAddFriendBtn() {
        SelectFriendsListFragment friendsListFragment = new SelectFriendsListFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_ADD_FRIENDS_TO_CHAT_MODE, true);
        args.putIntArray(Constants.EXCLUDED_FRIENDS_IDS, chatParticipants);
        friendsListFragment.setArguments(args);
        ((BaseActivity) getActivity()).changeFragment(friendsListFragment, true);
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
    public void onDestroy() {
        super.onDestroy();
        if (imagePickerUtil != null) {
            imagePickerUtil.onDestroy();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onImageReceived(String url) {
        imageResultHandler = new ImageResultHandler(this);
        Thread thread = new Thread(new PrepareImageRunnable(url));
        thread.start();
    }

    @Override
    public void onImageCanceledOrFailed() {

    }

    private class PrepareImageRunnable implements Runnable {
        private String imageUrl;

        public PrepareImageRunnable(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        public void run() {
            ScaleImageHelper scaleImageHelper = new ScaleImageHelper();
            byte[] bytes = scaleImageHelper.getScaledImageBytes(imageUrl, Constants.PROFILE_IMAGE_WIDTH);
            String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
            if (imageResultHandler != null) {
                Message msg = new Message();
                msg.obj = encodedImage;
                imageResultHandler.handleMessage(msg);
            }
        }
    }

    static class ImageResultHandler extends Handler {
        WeakReference<ChatDetailFragment> fragmentRef;

        ImageResultHandler(ChatDetailFragment fragment) {
            this.fragmentRef = new WeakReference<ChatDetailFragment>(fragment);
        }

        @Override
        public void handleMessage(Message message) {
            ChatDetailFragment fragment = this.fragmentRef.get();
            String encodedImage = (String) message.obj;
            if (fragment != null) {
                ChatMessageToSend chatMessageToSend = new ChatMessageToSend();
                chatMessageToSend.setMethod(ChatMessageTypes.POST_MESSAGE.getName());
                chatMessageToSend.setImage(encodedImage);
                chatMessageToSend.setRoom(fragment.chatId);
                EventBus.getDefault().post(new PostMessageEvent(chatMessageToSend));
            }
        }
    }
}
