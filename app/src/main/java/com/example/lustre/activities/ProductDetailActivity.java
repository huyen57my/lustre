package com.example.lustre.activities;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lustre.R;

import java.text.DecimalFormat;

import adapter.ProductImageAdapter;
import adapter.ThumbnailAdapter;
import firebase.ProductRepository;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductDescription, tvMaterial, tvCare, tvOriginalPrice, tvSalePrice;
    private ImageButton btnBack, btnFavorite;
    private LinearLayout layoutSizes, layoutColors;
    private ViewPager2 viewPagerMainImage;
    private androidx.recyclerview.widget.RecyclerView rvProductImages;

    private ProductRepository productRepository;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productRepository = new ProductRepository();

        productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            Toast.makeText(this, "Product ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadProductDetails();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Thêm logic yêu thích ở đây nếu cần
            }
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvSalePrice = findViewById(R.id.tvSalePrice);
        tvMaterial = findViewById(R.id.tvMaterial);
        tvCare = findViewById(R.id.tvCare);
        layoutSizes = findViewById(R.id.layoutSizes);
        layoutColors = findViewById(R.id.layoutColors);
        viewPagerMainImage = findViewById(R.id.viewPagerMainImage);
        rvProductImages = findViewById(R.id.rvProductImages);
    }

    private void loadProductDetails() {
        productRepository.getProductById(productId,
                product -> {
                    if (product == null) return;

                    tvProductName.setText(product.getName());
                    tvProductDescription.setText(product.getDescription());

                    // Giá gốc & Sale
                    if (product.getSale() != null && product.getSale() < product.getPrice()) {
                        tvSalePrice.setText(formatCurrency(product.getSale()));
                        tvSalePrice.setVisibility(View.VISIBLE);

                        tvOriginalPrice.setText(formatCurrency(product.getPrice()));
                        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        tvOriginalPrice.setVisibility(View.VISIBLE);
                    } else {
                        tvSalePrice.setVisibility(View.GONE);

                        tvOriginalPrice.setText(formatCurrency(product.getPrice()));
                        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        tvOriginalPrice.setVisibility(View.VISIBLE);
                    }

                    ProductImageAdapter imageAdapter = new ProductImageAdapter(this, product.getImg(), null);
                    viewPagerMainImage.setAdapter(imageAdapter);

                    ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter(product.getImg(), position -> {
                        viewPagerMainImage.setCurrentItem(position, true);
                    });
                    rvProductImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    rvProductImages.setAdapter(thumbnailAdapter);

                    viewPagerMainImage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            thumbnailAdapter.onThumbnailClick(position);
                        }
                    });

                    // Sizes
                    layoutSizes.removeAllViews();
                    for (String size : product.getSizes()) {
                        TextView sizeView = new TextView(this);
                        sizeView.setText(size);
                        sizeView.setPadding(24, 12, 24, 12);
                        sizeView.setBackgroundResource(R.drawable.button_background_selector);
                        sizeView.setTextColor(Color.BLACK);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(8, 0, 8, 0);
                        layoutSizes.addView(sizeView, params);
                    }

                    // Color
                    layoutColors.removeAllViews();
                    TextView colorView = new TextView(this);
                    colorView.setText(product.getColor());
                    colorView.setPadding(24, 12, 24, 12);
                    colorView.setBackgroundResource(R.drawable.button_background_selector);
                    colorView.setTextColor(Color.BLACK);
                    layoutColors.addView(colorView);

                    // Material & Care
                    tvMaterial.setText("Material: " + product.getMaterial());
                    tvCare.setText("Care: " + product.getCare());
                },
                e -> Toast.makeText(this, "Error loading product", Toast.LENGTH_SHORT).show()
        );
    }

    private String formatCurrency(Number number) {
        DecimalFormat formatter = new DecimalFormat("#,###đ");
        return formatter.format(number);
    }
}
