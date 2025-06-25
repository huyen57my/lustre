package fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lustre.R;
import com.example.lustre.activities.HelpCenterActivity;
import com.example.lustre.activities.MyOrderActivity;
import com.example.lustre.activities.PaymentMethodActivity;
import com.example.lustre.activities.PrivacyPolicyActivity;
import com.example.lustre.activities.SettingsActivity;
import com.example.lustre.activities.WelcomeActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        LinearLayout itemSettings = view.findViewById(R.id.itemSettings);
        LinearLayout itemPayment = view.findViewById(R.id.itemPayment);
        LinearLayout itemPrivacy = view.findViewById(R.id.itemPrivacy);
        LinearLayout itemLogout = view.findViewById(R.id.itemLogout);
        LinearLayout itemHelpCenter = view.findViewById(R.id.itemHelpCenter);
        LinearLayout itemMyOrder = view.findViewById(R.id.profile_tab_my_order);

        itemMyOrder.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyOrderActivity.class);
            startActivity(intent);
        });

        itemSettings.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SettingsActivity.class);
            startActivity(intent);
        });

        itemPayment.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PaymentMethodActivity.class);
            startActivity(intent);
        });

        itemPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PrivacyPolicyActivity.class);
            startActivity(intent);
        });

        itemHelpCenter.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), HelpCenterActivity.class);
            startActivity(intent);
        });

        itemLogout.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    private void showLogoutDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.activity_log_out, null);

        Button btnYes = bottomSheetView.findViewById(R.id.btnConfirmLogout);
        Button btnCancel = bottomSheetView.findViewById(R.id.btnCancelRemove);

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnYes.setOnClickListener(v -> {
            SharedPreferences prefs = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa hết các activity trước đó
            startActivity(intent);
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}
