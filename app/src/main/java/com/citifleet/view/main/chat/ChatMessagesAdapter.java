package com.citifleet.view.main.chat;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.ChatFriend;
import com.citifleet.model.ChatMessage;
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
 * Created by vika on 18.04.16.
 */
public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder> {
    private SimpleDateFormat simpleDateFormatToShow = new SimpleDateFormat(Constants.CHAT_DATETIME_FORMAT);
    private SimpleDateFormat simpleDateFormatFromServer = new SimpleDateFormat(Constants.INPUT_DATETIME_FORMAT);
    private List<ChatMessage> chatList = new ArrayList<ChatMessage>();
    private final static int LEFT_MESSAGE_TYPE = 1;
    private final static int RIGHT_MESSAGE_TYPE = 2;
    private Context context;

    public ChatMessagesAdapter(Context context) {
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.authorImage)
        ImageView authorImage;
        @Bind(R.id.chatMessage)
        TextView chatMessage;
        @Bind(R.id.chatTime)
        TextView chatTime;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }


    }

    public void addItems(List<ChatMessage> chatList) {
        //  this.chatList.clear();
        this.chatList.addAll(chatList);
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage chatMessage) {
        this.chatList.add(0, chatMessage);
        notifyDataSetChanged();
    }

    @Override
    public ChatMessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType == RIGHT_MESSAGE_TYPE ? R.layout.chat_item_right : R.layout.chat_item_left, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getAuthor() == PrefUtil.getId(context)) {
            return RIGHT_MESSAGE_TYPE;
        } else {
            return LEFT_MESSAGE_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ChatMessage chatMessage = chatList.get(position);
        holder.chatMessage.setText(chatMessage.getText());
        ChatFriend author = null;
        for (ChatFriend friend : chatMessage.getParticipants()) {
            if (friend.getId() == chatMessage.getAuthor()) {
                author = friend;
            }
        }
        if (TextUtils.isEmpty(author.getPhoto())) {
            Picasso.with(holder.itemView.getContext()).load(R.drawable.default_large).transform(new CircleTransform()).fit().centerCrop().into(holder.authorImage);
        } else {
            Picasso.with(holder.itemView.getContext()).load(author.getPhoto()).transform(new CircleTransform()).fit().centerCrop().into(holder.authorImage);
        }
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormatFromServer.parse(chatMessage.getCreated()));
        } catch (ParseException e) {
            Log.e(ChatRoomsAdapter.class.getName(), e.getMessage());
        }
        String dateTime = simpleDateFormatToShow.format(calendar.getTime());
        holder.chatTime.setText(dateTime);

        if (position == 0) {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{android.R.attr.actionBarSize};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            int size = array.getDimensionPixelSize(0, -1);
            holder.itemView.setPadding(holder.itemView.getPaddingLeft(), holder.itemView.getPaddingTop(), holder.itemView.getPaddingRight(), size + holder.itemView.getPaddingTop());
        } else {
            holder.itemView.setPadding(holder.itemView.getPaddingLeft(), holder.itemView.getPaddingTop(), holder.itemView.getPaddingRight(), 0);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
