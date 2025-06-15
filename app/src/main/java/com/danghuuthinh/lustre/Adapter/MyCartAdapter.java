package com.danghuuthinh.lustre.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danghuuthinh.lustre.R;
import com.danghuuthinh.lustre.models.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Product> productList;
    private ArrayList<Integer> quantities = new ArrayList<>();
    private ArrayList<Boolean> selectedStates = new ArrayList<>();

    public MyCartAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;

        for (int i = 0; i < productList.size(); i++) {
            quantities.add(1);
            selectedStates.add(false);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtSize, txtPrice, txtQuantity;
        ImageView imgProduct;
        ImageButton btnUp, btnDown;
        CheckBox chkProduct;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_product_name);
            txtSize = itemView.findViewById(R.id.txt_product_size);
            txtPrice = itemView.findViewById(R.id.txt_product_price);
            txtQuantity = itemView.findViewById(R.id.txt_quantity);
            imgProduct = itemView.findViewById(R.id.img_product);
            btnUp = itemView.findViewById(R.id.btn_up);
            btnDown = itemView.findViewById(R.id.btn_down);
            chkProduct = itemView.findViewById(R.id.chk_product);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_cart, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = productList.get(position);
        int quantity = quantities.get(position);
        boolean isChecked = selectedStates.get(position);

        holder.txtName.setText(product.getName());
        holder.txtSize.setText("Size: " + product.getSize());
        holder.txtPrice.setText(product.getPrice() + " $");
        holder.txtQuantity.setText(String.valueOf(quantity));
        holder.chkProduct.setChecked(isChecked);

        Log.d("IMAGE_URL", product.getImageLink());
        Picasso.get().load(product.getImageLink()).into(holder.imgProduct);

        holder.chkProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedStates.set(holder.getAdapterPosition(), isChecked);
                if (listener != null) {
                    listener.onProductCheckedChanged();
                }
            }
        });

        holder.btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPos = holder.getAdapterPosition();
                int newQuantity = quantities.get(currentPos) + 1;
                quantities.set(currentPos, newQuantity);
                holder.txtQuantity.setText(String.valueOf(newQuantity));
                if (selectedStates.get(currentPos) && listener != null) {
                    listener.onProductCheckedChanged();
                }
            }
        });

        holder.btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPos = holder.getAdapterPosition();
                int currentQuantity = quantities.get(currentPos);
                if (currentQuantity > 1) {
                    int newQuantity = currentQuantity - 1;
                    quantities.set(currentPos, newQuantity);
                    holder.txtQuantity.setText(String.valueOf(newQuantity));
                    if (selectedStates.get(currentPos) && listener != null) {
                        listener.onProductCheckedChanged();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface OnProductCheckedChangeListener {
        void onProductCheckedChanged();
    }

    private OnProductCheckedChangeListener listener;

    public void setOnProductCheckedChangeListener(OnProductCheckedChangeListener listener) {
        this.listener = listener;
    }

    public double calculateTotalSelectedPrice() {
        double total = 0;
        for (int i = 0; i < productList.size(); i++) {
            if (selectedStates.get(i)) {
                total += productList.get(i).getPrice() * quantities.get(i);
            }
        }
        return total;
    }

    public void setAllSelected(boolean isSelected) {
        for (int i = 0; i < selectedStates.size(); i++) {
            selectedStates.set(i, isSelected);
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onProductCheckedChanged();
        }
    }

    public boolean areAllSelected() {
        for (boolean selected : selectedStates) {
            if (!selected) return false;
        }
        return true;
    }
    public interface OnProductDeleteListener {
        void onProductDelete(Product product, int position);
    }

    private OnProductDeleteListener deleteListener;

    public void setOnProductDeleteListener(OnProductDeleteListener listener) {
        this.deleteListener = listener;
    }

}
