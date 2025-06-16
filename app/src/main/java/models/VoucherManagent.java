package models;

import java.util.ArrayList;
import java.util.List;


public class VoucherManagent {

    public static List<Voucher> getMockVouchers() {
        List<Voucher> list = new ArrayList<>();
        list.add(new Voucher("WELCOME200", "Add items worth $2 more to unlock", "Get 50% OFF"));
        list.add(new Voucher("NEWBIE10", "Spend at least $10", "Get 10% OFF"));
        list.add(new Voucher("FREESHIP", "Min order $25", "Free Shipping"));
        return list;
    }

    // TODO: Later add Firebase methods here (getAllVouchers, addVoucher, deleteVoucher, etc.)
}
