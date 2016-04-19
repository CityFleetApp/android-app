package com.citifleet.view.main.chat;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.ChatFriend;
import com.citifleet.model.ChatMessage;
import com.citifleet.model.ChatRoom;
import com.citifleet.util.CircleTransform;
import com.citifleet.util.Constants;
import com.citifleet.util.PrefUtil;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 12.04.16.
 */
public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.ViewHolder> {
    private List<ChatRoom> chatList = new ArrayList<ChatRoom>();
    private SimpleDateFormat simpleDateFormatToShow = new SimpleDateFormat(Constants.CHAT_DATETIME_FORMAT);
    private SimpleDateFormat simpleDateFormatFromServer = new SimpleDateFormat(Constants.INPUT_DATETIME_FORMAT);
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
        String imageTag;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }


    }

    public void onNewMessage(ChatMessage chatMessage) {
        ChatRoom chatRoomWithNewMessage = null;
        for (ChatRoom chatRoom : chatList) {
            if (chatRoom.getId() == chatMessage.getRoom()) {
                chatRoomWithNewMessage = chatRoom;
                chatRoom.setLastMessage(chatMessage.getText());
                chatRoom.setLastMessageTimestamp(chatMessage.getCreated());
                break;
            }
        }
        if (chatRoomWithNewMessage != null) {
            chatList.remove(chatRoomWithNewMessage);
            chatList.add(0, chatRoomWithNewMessage);
        }
        notifyDataSetChanged();
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
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Picasso.with(holder.itemView.getContext()).cancelTag(holder.imageTag);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ChatRoom chatRoom = chatList.get(position);
        StringBuilder chatName = new StringBuilder();
        List<String> images = new ArrayList<String>();
        for (ChatFriend chatFriend : chatRoom.getParticipants()) {
            if (chatFriend.getId() != PrefUtil.getId(holder.itemView.getContext())) {
                chatName.append(chatFriend.getName()).append(", ");
                images.add(chatFriend.getPhoto());
            }
        }
        holder.imageTag = chatRoom.getName() + chatRoom.getId();
        if (TextUtils.isEmpty(images.get(0))) {
            Picasso.with(holder.itemView.getContext()).load(R.drawable.default_large).transform(new CircleTransform()).
                    tag(holder.imageTag).into(holder.chatImage);
        } else {
            Picasso.with(holder.itemView.getContext()).load(images.get(0)).transform(new CircleTransform()).
                    tag(holder.imageTag).into(holder.chatImage);
        }
        String chatNameString = chatName.substring(0, chatName.lastIndexOf(","));
        holder.chatName.setText(chatNameString);
        if (!TextUtils.isEmpty(chatRoom.getLastMessage())) {
            holder.chatResentMessage.setText(chatRoom.getLastMessage());
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(simpleDateFormatFromServer.parse(chatRoom.getLastMessageTimestamp()));
            } catch (ParseException e) {
                Log.e(ChatRoomsAdapter.class.getName(), e.getMessage());
            }
            String dateTime = simpleDateFormatToShow.format(calendar.getTime());
            holder.chatResentMessageTime.setText(dateTime);
        } else {
            holder.chatResentMessage.setText(R.string.no_messages);
            holder.chatResentMessageTime.setText("");
        }
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

