package models;

public class Voucher {
    private String code;
    private String title;
    private String description;
    private String discountType; // "percentage" | "free_shipping"
    private int discountValue;   // 0 nếu free_shipping
    private int minOrderValue;
    private boolean isActive;

    public Voucher() {}

    public Voucher(String code, String title, String description, String discountType, int discountValue, int minOrderValue, boolean isActive) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderValue = minOrderValue;
        this.isActive = isActive;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDiscountType() {
        return discountType;
    }

    public int getDiscountValue() {
        return discountValue;
    }

    public int getMinOrderValue() {
        return minOrderValue;
    }

    public boolean isActive() {
        return isActive;
    }
}
