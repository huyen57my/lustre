package com.example.lustre_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private ImageView ivShowHide;
    private Button btnSignUp;
    private TextView tvSignIn;
    private CheckBox cbAgree;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivShowHide = findViewById(R.id.ivShowHide);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);
        cbAgree = findViewById(R.id.cbAgree);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle password visibility toggle
        ivShowHide.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(null);
                ivShowHide.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivShowHide.setImageResource(android.R.drawable.ic_menu_view);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Handle sign up button click
        btnSignUp.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!cbAgree.isChecked()) {
                Toast.makeText(this, "Please agree to Terms & Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển sang màn hình xác thực mã OTP
            Intent intent = new Intent(SignUpActivity.this, VerifyCodeActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // Navigate to Sign In
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Hàm kiểm tra email hợp lệ
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}