package com.danghuuthinh.lustre.K204060309Danghuuthinh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.danghuuthinh.lustre.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout itemSettings = findViewById(R.id.itemSettings);
        LinearLayout itemPayment = findViewById(R.id.itemPayment);
        LinearLayout itemPrivacy = findViewById(R.id.itemPrivacy);
        LinearLayout itemLogout = findViewById(R.id.itemLogout);
        LinearLayout itemHelpCenter = findViewById(R.id.itemHelpCenter);

        itemSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        itemPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this, PaymentMethodActivity.class);
                startActivity(intent);
            }
        });


        itemPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
        itemLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });
        itemHelpCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainProfileActivity.this, HelpCenterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showLogoutDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainProfileActivity.this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.activity_log_out, null);

        Button btnYes = bottomSheetView.findViewById(R.id.btnConfirmLogout);
        Button btnCancel = bottomSheetView.findViewById(R.id.btnCancelRemove);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        // Hiện tại bạn CHƯA cần xử lý "Yes", để đó cũng được,
        // Tạm thời chưa làm gì, có thể Toast cho biết:
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainProfileActivity.this, "Log out chưa xử lý", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}
