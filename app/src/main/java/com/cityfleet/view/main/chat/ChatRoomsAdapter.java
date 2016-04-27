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
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.model.ChatFriend;
import com.cityfleet.model.ChatRoom;
import com.cityfleet.util.ChatRoomImageView;
import com.cityfleet.util.Constants;
import com.cityfleet.util.PrefUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
        ChatRoomImageView chatImage;
        @Bind(R.id.chatName)
        TextView chatName;
        @Bind(R.id.chatResentMessage)
        TextView chatResentMessage;
        @Bind(R.id.chatResentMessageTime)
        TextView chatResentMessageTime;
        String imageTag;
        CustomTarget[] imagesTargets = new CustomTarget[MAX_IMAGES_IN_VIEW];

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
        return vh;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Picasso.with(holder.itemView.getContext()).cancelTag(holder.imageTag);
        for(CustomTarget target: holder.imagesTargets){
            if(target!=null){
                Picasso.with(context).cancelRequest(target);
            }
        }
        holder.chatImage.clearBitmaps();
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
                if (TextUtils.isEmpty(photo)) {
                    photo = "empty";
                }
                images.add(photo);
            }
        }
        holder.imageTag = chatRoom.getName() + chatRoom.getId();
        Context context = holder.chatImage.getContext();
        int size = context.getResources().getDimensionPixelSize(R.dimen.friends_list_image_height);
        for(CustomTarget target: holder.imagesTargets){
            if(target!=null){
                Picasso.with(context).cancelRequest(target);
            }
        }
        holder.chatImage.clearBitmaps();
        if (images.size() == 0) {
            holder.chatImage.setBitmaps(((BitmapDrawable) ContextCompat.getDrawable(holder.chatImage.getContext(), R.drawable.default_large)).getBitmap(), null, null, null);
            holder.chatImage.invalidate();
        } else if (images.size() == 1) {
            CustomTarget firstTarget = new CustomTarget(holder.chatImage, 0, new AtomicInteger(1));
            holder.imagesTargets[0] = firstTarget;
            Picasso.with(holder.itemView.getContext()).load(images.get(0)).resize(size, size).centerCrop().
                    tag(holder.imageTag).into(firstTarget);
        } else if (images.size() == 2) {
            AtomicInteger atomicInteger = new AtomicInteger(2);
            CustomTarget firstTarget = new CustomTarget(holder.chatImage, 0, atomicInteger);
            CustomTarget secondTarget = new CustomTarget(holder.chatImage, 1, atomicInteger);
            holder.imagesTargets[0] = firstTarget;
            holder.imagesTargets[1] = secondTarget;
            Picasso.with(holder.itemView.getContext()).load(images.get(0)).resize(size, size).centerCrop().
                    tag(holder.imageTag).into(firstTarget);
            Picasso.with(holder.itemView.getContext()).load(images.get(1)).resize(size, size).centerCrop().
                    tag(holder.imageTag).into(secondTarget);
        } else if (images.size() == 3) {
            AtomicInteger atomicInteger = new AtomicInteger(3);
            CustomTarget firstTarget = new CustomTarget(holder.chatImage, 0, atomicInteger);
            CustomTarget secondTarget = new CustomTarget(holder.chatImage, 1, atomicInteger);
            CustomTarget thirdTarget = new CustomTarget(holder.chatImage, 2, atomicInteger);
            holder.imagesTargets[0] = firstTarget;
            holder.imagesTargets[1] = secondTarget;
            holder.imagesTargets[2] = thirdTarget;
            Picasso.with(holder.itemView.getContext()).load(images.get(0)).resize(size, size).centerCrop().
                    tag(holder.imageTag).into(firstTarget);
            Picasso.with(holder.itemView.getContext()).load(images.get(1)).resize(size, size).centerCrop().
                    tag(holder.imageTag).into(secondTarget);
            Picasso.with(holder.itemView.getContext()).load(images.get(2)).resize(size, size).centerCrop().
                    tag(holder.imageTag).into(thirdTarget);
        } else if (images.size() > 3) {
            AtomicInteger atomicInteger = new AtomicInteger(4);
            CustomTarget firstTarget = new CustomTarget(holder.chatImage, 0, atomicInteger);
            CustomTarget secondTarget = new CustomTarget(holder.chatImage, 1, atomicInteger);
            CustomTarget thirdTarget = new CustomTarget(holder.chatImage, 2, atomicInteger);
            CustomTarget forthTarget = new CustomTarget(holder.chatImage, 3, atomicInteger);
            holder.imagesTargets[0] = firstTarget;
            holder.imagesTargets[1] = secondTarget;
            holder.imagesTargets[2] = thirdTarget;
            holder.imagesTargets[3] = forthTarget;
            Picasso.with(holder.itemView.getContext()).load(images.get(0)).resize(size, size).centerCrop().
                    tag(holder.imageTag).into(firstTarget);
            Picasso.with(holder.itemView.getContext()).load(images.get(1)).resize(size, size).centerCrop().
                    tag(holder.imageTag).into(secondTarget);
            Picasso.with(holder.itemView.getContext()).load(images.get(2)).resize(size, size).centerCrop().
                    tag(holder.imageTag).into(thirdTarget);
            Picasso.with(holder.itemView.getContext()).load(images.get(3)).resize(size, size).centerCrop().
                    tag(holder.imageTag).into(forthTarget);
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

    private class CustomTarget implements Target {
        private ChatRoomImageView imageView;
        private final AtomicInteger workCounter;
        private int position;

        public CustomTarget(ChatRoomImageView imageView, int position, AtomicInteger workCounter) {
            this.imageView = imageView;
            this.workCounter = workCounter;
            this.position = position;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageView.setBitmap(bitmap, position);
            int tasksLeft = workCounter.decrementAndGet();
            if (tasksLeft == 0) {
                imageView.invalidate();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            imageView.setBitmap(((BitmapDrawable) ContextCompat.getDrawable(imageView.getContext(), R.drawable.default_large)).getBitmap(), position);
            int tasksLeft = workCounter.decrementAndGet();
            if (tasksLeft == 0) {
                imageView.invalidate();
            }
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

    }
}

