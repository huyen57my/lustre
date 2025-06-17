package com.example.lustre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lustre.R;
import com.google.android.material.slider.RangeSlider;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FilterActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnAllCategory, btnTShirt, btnPant, btnDress;
    private Button btnResetFilter, btnApplyFilter;
    private RangeSlider priceRangeSlider;
    private TextView tvMinPrice, tvMaxPrice;

    private String selectedCategory = null;
    private double minPrice = 0f;

    private double maxPrice = 10_000_000f;


    // Category buttons array for easier management
    private Button[] categoryButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initViews();
        priceRangeSlider.setStepSize(10_000f);
        priceRangeSlider.setValues((float) minPrice, (float) maxPrice);
        setupListeners();
        loadExistingFilters();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnAllCategory = findViewById(R.id.btnAllCategory);
        btnTShirt = findViewById(R.id.btnTShirt);
        btnPant = findViewById(R.id.btnPant);
        btnDress = findViewById(R.id.btnDress);
        btnResetFilter = findViewById(R.id.btnResetFilter);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        priceRangeSlider = findViewById(R.id.priceRangeSlider);
        tvMinPrice = findViewById(R.id.tvMinPrice);
        tvMaxPrice = findViewById(R.id.tvMaxPrice);

        categoryButtons = new Button[]{btnAllCategory, btnTShirt, btnPant, btnDress};
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Category selection
        btnAllCategory.setOnClickListener(v -> selectCategory(null, btnAllCategory));
        btnTShirt.setOnClickListener(v -> selectCategory("shirt", btnTShirt));
        btnPant.setOnClickListener(v -> selectCategory("pant", btnPant));
        btnDress.setOnClickListener(v -> selectCategory("dress", btnDress));

        // Price range slider
        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            long min = Math.round(values.get(0)); // Làm tròn giá trị float
            long max = Math.round(values.get(1));

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            formatter.setMaximumFractionDigits(0); // Không hiện phần thập phân

            String formattedMin = formatter.format(min) + "₫";
            String formattedMax = formatter.format(max) + "₫";

            tvMinPrice.setText(formattedMin);
            tvMaxPrice.setText(formattedMax);
        });

        // Bottom buttons
        btnResetFilter.setOnClickListener(v -> resetFilters());
        btnApplyFilter.setOnClickListener(v -> applyFilters());
    }

    private void loadExistingFilters() {
        Intent intent = getIntent();

        // Load price range
        if (intent.hasExtra("min_price")) {
            minPrice = intent.getDoubleExtra("min_price", 0);
        }
        if (intent.hasExtra("max_price")) {
            maxPrice = intent.getDoubleExtra("max_price", 10_000_000f);
        }

        // Load category
        selectedCategory = intent.getStringExtra("category");

        // Update UI
        priceRangeSlider.setValues((float) minPrice, (float) maxPrice);
        updatePriceLabels();
        updateCategorySelection();
    }

    private void selectCategory(String category, Button selectedButton) {
        selectedCategory = category;
        updateCategorySelection();
    }

    private void updateCategorySelection() {
        for (Button button : categoryButtons) {
            button.setSelected(false);
            button.setBackgroundTintList(null);
        }

        Button selectedButton = null;
        if (selectedCategory == null) {
            selectedButton = btnAllCategory;
        } else {
            switch (selectedCategory) {
                case "shirt":
                    selectedButton = btnTShirt;
                    break;
                case "pant":
                    selectedButton = btnPant;
                    break;
                case "dress":
                    selectedButton = btnDress;
                    break;
            }
        }

        if (selectedButton != null) {
            selectedButton.setSelected(true);
            selectedButton.setBackgroundTintList(getColorStateList(R.color.second));
        }
    }

    private void updatePriceLabels() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);

        tvMinPrice.setText(formatter.format(minPrice) + "₫");
        tvMaxPrice.setText(formatter.format(maxPrice) + "₫");
    }

    private void resetFilters() {
        selectedCategory = null;
        float minPrice = 0f;
        float maxPrice = 10_000_000f;
        priceRangeSlider.setValueFrom(minPrice);
        priceRangeSlider.setValueTo(maxPrice);
        priceRangeSlider.setValues(minPrice, maxPrice);

        updatePriceLabels();
        updateCategorySelection();
    }

    private void applyFilters() {
        Intent resultIntent = new Intent();

        List<Float> values = priceRangeSlider.getValues();
        double min = values.get(0);
        double max = values.get(1);

        if (min > 0) {
            resultIntent.putExtra("min_price", min);  // dùng double
        }
        if (max < 10_000_000) {
            resultIntent.putExtra("max_price", max);  // dùng double
        }

        if (selectedCategory != null) {
            resultIntent.putExtra("category", selectedCategory);
        }

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}