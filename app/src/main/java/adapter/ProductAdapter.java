package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.lustre.R;

import java.util.List;

import models.Product;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onFavoriteClick(Product product, int position);
    }

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, position);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private ImageButton btnLove;
        private TextView tvProductName;
        private TextView tvRating;
        private TextView tvProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            btnLove = itemView.findViewById(R.id.btn_love);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
        }

        public void bind(Product product, int position) {
            // Set product name
            tvProductName.setText(product.getName());

            // ✅ Correct: Set rating (ví dụ: "4.5")
            tvRating.setText("5"); // Nếu có getRating()

            // ✅ Correct: Set price
            tvProductPrice.setText(product.getFormattedPrice());

            // Load image
            Glide.with(context)
                    .load(product.getImageUrl())
                    .apply(new RequestOptions()
                            .transform(new RoundedCorners(16))
                            .placeholder(R.drawable.ic_logo)
                            .error(R.drawable.ic_logo))
                    .into(ivProductImage);

            // Set heart icon
            btnLove.setImageResource(product.isFavorite() ? R.drawable.heart_fill : R.drawable.heart);

            // Handle click events
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            btnLove.setOnClickListener(v -> {
                if (listener != null) {
                    product.setFavorite(!product.isFavorite());
                    listener.onFavoriteClick(product, position);
                    notifyItemChanged(position);
                }
            });
        }
    }
}