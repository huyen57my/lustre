package com.example.lustre_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class CompleteProfileActivity extends AppCompatActivity {
    private EditText etName, etCountryCode, etPhone;
    private Spinner spinnerGender;
    private ImageView ivAvatar, ivEditAvatar, ivBack;
    private Button btnCompleteProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        etName = findViewById(R.id.etName);
        etCountryCode = findViewById(R.id.etCountryCode);
        etPhone = findViewById(R.id.etPhone);
        spinnerGender = findViewById(R.id.spinnerGender);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivEditAvatar = findViewById(R.id.ivEditAvatar);
        ivBack = findViewById(R.id.ivBack);
        btnCompleteProfile = findViewById(R.id.btnCompleteProfile);

        // Set up gender spinner
        List<String> genderOptions = Arrays.asList("Select Gender", "Male", "Female");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // Set default country code
        etCountryCode.setText("+84");

        // Set input type for name field
        etName.setInputType(android.text.InputType.TYPE_CLASS_TEXT | 
                          android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        // Set input type for phone field
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);

        btnCompleteProfile.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String countryCode = etCountryCode.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem() != null ? 
                          spinnerGender.getSelectedItem().toString() : "";

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (countryCode.isEmpty()) {
                Toast.makeText(this, "Please enter country code", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (gender.equals("Select Gender")) {
                Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Gửi thông tin hoàn thiện profile lên server
            Toast.makeText(this, "Profile completed!", Toast.LENGTH_SHORT).show();
            // Chuyển sang MainActivity và clear stack
            Intent intent = new Intent(CompleteProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        ivBack.setOnClickListener(v -> onBackPressed());
        ivEditAvatar.setOnClickListener(v -> {
            // TODO: Xử lý chọn/chụp ảnh đại diện
            Toast.makeText(this, "Change avatar clicked", Toast.LENGTH_SHORT).show();
        });
    }
} 