package adapter;

import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lustre.R;

import java.util.List;
import java.util.Locale;

import models.OrderItem;
import models.Product;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderViewHolder> {
    private List<OrderItem> orderItems;

    public OrderDetailAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void updateData(List<OrderItem> items) {
        this.orderItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        Product product = item.getProduct();

        // Đảm bảo luôn có giá trị ảnh hoặc log ra nếu không
        String imageUrl = null;
        if (product != null) {
            List<String> images = product.getImg();

            if (images != null && !images.isEmpty() && images.get(0) != null && !images.get(0).trim().isEmpty()) {
                imageUrl = images.get(0);
            }
        }

        if (imageUrl != null) {
            Log.d("DEBUG_GLIDE", "Load ảnh: " + imageUrl);
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_logo)
                    .into(holder.imgProduct);
        } else {
            Log.e("DEBUG_GLIDE", "Không có ảnh cho sản phẩm ở vị trí " + position);
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_logo)
                    .into(holder.imgProduct);
        }

        holder.txtProductName.setText(product != null ? product.getName() : "Null");
        holder.txtColorSize.setText("Color: " + (product != null ? product.getColor() : "??") + " | Size: " + item.getSize());
        holder.txtQuantity.setText("Quantity: " + item.getQuantity());

        double price = product != null ? product.getPrice() : 0;
        Double sale = product != null ? product.getSale() : null;

        if (sale != null && sale > 0 && sale < price) {
            holder.txtOldPrice.setPaintFlags(holder.txtOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtOldPrice.setVisibility(View.VISIBLE);
            holder.txtSalePrice.setVisibility(View.VISIBLE);
        } else {
            holder.txtOldPrice.setPaintFlags(holder.txtOldPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.txtSalePrice.setVisibility(View.GONE); // Ẩn nếu không có sale
        }
    }


    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtColorSize, txtQuantity, txtOldPrice, txtSalePrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtColorSize = itemView.findViewById(R.id.txtColorSize);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtOldPrice = itemView.findViewById(R.id.txtOldPrice);
            txtSalePrice = itemView.findViewById(R.id.txtSalePrice);
        }
    }
}
