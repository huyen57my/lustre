package models;

public class SimpleOrder {
    private String orderId;
    private double finalAmount;
    private int itemCount;

    public SimpleOrder(String orderId, double finalAmount, int itemCount) {
        this.orderId = orderId;
        this.finalAmount = finalAmount;
        this.itemCount = itemCount;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public int getItemCount() {
        return itemCount;
    }
}
