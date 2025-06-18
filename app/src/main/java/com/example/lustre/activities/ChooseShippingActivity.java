package com.example.lustre.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lustre.R;

import models.ShippingType;

public class ChooseShippingActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton rbTietKiem, rbHoaToc;
    private Button btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_shipping);

        radioGroup = findViewById(R.id.radioGroupShipping);
        rbTietKiem = findViewById(R.id.rbTietKiem);
        rbHoaToc = findViewById(R.id.rbHoaToc);
        btnApply = findViewById(R.id.btnApplyShipping);

        rbTietKiem.setChecked(true); // ✅ Mặc định chọn tiết kiệm

        btnApply.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            ShippingType selectedType = ShippingType.TIET_KIEM;

            if (checkedId == R.id.rbTietKiem) {
                selectedType = ShippingType.TIET_KIEM;
            } else if (checkedId == R.id.rbHoaToc) {
                selectedType = ShippingType.HOA_TOC;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedShipping", selectedType.name());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

}
