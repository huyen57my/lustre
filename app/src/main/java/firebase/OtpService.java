package firebase;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import okhttp3.*;

import java.io.IOException;
import java.util.Random;

public class OtpService {

    public interface OtpCallback {
        void onSent(String message);
        void onFailed(String error);
    }

    private static final String TAG = "OtpService";
    private static final String API_KEY = "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";
    private static final String FROM_EMAIL = "mynvh22411c@st.uel.edu.vn";

    public void sendOtpToEmail(String email, String otp) {
        OkHttpClient client = new OkHttpClient();

        String json = "{"
                + "\"personalizations\":[{\"to\":[{\"email\":\"" + email + "\"}]}],"
                + "\"from\":{\"email\":\"" + FROM_EMAIL + "\"},"
                + "\"subject\":\"Mã xác thực OTP của bạn\","
                + "\"content\":[{\"type\":\"text/plain\",\"value\":\"Mã OTP của bạn là: " + otp + "\"}]"
                + "}";

        Request request = new Request.Builder()
                .url("https://api.sendgrid.com/v3/mail/send")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Send OTP failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "OTP sent successfully to " + email);
                } else {
                    Log.e(TAG, "SendGrid error: " + response.code() + " - " + response.body().string());
                }
            }
        });
    }

    // Hàm generate OTP gồm 4 số ngẫu nhiên
    public String generateOtpCode() {
        Random rand = new Random();
        int otp = 1000 + rand.nextInt(9000);
        return String.valueOf(otp);
    }


    public void verifyOtp(String email, String inputOtp, OtpCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String savedOtp = document.getString("otpCode");
                            if (savedOtp != null && savedOtp.equals(inputOtp)) {
                                callback.onSent("OTP hợp lệ");
                            } else {
                                callback.onFailed("Mã OTP không đúng");
                            }
                        }
                    } else {
                        callback.onFailed("Không tìm thấy người dùng");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailed("Lỗi kiểm tra OTP: " + e.getMessage());
                });
    }

    // Hàm gửi kèm callback (nếu bạn dùng callback để hiển thị Toast, v.v)
    public void sendOtp(String email, String otp, OtpCallback callback) {
        OkHttpClient client = new OkHttpClient();

        String json = "{"
                + "\"personalizations\":[{\"to\":[{\"email\":\"" + email + "\"}]}],"
                + "\"from\":{\"email\":\"" + FROM_EMAIL + "\"},"
                + "\"subject\":\"Mã xác thực OTP của bạn\","
                + "\"content\":[{\"type\":\"text/plain\",\"value\":\"Mã OTP của bạn là: " + otp + "\"}]"
                + "}";

        Request request = new Request.Builder()
                .url("https://api.sendgrid.com/v3/mail/send")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailed("Lỗi gửi email: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSent("OTP sent to email");
                } else {
                    callback.onFailed("Lỗi từ SendGrid: " + response.code() + " - " + response.body().string());
                }
            }
        });
    }
    public void sendOtp(String email, OtpCallback callback) {
        String otp = generateOtpCode();
        sendOtp(email, otp, new OtpCallback() {
            @Override
            public void onSent(String message) {
                callback.onSent(message);
            }

            @Override
            public void onFailed(String error) {
                callback.onFailed(error);
            }
        });
    }
}
