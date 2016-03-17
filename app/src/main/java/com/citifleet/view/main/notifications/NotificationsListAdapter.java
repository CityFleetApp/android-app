package com.citifleet.view.main.notifications;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.Notification;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 17.03.16.
 */
public class NotificationsListAdapter extends RecyclerView.Adapter<NotificationsListAdapter.ViewHolder> {
    private List<Notification> notifications = new ArrayList<Notification>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Notification item);
    }

    public NotificationsListAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.notificationType)
        TextView notificationType;
        @Bind(R.id.date)
        TextView date;
        @Bind(R.id.notificationTitle)
        TextView notificationTitle;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications.clear();
        this.notifications.addAll(notifications);
    }

    public void setNotificationsList(List<Notification> benefitList) {
        this.notifications.clear();
        this.notifications.addAll(benefitList);
    }

    @Override
    public NotificationsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Notification notification = notifications.get(position);
        holder.notificationType.setText(notification.getType());
        holder.date.setText(notification.getCreated());
        holder.notificationTitle.setText(notification.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}