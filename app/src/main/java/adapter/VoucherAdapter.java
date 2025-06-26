package adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lustre.R;

import java.util.List;

import models.Voucher;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    private final List<Voucher> voucherList;
    private final Context context;

    public VoucherAdapter(List<Voucher> voucherList, Context context) {
        this.voucherList = voucherList;
        this.context = context;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.voucher_item, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.tvCode.setText(voucher.getCode());
        holder.tvCondition.setText(voucher.getDescription());

        if ("percentage".equals(voucher.getDiscountType())) {
            holder.tvDiscount.setText("Get " + voucher.getDiscountValue() + "% off");
        } else {
            holder.tvDiscount.setText("Free shipping");
        }

        holder.btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("voucher_code", voucher.getCode());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(context, "Copied: " + voucher.getCode(), Toast.LENGTH_SHORT).show();

            SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String userId = prefs.getString("user_id", "");
            String key = "voucher_used_" + userId + "_" + voucher.getCode();
            prefs.edit().putBoolean(key, true).apply();
        });
    }


    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvCondition, tvDiscount, btnCopy;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tv_voucher_code);
            tvCondition = itemView.findViewById(R.id.tv_voucher_condition);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            btnCopy = itemView.findViewById(R.id.btn_copy_code);
        }
    }
}
