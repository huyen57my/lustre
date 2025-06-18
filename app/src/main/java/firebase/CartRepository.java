package firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import models.CartItem;

public class CartRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CollectionReference getCartRef(String userId) {
        return db.collection("users").document(userId).collection("cart");
    }

    public void addToCart(String userId, CartItem item) {
        getCartRef(userId)
                .document(item.getProductId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long currentQuantity = documentSnapshot.getLong("quantity");
                        int newQuantity = (currentQuantity != null ? currentQuantity.intValue() : 0) + item.getQuantity();

                        getCartRef(userId)
                                .document(item.getProductId())
                                .update("quantity", newQuantity)
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                });
                    } else {
                        // Nếu chưa có: thêm mới
                        getCartRef(userId)
                                .document(item.getProductId())
                                .set(item)
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // notify failure khi đọc dữ liệu
                });
    }
    public void updateCartItem(String userId, String productId, int quantity) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("quantity", quantity);

        getCartRef(userId)
                .document(productId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    public void removeCartItem(String userId, String productId) {
        getCartRef(userId)
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    public void clearCart(String userId) {
        getCartRef(userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (var doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }
}
