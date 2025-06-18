package com.example.lustre.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lustre.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 100;
    private EditText edtEmail, edtPhone;
    private Spinner spinnerGender;
    private ImageView editIcon;
    private ShapeableImageView avatarImage;
    private Button btnSubmit;

    private String username;
    private Uri imageUri;

    private FirebaseFirestore db;

    private static final String CLOUD_NAME = "dj4jk0zfo";
    private static final String UPLOAD_PRESET = "unsigned_preset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtEmail = findViewById(R.id.onboard_edtEmail);
        edtPhone = findViewById(R.id.onboard_edtPhone);
        spinnerGender = findViewById(R.id.spinnerGender);
        editIcon = findViewById(R.id.edit_icon);
        avatarImage = findViewById(R.id.onboard_pencil);
        btnSubmit = findViewById(R.id.sign_up_btnLogin);

        db = FirebaseFirestore.getInstance();

        String email = getIntent().getStringExtra("email");
        String phone = edtPhone.getText().toString();
        String name = getIntent().getStringExtra("name");
        if (email != null) edtEmail.setText(email);
        if (name != null) username = name;

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Nam", "Nữ"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        String phone = edtPhone.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String email = edtEmail.getText().toString().trim();

        if (phone.isEmpty()) {
            edtPhone.setError("Vui lòng nhập sdt");
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            uploadImageToCloudinary(inputStream, (avatarUrl) -> {
                db.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (!querySnapshot.isEmpty()) {
                                String userId = querySnapshot.getDocuments().get(0).getId();
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("name", username);
                                updates.put("phone", phone);
                                updates.put("gender", gender);
                                updates.put("avatar", avatarUrl);
                                updates.put("address", new ArrayList<String>());

                                db.collection("users").document(userId)
                                        .update(updates)
                                        .addOnSuccessListener(unused -> Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user_id", userId);
                                editor.putString("username", username);
                                editor.apply();

                                Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Lỗi chọn ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private interface UploadCallback {
        void onSuccess(String imageUrl);
    }

    private byte[] readBytesFromInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        try (inputStream) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }
    }
    private void uploadImageToCloudinary(InputStream inputStream, UploadCallback callback) {
        OkHttpClient client = new OkHttpClient();

        byte[] bytes;
        try {
            bytes = readBytesFromInputStream(inputStream);
        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(this, "Lỗi đọc ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            return;
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "avatar.jpg", RequestBody.create(bytes, MediaType.parse("image/jpeg")))
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build();

        Request request = new Request.Builder()
                .url("https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(CreateProfileActivity.this, "Lỗi upload ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        String imageUrl = body.split("\"url\":\"")[1].split("\"")[0].replace("\\/", "/");
                        runOnUiThread(() -> callback.onSuccess(imageUrl));
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(CreateProfileActivity.this, "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(CreateProfileActivity.this, "Upload thất bại: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            avatarImage.setImageURI(imageUri);
        }
    }
}
