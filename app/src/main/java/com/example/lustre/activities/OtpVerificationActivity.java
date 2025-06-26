package com.example.lustre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lustre.R;
import firebase.OtpService;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4;
    private Button btnVerify;
    private TextView tvResend, tvEmail;
    private String email, name, id, phone, gender, avatar, role;

    private OtpService otpService;
    private CountDownTimer resendTimer;
    private boolean canResend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Nhận thông tin từ Intent
        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");
        phone = getIntent().getStringExtra("phone");
        gender = getIntent().getStringExtra("gender");
        avatar = getIntent().getStringExtra("avatar");
        role = getIntent().getStringExtra("role");

        // Ánh xạ
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        btnVerify = findViewById(R.id.btnVerify);
        tvResend = findViewById(R.id.tvResend);
        tvEmail = findViewById(R.id.tvEmail);
        ImageView btnBack = findViewById(R.id.btnBack);

        tvEmail.setText(email);
        otpService = new OtpService();

        setupOtpInputs();
        sendOtp();
        startResendTimer();

        btnVerify.setOnClickListener(v -> verifyOtp());
        tvResend.setOnClickListener(v -> {
            if (canResend) {
                sendOtp();
                startResendTimer();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupOtpInputs() {
        otp1.addTextChangedListener(new OtpTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new OtpTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new OtpTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new OtpTextWatcher(otp4, null));
    }

    private void sendOtp() {
        otpService.sendOtp(email, new OtpService.OtpCallback() {
            @Override
            public void onSent(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(OtpVerificationActivity.this, "OTP sent to email", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailed(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(OtpVerificationActivity.this, "Failed to send OTP: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void verifyOtp() {
        String code = otp1.getText().toString().trim()
                + otp2.getText().toString().trim()
                + otp3.getText().toString().trim()
                + otp4.getText().toString().trim();

        if (code.length() != 4) {
            Toast.makeText(this, "Please enter 4-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        otpService.verifyOtp(email, code, new OtpService.OtpCallback() {
            @Override
            public void onSent(String message) {
                Toast.makeText(OtpVerificationActivity.this, "OTP verified", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtpVerificationActivity.this, CreateProfileActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("name", name);
                intent.putExtra("id", id);
                intent.putExtra("phone", phone);
                intent.putExtra("gender", gender);
                intent.putExtra("avatar", avatar);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(OtpVerificationActivity.this, "OTP incorrect: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startResendTimer() {
        canResend = false;
        tvResend.setAlpha(0.4f);
        tvResend.setEnabled(false);

        if (resendTimer != null) resendTimer.cancel();

        resendTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvResend.setText("Resend in " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                tvResend.setText("Resend code");
                tvResend.setAlpha(1f);
                tvResend.setEnabled(true);
                canResend = true;
            }
        };
        resendTimer.start();
    }

    static class OtpTextWatcher implements TextWatcher {
        private final EditText current;
        private final EditText next;

        public OtpTextWatcher(EditText current, EditText next) {
            this.current = current;
            this.next = next;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && next != null) {
                next.requestFocus();
            }
        }
    }
}
