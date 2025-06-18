package com.example.lustre_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VerifyCodeActivity extends AppCompatActivity {
    private EditText etCode1, etCode2, etCode3, etCode4;
    private TextView tvEmail, tvResend;
    private Button btnVerify;
    private ImageView ivBack;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        etCode1 = findViewById(R.id.etCode1);
        etCode2 = findViewById(R.id.etCode2);
        etCode3 = findViewById(R.id.etCode3);
        etCode4 = findViewById(R.id.etCode4);
        tvEmail = findViewById(R.id.tvEmail);
        tvResend = findViewById(R.id.tvResend);
        btnVerify = findViewById(R.id.btnVerify);
        ivBack = findViewById(R.id.ivBack);

        // Nhận email từ Intent
        email = getIntent().getStringExtra("email");
        if (email != null) {
            tvEmail.setText(email);
        }

        // Tự động chuyển focus khi nhập số
        setupOtpInputs();

        btnVerify.setOnClickListener(v -> {
            String code = etCode1.getText().toString() + etCode2.getText().toString()
                    + etCode3.getText().toString() + etCode4.getText().toString();
            if (code.length() < 4) {
                Toast.makeText(this, "Please enter the 4-digit code", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Xác thực mã OTP ở đây
            // Phân biệt nguồn gọi
            Intent fromIntent = getIntent();
            String from = fromIntent.getStringExtra("from");
            if (from != null && from.equals("forgot")) {
                Intent intent = new Intent(VerifyCodeActivity.this, NewPasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            } else {
                Intent intent = new Intent(VerifyCodeActivity.this, CompleteProfileActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        tvResend.setOnClickListener(v -> {
            // TODO: Gửi lại mã OTP
            Toast.makeText(this, "OTP resent!", Toast.LENGTH_SHORT).show();
        });

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupOtpInputs() {
        etCode1.addTextChangedListener(new GenericTextWatcher(etCode1, etCode2));
        etCode2.addTextChangedListener(new GenericTextWatcher(etCode2, etCode3));
        etCode3.addTextChangedListener(new GenericTextWatcher(etCode3, etCode4));
        etCode4.addTextChangedListener(new GenericTextWatcher(etCode4, null));
    }

    private class GenericTextWatcher implements TextWatcher {
        private final EditText currentView;
        private final EditText nextView;

        GenericTextWatcher(EditText currentView, EditText nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
} 