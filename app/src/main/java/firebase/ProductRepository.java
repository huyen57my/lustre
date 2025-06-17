package firebase;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import models.Product;

public class ProductRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference productsRef = db.collection("products");
    public void getSaleProducts(int limit, @Nullable DocumentSnapshot lastDoc, OnSuccessListener<List<Product>> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productRef = db.collection("products");

        Query query = productRef
                .whereGreaterThan("sale", 0)
                .orderBy("sale")
                .limit(limit);

        if (lastDoc != null) {
            query = query.startAfter(lastDoc);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            product.setDocumentSnapshot(doc);
                            products.add(product);
                        }
                    }
                    onSuccess.onSuccess(products);
                    Log.d("DEBUG_SALE", "Loaded " + products.size() + " products");
                })
                .addOnFailureListener(onFailure);
    }

    public void getProductsByFilter(@Nullable String keyword,
                                    @Nullable Double minPrice,
                                    @Nullable Double maxPrice,
                                    @Nullable String category,
                                    OnSuccessListener<List<Product>> onSuccess,
                                    OnFailureListener onFailure) {

        CollectionReference productRef = FirebaseFirestore.getInstance().collection("products");
        Query query = productRef;

        if (category != null && !category.isEmpty()) {
            query = query.whereEqualTo("category", category);
        }

        if (minPrice != null) {
            query = query.whereGreaterThanOrEqualTo("price", minPrice);
        }

        if (maxPrice != null) {
            query = query.whereLessThanOrEqualTo("price", maxPrice);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Product> results = new ArrayList<>();
            String normalizedKeyword = normalize(keyword);

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Product product = doc.toObject(Product.class);
                if (product != null) {
                    if (normalizedKeyword != null && !normalizedKeyword.isEmpty()) {
                        String normalizedName = normalize(product.getName());

                        // Tìm gần đúng: tất cả từ trong keyword đều phải xuất hiện trong tên
                        boolean matches = true;
                        for (String word : normalizedKeyword.split(" ")) {
                            if (!normalizedName.contains(word)) {
                                matches = false;
                                break;
                            }
                        }

                        if (!matches) continue;
                    }

                    product.setDocumentSnapshot(doc);
                    results.add(product);
                }
            }

            onSuccess.onSuccess(results);
        }).addOnFailureListener(onFailure);
    }


    private String normalize(String input) {
        if (input == null) return "";
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase().trim();
    }

    public void getProductById(String id, OnSuccessListener<Product> onSuccess, OnFailureListener onFailure) {
        productsRef.document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            product.setDocumentSnapshot(documentSnapshot);
                            onSuccess.onSuccess(product);
                        } else {
                            onFailure.onFailure(new Exception("Product is null"));
                        }
                    } else {
                        onFailure.onFailure(new Exception("Product not found"));
                    }
                })
                .addOnFailureListener(onFailure);
    }
}
