package com.example.lustre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lustre.R;

public class WelcomeActivity extends AppCompatActivity {

    TextView txtLogin;
    Button btnStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtLogin = findViewById(R.id.txt_welcome_sign_in);
        btnStarted = findViewById(R.id.welcome_btn_start);

        txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, SignInActivity.class));
        });

        btnStarted.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, SignInActivity.class));
        });
    }
}
