package fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.lustre.R;
import com.example.lustre.activities.CheckoutActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adapter.MyCartAdapter;
import models.CartItem;
import models.CartDisplayItem;
import models.Product;
import models.Voucher;

public class CartFragment extends Fragment {

    private RecyclerView rvMyCart;
    private TextView txtTotalPrice;
    private CheckBox chkSelectAll;
    private Button btnBuy;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MyCartAdapter adapter;
    private Voucher appliedVoucher = null;
    private ArrayList<CartDisplayItem> cartItems = new ArrayList<>();

    private double totalPrice;

    public CartFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        rvMyCart = view.findViewById(R.id.recycler_cart);
        txtTotalPrice = view.findViewById(R.id.txt_total_price);
        chkSelectAll = view.findViewById(R.id.chk_select_all);
        btnBuy = view.findViewById(R.id.btn_buy);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        adapter = new MyCartAdapter(getContext(), new ArrayList<>());
        rvMyCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyCart.setAdapter(adapter);

        adapter.setOnProductCheckedChangeListener(() -> {
            double total = adapter.calculateTotalSelectedPrice();
            totalPrice = total;
            txtTotalPrice.setText("Tổng: " + formatVND(total));
            chkSelectAll.setChecked(adapter.areAllSelected());
        });

        adapter.setQuantityChangeListener(new MyCartAdapter.QuantityChangeListener() {
            @Override
            public void onQuantityIncreased(CartDisplayItem item, int position) {
                updateQuantityInFirestore(item);
            }

            @Override
            public void onQuantityDecreasedToZero(CartDisplayItem item, int position) {
                showRemoveBottomSheet(item, position, false);
            }
        });

        chkSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.setAllSelected(isChecked);
        });

        btnBuy.setOnClickListener(v -> showCheckoutBottomSheet());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CartDisplayItem item = adapter.getItems().get(position);
                showRemoveBottomSheet(item, position, false);
                adapter.notifyItemChanged(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(rvMyCart);

        swipeRefreshLayout.setOnRefreshListener(this::fetchCartItems);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCartItems();
    }

    private void updateQuantityInFirestore(CartDisplayItem item) {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        if (userId == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .collection("cart")
                .whereEqualTo("productId", item.getProduct().getId())
                .whereEqualTo("size", item.getSize())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            doc.getReference()
                                    .update("quantity", item.getQuantity())
                                    .addOnSuccessListener(unused ->
                                            Log.d("Cart", "Quantity updated for size " + item.getSize()))
                                    .addOnFailureListener(e ->
                                            Log.e("Cart", "Failed to update quantity: " + e.getMessage()));
                        }
                    } else {
                        Log.e("Cart", "No matching item found to update.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Cart", "Failed to find cart item: " + e.getMessage()));
    }


    private void fetchCartItems() {
        cartItems.clear();

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        if (userId == null) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cartRef = db.collection("users").document(userId).collection("cart");

        cartRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<CartItem> cartRawItems = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        CartItem cartItem = doc.toObject(CartItem.class);
                        if (cartItem != null) {
                            cartRawItems.add(cartItem);
                        }
                    }

                    fetchProductsFromCartItems(cartRawItems);
                })
                .addOnFailureListener(e -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e("Cart", "Lỗi khi lấy cart: " + e.getMessage());
                });
    }

    private void fetchProductsFromCartItems(List<CartItem> cartRawItems) {
        if (cartRawItems.isEmpty()) {
            cartItems.clear();
            adapter.updateItems(cartItems);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        cartItems.clear();

        final int[] loadedCount = {0};

        for (CartItem cartItem : cartRawItems) {
            db.collection("products")
                    .whereEqualTo("id", cartItem.getProductId())
                    .get()
                    .addOnSuccessListener(querySnapshots -> {
                        for (QueryDocumentSnapshot doc : querySnapshots) {
                            Product product = doc.toObject(Product.class);
                            CartDisplayItem item = new CartDisplayItem(product, cartItem.getQuantity(), cartItem.getSize(), product.getImageUrl());
                            cartItems.add(item);
                        }

                        loadedCount[0]++;
                        if (loadedCount[0] == cartRawItems.size()) {
                            adapter.updateItems(cartItems);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        loadedCount[0]++;
                        if (loadedCount[0] == cartRawItems.size()) {
                            adapter.updateItems(cartItems);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
        }
    }

    private void showRemoveBottomSheet(CartDisplayItem item, int position, boolean isQuantityZero)
    {
        View view = getLayoutInflater().inflate(R.layout.remove_product, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(view);

        ImageView imgProduct = view.findViewById(R.id.imgProduct);
        TextView tvProductName = view.findViewById(R.id.tvProductName);
        TextView tvProductSize = view.findViewById(R.id.tvProductSize);
        TextView tvProductPrice = view.findViewById(R.id.tvProductPrice);
        Button btnCancel = view.findViewById(R.id.btnCancelRemove);
        Button btnConfirm = view.findViewById(R.id.btnConfirmRemove);

        tvProductName.setText(item.getProduct().getName());
        tvProductSize.setText("Size: " + item.getSize());
        tvProductPrice.setText(formatVND(item.getProduct().getPrice()));

        Glide.with(requireContext())
                .load(item.getProduct().getImageUrl().get(0))
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(imgProduct);

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            if (isQuantityZero) {
                item.setQuantity(1);
                adapter.notifyItemChanged(position);
                updateQuantityInFirestore(item);
            }
        });

        btnConfirm.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            String userId = prefs.getString("user_id", null);
            if (userId == null) return;

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("cart")
                    .document(item.getProduct().getId())
                    .delete()
                    .addOnSuccessListener(unused -> {
                        adapter.removeItem(position);
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Xoá thất bại", Toast.LENGTH_SHORT).show();
                    });
        });


        dialog.show();
    }

    private void showCheckoutBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.checkout, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(view);

        EditText edtPromoCode = view.findViewById(R.id.edtPromoCode);
        Button btnApply = view.findViewById(R.id.btnApply);
        TextView txtSubtotal = view.findViewById(R.id.txtSubtotal);
        TextView txtDiscount = view.findViewById(R.id.txtDiscount);
        TextView txtTotalCost = view.findViewById(R.id.txtTotalCost);
        Button btnProceedToCheckout = view.findViewById(R.id.btnProceedToCheckout);

        final double[] subtotal = {adapter.calculateTotalSelectedPrice()};
        final double[] deliveryFee = {15000}; // 15,000₫
        final double[] discount = {0};
        final double[] totalCost = {subtotal[0] + deliveryFee[0] - discount[0]};

        txtSubtotal.setText("Tạm tính: " + formatVND(subtotal[0]));
        txtDiscount.setText("Giảm giá: -" + formatVND(discount[0]));
        txtTotalCost.setText("Tổng thanh toán: " + formatVND(totalCost[0]));

        btnApply.setOnClickListener(v -> {
            String code = edtPromoCode.getText().toString().trim().toUpperCase();
            subtotal[0] = adapter.calculateTotalSelectedPrice();
            deliveryFee[0] = 15000;
            discount[0] = 0;

            if (code.equals("DISCOUNT2")) {
                discount[0] = 2000;
            } else if (code.equals("FREESHIP")) {
                if (subtotal[0] >= 78000) {
                    discount[0] = 15000;
                } else {
                    edtPromoCode.setError("Đơn hàng phải từ 78,000₫ để dùng mã FREESHIP");
                    return;
                }
            } else {
                edtPromoCode.setError("Mã không hợp lệ");
                return;
            }

            totalCost[0] = subtotal[0] + deliveryFee[0] - discount[0];
            txtSubtotal.setText("Tạm tính: " + formatVND(subtotal[0]));
            txtDiscount.setText("Giảm giá: -" + formatVND(discount[0]));
            txtTotalCost.setText("Tổng thanh toán: " + formatVND(totalCost[0]));
        });

        btnProceedToCheckout.setOnClickListener(v -> {
            ArrayList<CartDisplayItem> selectedItems = adapter.getSelectedItems();

            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "Bạn chưa chọn sản phẩm nào", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            String userId = prefs.getString("user_id", null);
            if (userId == null) return;

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            List<Task<QuerySnapshot>> tasks = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (CartDisplayItem item : selectedItems) {
                Task<QuerySnapshot> task = db.collection("products")
                        .whereEqualTo("id", item.getProduct().getId())
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            if (!snapshot.isEmpty()) {
                                Product product = snapshot.getDocuments().get(0).toObject(Product.class);
                                if (product.getStock() < item.getQuantity()) {
                                    errors.add("Sản phẩm " + product.getName() + " chỉ còn " + product.getStock() + " sản phẩm.");
                                }
                            } else {
                                errors.add("Không tìm thấy sản phẩm: " + item.getProduct().getName());
                            }
                        })
                        .addOnFailureListener(e -> {
                            errors.add("Lỗi khi kiểm tra sản phẩm: " + item.getProduct().getName());
                        });
                tasks.add(task);
            }

            Tasks.whenAllComplete(tasks).addOnSuccessListener(results -> {
                if (!errors.isEmpty()) {
                    Toast.makeText(requireContext(), errors.get(0), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(requireContext(), CheckoutActivity.class);
                    intent.putExtra("selectedItems", selectedItems);
                    intent.putExtra("totalAmount", subtotal[0]);
                    intent.putExtra("discountAmount", discount[0]);
                    intent.putExtra("finalAmount", totalCost[0]);
                    if (appliedVoucher != null) {
                        intent.putExtra("voucherCode", appliedVoucher.getCode());
                        intent.putExtra("discountType", appliedVoucher.getDiscountType());
                    }
                    startActivity(intent);
                }
            });
        });


        edtPromoCode.setFocusable(false);
        edtPromoCode.setOnClickListener(v -> showPromoCodePopup(
                edtPromoCode, txtSubtotal, txtDiscount, txtTotalCost,
                subtotal, deliveryFee, discount, totalCost
        ));
        dialog.show();
    }

    private void showPromoCodePopup(EditText edtPromoCode, TextView txtSubtotal, TextView txtDiscount, TextView txtTotalCost,
                                    double[] subtotal, double[] deliveryFee, double[] discount, double[] totalCost) {
        View promoView = getLayoutInflater().inflate(R.layout.promo_code, null);
        BottomSheetDialog promoDialog = new BottomSheetDialog(requireContext());
        promoDialog.setContentView(promoView);

        LinearLayout container = promoView.findViewById(R.id.llPromoContainer);
        container.removeAllViews();

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        if (userId == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vouchers")
                .whereEqualTo("isActive", true)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Voucher voucher = doc.toObject(Voucher.class);
                        String usageId = voucher.getCode() + "_" + userId;

                        db.collection("voucher_usages").document(usageId).get()
                                .addOnSuccessListener(usage -> {
                                    if (!usage.exists()) {
                                        View voucherItem = LayoutInflater.from(requireContext())
                                                .inflate(R.layout.item_voucher, container, false);

                                        TextView tvCode = voucherItem.findViewById(R.id.tvVoucherCode);
                                        TextView tvTitle = voucherItem.findViewById(R.id.tvVoucherTitle);

                                        tvCode.setText(voucher.getCode());
                                        tvTitle.setText(voucher.getTitle());

                                        voucherItem.setOnClickListener(v -> {
                                            if (subtotal[0] < voucher.getMinOrderValue()) {
                                                Toast.makeText(requireContext(), "Tối thiểu " + formatVND(voucher.getMinOrderValue()), Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            appliedVoucher = voucher;
                                            edtPromoCode.setText(voucher.getCode());

                                            if (voucher.getDiscountType().equals("percentage")) {
                                                discount[0] = subtotal[0] * voucher.getDiscountValue() / 100.0;
                                                deliveryFee[0] = 15000;
                                            } else if (voucher.getDiscountType().equals("free_shipping")) {
                                                discount[0] = 0;
                                                deliveryFee[0] = 0;
                                            }

                                            totalCost[0] = subtotal[0] + deliveryFee[0] - discount[0];

                                            txtSubtotal.setText("Tạm tính: " + formatVND(subtotal[0]));
                                            txtDiscount.setText("Giảm giá: -" + formatVND(discount[0]));
                                            txtTotalCost.setText("Tổng thanh toán: " + formatVND(totalCost[0]));

                                            promoDialog.dismiss();
                                        });

                                        container.addView(voucherItem);
                                    }
                                });
                    }
                });

        promoDialog.show();
    }

    private String formatVND(double amount) {
        return String.format(Locale.US, "%,.0f₫", amount);
    }
}
