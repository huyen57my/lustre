package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lustre.R;

import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {

    private final List<String> imageUrls;
    private final OnThumbnailClickListener listener;
    private int selectedPosition = 0;

    public interface OnThumbnailClickListener {
        void onThumbnailClick(int position);
    }

    public ThumbnailAdapter(List<String> imageUrls, OnThumbnailClickListener listener) {
        this.imageUrls = imageUrls;
        this.listener = listener;
    }

    public void onThumbnailClick(int position) {
        int oldPos = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPos);
        notifyItemChanged(selectedPosition);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .load(imageUrls.get(position))
                .placeholder(R.drawable.ic_logo)
                .into(holder.imageView);

        holder.itemView.setAlpha(position == selectedPosition ? 1f : 0.5f);

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            int oldPos = selectedPosition;
            selectedPosition = adapterPosition;
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
            listener.onThumbnailClick(adapterPosition);
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgThumbnail);
        }
    }
}