package models;

public enum ShippingType {
    TIET_KIEM("Tiết kiệm (Giao sau 3-5 ngày)"),
    HOA_TOC("Hỏa tốc (Giao trong ngày)");

    private final String description;

    ShippingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
