package com.example.lustre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lustre.R;

import java.util.ArrayList;
import java.util.List;

import adapter.ProductAdapter;
import customizes.SpacingItemDecoration;
import firebase.ProductRepository;
import models.Product;

public class SearchActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private static final int FILTER_REQUEST_CODE = 100;
    private static final int SEARCH_DELAY_MS = 500;

    private EditText edtSearchKeyword;
    private ImageButton btnBack, btnFilter;
    private RecyclerView recyclerViewSearchResults;
    private ProgressBar progressBarLoading;

    private ProductAdapter productAdapter;
    private List<Product> allProducts = new ArrayList<>();

    // Filter parameters
    private String currentKeyword = "";
    private Double minPrice = null;
    private Double maxPrice = null;
    private String selectedCategory = null;

    // Debounce
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupRecyclerView();
        setupListeners();

        // Get search keyword from intent
        String searchKeyword = getIntent().getStringExtra("search_keyword");
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            edtSearchKeyword.setText(searchKeyword);
            currentKeyword = searchKeyword;
            performSearch();
        }
    }

    private void initViews() {
        edtSearchKeyword = findViewById(R.id.edtSearchKeyword);
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);
        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);
        progressBarLoading = findViewById(R.id.progressBarLoading);
    }
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnFilter.setOnClickListener(v -> {
            Intent intent = new Intent(this, FilterActivity.class);
            intent.putExtra("min_price", minPrice);
            intent.putExtra("max_price", maxPrice);
            intent.putExtra("category", selectedCategory);
            startActivityForResult(intent, FILTER_REQUEST_CODE);
        });

        // Search when user presses enter
        edtSearchKeyword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                currentKeyword = edtSearchKeyword.getText().toString().trim();
                performSearch();
                return true;
            }
            return false;
        });

        // Real-time debounce search
        edtSearchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentKeyword = s.toString().trim();
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> performSearch();
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }
        });
    }

    private void performSearch() {
        if (isLoading) return;

        isLoading = true;
        progressBarLoading.setVisibility(View.VISIBLE);

        String keyword = currentKeyword.isEmpty() ? null : currentKeyword;

        new ProductRepository().getProductsByFilter(
                keyword,
                minPrice,
                maxPrice,
                selectedCategory,
                products -> {
                    allProducts.clear();
                    allProducts.addAll(products);
                    productAdapter.notifyDataSetChanged();

                    progressBarLoading.setVisibility(View.GONE);
                    isLoading = false;

                    if (products.isEmpty()) {
                        Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show();
                    }
                },
                e -> {
                    progressBarLoading.setVisibility(View.GONE);
                    isLoading = false;

                    Toast.makeText(this, "Error searching products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            minPrice = data.hasExtra("min_price") ? data.getDoubleExtra("min_price", 0) : null;
            maxPrice = data.hasExtra("max_price") ? data.getDoubleExtra("max_price", 10_000_000) : null;
            selectedCategory = data.getStringExtra("category");
            performSearch();
        }
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, allProducts);
        productAdapter.setOnProductClickListener(this);

        // Sử dụng GridLayoutManager giống HomeFragment
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewSearchResults.setLayoutManager(layoutManager);

        // Thêm spacing giống HomeFragment (spacing có thể lấy từ dimen như: R.dimen.grid_spacing)
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        recyclerViewSearchResults.addItemDecoration(new SpacingItemDecoration(2, spacing, true));

        recyclerViewSearchResults.setAdapter(productAdapter);
    }

    @Override
    public void onProductClick(Product product) {
        // Intent intent = new Intent(this, ProductDetailActivity.class);
        // intent.putExtra("product_id", product.getId());
        // startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Product product, int position) {
        Toast.makeText(this,
                product.isFavorite() ? "Added to favorites" : "Removed from favorites",
                Toast.LENGTH_SHORT).show();
    }
}
