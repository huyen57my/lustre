package com.example.lustre_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewPasswordActivity extends AppCompatActivity {
    private EditText etPassword, etConfirmPassword;
    private ImageView ivShowHidePassword, ivShowHideConfirm, ivBack;
    private Button btnCreateNewPassword;
    private boolean isPasswordVisible = false, isConfirmVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivShowHidePassword = findViewById(R.id.ivShowHidePassword);
        ivShowHideConfirm = findViewById(R.id.ivShowHideConfirm);
        btnCreateNewPassword = findViewById(R.id.btnCreateNewPassword);
        ivBack = findViewById(R.id.ivBack);

        ivShowHidePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(null);
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        ivShowHideConfirm.setOnClickListener(v -> {
            isConfirmVisible = !isConfirmVisible;
            if (isConfirmVisible) {
                etConfirmPassword.setTransformationMethod(null);
            } else {
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        btnCreateNewPassword.setOnClickListener(v -> {
            String password = etPassword.getText().toString();
            String confirm = etConfirmPassword.getText().toString();
            if (password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Gửi mật khẩu mới lên server
            Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
            // Quay về màn hình đăng nhập và clear stack
            Intent intent = new Intent(NewPasswordActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        ivBack.setOnClickListener(v -> onBackPressed());
    }
} 