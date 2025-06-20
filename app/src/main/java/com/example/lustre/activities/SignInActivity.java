package com.example.lustre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lustre.R;

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
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        });
    }
    private void loadComponent() {
        txtSignUp = findViewById(R.id.sign_in_txtSignUp);
        btnLogin = findViewById(R.id.sign_in_btnLogin);
        edtEmail = findViewById(R.id.sign_in_txtEmail);
        edtPassword = findViewById(R.id.sign_in_txtPassword);
    }

    private void handleLogin() {
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
    }
}