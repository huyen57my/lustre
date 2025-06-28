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
                .whereEqualTo("productId", item.getProductId())
                .whereEqualTo("size", item.getSize())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        var doc = querySnapshot.getDocuments().get(0);
                        Long currentQuantity = doc.getLong("quantity");
                        int newQuantity = (currentQuantity != null ? currentQuantity.intValue() : 0) + item.getQuantity();

                        doc.getReference()
                                .update("quantity", newQuantity)
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                });
                    } else {
                        getCartRef(userId)
                                .add(item)
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // xử lý lỗi khi truy vấn
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
