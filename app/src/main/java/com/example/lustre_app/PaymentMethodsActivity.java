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

public class PaymentMethodsActivity extends AppCompatActivity {

    ImageView ivBack;
    RadioGroup radioGroup;
    Button btnApply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_methods);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack = findViewById(R.id.ivBack);

        radioGroup = findViewById(R.id.radioGroupPayment);
        btnApply = findViewById(R.id.btnApply);
        btnApply.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            String payment = "";
            if (checkedId == R.id.radioCOD) {
                payment = "Thanh toán khi nhận hàng (COD)\nThanh toán tiền mặt khi nhận hàng.";
            } else if (checkedId == R.id.radioMomo) {
                payment = "Momo\nThanh toán qua ví điện tử Momo.";
            } else if (checkedId == R.id.radioBanking) {
                payment = "Chuyển khoản ngân hàng\nThanh toán qua tài khoản ngân hàng.";
            }
            Intent resultIntent = new Intent(PaymentMethodsActivity.this, CheckoutActivity.class);
            resultIntent.putExtra("selected_payment", payment);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(resultIntent);
            finish();
        });
        ivBack.setOnClickListener(v -> onBackPressed());
    }
}