package com.danghuuthinh.lustre.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danghuuthinh.lustre.R;
import com.danghuuthinh.lustre.models.HelpItem;

import java.util.List;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.HelpViewHolder> {
    private List<HelpItem> helpList;
    private boolean isExpanded = false;
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }

    public HelpAdapter(List<HelpItem> helpList) {
        this.helpList = helpList;
    }

    @NonNull
    @Override
    public HelpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help, parent, false);
        return new HelpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpViewHolder holder, int position) {
        HelpItem item = helpList.get(position);
        holder.tvQuestion.setText(item.getQuestion());
        holder.tvAnswer.setText(item.getAnswer());

        boolean isExpanded = item.isExpanded();
        holder.tvAnswer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivToggle.setImageResource(isExpanded ? R.mipmap.ic_up : R.mipmap.ic_down);

        // Bắt sự kiện click icon
        holder.ivToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setExpanded(!item.isExpanded());
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        // Cũng cho phép click vào cả phần câu hỏi để mở/đóng
        holder.tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setExpanded(!item.isExpanded());
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return helpList.size();
    }

    public static class HelpViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvAnswer;
        ImageView ivToggle;

        public HelpViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            ivToggle = itemView.findViewById(R.id.ivToggle);
        }
    }

    public void updateList(List<HelpItem> newList) {
        helpList = newList;
        notifyDataSetChanged();
    }
}

