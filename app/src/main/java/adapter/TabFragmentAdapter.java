package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fragments.CartFragment;
import fragments.HomeFragment;
import fragments.ProfileFragment;
import fragments.VoucherFragment;
import fragments.WishlistFragment;

public class TabFragmentAdapter extends FragmentStateAdapter {

    public TabFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new CartFragment();
            case 2:
                return new WishlistFragment();
            case 3:
                return new VoucherFragment();
            case 4:
                return new ProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}