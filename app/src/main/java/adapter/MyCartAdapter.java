package adapter;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.example.lustre.R;

import java.util.ArrayList;
import java.util.List;

import models.CartDisplayItem;
import models.Product;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<CartDisplayItem> items = new ArrayList<>();
    private ArrayList<Boolean> selectedStates = new ArrayList<>();
    private OnProductCheckedChangeListener listener;

    public MyCartAdapter(Context context, ArrayList<CartDisplayItem> items) {
        this.context = context;
        updateItems(items);
    }

    public interface QuantityChangeListener {
        void onQuantityIncreased(CartDisplayItem item, int position);
        void onQuantityDecreasedToZero(CartDisplayItem item, int position);
    }

    private QuantityChangeListener quantityChangeListener;

    public void setQuantityChangeListener(QuantityChangeListener listener) {
        this.quantityChangeListener = listener;
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
        CartDisplayItem item = items.get(position);
        Product product = item.getProduct();

        holder.txtName.setText(product.getName());
        holder.txtSize.setText("Size: " + item.getSize());
        if (product.getSale() != null && product.getSale() > 0 ) {
            holder.txtPrice.setText(formatVND(product.getSale()));
        } else {
            holder.txtPrice.setText(formatVND(product.getPrice()));
        }
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));

        List<String> imageList = product.getImageUrl();
        if (imageList != null && !imageList.isEmpty()) {
            Glide.with(context)
                    .load(imageList.get(0))
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(holder.imgProduct);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_logo)
                    .into(holder.imgProduct);
        }

        holder.chkProduct.setOnCheckedChangeListener(null);
        holder.chkProduct.setChecked(selectedStates.get(position));

        holder.chkProduct.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            selectedStates.set(position, isChecked);
            if (listener != null) listener.onProductCheckedChanged();
        });

        holder.btnUp.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            item.setQuantity(newQty);
            notifyItemChanged(position);

            if (quantityChangeListener != null) {
                quantityChangeListener.onQuantityIncreased(item, position);
            }

            if (selectedStates.get(position) && listener != null) {
                listener.onProductCheckedChanged();
            }
        });

        holder.btnDown.setOnClickListener(v -> {
            int currQty = item.getQuantity();
            if (currQty > 1) {
                item.setQuantity(currQty - 1);
                notifyItemChanged(position);

                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityIncreased(item, position);
                }

                if (selectedStates.get(position) && listener != null) {
                    listener.onProductCheckedChanged();
                }
            } else {
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityDecreasedToZero(item, position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnProductCheckedChangeListener {
        void onProductCheckedChanged();
    }

    public ArrayList<CartDisplayItem> getSelectedItems() {
        ArrayList<CartDisplayItem> selectedItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (selectedStates.get(i)) {
                selectedItems.add(items.get(i));
            }
        }
        return selectedItems;
    }

    private String formatVND(double amount) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        return formatter.format(amount) + " VND";
    }

    public void setOnProductCheckedChangeListener(OnProductCheckedChangeListener listener) {
        this.listener = listener;
    }

    public double calculateTotalSelectedPrice() {
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            if (selectedStates.get(i)) {
                Product p = items.get(i).getProduct();
                double itemPrice = p.getSale() > 0 ? p.getSale() : p.getPrice();
                total += itemPrice * items.get(i).getQuantity();
            }
        }
        return total;
    }

    public void setAllSelected(boolean isSelected) {
        for (int i = 0; i < selectedStates.size(); i++) {
            selectedStates.set(i, isSelected);
        }
        notifyDataSetChanged();
        if (listener != null) listener.onProductCheckedChanged();
    }

    public boolean areAllSelected() {
        for (boolean state : selectedStates) {
            if (!state) return false;
        }
        return true;
    }

    public void updateItems(List<CartDisplayItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        selectedStates.clear();
        for (int i = 0; i < newItems.size(); i++) {
            selectedStates.add(false);
        }
        notifyDataSetChanged();
        if (listener != null) listener.onProductCheckedChanged();
    }

    public void removeItem(int position) {
        items.remove(position);
        selectedStates.remove(position);
        notifyItemRemoved(position);
        if (listener != null) listener.onProductCheckedChanged();
    }

    public ArrayList<CartDisplayItem> getItems() {
        return items;
    }
}
