package com.danghuuthinh.lustre.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.danghuuthinh.lustre.R;
import com.danghuuthinh.lustre.models.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SECTION = 0;
    private static final int VIEW_TYPE_NOTIFICATION = 1;

    private List<NotificationItem> items;

    public NotificationAdapter(List<NotificationItem> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).isSection()) {
            return VIEW_TYPE_SECTION;
        } else {
            return VIEW_TYPE_NOTIFICATION;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_SECTION) {
            View view = inflater.inflate(R.layout.item_notification, parent, false);
            return new SectionViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_notification_detail, parent, false);
            return new NotificationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotificationItem item = items.get(position);

        if (holder instanceof SectionViewHolder) {
            SectionViewHolder sectionHolder = (SectionViewHolder) holder;
            sectionHolder.sectionTitle.setText(item.getSectionTitle());
        } else if (holder instanceof NotificationViewHolder) {
            NotificationViewHolder notificationHolder = (NotificationViewHolder) holder;
            notificationHolder.title.setText(item.getTitle());
            notificationHolder.message.setText(item.getMessage());
            notificationHolder.time.setText(item.getTime());
            notificationHolder.icon.setImageResource(item.getIconResId());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;
        TextView markAllRead;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
            markAllRead = itemView.findViewById(R.id.markAllRead);
        }
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, message, time;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }
}
