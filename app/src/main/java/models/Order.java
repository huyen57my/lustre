// models/Order.java
package models;

import java.util.List;

public class Order {
    private String id;
    private String userId;
    private List<CartDisplayItem> products;
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private String voucherCode;
    private OrderStatus status;
    private long createdAt;
    private String address;

    public Order() {}

    public Order(String id, String userId, List<CartDisplayItem> products,
                 double totalAmount, double discountAmount, double finalAmount,
                 String voucherCode, OrderStatus status, String address ,long createdAt) {
        this.id = id;
        this.userId = userId;
        this.products = products;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.voucherCode = voucherCode;
        this.status = status;
        this.createdAt = createdAt;
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartDisplayItem> getProducts() {
        return products;
    }

    public void setProducts(List<CartDisplayItem> products) {
        this.products = products;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Getters & Setters
}
