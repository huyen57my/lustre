package com.danghuuthinh.lustre.K204060309Danghuuthinh;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danghuuthinh.lustre.Adapter.MyCartAdapter;
import com.danghuuthinh.lustre.Connector.SQLiteDatabaseConnector;
import com.danghuuthinh.lustre.R;
import com.danghuuthinh.lustre.models.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    RecyclerView rvMyCart;
    SQLiteDatabaseConnector connector;
    TextView txtTotalPrice;
    CheckBox chkSelectAll;
    Button btnBuy;

    MyCartAdapter adapter;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvMyCart = findViewById(R.id.rv_mycart);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        chkSelectAll = findViewById(R.id.chk_select_all);
        btnBuy = findViewById(R.id.btn_buy);

        connector = new SQLiteDatabaseConnector(this);
        products = connector.getAllProducts();

        adapter = new MyCartAdapter(this, products);
        rvMyCart.setLayoutManager(new LinearLayoutManager(this));
        rvMyCart.setAdapter(adapter);


        adapter.setOnProductCheckedChangeListener(new MyCartAdapter.OnProductCheckedChangeListener() {
            @Override
            public void onProductCheckedChanged() {
                double total = adapter.calculateTotalSelectedPrice();
                txtTotalPrice.setText("Total: " + total + " $");
                chkSelectAll.setChecked(adapter.areAllSelected());
            }
        });

        chkSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.setAllSelected(isChecked);
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckoutBottomSheet();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Product product = products.get(position);
                showRemoveBottomSheet(product, position);
                adapter.notifyItemChanged(position); // để reset swipe
            }
        });
        itemTouchHelper.attachToRecyclerView(rvMyCart);

    }

    private void showRemoveBottomSheet(Product product, int position) {
        View view = getLayoutInflater().inflate(R.layout.remove_product, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        ImageView imgProduct = view.findViewById(R.id.imgProduct);
        TextView tvProductName = view.findViewById(R.id.tvProductName);
        TextView tvProductSize = view.findViewById(R.id.tvProductSize);
        TextView tvProductPrice = view.findViewById(R.id.tvProductPrice);
        Button btnCancel = view.findViewById(R.id.btnCancelRemove);
        Button btnConfirm = view.findViewById(R.id.btnConfirmRemove);

        // Hiển thị thông tin sản phẩm
        tvProductName.setText(product.getName());
        tvProductSize.setText("Size: " + product.getSize());
        tvProductPrice.setText(product.getPrice() + " $");

        // Dùng Picasso để load ảnh
        Picasso.get().load(product.getImageLink()).into(imgProduct);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xoá khỏi database
                connector.deleteProductById(product.getId());

                // Xoá khỏi danh sách hiển thị
                products.remove(position);
                adapter.notifyItemRemoved(position);

                // Cập nhật lại tổng tiền
                txtTotalPrice.setText("Total: " + adapter.calculateTotalSelectedPrice() + " $");

                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void showCheckoutBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.checkout, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
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

        // Hiển thị ban đầu
        txtSubtotal.setText(String.format("Subtotal: $%.2f", subtotal[0]));
        txtDeliveryFee.setText(String.format("Delivery Fee: $%.2f", deliveryFee[0]));
        txtDiscount.setText(String.format("Discount: -$%.2f", discount[0]));
        txtTotalCost.setText(String.format("Total Cost: $%.2f", totalCost[0]));

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = edtPromoCode.getText().toString().trim().toUpperCase();

                //  Tính lại subtotal trước khi áp mã giảm giá
                subtotal[0] = adapter.calculateTotalSelectedPrice();
                deliveryFee[0] = 5;
                discount[0] = 0;

                if (code.equals("DISCOUNT2")) {
                    discount[0] =2 ;
                } else if (code.equals("FREESHIP")) {
                    if (subtotal[0] >= 78) {
                        discount[0] = 5;
                    } else {
                        edtPromoCode.setError("Order must be at least $78 to use FREESHIP");
                        return; // dừng tại đây nếu không đủ điều kiện
                    }
                } else {
                    edtPromoCode.setError("Invalid promo code");
                    return;
                }

                totalCost[0] = subtotal[0] + deliveryFee[0] - discount[0];

                //Cập nhật lại tất cả hiển thị
                txtSubtotal.setText(String.format("Subtotal: $%.2f", subtotal[0]));
                txtDeliveryFee.setText(String.format("Delivery Fee: $%.2f", deliveryFee[0]));
                txtDiscount.setText(String.format("Discount: -$%.2f", discount[0]));
                txtTotalCost.setText(String.format("Total Cost: $%.2f", totalCost[0]));
            }
        });


        btnProceedToCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtPromoCode.setFocusable(false); // ngăn bàn phím
        edtPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPromoCodePopup(edtPromoCode);
            }
        });

        dialog.show();
    }

    private void showPromoCodePopup(EditText edtPromoCode) {
        View promoView = getLayoutInflater().inflate(R.layout.promo_code, null);
        TextView tvFreeship = promoView.findViewById(R.id.tvPromoFreeship);
        TextView tvDiscount10 = promoView.findViewById(R.id.tvPromoDiscount10);

        BottomSheetDialog promoDialog = new BottomSheetDialog(this);
        promoDialog.setContentView(promoView);

        tvFreeship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPromoCode.setText("FREESHIP");
                promoDialog.dismiss();
            }
        });

        tvDiscount10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPromoCode.setText("DISCOUNT2");
                promoDialog.dismiss();
            }
        });

        promoDialog.show();
    }

}
