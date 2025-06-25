package fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lustre.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import adapter.ProductAdapter;
import customizes.SpacingItemDecoration;
import models.Product;

public class WishlistFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;

    private String filter = "all";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText("Wishlist");
        }
        setupProductsRecyclerView(view);
        loadProducts();

        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // Optional: mở chi tiết sản phẩm
            }

            @Override
            public void onFavoriteClick(Product product, int position) {
                SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                String userId = prefs.getString("user_id", null);
                if (userId == null) return;

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (!product.isFavorite()) {
                    // Người dùng bỏ thích → xóa khỏi wishlist
                    db.collection("wishlists")
                            .whereEqualTo("userId", userId)
                            .whereEqualTo("productId", product.getId())
                            .get()
                            .addOnSuccessListener(snapshot -> {
                                for (QueryDocumentSnapshot doc : snapshot) {
                                    db.collection("wishlists").document(doc.getId()).delete();
                                }
                                loadProducts(); // Refresh lại danh sách
                            });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProducts(); // Reload mỗi khi quay lại màn hình
    }

    private void setupProductsRecyclerView(View view) {
        recyclerViewProducts = view.findViewById(R.id.wishlist_product);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewProducts.setLayoutManager(gridLayoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        recyclerViewProducts.addItemDecoration(new SpacingItemDecoration(2, spacingInPixels, true));

        productAdapter = new ProductAdapter(getContext(), new ArrayList<>());
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(getContext(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("wishlists")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        productAdapter.updateData(new ArrayList<>());
                        return;
                    }

                    List<String> productIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String productId = doc.getString("productId");
                        if (productId != null) {
                            productIds.add(productId);
                        }
                    }

                    if (productIds.isEmpty()) {
                        productAdapter.updateData(new ArrayList<>());
                        return;
                    }

                    List<Product> wishlistedProducts = new ArrayList<>();

                    for (String productId : productIds) {
                        db.collection("products")
                                .document(productId)
                                .get()
                                .addOnSuccessListener(productDoc -> {
                                    if (productDoc.exists()) {
                                        Product product = productDoc.toObject(Product.class);
                                        if (product != null) {
                                            product.setId(productDoc.getId());
                                            product.setFavorite(true);
                                            wishlistedProducts.add(product);
                                        }
                                    }

                                    if (wishlistedProducts.size() == productIds.size()) {
                                        productAdapter.updateData(wishlistedProducts);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
