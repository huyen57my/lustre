package adapter;// ProductImageAdapter.java
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lustre.R;

import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder> {

    private final List<String> imageUrls;
    private final Context context;
    private final OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(String imageUrl);
    }

    public ProductImageAdapter(Context context, List<String> imageUrls, OnImageClickListener listener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageThumb);

        holder.itemView.setOnClickListener(v -> listener.onImageClick(imageUrl));
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageThumb;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageThumb = itemView.findViewById(R.id.imgThumb);
        }
    }
}
