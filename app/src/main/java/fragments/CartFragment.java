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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import adapter.MyCartAdapter;
import models.CartItem;
import models.CartDisplayItem;
import models.Product;

public class CartFragment extends Fragment {

    private RecyclerView rvMyCart;
    private TextView txtTotalPrice;
    private CheckBox chkSelectAll;
    private Button btnBuy;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MyCartAdapter adapter;
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
            txtTotalPrice.setText("Total: " + total + " $");
            chkSelectAll.setChecked(adapter.areAllSelected());
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
                showRemoveBottomSheet(item, position);
                adapter.notifyItemChanged(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(rvMyCart);

        swipeRefreshLayout.setOnRefreshListener(() -> fetchCartItems());

        fetchCartItems();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCartItems();
    }

    private void fetchCartItems() {
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
            Log.d("Cart", "Không có item nào trong giỏ hàng.");
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
                        Log.e("Cart", "Lỗi khi fetch sản phẩm: " + e.getMessage());
                        if (loadedCount[0] == cartRawItems.size()) {
                            adapter.updateItems(cartItems);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
        }
    }

    private void showRemoveBottomSheet(CartDisplayItem item, int position) {
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
        tvProductPrice.setText(item.getProduct().getPrice() + " $");

        Glide.with(requireContext())
                .load(item.getProduct().getImageUrl().get(0))
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(imgProduct);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            adapter.removeItem(position);
            dialog.dismiss();
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
        TextView txtDeliveryFee = view.findViewById(R.id.txtDeliveryFee);
        TextView txtDiscount = view.findViewById(R.id.txtDiscount);
        TextView txtTotalCost = view.findViewById(R.id.txtTotalCost);
        Button btnProceedToCheckout = view.findViewById(R.id.btnProceedToCheckout);

        final double[] subtotal = {adapter.calculateTotalSelectedPrice()};
        final double[] deliveryFee = {5};
        final double[] discount = {0};
        final double[] totalCost = {subtotal[0] + deliveryFee[0] - discount[0]};

        txtSubtotal.setText(String.format("Subtotal: $%.2f", subtotal[0]));
        txtDeliveryFee.setText(String.format("Delivery Fee: $%.2f", deliveryFee[0]));
        txtDiscount.setText(String.format("Discount: -$%.2f", discount[0]));
        txtTotalCost.setText(String.format("Total Cost: $%.2f", totalCost[0]));

        btnApply.setOnClickListener(v -> {
            String code = edtPromoCode.getText().toString().trim().toUpperCase();
            subtotal[0] = adapter.calculateTotalSelectedPrice();
            deliveryFee[0] = 5;
            discount[0] = 0;

            if (code.equals("DISCOUNT2")) {
                discount[0] = 2;
            } else if (code.equals("FREESHIP")) {
                if (subtotal[0] >= 78) {
                    discount[0] = 5;
                } else {
                    edtPromoCode.setError("Order must be at least $78 to use FREESHIP");
                    return;
                }
            } else {
                edtPromoCode.setError("Invalid promo code");
                return;
            }

            totalCost[0] = subtotal[0] + deliveryFee[0] - discount[0];
            txtSubtotal.setText(String.format("Subtotal: $%.2f", subtotal[0]));
            txtDeliveryFee.setText(String.format("Delivery Fee: $%.2f", deliveryFee[0]));
            txtDiscount.setText(String.format("Discount: -$%.2f", discount[0]));
            txtTotalCost.setText(String.format("Total Cost: $%.2f", totalCost[0]));
        });

        btnProceedToCheckout.setOnClickListener(v -> {
            ArrayList<CartDisplayItem> selectedItems = adapter.getSelectedItems();

            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "Bạn chưa chọn sản phẩm nào", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(requireContext(), CheckoutActivity.class);
            intent.putExtra("selectedItems", selectedItems);
            intent.putExtra("totalAmount", subtotal[0]);        // tổng sản phẩm
            intent.putExtra("discountAmount", discount[0]);     // giảm giá
            intent.putExtra("finalAmount", totalCost[0]);       // tổng thanh toán
            startActivity(intent);
        });

        edtPromoCode.setFocusable(false);
        edtPromoCode.setOnClickListener(v -> showPromoCodePopup(edtPromoCode));

        dialog.show();
    }

    private void showPromoCodePopup(EditText edtPromoCode) {
        View promoView = getLayoutInflater().inflate(R.layout.promo_code, null);
        TextView tvFreeship = promoView.findViewById(R.id.tvPromoFreeship);
        TextView tvDiscount10 = promoView.findViewById(R.id.tvPromoDiscount10);

        BottomSheetDialog promoDialog = new BottomSheetDialog(requireContext());
        promoDialog.setContentView(promoView);

        tvFreeship.setOnClickListener(v -> {
            edtPromoCode.setText("FREESHIP");
            promoDialog.dismiss();
        });

        tvDiscount10.setOnClickListener(v -> {
            edtPromoCode.setText("DISCOUNT2");
            promoDialog.dismiss();
        });

        promoDialog.show();
    }
}
