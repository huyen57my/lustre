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

public class ShippingAddressActivity extends AppCompatActivity {

    Button btnApply;
    RadioGroup radioGroup;
    ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shipping_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack = findViewById(R.id.ivBack);
        btnApply = findViewById(R.id.btnApply);
        radioGroup = findViewById(R.id.radioGroupAddresses);

        btnApply.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            String address = "";
            if (checkedId == R.id.radioHome) {
                address = "Home\n1901 Thornridge Cir. Shiloh, Hawaii 81063";
            } else if (checkedId == R.id.radioOffice) {
                address = "Office\n4517 Washington Ave. Manchester, Kentucky 39495";
            } else if (checkedId == R.id.radioParent) {
                address = "Parent's House\n8502 Preston Rd. Inglewood, Maine 98380";
            } else if (checkedId == R.id.radioFriend) {
                address = "Friend's House\n2464 Royal Ln. Mesa, New Jersey 45463";
            }
            Intent resultIntent = new Intent(ShippingAddressActivity.this, CheckoutActivity.class);
            resultIntent.putExtra("selected_address", address);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(resultIntent);
            finish();
        });
        ivBack.setOnClickListener(v -> onBackPressed());
    }
}