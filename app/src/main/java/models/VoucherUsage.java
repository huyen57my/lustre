package models;

public class VoucherUsage {
    private String userId;
    private String voucherCode;

    public VoucherUsage() {
    }

    public VoucherUsage(String userId, String voucherCode) {
        this.userId = userId;
        this.voucherCode = voucherCode;
    }

    public String getUserId() {
        return userId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }
}
