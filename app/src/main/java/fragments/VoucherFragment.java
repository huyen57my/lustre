package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lustre.R;

import java.util.List;

import adapters.VoucherAdapter;
import models.Voucher;
import models.VoucherManagent;

public class VoucherFragment extends Fragment {

    private RecyclerView recyclerView;

    public VoucherFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText("Vouchers");
        }

        recyclerView = view.findViewById(R.id.rv_vouchers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Voucher> voucherList = VoucherManagent.getMockVouchers();
        VoucherAdapter adapter = new VoucherAdapter(voucherList, getContext());
        recyclerView.setAdapter(adapter);
    }
}
