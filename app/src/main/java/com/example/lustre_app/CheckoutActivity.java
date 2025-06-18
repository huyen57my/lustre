package com.example.lustre_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CheckoutActivity extends AppCompatActivity {
    ImageView ivBack;

    Button btnChangeShippingAddress, btnChangeShippingType, btnChangePaymentMethod, btnContinueToPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack = findViewById(R.id.ivBack);

        btnChangeShippingAddress = findViewById(R.id.btnChangeShippingAddress);
        btnChangeShippingAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, ShippingAddressActivity.class);
            startActivity(intent);
        });

        btnChangeShippingType = findViewById(R.id.btnChangeShippingType);
        btnChangeShippingType.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, ChooseShippingActivity.class);
            startActivity(intent);
        });

        btnChangePaymentMethod = findViewById(R.id.btnChangePaymentMethod);
        btnChangePaymentMethod.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, PaymentMethodsActivity.class);
            startActivity(intent);
        });

        btnContinueToPayment = findViewById(R.id.btnContinueToPayment);
        btnContinueToPayment.setOnClickListener(v -> {
            Intent intent1 = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
            startActivity(intent1);
        });

        // Nhận dữ liệu trả về từ các màn hình khác
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("selected_address")) {
                String address = intent.getStringExtra("selected_address");
                if (address != null) {
                    String[] parts = address.split("\\n", 2);
                    TextView tvTitle = findViewById(R.id.tvShippingAddressTitle);
                    TextView tvDetail = findViewById(R.id.tvShippingAddress);
                    if (tvTitle != null && tvDetail != null) {
                        tvTitle.setText(parts[0]);
                        tvDetail.setText(parts.length > 1 ? parts[1] : "");
                    }
                }
            }
            if (intent.hasExtra("selected_shipping")) {
                String shipping = intent.getStringExtra("selected_shipping");
                if (shipping != null) {
                    String[] parts = shipping.split("\\n", 2);
                    TextView tvTitle = findViewById(R.id.tvShippingTypeTitle);
                    TextView tvDetail = findViewById(R.id.tvShippingTypeDetail);
                    if (tvTitle != null && tvDetail != null) {
                        tvTitle.setText(parts[0]);
                        tvDetail.setText(parts.length > 1 ? parts[1] : "");
                    }
                }
            }
            if (intent.hasExtra("selected_payment")) {
                String payment = intent.getStringExtra("selected_payment");
                if (payment != null) {
                    String[] parts = payment.split("\\n", 2);
                    TextView tvTitle = findViewById(R.id.tvPaymentMethodTitle);
                    TextView tvDetail = findViewById(R.id.tvPaymentMethodDetail);
                    if (tvTitle != null && tvDetail != null) {
                        tvTitle.setText(parts[0]);
                        tvDetail.setText(parts.length > 1 ? parts[1] : "");
                    }
                }
            }

            ivBack.setOnClickListener(v -> onBackPressed());
        }
    }
}