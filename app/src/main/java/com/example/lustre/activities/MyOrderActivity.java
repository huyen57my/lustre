package com.example.lustre.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lustre.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import adapter.OrderPagerAdapter;
import fragments.OrderListFragment;

public class MyOrderActivity extends AppCompatActivity {

        private TabLayout tabLayout;
        private ViewPager2 viewPager;
        private OrderPagerAdapter adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_my_order);

            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.viewPager);

            adapter = new OrderPagerAdapter(this);
            viewPager.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> {
                        switch (position) {
                            case 0:
                                tab.setText("Active");
                                break;
                            case 1:
                                tab.setText("Completed");
                                break;
                            case 2:
                                tab.setText("Cancelled");
                                break;
                        }
                    }).attach();
        }

    @Override
    protected void onResume() {
        super.onResume();

        int currentItem = viewPager.getCurrentItem();
        String tag = "f" + currentItem;
        OrderListFragment fragment = (OrderListFragment)
                getSupportFragmentManager().findFragmentByTag("f" + currentItem);

        if (fragment != null) {
            fragment.refresh();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("goToCancelledTab", false)) {
            viewPager.setCurrentItem(2, true);
        }
    }
}