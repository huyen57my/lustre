package adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lustre.R;
import com.example.lustre.activities.OrderDetailActivity;

import java.util.List;
import java.util.Locale;

import models.Order;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false); // đúng layout rồi
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.txtOrderId.setText("Mã đơn: " + order.getId());
        holder.txtFinalAmount.setText(String.format(Locale.getDefault(), "Tổng: %,.0f VND", order.getFinalAmount()));
        holder.txtItemCount.setText("Số lượng: " + order.getTotalQuantity());

        holder.btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), OrderDetailActivity.class);
            intent.putExtra("order_id", order.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
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
}
