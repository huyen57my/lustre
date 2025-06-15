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

public class SignUpActivity extends AppCompatActivity {

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

        loadComponent();

        txtSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
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