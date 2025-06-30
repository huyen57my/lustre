package firebase;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import models.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class AuthRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "AuthRepository";

    public interface AuthCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public void register(User user, AuthCallback callback) {
        String hashedPassword = hashPassword(user.getPassword());
        if (hashedPassword == null) {
            callback.onFailure("Lỗi mã hóa mật khẩu.");
            return;
        }
        user.setPassword(hashedPassword);

        OtpService otpService = new OtpService();
        String otp = otpService.generateOtpCode();
        user.setOtpCode(otp);

        db.collection("users")
                .whereEqualTo("email", user.getEmail())
                .get()
                .addOnSuccessListener(emailSnapshot -> {
                    if (!emailSnapshot.isEmpty()) {
                        callback.onFailure("Email đã tồn tại.");
                        return;
                    }

                    if (!user.getPhone().isEmpty()) {
                        db.collection("users")
                                .whereEqualTo("phone", user.getPhone())
                                .get()
                                .addOnSuccessListener(phoneSnapshot -> {
                                    if (!phoneSnapshot.isEmpty()) {
                                        callback.onFailure("Số điện thoại đã tồn tại.");
                                        return;
                                    }
                                    createUser(user, callback, otpService, otp);
                                })
                                .addOnFailureListener(e -> callback.onFailure("Lỗi kiểm tra số điện thoại: " + e.getMessage()));
                    } else {
                        // Nếu không có số điện thoại, tạo user luôn
                        createUser(user, callback, otpService, otp);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Lỗi kiểm tra email: " + e.getMessage()));
    }

    private void createUser(User user, AuthCallback callback, OtpService otpService, String otp) {
        db.collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> callback.onFailure("Lỗi tạo tài khoản: " + e.getMessage()));
    }


    public void login(String email, String password, AuthCallback callback) {
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            callback.onFailure("Lỗi mã hóa mật khẩu.");
            return;
        }

        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", hashedPassword)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        User user = snapshot.getDocuments().get(0).toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("Email hoặc mật khẩu không đúng.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Lỗi đăng nhập: " + e.getMessage()));
    }
}