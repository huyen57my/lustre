package adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.lustre.activities.ProductDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;
    private FirebaseFirestore db;
    private String userId;

    public List<Product> getProducts() {
        return productList;
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onFavoriteClick(Product product, int position);
    }

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.db = FirebaseFirestore.getInstance();

        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        this.userId = prefs.getString("user_id", null);
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
            tvProductName.setText(product.getName());
            tvRating.setText("5");

            if (product.getSale() != null && product.getSale() > 0) {
                tvProductPrice.setText(product.getFormattedSalePrice());
            } else {
                tvProductPrice.setText(product.getFormattedPrice());
            }

            List<String> imageUrls = product.getImageUrl();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                Glide.with(context)
                        .load(imageUrls.get(0))
                        .apply(new RequestOptions()
                                .transform(new RoundedCorners(16))
                                .placeholder(R.drawable.ic_logo)
                                .error(R.drawable.ic_logo))
                        .into(ivProductImage);
            }

            // Check if the product is in the wishlist
            if (userId != null) {
                db.collection("wishlists")
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("productId", product.getId())
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            boolean liked = !snapshot.isEmpty();
                            product.setFavorite(liked);
                            btnLove.setImageResource(liked ? R.drawable.heart_fill : R.drawable.heart);
                        });
            }

            // Open product detail activity on item click
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                context.startActivity(intent);

                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            // Handle wishlist button click
            btnLove.setOnClickListener(v -> {
                if (userId == null) return;

                if (product.isFavorite()) {
                    // Remove from wishlist
                    db.collection("wishlists")
                            .whereEqualTo("userId", userId)
                            .whereEqualTo("productId", product.getId())
                            .get()
                            .addOnSuccessListener(snapshot -> {
                                for (QueryDocumentSnapshot doc : snapshot) {
                                    db.collection("wishlists").document(doc.getId()).delete();
                                }
                                product.setFavorite(false);
                                notifyItemChanged(position);
                                if (listener != null) {
                                    listener.onFavoriteClick(product, position);
                                }
                            });
                } else {
                    // Add to wishlist
                    Map<String, Object> data = new HashMap<>();
                    data.put("userId", userId);
                    data.put("productId", product.getId());
                    data.put("createdAt", System.currentTimeMillis());

                    db.collection("wishlists")
                            .add(data)
                            .addOnSuccessListener(doc -> {
                                product.setFavorite(true);
                                notifyItemChanged(position);
                                if (listener != null) {
                                    listener.onFavoriteClick(product, position);
                                }
                            });
                }
            });
        }
    }
}
