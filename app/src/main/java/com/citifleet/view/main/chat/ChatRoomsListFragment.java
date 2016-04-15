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
import com.citifleet.model.ChatFriend;
import com.citifleet.model.ChatRoom;
import com.citifleet.util.DividerItemDecoration;
import com.citifleet.view.BaseFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 12.04.16.
 */
public class ChatRoomsListFragment extends BaseFragment implements FriendsListAdapter.OnItemClickListener, ChatRoomsAdapter.OnItemClickListener, ChatRoomPresenter.ChatRoomsListView {
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
        adapter = new ChatRoomsAdapter(this);
        contactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsList.setAdapter(adapter);
        contactsList.addItemDecoration(new DividerItemDecoration(getContext()));
        contactsList.setNestedScrollingEnabled(false);
        presenter = new ChatRoomPresenter(this, CitiFleetApp.getInstance().getNetworkManager());
        presenter.loadAllChatRooms();
        return view;
    }

    @Override
    public void onItemClick(ChatFriend item) {

    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        progressBar.setVisibility(View.GONE);
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
    public void onItemClick(ChatRoom item) {

    }
}
