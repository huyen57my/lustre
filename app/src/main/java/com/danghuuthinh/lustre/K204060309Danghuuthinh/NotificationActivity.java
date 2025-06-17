package com.danghuuthinh.lustre.K204060309Danghuuthinh;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danghuuthinh.lustre.R;
import com.danghuuthinh.lustre.Adapter.NotificationAdapter;
import com.danghuuthinh.lustre.models.NotificationItem;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationRecyclerView;
    private NotificationAdapter adapter;
    private List<NotificationItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification); // đảm bảo activity_main.xml có RecyclerView đúng ID

        notificationRecyclerView = findViewById(R.id.rvnotification);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo dữ liệu mẫu
        itemList = new ArrayList<>();

        itemList.add(new NotificationItem("TODAY")); // Section Header
        itemList.add(new NotificationItem("Order Shipped", "Your order #1234 has been shipped.", "1h", R.mipmap.ic_shipped));
        itemList.add(new NotificationItem("Payment Received", "We’ve received your payment.", "3h", R.mipmap.ic_payment));

        itemList.add(new NotificationItem("YESTERDAY")); // Section Header
        itemList.add(new NotificationItem("Promo Alert", "Flash sale is live now!", "20h", R.mipmap.ic_sales));

        adapter = new NotificationAdapter(itemList);
        notificationRecyclerView.setAdapter(adapter);
    }
}
