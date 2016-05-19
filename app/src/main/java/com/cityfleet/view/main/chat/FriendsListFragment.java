package com.cityfleet.view.main.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.cityfleet.model.ChatRoom;
import com.cityfleet.util.DividerItemDecoration;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;

/**
 * Created by vika on 11.04.16.
 */
public class FriendsListFragment extends BaseFragment implements FriendsListAdapter.OnItemClickListener, FriendsListPresenter.FriendsListView, SearchEditText.EditTextImeBackListener {
    @Bind(R.id.searchField)
    SearchEditText searchField;
    @Bind(R.id.contactsList)
    RecyclerView contactsList;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.toolbar)
    AppBarLayout toolbar;
    @Bind(R.id.emptyView)
    TextView emptyView;
    private FriendsListAdapter adapter;
    private FriendsListPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_list_fragment, container, false);
        ButterKnife.bind(this, view);
        toolbar.setVisibility(View.GONE);
        if (adapter == null) {
            adapter = new FriendsListAdapter(this);
            presenter = new FriendsListPresenter(this, CityFleetApp.getInstance().getNetworkManager());
            presenter.loadAllFriends();
        }
        contactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsList.setAdapter(adapter);
        contactsList.addItemDecoration(new DividerItemDecoration(getContext()));
        searchField.setOnEditTextImeBackListener(this);
        adapter.registerAdapterDataObserver(dataObserver);
        return view;
    }

    private RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            contactsList.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.unregisterAdapterDataObserver(dataObserver);
        ButterKnife.unbind(this);
    }

    @OnEditorAction(R.id.searchField)
    protected boolean onSearchClicked(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            adapter.getFilter().filter(searchField.getText().toString());
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

    @Override
    public void onItemClick(ChatFriend item) {
        Set<Integer> set = new LinkedHashSet<Integer>();
        set.add(item.getId());
        ((ChatActivity) getActivity()).createChatRoomAndOpen(set, true);
    }


    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
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
    public void onFriendsLoaded(List<ChatFriend> friends) {
        searchField.getText().clear();
        if (friends.size() > 0) {
            adapter.setList(friends);
            adapter.notifyDataSetChanged();
        } else {
            emptyView.setVisibility(View.VISIBLE);
            contactsList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onChatRoomCreated(ChatRoom chatRoom) {
        List<ChatFriend> chatFriends = chatRoom.getParticipants();
        int[] participants = new int[chatFriends.size()];
        for (int i = 0; i < participants.length; i++) {
            participants[i] = chatFriends.get(i).getId();
        }
        ((BaseActivity) getActivity()).changeFragment(ChatDetailFragment.getInstance(chatRoom.getId(), participants), true);
    }

    @Override
    public void onImeBack(SearchEditText ctrl, String text) {
        adapter.getFilter().filter(searchField.getText().toString());
        hideKeyboard();
    }
}
