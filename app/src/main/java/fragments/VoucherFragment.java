package fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.example.lustre.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import adapter.VoucherAdapter;
import models.Voucher;

public class VoucherFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;

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

        db = FirebaseFirestore.getInstance();

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId != null) {
            fetchAvailableVouchers(userId);
        } else {
            Toast.makeText(getContext(), "User ID not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAvailableVouchers(String userId) {
        db.collection("vouchers")
                .whereEqualTo("isActive", true)
                .get()
                .addOnSuccessListener(voucherSnapshots -> {
                    List<Voucher> availableVouchers = new ArrayList<>();
                    List<Task<DocumentSnapshot>> usageTasks = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : voucherSnapshots) {
                        Voucher voucher = doc.toObject(Voucher.class);
                        String docId = voucher.getCode() + "_" + userId;

                        Task<DocumentSnapshot> usageTask = db.collection("voucher_usages")
                                .document(docId)
                                .get()
                                .addOnSuccessListener(usageDoc -> {
                                    if (!usageDoc.exists()) {
                                        availableVouchers.add(voucher);
                                    }
                                });

                        usageTasks.add(usageTask);
                    }

                    Tasks.whenAllSuccess(usageTasks).addOnSuccessListener(results -> {
                        VoucherAdapter adapter = new VoucherAdapter(availableVouchers, getContext());
                        recyclerView.setAdapter(adapter);
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch vouchers", Toast.LENGTH_SHORT).show());
    }
}
