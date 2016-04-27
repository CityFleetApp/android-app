package com.cityfleet.view.main.chat;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.model.ChatFriend;
import com.cityfleet.util.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 19.04.16.
 */
public class SelectFriendsListAdapter extends RecyclerView.Adapter<SelectFriendsListAdapter.ViewHolder> implements Filterable {
    private List<ChatFriend> friendList = new ArrayList<ChatFriend>();
    private List<ChatFriend> origFriendsList = new ArrayList<ChatFriend>();
    private Set<Integer> selectedItemsIds = new LinkedHashSet<Integer>();
    private OnItemClickListener listener;
    private ContactFilter filter;

    public interface OnItemClickListener {
        void onSelectionCountChanged(int count);
    }

    public SelectFriendsListAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.friendImage)
        ImageView friendImage;
        @Bind(R.id.friendName)
        TextView friendName;
        @Bind(R.id.friendNumber)
        TextView friendNumber;
        @Bind(R.id.checkbox)
        CheckBox checkbox;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public void setList(List<ChatFriend> friendList) {
        this.friendList.clear();
        this.friendList.addAll(friendList);
        this.origFriendsList.clear();
        this.origFriendsList.addAll(friendList);
    }

    @Override
    public SelectFriendsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ChatFriend friend = friendList.get(position);
        if (!TextUtils.isEmpty(friend.getPhoto())) {
            Picasso.with(holder.itemView.getContext()).load(friend.getPhoto()).transform(new CircleTransform()).fit().centerCrop().into(holder.friendImage);
        } else {
            Picasso.with(holder.itemView.getContext()).load(R.drawable.default_large).transform(new CircleTransform()).fit().centerCrop().into(holder.friendImage);
        }
        holder.friendName.setText(friend.getName());
        holder.friendNumber.setText(friend.getPhoneNumber());

        holder.checkbox.setVisibility(View.VISIBLE);
        holder.checkbox.setChecked(selectedItemsIds.contains(friend.getId()));
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedItemsIds.add(friend.getId());
                } else {
                    selectedItemsIds.remove(friend.getId());
                }
                listener.onSelectionCountChanged(selectedItemsIds.size());
            }
        });
    }

    public Set<Integer> getSelectedItemsIds() {
        return selectedItemsIds;
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new ContactFilter();
        return filter;
    }

    private class ContactFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = origFriendsList;
                results.count = origFriendsList.size();
            } else {
                List<ChatFriend> tempListItems = new ArrayList<ChatFriend>();
                for (ChatFriend friend : origFriendsList) {
                    if (friend.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        tempListItems.add(friend);
                }
                results.values = tempListItems;
                results.count = tempListItems.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            friendList = (List<ChatFriend>) results.values;
            notifyDataSetChanged();
        }

    }
}
