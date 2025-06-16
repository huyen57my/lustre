package models;

public class Voucher {
    private String code;
    private String condition;
    private String discount;

    public Voucher() {
    }

    public Voucher(String code, String condition, String discount) {
        this.code = code;
        this.condition = condition;
        this.discount = discount;
    }

    public String getCode() {
        return code;
    }

    public String getCondition() {
        return condition;
    }

    public String getDiscount() {
        return discount;
    }
}
