package fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lustre.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapter.BannerApdater;
import adapter.ProductAdapter;
import customizes.SpacingItemDecoration;
import models.Product;
import models.Product;
import models.ProductDataManager;

public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private Handler handler = new Handler();
    private Runnable autoSlideRunnable;
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup banner ViewPager (existing code)
        setupBanner(view);

        // Setup products RecyclerView
        setupProductsRecyclerView(view);

        // Load products data
        loadProducts();
    }

    private void setupBanner(View view) {
        ViewPager2 viewPager = view.findViewById(R.id.bannerViewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        List<String> imageUrls = Arrays.asList(
                "https://i-giaitri.vnecdn.net/2025/05/20/doramon-1747738160-1747738183-2577-6717-1747739217.jpg",
                "https://i-giaitri.vnecdn.net/2025/05/20/doramon-1747738160-1747738183-2577-6717-1747739217.jpg",
                "https://i-giaitri.vnecdn.net/2025/05/20/doramon-1747738160-1747738183-2577-6717-1747739217.jpg",
                "https://i-giaitri.vnecdn.net/2025/05/20/doramon-1747738160-1747738183-2577-6717-1747739217.jpg",
                "https://i-giaitri.vnecdn.net/2025/05/20/doramon-1747738160-1747738183-2577-6717-1747739217.jpg"
        );

        BannerApdater adapter = new BannerApdater(requireContext(), imageUrls);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer((page, position) -> {
            page.setAlpha(0.25f + (1 - Math.abs(position)));
            page.setScaleY(0.85f + (1 - Math.abs(position)) * 0.15f);
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View dot = LayoutInflater.from(requireContext()).inflate(R.layout.tab_dot, null);
                tab.setCustomView(dot);
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null)
                    tab.getCustomView().setBackgroundResource(R.drawable.dot_selected);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null)
                    tab.getCustomView().setBackgroundResource(R.drawable.dot_unselected);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        //auto slide
        autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = (viewPager.getCurrentItem() + 1) % imageUrls.size();
                viewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(autoSlideRunnable, 2000);
    }

    private void setupProductsRecyclerView(View view) {
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewProducts.setLayoutManager(gridLayoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing); // e.g., 16dp
        recyclerViewProducts.addItemDecoration(new SpacingItemDecoration(2, spacingInPixels, true));

        productAdapter = new ProductAdapter(getContext(), new ArrayList<>());
        productAdapter.setOnProductClickListener(this);
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        // Option 1: Sử dụng mock data (hiện tại)
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
    // Product click callbacks
    @Override
    public void onProductClick(Product product) {
        // Navigate to product detail
        Toast.makeText(getContext(), "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to ProductDetailFragment
    }

    @Override
    public void onFavoriteClick(Product product, int position) {
        // Handle favorite toggle
        String message = product.isFavorite() ? "Added to favorites" : "Removed from favorites";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        // TODO: Update Firebase or local storage
    }
}