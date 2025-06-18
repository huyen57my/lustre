package com.example.lustre.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lustre.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import adapter.AddressAdapter;

public class ChooseAddressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnAdd, btnApply;
    private AddressAdapter adapter;
    private FirebaseFirestore firestore;
    private String userId;
    private String selectedAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);

        recyclerView = findViewById(R.id.recyclerAddresses);
        btnAdd = findViewById(R.id.btnAddNewAddress);
        btnApply = findViewById(R.id.btnApplyAddress);
        firestore = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setUserId();
        loadAddresses();

        btnAdd.setOnClickListener(v -> showAddAddressDialog());

        btnApply.setOnClickListener(v -> {
            if (selectedAddress != null) {
                Intent intent = new Intent();
                intent.putExtra("selectedAddress", selectedAddress);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Please select an address", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserId() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", null); // chỉ dùng user_id
    }

    private void loadAddresses() {
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    List<String> addresses = (List<String>) doc.get("address");
                    if (addresses == null) addresses = new ArrayList<>();
                    adapter = new AddressAdapter(addresses, address -> selectedAddress = address);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load addresses", Toast.LENGTH_SHORT).show());
    }

    private void showAddAddressDialog() {
        EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add New Address")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String newAddr = input.getText().toString().trim();
                    if (!newAddr.isEmpty()) {
                        addAddressToUser(newAddr);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addAddressToUser(String newAddr) {
        DocumentReference userRef = firestore.collection("users").document(userId);
        userRef.update("address", FieldValue.arrayUnion(newAddr))
                .addOnSuccessListener(unused -> loadAddresses())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add address", Toast.LENGTH_SHORT).show());
    }
}
