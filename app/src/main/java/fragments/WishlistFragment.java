package fragments;

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

import java.util.ArrayList;
import java.util.List;

import adapter.ProductAdapter;
import customizes.SpacingItemDecoration;
import models.Product;
import models.ProductDataManager;

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
        setAppBarTitle("My Wishlist");
        setupProductsRecyclerView(view);
        setupFilterButtons(view);
        loadProducts();
    }

    private void setupFilterButtons(View view) {
        AppCompatButton btnAll = view.findViewById(R.id.btn_all);
        AppCompatButton btnShirt = view.findViewById(R.id.btn_shirt);
        AppCompatButton btnPant = view.findViewById(R.id.btn_pant);
        AppCompatButton btnDress = view.findViewById(R.id.btn_dress);

        View.OnClickListener listener = v -> {
            // Reset selected state
            btnAll.setSelected(false);
            btnShirt.setSelected(false);
            btnPant.setSelected(false);
            btnDress.setSelected(false);

            // Set selected
            v.setSelected(true);

            // Update filter
            if (v == btnAll) filter = "all";
            else if (v == btnShirt) filter = "shirt";
            else if (v == btnPant) filter = "pant";
            else if (v == btnDress) filter = "dress";

            // TODO: Reload product list based on filter
            Toast.makeText(getContext(), "Filter: " + filter, Toast.LENGTH_SHORT).show();
        };

        btnAll.setOnClickListener(listener);
        btnShirt.setOnClickListener(listener);
        btnPant.setOnClickListener(listener);
        btnDress.setOnClickListener(listener);

        btnAll.setSelected(true);
    }


    private void setAppBarTitle(String title) {
        if (getActivity() == null) return;

        TextView tvTitle = getActivity().findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    private void setupProductsRecyclerView(View view) {
        recyclerViewProducts = view.findViewById(R.id.wishlist_product);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewProducts.setLayoutManager(gridLayoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing); // e.g., 16dp
        recyclerViewProducts.addItemDecoration(new SpacingItemDecoration(2, spacingInPixels, true));

        productAdapter = new ProductAdapter(getContext(), new ArrayList<>());
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        ProductDataManager.fetchProductsFromFirebase(new ProductDataManager.ProductDataCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        productAdapter.updateData(products);
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi tải sản phẩm: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

        // Option 2: Sử dụng Firebase Service (uncomment khi ready)
        /*
        FirebaseProductService firebaseService = new FirebaseProductService();
        firebaseService.fetchSaleProducts(new ProductDataManager.ProductDataCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        productAdapter.updateData(products);
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi tải sản phẩm: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
        */
    }
}