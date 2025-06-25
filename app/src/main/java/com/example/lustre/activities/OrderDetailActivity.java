package com.example.lustre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lustre.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapter.OrderDetailAdapter;
import models.OrderItem;
import models.Product;

public class OrderDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderDetailAdapter adapter;
    private TextView txtTotal, txtDiscount, txtFinal;
    private MaterialButton btnCancel, btnReview;
    private String orderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        recyclerView = findViewById(R.id.recyclerViewOrderDetail);
        txtTotal = findViewById(R.id.txtTotalAmount);
        txtDiscount = findViewById(R.id.txtDiscountAmount);
        txtFinal = findViewById(R.id.txtFinalAmount);
        btnCancel = findViewById(R.id.btnCancel);
        btnReview = findViewById(R.id.btnReview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderDetailAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        String orderId = getIntent().getStringExtra("order_id");
        Log.d("DEBUG_ORDERID", orderId);
        if (orderId != null) fetchOrderDetails(orderId);
    }

    private void fetchOrderDetails(String orderId) {
        FirebaseFirestore.getInstance().collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) return;

                    long totalAmount = documentSnapshot.getLong("totalAmount");
                    long discount = documentSnapshot.getLong("discountAmount");
                    long finalAmount = documentSnapshot.getLong("finalAmount");

                    txtTotal.setText(formatVND(totalAmount));
                    txtDiscount.setText(formatVND(discount));
                    txtFinal.setText(formatVND(finalAmount));

                    String status = documentSnapshot.getString("status");
                    orderStatus = status;

                    if ("ORDER_PLACED".equals(status)) {
                        btnCancel.setVisibility(View.VISIBLE);
                        btnCancel.setOnClickListener(v -> cancelOrder(orderId));
                    } else {
                        btnCancel.setVisibility(View.GONE);
                    }

                    if ("SHIPPED".equals(status)) {
                        btnReview.setVisibility(View.VISIBLE);
                    } else {
                        btnReview.setVisibility(View.GONE);
                    }

                    List<Map<String, Object>> products = (List<Map<String, Object>>) documentSnapshot.get("products");
                    List<OrderItem> orderItems = new ArrayList<>();

                    for (Map<String, Object> itemMap : products) {
                        OrderItem item = new OrderItem();
                        item.setSize((String) itemMap.get("size"));
                        item.setQuantity(((Number) itemMap.get("quantity")).intValue());
                        item.setImages((List<String>) itemMap.get("images"));

                        Map<String, Object> prodMap = (Map<String, Object>) itemMap.get("product");
                        Log.d("DEBUG_ORDER_PRODUCT", prodMap.toString());

                        Product p = new Product();
                        p.setId((String) prodMap.get("id"));
                        p.setName((String) prodMap.get("name"));
                        p.setPrice(((Double) prodMap.get("price")).doubleValue());
                        p.setSale(((Double) prodMap.get("sale")).doubleValue());
                        p.setImg((List<String>) prodMap.get("imageUrl"));
                        p.setColor((String) prodMap.get("color"));

                        Log.d("DEBUG_ORDER_PRODUCT_DETAIL", p.getImageUrl().get(0));
                        item.setProduct(p);
                        orderItems.add(item);
                    }

                    adapter.updateData(orderItems);
                });
    }

    private void cancelOrder(String orderId) {
        FirebaseFirestore.getInstance().collection("orders")
                .document(orderId)
                .update("status", "CANCELED")
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cancel success", Toast.LENGTH_SHORT).show();

                    // Gửi kết quả về MyOrderActivity
                    setResult(RESULT_OK, new Intent().putExtra("goToCancelledTab", true));
                    finish(); // quay lại MyOrderActivity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                });
    }
    private String formatVND(long amount) {
        return String.format("%,d VND", amount);
    }
}
