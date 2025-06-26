package com.example.lustre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lustre.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
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
    private MaterialButton btnCancel, btnReview, btnSubmitReview;
    private EditText edtReview;
    private MaterialCardView cardReview;
    private TextView tvYourReview, tvReviewContent;

    private String orderStatus;
    private String orderId;
    private FirebaseFirestore db;

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
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        edtReview = findViewById(R.id.edtReview);
        cardReview = findViewById(R.id.cardReview);
        tvYourReview = findViewById(R.id.tvYourReview);
        tvReviewContent = findViewById(R.id.tvReviewContent);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderDetailAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        orderId = getIntent().getStringExtra("order_id");
        if (orderId != null) fetchOrderDetails(orderId);

        btnReview.setOnClickListener(v -> {
            edtReview.setVisibility(View.VISIBLE);
            btnSubmitReview.setVisibility(View.VISIBLE);
            btnReview.setVisibility(View.GONE);
        });

        btnSubmitReview.setOnClickListener(v -> {
            String reviewText = edtReview.getText().toString().trim();
            if (reviewText.isEmpty()) {
                Toast.makeText(this, "Please enter your review", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("orders")
                    .document(orderId)
                    .update("review", reviewText)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Review submitted", Toast.LENGTH_SHORT).show();
                        edtReview.setVisibility(View.GONE);
                        btnSubmitReview.setVisibility(View.GONE);
                        btnReview.setVisibility(View.GONE);

                        tvYourReview.setVisibility(View.VISIBLE);
                        tvReviewContent.setText(reviewText);
                        tvReviewContent.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void fetchOrderDetails(String orderId) {
        db.collection("orders")
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

                    orderStatus = documentSnapshot.getString("status");

                    if ("ORDER_PLACED".equals(orderStatus)) {
                        btnCancel.setVisibility(View.VISIBLE);
                        btnCancel.setOnClickListener(v -> cancelOrder(orderId));
                    } else {
                        btnCancel.setVisibility(View.GONE);
                    }

                    String review = documentSnapshot.getString("review");

                    if (review != null && !review.trim().isEmpty()) {
                        cardReview.setVisibility(View.VISIBLE);
                        tvYourReview.setVisibility(View.VISIBLE);
                        tvReviewContent.setVisibility(View.VISIBLE);
                        tvReviewContent.setText(review);

                        edtReview.setVisibility(View.GONE);
                        btnReview.setVisibility(View.GONE);
                        btnSubmitReview.setVisibility(View.GONE);
                    } else if ("SHIPPED".equals(orderStatus)) {
                        cardReview.setVisibility(View.VISIBLE);
                        btnReview.setVisibility(View.VISIBLE);
                    }

                    List<Map<String, Object>> products = (List<Map<String, Object>>) documentSnapshot.get("products");
                    List<OrderItem> orderItems = new ArrayList<>();

                    for (Map<String, Object> itemMap : products) {
                        OrderItem item = new OrderItem();
                        item.setSize((String) itemMap.get("size"));
                        item.setQuantity(((Number) itemMap.get("quantity")).intValue());
                        item.setImages((List<String>) itemMap.get("images"));

                        Map<String, Object> prodMap = (Map<String, Object>) itemMap.get("product");

                        Product p = new Product();
                        p.setId((String) prodMap.get("id"));
                        p.setName((String) prodMap.get("name"));
                        p.setPrice(((Double) prodMap.get("price")).doubleValue());
                        p.setSale(((Double) prodMap.get("sale")).doubleValue());
                        p.setImg((List<String>) prodMap.get("imageUrl"));
                        p.setColor((String) prodMap.get("color"));

                        item.setProduct(p);
                        orderItems.add(item);
                    }

                    adapter.updateData(orderItems);
                });

        if ("CANCELED".equals(orderStatus)) {
            btnCancel.setVisibility(View.GONE);
            btnReview.setVisibility(View.GONE);
            btnSubmitReview.setVisibility(View.GONE);
            edtReview.setVisibility(View.GONE);
        }
    }

    private void cancelOrder(String orderId) {
        db.collection("orders")
                .document(orderId)
                .update("status", "CANCELED")
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cancel success", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, new Intent().putExtra("goToCancelledTab", true));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                });
    }

    private String formatVND(long amount) {
        return String.format("%,d VND", amount);
    }
}
