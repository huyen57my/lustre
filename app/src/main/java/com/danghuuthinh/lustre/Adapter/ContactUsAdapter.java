package com.danghuuthinh.lustre.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.danghuuthinh.lustre.R;
import com.danghuuthinh.lustre.models.ContactUsItem;

import java.util.List;

public class ContactUsAdapter extends RecyclerView.Adapter<ContactUsAdapter.ViewHolder> {
    private List<ContactUsItem> itemList;

    public ContactUsAdapter(List<ContactUsItem> itemList) {
        this.itemList = itemList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        ImageView ivToggle, ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivToggle = itemView.findViewById(R.id.ivToggle);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }

    @Override
    public ContactUsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_us, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactUsAdapter.ViewHolder holder, int position) {
        ContactUsItem item = itemList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.ivIcon.setImageResource(item.getIconResId());

        holder.tvDescription.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);
        holder.ivToggle.setImageResource(item.isExpanded() ? R.mipmap.ic_up : R.mipmap.ic_down);

        holder.itemView.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

