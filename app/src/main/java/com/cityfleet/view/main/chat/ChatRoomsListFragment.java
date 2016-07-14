package com.cityfleet.view.main.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.ChatFriend;
import com.cityfleet.model.ChatMessage;
import com.cityfleet.model.ChatRoom;
import com.cityfleet.util.Constants;
import com.cityfleet.util.DividerItemDecoration;
import com.cityfleet.util.EndlessRecyclerOnScrollListener;
import com.cityfleet.util.MarkMessageSeenEvent;
import com.cityfleet.util.NewMessageEvent;
import com.cityfleet.util.PrefUtil;
import com.cityfleet.util.RoomInvitationEvent;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * Created by vika on 12.04.16.
 */
public class ChatRoomsListFragment extends BaseFragment implements ChatRoomsAdapter.OnItemClickListener, ChatRoomPresenter.ChatRoomsListView, SearchEditText.EditTextImeBackListener {
    @Bind(R.id.contactsList)
    RecyclerView chatRoomsList;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.searchField)
    SearchEditText searchField;
    @Bind(R.id.emptyView)
    TextView emptyView;
    private ChatRoomsAdapter adapter;
    private ChatRoomPresenter presenter;
    private EndlessRecyclerOnScrollListener scrollListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_rooms_list_fragment, container, false);
        ButterKnife.bind(this, view);
        if (adapter == null) {
            adapter = new ChatRoomsAdapter(this, getContext());
            presenter = new ChatRoomPresenter(this, CityFleetApp.getInstance().getNetworkManager());
            presenter.loadNextPage(Constants.DEFAULT_UNSELECTED_POSITION);
        }
        chatRoomsList.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRoomsList.setAdapter(adapter);
        chatRoomsList.addItemDecoration(new DividerItemDecoration(getContext()));
        setScrollListener((LinearLayoutManager) chatRoomsList.getLayoutManager());
        searchField.setOnEditTextImeBackListener(this);
        adapter.registerAdapterDataObserver(dataObserver);
        return view;
    }

    private RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            chatRoomsList.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        }
    };

    @OnEditorAction(R.id.searchField)
    protected boolean onSearchClicked(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            presenter.search(searchField.getText().toString());
            hideKeyboard();
            return true;
        }

        return false;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    @OnClick(R.id.newChatRoomBtn)
    public void onNewChatRoomBtnClicked() {
        ((BaseActivity) (getActivity())).changeFragment(new SelectFriendsListFragment(), true);
    }

    @Override
    public void onItemClick(ChatRoom item) {
        List<ChatFriend> chatFriends = item.getParticipants();
        int[] participants = new int[chatFriends.size()];
        for (int i = 0; i < participants.length; i++) {
            participants[i] = chatFriends.get(i).getId();
        }
        ((BaseActivity) getActivity()).changeFragment(ChatDetailFragment.getInstance(item.getId(), participants), true);
        item.setUnseen(0);
    }

    @Override
    public void startLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    public void setScrollListener() {
        setScrollListener((LinearLayoutManager) chatRoomsList.getLayoutManager());
    }

    public void removeScrollListener() {
        chatRoomsList.removeOnScrollListener(scrollListener);
    }

    private void setScrollListener(LinearLayoutManager llm) {
        scrollListener = new EndlessRecyclerOnScrollListener(llm) {
            @Override
            public void onLoadMore(int current_page) {
                presenter.loadNextPage(adapter.getItemCount());
            }
        };
        chatRoomsList.addOnScrollListener(scrollListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(final NewMessageEvent event) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
              //      if (getView() != null) {
                        ChatMessage chatMessage = event.getChatMessage();
                        ChatRoom chatRoomWithNewMessage = null;
                        for (ChatRoom chatRoom : adapter.getChatList()) {
                            if (chatRoom.getId() == chatMessage.getRoom()) {
                                chatRoomWithNewMessage = chatRoom;
                                if (chatMessage.getAuthor() != PrefUtil.getId(getContext())) {
                                    chatRoom.setUnseen(chatRoom.getUnseen() + 1);
                                }
                                chatRoom.setLastMessage(chatMessage.getText());
                                chatRoom.setLastMessageTimestamp(chatMessage.getCreated());
                                break;
                            }
                        }

                        if (chatRoomWithNewMessage != null) {
                            adapter.getChatList().remove(chatRoomWithNewMessage);
                            adapter.getChatList().add(0, chatRoomWithNewMessage);
                        } else {

                            boolean containsSearchWords = false;
                            if (searchField != null && TextUtils.isEmpty(searchField.getText())) {
                                containsSearchWords = true;
                            } else if(searchField!=null){
                                for (ChatFriend chatFriend : chatMessage.getParticipants()) {
                                    if (chatFriend.getId() != PrefUtil.getId(getContext()) && chatFriend.getName().toLowerCase().contains(searchField.getText().toString().toLowerCase())) {
                                        containsSearchWords = true;
                                        break;
                                    }
                                }
                            }

                            if (containsSearchWords) {
                                chatRoomWithNewMessage = new ChatRoom();
                                chatRoomWithNewMessage.setLastMessage(chatMessage.getText());
                                chatRoomWithNewMessage.setLastMessageTimestamp(chatMessage.getCreated());
                                chatRoomWithNewMessage.setUnseen(1);
                                chatRoomWithNewMessage.setId(chatMessage.getRoom());
                                chatRoomWithNewMessage.setParticipants(chatMessage.getParticipants());
                                adapter.getChatList().add(0, chatRoomWithNewMessage);
                                presenter.onNewMessageWithoutChatRoom();
                            }
                        }
                        adapter.notifyDataSetChanged();
                   // }
                }
            });
        }
    }

    @Subscribe
    public void onEventMainThread(final RoomInvitationEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChatRoom chatRoom = event.getChatRoom();
                boolean containsSearchWords = false;
                if (searchField != null && searchField.getText().toString().isEmpty()) {
                    containsSearchWords = true;
                } else {
                    for (ChatFriend chatFriend : chatRoom.getParticipants()) {
                        if (chatFriend.getId() != PrefUtil.getId(getContext()) && searchField != null && chatFriend.getName().toLowerCase().contains(searchField.getText().toString().toLowerCase())) {
                            containsSearchWords = true;
                            break;
                        }
                    }
                }
                if (containsSearchWords) {
                    adapter.onNewChatRoom(event.getChatRoom());
                    presenter.onNewChatRoom();
                }
            }
        });
    }


    @Subscribe
    public void onEventMainThread(final MarkMessageSeenEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.markMessageAsSeen(event.getMarkRoomAsRead().getRoom());
            }
        });
    }

    @Override
    public void stopLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = getString(R.string.default_error_mes);
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }

    @Override
    public void clearList() {
        adapter.clearList();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChatRoomsListLoaded(List<ChatRoom> chatRooms) {
        if (chatRooms.size() > 0) {
            adapter.addItems(chatRooms);
            adapter.notifyDataSetChanged();
        } else {
            emptyView.setVisibility(View.VISIBLE);
            chatRoomsList.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.unregisterAdapterDataObserver(dataObserver);
        ButterKnife.unbind(this);
    }

    @Override
    public void onImeBack(SearchEditText ctrl, String text) {
        presenter.search(searchField.getText().toString());
        hideKeyboard();
    }
}
