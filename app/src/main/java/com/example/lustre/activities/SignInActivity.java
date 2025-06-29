package com.example.lustre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lustre.R;

import firebase.AuthRepository;
import models.User;

public class SignInActivity extends AppCompatActivity {

    TextView txtSignUp;
    Button btnLogin;

    EditText edtEmail;
    EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadComponent();

        txtSignUp.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignInActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthRepository authRepo = new AuthRepository();
            authRepo.login(email, password, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(User user) {
                    // Lưu user_id vào SharedPreferences
                    getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("user_id", user.getId())
                            .apply();

                    Toast.makeText(SignInActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                    new android.os.Handler().postDelayed(() -> {
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        finish();
                    }, 300);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
    private void loadComponent() {
        txtSignUp = findViewById(R.id.sign_in_txtSignUp);
        btnLogin = findViewById(R.id.sign_in_btnLogin);
        edtEmail = findViewById(R.id.sign_in_txtEmail);
        edtPassword = findViewById(R.id.sign_in_txtPassword);
    }

}