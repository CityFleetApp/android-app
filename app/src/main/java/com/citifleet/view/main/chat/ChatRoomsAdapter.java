package com.citifleet.view.main.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.ChatFriend;
import com.citifleet.model.ChatRoom;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 12.04.16.
 */
public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.ViewHolder> {
    private List<ChatRoom> chatList = new ArrayList<ChatRoom>();

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ChatRoom item);
    }

    public ChatRoomsAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.chatImage)
        ImageView chatImage;
        @Bind(R.id.chatName)
        TextView chatName;
        @Bind(R.id.chatResentMessage)
        TextView chatResentMessage;
        @Bind(R.id.chatResentMessageTime)
        TextView chatResentMessageTime;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public void setList(List<ChatRoom> chatList) {
        this.chatList.clear();
        this.chatList.addAll(chatList);
    }

    @Override
    public ChatRoomsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ChatRoom chatRoom = chatList.get(position);
        StringBuilder chatName = new StringBuilder();
        for (ChatFriend chatFriend : chatRoom.getParticipants()) {
            chatName.append(chatFriend.getName()).append(", ");
        }
        String chatNameString = chatName.substring(0, chatName.lastIndexOf(","));
        holder.chatName.setText(chatNameString);
        holder.chatResentMessage.setText(chatRoom.getLastMessage());
//        Date date = new Date();
//        date.setTime(Long.parseLong(chatRoom.getLastMessageTimestamp()));
//        Calendar calendar = Calendar.getInstance();
//        String dateTime="";
        holder.chatResentMessageTime.setText(chatRoom.getLastMessageTimestamp());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(chatRoom);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}

