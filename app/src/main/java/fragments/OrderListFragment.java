package fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lustre.R;
import com.example.lustre.activities.OrderDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapter.OrderListAdapter;
import models.SimpleOrder;

public class OrderListFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String status;

    private RecyclerView recyclerView;
    private OrderListAdapter adapter;
    private List<SimpleOrder> orders = new ArrayList<>();

    public static OrderListFragment newInstance(String status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderListAdapter(orders, order -> {
            Intent intent = new Intent(requireContext(), OrderDetailActivity.class);
            intent.putExtra("order_id", order.getOrderId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        fetchOrders();
    }

    public void refresh() {
        fetchOrders();
    }
    private void fetchOrders() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(getContext(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", status)
                .get()
                .addOnSuccessListener(query -> {
                    List<SimpleOrder> list = new ArrayList<>();
                    for (var doc : query.getDocuments()) {
                        Map<String, Object> map = doc.getData();
                        if (map == null) continue;

                        double finalAmount = map.get("finalAmount") instanceof Number
                                ? ((Number) map.get("finalAmount")).doubleValue() : 0;

                        List<?> products = (List<?>) map.get("products");
                        int itemCount = products != null ? products.size() : 0;

                        list.add(new SimpleOrder(doc.getId(), finalAmount, itemCount));
                    }

                    orders.clear();
                    orders.addAll(list);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
