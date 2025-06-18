package com.example.lustre_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChooseShippingActivity extends AppCompatActivity {

    ImageView ivBack;
    RadioGroup radioGroup;
    Button btnApply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_shipping);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack = findViewById(R.id.ivBack);

        radioGroup = findViewById(R.id.radioGroupShipping);
        btnApply = findViewById(R.id.btnApply);
        btnApply.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            String shipping = "";
            if (checkedId == R.id.radioEconomy) {
                shipping = "Giao hàng Tiết kiệm\nMô tả:.............";
            } else if (checkedId == R.id.radioRegular) {
                shipping = "Giao hàng Hỏa Tốc\nMô tả:.............";
            }
            Intent resultIntent = new Intent(ChooseShippingActivity.this, CheckoutActivity.class);
            resultIntent.putExtra("selected_shipping", shipping);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(resultIntent);
            finish();
        });
        ivBack.setOnClickListener(v -> onBackPressed());
    }
}