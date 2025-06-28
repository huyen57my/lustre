package com.example.lustre.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import models.VoucherUsage;

public class CheckoutActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SHIPPING = 1001;

    private RecyclerView recyclerView;
    private Button btnContinue, btnShip, btnAddress;
    private TextView txtShippingAddress, txtShippingType;
    private TextView txtTotal, txtDiscount, txtFinal, txtShippingFee;
    private ImageButton btnBack;

    private CheckoutAdapter adapter;
    private List<CartDisplayItem> selectedItems;

    private ShippingType selectedShippingType = ShippingType.TIET_KIEM;
    private FirebaseFirestore firestore;

    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private double shippingFee = 0;

    private String voucherCode = "";
    private String discountType = "";

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
        txtShippingFee = findViewById(R.id.txtShippingFee);

        recyclerView = findViewById(R.id.recycler_checkout);
        btnContinue = findViewById(R.id.btnContinueToPayment);
        btnShip = findViewById(R.id.btn_ShippingType);
        btnAddress = findViewById(R.id.btn_address);

        selectedItems = (List<CartDisplayItem>) getIntent().getSerializableExtra("selectedItems");
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
        discountAmount = getIntent().getDoubleExtra("discountAmount", 0);
        finalAmount = getIntent().getDoubleExtra("finalAmount", 0);

        // Lấy voucher
        voucherCode = getIntent().getStringExtra("voucherCode");
        discountType = getIntent().getStringExtra("discountType");

        // Xử lý miễn phí ship nếu voucher là free_shipping
        if ("free_shipping".equals(discountType)) {
            shippingFee = 0;
        } else {
            updateShippingType(); // gán shippingFee bình thường
        }

        finalAmount = totalAmount - discountAmount + shippingFee;

        adapter = new CheckoutAdapter(selectedItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        txtShippingType.setText(selectedShippingType.getDescription());
        updateAmountDisplay();
        fetchDefaultAddress();

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

            saveOrderAndVoucherUsage(order, userId, voucherCode);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SHIPPING && resultCode == RESULT_OK && data != null) {
            String typeName = data.getStringExtra("selectedShipping");
            selectedShippingType = ShippingType.valueOf(typeName);

            // Nếu không dùng freeship thì cập nhật lại shipping
            if (!"free_shipping".equals(discountType)) {
                updateShippingType();
                finalAmount = totalAmount - discountAmount + shippingFee;
            } else {
                shippingFee = 0;
                finalAmount = totalAmount - discountAmount;
            }

            updateAmountDisplay();
        }
    }

    private void updateShippingType() {
        txtShippingType.setText(selectedShippingType.getDescription());

        switch (selectedShippingType) {
            case HOA_TOC:
                shippingFee = 30000;
                break;
            case TIET_KIEM:
                shippingFee = 15000;
                break;
        }
    }

    private void updateAmountDisplay() {
        if (txtTotal != null) txtTotal.setText("Tổng tiền: " + totalAmount + "đ");
        if (txtShippingFee != null) txtShippingFee.setText("Phí vận chuyển: " + shippingFee + "đ");
        if (txtDiscount != null) txtDiscount.setText("Giảm giá: " + discountAmount + "đ");
        if (txtFinal != null) txtFinal.setText("Thành tiền: " + finalAmount + "đ");
    }

    private void saveOrderAndVoucherUsage(Order order, String userId, String voucherCode) {
        firestore.collection("orders")
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    updateCartAfterOrder(userId, selectedItems);

                    if (voucherCode != null && !voucherCode.isEmpty()) {
                        String usageId = voucherCode + "_" + userId;
                        firestore.collection("voucher_usages")
                                .document(usageId)
                                .set(new VoucherUsage(userId, voucherCode))
                                .addOnSuccessListener(ref -> {
                                    Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CheckoutActivity.this, MyOrderActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Đặt hàng thành công nhưng không lưu được voucher", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                    } else {
                        Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CheckoutActivity.this, MyOrderActivity.class);
                        startActivity(intent);
                        finish();
                    }
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

    private void updateCartAfterOrder(String userId, List<CartDisplayItem> orderedItems) {
        for (CartDisplayItem item : orderedItems) {
            firestore.collection("users")
                    .document(userId)
                    .collection("cart")
                    .whereEqualTo("productId", item.getProduct().getId())
                    .whereEqualTo("size", item.getSize())
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            var doc = querySnapshot.getDocuments().get(0);
                            Long currentQuantity = doc.getLong("quantity");

                            if (currentQuantity != null) {
                                int remaining = currentQuantity.intValue() - item.getQuantity();
                                if (remaining > 0) {
                                    doc.getReference().update("quantity", remaining);
                                } else {
                                    doc.getReference().delete();
                                }
                            }
                        }
                    });
        }
    }

}
