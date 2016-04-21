package com.citifleet.view.main.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.ChatRoom;
import com.citifleet.util.DividerItemDecoration;
import com.citifleet.util.MarkMessageSeenEvent;
import com.citifleet.util.NewMessageEvent;
import com.citifleet.util.RoomInvitationEvent;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 12.04.16.
 */
public class ChatRoomsListFragment extends BaseFragment implements ChatRoomsAdapter.OnItemClickListener, ChatRoomPresenter.ChatRoomsListView {
    @Bind(R.id.contactsList)
    RecyclerView contactsList;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    private ChatRoomsAdapter adapter;
    private ChatRoomPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_rooms_list_fragment, container, false);
        ButterKnife.bind(this, view);
        if (adapter == null) {
            adapter = new ChatRoomsAdapter(this, getContext());
            presenter = new ChatRoomPresenter(this, CitiFleetApp.getInstance().getNetworkManager());
            presenter.loadAllChatRooms();
        }
        contactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsList.setAdapter(adapter);
        contactsList.addItemDecoration(new DividerItemDecoration(getContext()));
        contactsList.setNestedScrollingEnabled(false);
        return view;
    }

    @OnClick(R.id.newChatRoomBtn)
    public void onNewChatRoomBtnClicked() {
        ((BaseActivity) (getActivity())).changeFragment(new SelectFriendsListFragment(), true);
    }

    @Override
    public void onItemClick(ChatRoom item) {
        ((BaseActivity) getActivity()).changeFragment(ChatDetailFragment.getInstance(item.getId()), true);
        item.setUnseen(0);
    }

    @Override
    public void startLoading() {
        if (isAdded()) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(final NewMessageEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.onNewMessage(event.getChatMessage());
            }
        });
    }

    @Subscribe
    public void onEvent(final RoomInvitationEvent event){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.onNewChatRoom(event.getChatRoom());
            }
        });
    }


    @Subscribe
    public void onEvent(final MarkMessageSeenEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              adapter.markMessageAsSeen(event.getMarkRoomAsRead().getRoom());
            }
        });
    }

    @Override
    public void stopLoading() {
        if (isAdded()) {
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
    public void onChatRoomsListLoaded(List<ChatRoom> chatRooms) {
        adapter.setList(chatRooms);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
