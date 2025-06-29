package fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.lustre.R;
import com.example.lustre.activities.*;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import okhttp3.*;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private static final String CLOUD_NAME = "dj4jk0zfo";
    private static final String UPLOAD_PRESET = "unsigned_preset";

    private FirebaseFirestore db;
    private String userId;

    private ImageView imgAvatar, btnEditAvatar;
    private TextView txtUserName;

    interface UploadCallback {
        void onSuccess(String avatarUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgAvatar = view.findViewById(R.id.imgAvatar);
        btnEditAvatar = view.findViewById(R.id.btnEditAvatar);
        txtUserName = view.findViewById(R.id.txtUserName);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", null);
        db = FirebaseFirestore.getInstance();

        if (userId != null) {
            loadUserProfile();
        }

        btnEditAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        setupMenuClick(view);
        return view;
    }
    private void loadUserProfile() {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        txtUserName.setText(doc.getString("name"));
                        String avatarUrl = doc.getString("avatar");
                        Log.d("DEBUG_AVATAR", "Original URL: " + avatarUrl);

                        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                            if (avatarUrl.startsWith("http://")) {
                                avatarUrl = avatarUrl.replace("http://", "https://");
                                Log.d("DEBUG_AVATAR", "Converted to HTTPS: " + avatarUrl);
                            }

                            Glide.with(this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.ic_logo)
                                    .error(R.drawable.ic_logo)
                                    .into(imgAvatar);
                        } else {
                            imgAvatar.setImageResource(R.drawable.ic_logo);
                        }
                    }
                });
    }
    private void setupMenuClick(View view) {
        view.findViewById(R.id.profile_tab_my_order).setOnClickListener(v -> startActivity(new Intent(requireContext(), MyOrderActivity.class)));
        view.findViewById(R.id.itemSettings).setOnClickListener(v -> startActivity(new Intent(requireContext(), SettingsActivity.class)));
        view.findViewById(R.id.itemPayment).setOnClickListener(v -> startActivity(new Intent(requireContext(), PaymentMethodActivity.class)));
        view.findViewById(R.id.itemPrivacy).setOnClickListener(v -> startActivity(new Intent(requireContext(), PrivacyPolicyActivity.class)));
        view.findViewById(R.id.itemHelpCenter).setOnClickListener(v -> startActivity(new Intent(requireContext(), HelpCenterActivity.class)));
        view.findViewById(R.id.itemLogout).setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = LayoutInflater.from(getContext()).inflate(R.layout.activity_log_out, null);
        dialog.setContentView(sheet);

        sheet.findViewById(R.id.btnCancelRemove).setOnClickListener(v -> dialog.dismiss());
        sheet.findViewById(R.id.btnConfirmLogout).setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                uploadImageToCloudinary(inputStream, avatarUrl -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("avatar", avatarUrl);

                    db.collection("users").document(userId)
                            .update(updates)
                            .addOnSuccessListener(unused -> {
                                Glide.with(requireContext()).load(avatarUrl).into(imgAvatar);
                                Toast.makeText(requireContext(), "Đổi avatar thành công", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(requireContext(), "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                });
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Lỗi chọn ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToCloudinary(InputStream inputStream, UploadCallback callback) {
        OkHttpClient client = new OkHttpClient();

        byte[] bytes;
        try {
            bytes = readBytesFromInputStream(inputStream);
        } catch (IOException e) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Lỗi đọc ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Lỗi upload ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    String imageUrl = body.split("\"url\":\"")[1].split("\"")[0].replace("\\/", "/");
                    requireActivity().runOnUiThread(() -> callback.onSuccess(imageUrl));
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Upload thất bại: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private byte[] readBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int len;
        byte[] data = new byte[4096];
        while ((len = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, len);
        }
        return buffer.toByteArray();
    }
}
