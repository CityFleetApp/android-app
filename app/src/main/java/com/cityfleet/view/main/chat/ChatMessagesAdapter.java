package com.cityfleet.view.main.chat;

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

import com.cityfleet.R;
import com.cityfleet.model.ChatFriend;
import com.cityfleet.model.ChatMessage;
import com.cityfleet.util.CircleTransform;
import com.cityfleet.util.Constants;
import com.cityfleet.util.OpenChatImageEvent;
import com.cityfleet.util.PrefUtil;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

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
        simpleDateFormatFromServer.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.authorImage)
        ImageView authorImage;
        @Bind(R.id.chatMessage)
        TextView chatMessage;
        @Bind(R.id.chatTime)
        TextView chatTime;
        @Bind(R.id.chatImage)
        ImageView chatImage;

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
        if (TextUtils.isEmpty(chatMessage.getText())) {
            holder.chatMessage.setVisibility(View.GONE);
        } else {
            holder.chatMessage.setVisibility(View.VISIBLE);
            holder.chatMessage.setText(chatMessage.getText());
        }
        holder.chatMessage.setText(chatMessage.getText());
        if (!TextUtils.isEmpty(chatMessage.getImage())) {
            holder.chatImage.setVisibility(View.VISIBLE);
            Picasso.with(context).load(chatMessage.getImage()).transform(new RoundedCornersTransformation(context.getResources().getDimensionPixelSize(R.dimen.chat_image_corner), 0)).into(holder.chatImage);
            holder.chatImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new OpenChatImageEvent(chatMessage.getImage()));
                }
            });
        } else {
            holder.chatImage.setVisibility(View.GONE);
            holder.chatImage.setImageResource(0);
            holder.chatImage.setOnClickListener(null);
        }
        ChatFriend author = null;
        for (ChatFriend friend : chatMessage.getParticipants()) {
            if (friend.getId() == chatMessage.getAuthor()) {
                author = friend;
            }
        }
        String authorName = author == null ? "" : context.getString(R.string.chat_author_name, author.getName());
        if (author == null || TextUtils.isEmpty(author.getPhoto())) {
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
        holder.chatTime.setText(authorName + dateTime);

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
