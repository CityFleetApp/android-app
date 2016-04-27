package com.cityfleet.view.main.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.ChatFriend;
import com.cityfleet.util.DividerItemDecoration;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;

import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * Created by vika on 19.04.16.
 */
public class SelectFriendsListFragment extends BaseFragment implements SelectFriendsListAdapter.OnItemClickListener, FriendsListPresenter.FriendsListView, SearchEditText.EditTextImeBackListener {
    @Bind(R.id.searchField)
    SearchEditText searchField;
    @Bind(R.id.contactsList)
    RecyclerView contactsList;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.okBtn)
    ImageButton okBtn;
    private SelectFriendsListAdapter adapter;
    private FriendsListPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_list_fragment, container, false);
        ButterKnife.bind(this, view);
        //    searchField.setVisibility(View.GONE);
        if (adapter == null) {
            adapter = new SelectFriendsListAdapter(this);
            presenter = new FriendsListPresenter(this, CityFleetApp.getInstance().getNetworkManager());
            presenter.loadAllFriends();
        }
        contactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsList.setAdapter(adapter);
        contactsList.addItemDecoration(new DividerItemDecoration(getContext()));
        contactsList.setNestedScrollingEnabled(false);
        title.setText(getString(R.string.create_chat_room));
        okBtn.setVisibility(View.INVISIBLE);
        searchField.setOnEditTextImeBackListener(this);
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.okBtn)
    void onOkBtnClick() {
        Set<Integer> participantIds = adapter.getSelectedItemsIds();
        ((ChatActivity) getActivity()).createChatRoomAndOpen(participantIds, false);
    }

    @Override
    public void onSelectionCountChanged(int count) {
        if (count == 0) {
            okBtn.setVisibility(View.INVISIBLE);
            title.setText(getString(R.string.create_chat_room));
        } else {
            okBtn.setVisibility(View.VISIBLE);
            title.setText(getString(R.string.selected, count));
        }
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
    public void onFriendsLoaded(List<ChatFriend> friends) {
        searchField.getText().clear();
        adapter.setList(friends);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChatRoomCreated(int roomId) {
        ((BaseActivity) getActivity()).changeFragment(ChatDetailFragment.getInstance(getId()), true);
    }

    @Override
    public void onImeBack(SearchEditText ctrl, String text) {
        adapter.getFilter().filter(searchField.getText().toString());
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
}
