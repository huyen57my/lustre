package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lustre.R;

import java.util.List;

import models.SimpleOrder;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private List<SimpleOrder> orders;
    private OnTrackClickListener listener;

    public interface OnTrackClickListener {
        void onTrackClick(SimpleOrder order);
    }

    public OrderListAdapter(List<SimpleOrder> orders, OnTrackClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        SimpleOrder order = orders.get(position);
        holder.txtOrderId.setText("Order ID: #" + order.getOrderId());
        holder.txtFinalAmount.setText("Total: " + String.format("%,.0f VND", order.getFinalAmount()));
        holder.txtItemCount.setText("Items: " + order.getItemCount());

        holder.btnTrackOrder.setOnClickListener(v -> {
            if (listener != null) listener.onTrackClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtFinalAmount, txtItemCount;
        Button btnTrackOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtFinalAmount = itemView.findViewById(R.id.txtFinalAmount);
            txtItemCount = itemView.findViewById(R.id.txtItemCount);
            btnTrackOrder = itemView.findViewById(R.id.btnTrackOrder);
        }
    }

    public void updateData(List<SimpleOrder> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }
}
