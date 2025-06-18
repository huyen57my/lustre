package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lustre.R;

import java.util.ArrayList;
import java.util.List;

import models.CartDisplayItem;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {
    private List<CartDisplayItem> items;
    private Context context;


    public CheckoutAdapter(List<CartDisplayItem> data) {
        this.items = data != null ? data : new ArrayList<>();
    }

    public CheckoutAdapter(List<CartDisplayItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkout_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartDisplayItem item = items.get(position);
        holder.tvName.setText(item.getProduct().getName());
        holder.tvPrice.setText("$" + item.getProduct().getPrice());
        holder.tvSize.setText("Size: " + item.getSize());

        if (item.getQuantity() > 1) {
            holder.tvQuantity.setText("x " + item.getQuantity() + " sản phẩm");
            holder.tvQuantity.setVisibility(View.VISIBLE);
        } else {
            holder.tvQuantity.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(item.getProduct().getImageUrl().get(0))
                .placeholder(R.drawable.ic_logo)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvSize, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
