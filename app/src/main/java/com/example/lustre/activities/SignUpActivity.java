package com.example.lustre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lustre.R;
import com.google.firebase.firestore.FirebaseFirestore;

import firebase.AuthRepository;
import models.User;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    EditText edtUsername;
    EditText edtEmail;
    EditText edtPassword;

    Button btnSignUp;
    TextView txtSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();

        loadComponent();

        txtSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        });

        btnSignUp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String name = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = db.collection("users").document().getId();
            User user = new User();
            user.setId(userId);
            user.setEmail(email);
            user.setName(name);
            user.setPassword(password);
            user.setRole("user");
            user.setPhone("");
            user.setGender("");
            user.setAvatar("");

            AuthRepository authRepo = new AuthRepository();
            authRepo.register(user, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(User user) {
                    runOnUiThread(() -> {
                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công! Vui lòng kiểm tra email để nhận OTP.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("type", "sign_up");
                        intent.putExtra("email", user.getEmail());
                        intent.putExtra("name", user.getName());
                        intent.putExtra("id", user.getId());
                        intent.putExtra("phone", user.getPhone());
                        intent.putExtra("gender", user.getGender());
                        intent.putExtra("avatar", user.getAvatar());
                        intent.putExtra("role", user.getRole());
                        startActivity(intent);
                        finish();
                    });
                }
                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
                        Log.d("DEBUG", errorMessage);
                        Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

    }

    private void loadComponent() {
        edtEmail = findViewById(R.id.sign_up_txtEmail);
        edtUsername = findViewById(R.id.sign_up_edtName);
        edtPassword = findViewById(R.id.sign_up_txtPassword);
        btnSignUp = findViewById(R.id.sign_up_btnLogin);
        txtSignIn = findViewById(R.id.sign_up_txtSignIn);
    }

}