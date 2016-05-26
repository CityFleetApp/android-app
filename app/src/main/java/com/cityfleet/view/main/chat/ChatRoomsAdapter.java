package com.cityfleet.view.main.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.model.ChatFriend;
import com.cityfleet.model.ChatRoom;
import com.cityfleet.util.Constants;
import com.cityfleet.util.GroupChatImageView;
import com.cityfleet.util.PrefUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static final int MAX_IMAGES_IN_VIEW = 4;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(ChatRoom item);
    }

    public ChatRoomsAdapter(OnItemClickListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.chatImage)
        GroupChatImageView chatImage;
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

    public void onNewChatRoom(ChatRoom chatRoom) {
        chatList.add(0, chatRoom);
        notifyDataSetChanged();
    }


    public void markMessageAsSeen(int roomId) {
        for (ChatRoom chatRoom : chatList) {
            if (chatRoom.getId() == roomId) {
                chatRoom.setUnseen(0);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public List<ChatRoom> getChatList() {
        return chatList;
    }

    public void addItems(List<ChatRoom> chatList) {
        this.chatList.addAll(chatList);
    }

    public void clearList() {
        this.chatList.clear();
    }

    @Override
    public ChatRoomsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        List<CustomTarget> targetList = new ArrayList<CustomTarget>(4);
        for (int i = 0; i < 4; i++) {
            CustomTarget target = new CustomTarget(vh.chatImage);
            targetList.add(target);
        }
        vh.chatImage.setTag(targetList);
        return vh;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.chatImage.clearBitmaps();
        holder.chatImage.invalidate();
        List<CustomTarget> targetList = (List<CustomTarget>) holder.chatImage.getTag();
        for (int i = 0; i > targetList.size(); i++) {
            Picasso.with(context).cancelRequest(targetList.get(i));
        }
        Log.d("TAG", "recycled view for position: "+ holder.getAdapterPosition());
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChatRoom chatRoom = chatList.get(position);
        StringBuilder chatName = new StringBuilder();
        List<String> images = new ArrayList<String>();
        for (ChatFriend chatFriend : chatRoom.getParticipants()) {
            if (chatFriend.getId() != PrefUtil.getId(holder.itemView.getContext())) {
                chatName.append(chatFriend.getName()).append(", ");
                String photo = chatFriend.getPhoto();
                images.add(photo);
            }
        }
        Context context = holder.chatImage.getContext();
        int size = context.getResources().getDimensionPixelSize(R.dimen.friends_list_image_height);
//        holder.chatImage.clearBitmaps();
//        holder.chatImage.invalidate();
        List<CustomTarget> targetList = (List<CustomTarget>) holder.chatImage.getTag();
        images = images.subList(0, Math.min(images.size(), targetList.size()));
        for (int i = 0; i < images.size(); i++) {
            String url = images.get(i);
            if (TextUtils.isEmpty(url)) {
                Picasso.with(context).load(R.drawable.default_large).
                        resize(size, size).centerCrop().into(targetList.get(i));
            } else {
                Picasso.with(context).load(url).
                        resize(size, size).centerCrop().into(targetList.get(i));
            }
        }
        for (int i = targetList.size(); i > images.size(); i--) {
            Picasso.with(context).cancelRequest(targetList.get(i - 1));
        }

        String chatNameString = chatName.substring(0, chatName.lastIndexOf(","));
        holder.chatName.setText(chatNameString);
        if (!TextUtils.isEmpty(chatRoom.getLastMessageTimestamp())) {
            if (!TextUtils.isEmpty(chatRoom.getLastMessage())) {
                holder.chatResentMessage.setText(chatRoom.getLastMessage());
            } else {
                holder.chatResentMessage.setText(context.getString(R.string.image_message));
            }
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
        if (chatRoom.getUnseen() == 0) {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.unread_message_color));
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    private static class CustomTarget implements Target {
        private final WeakReference<GroupChatImageView> imageViewRef;

        public CustomTarget(GroupChatImageView imageView) {
            imageViewRef = new WeakReference<GroupChatImageView>(imageView);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (imageViewRef != null) {
                GroupChatImageView imageView = imageViewRef.get();
                if (imageView != null) {
                    imageView.addBitmap(bitmap);
                    imageView.invalidate();
                } else {
                    Log.d("TAG", "view is null");
                }
            } else {
                Log.d("TAG", "view is null");
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            if (imageViewRef != null) {
                GroupChatImageView imageView = imageViewRef.get();
                if (imageView != null) {
                    BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(imageView.getContext(), R.drawable.default_large);
                    imageView.addBitmap(drawable.getBitmap());
                    imageView.invalidate();
                } else {
                    Log.d("TAG", "view is null");
                }
            } else {
                Log.d("TAG", "view is null");
            }
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

    }
}

