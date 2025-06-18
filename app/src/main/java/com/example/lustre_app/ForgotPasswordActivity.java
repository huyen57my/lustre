package com.example.lustre_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button btnSend;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnSend = findViewById(R.id.btnSend);
        ivBack = findViewById(R.id.ivBack);

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Gửi email xác thực ở đây nếu cần
            Intent intent = new Intent(ForgotPasswordActivity.this, VerifyCodeActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("from", "forgot");
            startActivity(intent);
        });

        ivBack.setOnClickListener(v -> onBackPressed());
    }
} 