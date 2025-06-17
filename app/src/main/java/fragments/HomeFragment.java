package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lustre.R;
import com.example.lustre.activities.FilterActivity;
import com.example.lustre.activities.ProductDetailActivity;
import com.example.lustre.activities.SearchActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapter.BannerApdater;
import adapter.ProductAdapter;
import customizes.SpacingItemDecoration;
import firebase.ProductRepository;
import models.Product;

public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private Handler handler = new Handler();
    private Runnable autoSlideRunnable;
    private EditText edtSearchKeyword;
    private ImageButton btnSearch;
    private ViewPager2 bannerViewPager;
    private TabLayout tabLayout;
    private ImageView btnShirt, btnPant, btnDress;
    private RecyclerView recyclerViewProducts;

    private ProductAdapter productAdapter;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private DocumentSnapshot lastVisibleDoc = null;
    private final int PAGE_SIZE = 10;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupSearchAndFilter();
        setupCategoryClicks();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupBanner(view);
        setupProductsRecyclerView(view);
        loadProducts();
    }

    private void initViews(View view) {
        // Search and Filter
        edtSearchKeyword = view.findViewById(R.id.edtSearchKeyword);
        btnSearch = view.findViewById(R.id.btnSearch);

        // Banner
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        // Categories
        btnShirt = view.findViewById(R.id.home_btn_Shirt);
        btnPant = view.findViewById(R.id.home_btn_Pant);
        btnDress = view.findViewById(R.id.home_btn_dress);

        // Products
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
    }

    private void setupSearchAndFilter() {
        // Handle search action when user presses enter
        edtSearchKeyword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                String searchKeyword = edtSearchKeyword.getText().toString().trim();
                if (!searchKeyword.isEmpty()) {
                    navigateToSearch(searchKeyword);
                } else {
                    navigateToSearch("");
                }
                return true;
            }
            return false;
        });

        // Handle search text click - navigate to search activity
        edtSearchKeyword.setOnClickListener(v -> {
            String searchKeyword = edtSearchKeyword.getText().toString().trim();
            navigateToSearch(searchKeyword);
        });

        // Handle filter button click
        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearchKeyword.getText().toString().trim();
            navigateToSearch(keyword);
        });
    }


    private void setupCategoryClicks() {
        btnShirt.setOnClickListener(v -> navigateToSearchWithCategory("T-Shirt"));
        btnPant.setOnClickListener(v -> navigateToSearchWithCategory("Pant"));
        btnDress.setOnClickListener(v -> navigateToSearchWithCategory("Dress"));
    }

    private void navigateToSearch(String searchKeyword) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            intent.putExtra("search_keyword", searchKeyword);
        }
        startActivity(intent);
    }

    private void navigateToSearchWithCategory(String category) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void navigateToFilter() {
        Intent intent = new Intent(getActivity(), FilterActivity.class);
        startActivity(intent);
    }

    private void setupBanner(View view) {
        ViewPager2 viewPager = view.findViewById(R.id.bannerViewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        List<String> imageUrls = Arrays.asList(
                "https://i-giaitri.vnecdn.net/2025/05/20/doramon-1747738160.jpg",
                "https://i-giaitri.vnecdn.net/2025/05/20/doramon-1747738183.jpg",
                "https://i-giaitri.vnecdn.net/2025/05/20/doramon-1747739217.jpg"
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

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewProducts.setLayoutManager(layoutManager);

        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        recyclerViewProducts.addItemDecoration(new SpacingItemDecoration(2, spacing, true));

        productAdapter = new ProductAdapter(getContext(), new ArrayList<>());
        productAdapter.setOnProductClickListener(this);
        recyclerViewProducts.setAdapter(productAdapter);

        recyclerViewProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && !isLastPage && !recyclerView.canScrollVertically(1)) {
                    loadProducts();
                }
            }
        });
    }

    private void loadProducts() {
        if (isLoading || isLastPage) return;
        isLoading = true;

        new ProductRepository().getSaleProducts(PAGE_SIZE, lastVisibleDoc,
                products -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (products.size() < PAGE_SIZE) {
                                isLastPage = true; // Không còn dữ liệu
                            }

                            if (!products.isEmpty()) {
                                lastVisibleDoc = products.get(products.size() - 1).getDocumentSnapshot();
                                List<Product> current = new ArrayList<>(productAdapter.getProducts());
                                current.addAll(products);
                                productAdapter.updateData(current);
                            }

                            isLoading = false;
                        });
                    }
                },
                error -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Lỗi tải sản phẩm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            isLoading = false;
                        });
                    }
                }
        );
    }
    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }
    @Override
    public void onFavoriteClick(Product product, int position) {
        String message = product.isFavorite() ? "Added to favorites" : "Removed from favorites";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
