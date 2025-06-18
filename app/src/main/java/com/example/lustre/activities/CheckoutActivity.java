package com.example.lustre.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lustre.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import adapter.CheckoutAdapter;
import models.CartDisplayItem;
import models.Order;
import models.OrderStatus;
import models.ShippingType;

public class CheckoutActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SHIPPING = 1001;

    private RecyclerView recyclerView;
    private Button btnContinue, btnShip, btnAddress;
    private TextView txtShippingAddress, txtShippingType;
    private TextView txtTotal, txtDiscount, txtFinal;

    private CheckoutAdapter adapter;
    private List<CartDisplayItem> selectedItems;

    private ImageButton btnBack;

    private ShippingType selectedShippingType = ShippingType.TIET_KIEM;
    private FirebaseFirestore firestore;

    private double totalAmount;
    private double discountAmount;
    private double finalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        TextView tvTitle = findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText("Checkout Order");
        }

        firestore = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btn_back);
        txtShippingAddress = findViewById(R.id.txtShippingAddress);
        txtShippingType = findViewById(R.id.txtShippingType);
        txtTotal = findViewById(R.id.txtTotal);
        txtDiscount = findViewById(R.id.txtDiscount);
        txtFinal = findViewById(R.id.txtFinal);

        recyclerView = findViewById(R.id.recycler_checkout);
        btnContinue = findViewById(R.id.btnContinueToPayment);
        btnShip = findViewById(R.id.btn_ShippingType);
        btnAddress = findViewById(R.id.btn_address);

        selectedItems = (List<CartDisplayItem>) getIntent().getSerializableExtra("selectedItems");
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
        discountAmount = getIntent().getDoubleExtra("discountAmount", 0);
        finalAmount = getIntent().getDoubleExtra("finalAmount", 0);

        adapter = new CheckoutAdapter(selectedItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateShippingType();
        fetchDefaultAddress();

        Log.d("DEBUG", "onCreate: " + finalAmount);

        txtShippingType.setText(selectedShippingType.getDescription());
        txtFinal.setText("Thành tiền: " + finalAmount + "đ");
        btnShip.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, ChooseShippingActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SHIPPING);
        });

        btnAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, ChooseAddressActivity.class);
            startActivity(intent);
        });

        btnContinue.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String userId = prefs.getString("user_id", null);
            if (userId == null) return;

            String address = txtShippingAddress.getText().toString();
            long createdAt = System.currentTimeMillis();
            String voucherCode = "";

            Order order = new Order(
                    null,
                    userId,
                    selectedItems,
                    totalAmount,
                    discountAmount,
                    finalAmount,
                    voucherCode,
                    OrderStatus.ORDER_PLACED,
                    address,
                    createdAt
            );

            saveOrderToFirestore(order);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SHIPPING && resultCode == RESULT_OK && data != null) {
            String typeName = data.getStringExtra("selectedShipping");
            selectedShippingType = ShippingType.valueOf(typeName);
            updateShippingType();
        }
    }

    private void updateShippingType() {
        txtShippingType.setText(selectedShippingType.getDescription());

        double shippingFee = 0;
        switch (selectedShippingType) {
            case HOA_TOC:
                shippingFee = 30000;
                break;
            case TIET_KIEM:
                shippingFee = 15000;
                break;
        }

        finalAmount = totalAmount - discountAmount + shippingFee;
        updateAmountDisplay();
    }

    private void updateAmountDisplay() {
        if (txtTotal != null) txtTotal.setText("Tổng tiền: " + totalAmount + "đ");
        if (txtDiscount != null) txtDiscount.setText("Giảm giá: " + discountAmount + "đ");
        if (txtFinal != null) txtFinal.setText("Thành tiền: " + finalAmount + "đ");
    }

    private void saveOrderToFirestore(Order order) {
        firestore.collection("orders")
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi đặt hàng", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchDefaultAddress() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) return;

        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> addresses = (List<String>) documentSnapshot.get("address");
                        if (addresses != null && !addresses.isEmpty()) {
                            txtShippingAddress.setText(addresses.get(0));
                        } else {
                            txtShippingAddress.setText("Chưa có địa chỉ");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    txtShippingAddress.setText("Không thể lấy địa chỉ");
                });
    }
}
