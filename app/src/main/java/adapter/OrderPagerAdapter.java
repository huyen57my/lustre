package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fragments.OrderListFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String status;
        switch (position) {
            case 0:
                status = "ORDER_PLACED";
                break;
            case 1:
                status = "SHIPPED";
                break;
            case 2:
                status = "CANCELED";
                break;
            default:
                status = "ORDER_PLACED";
        }
        return OrderListFragment.newInstance(status);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

