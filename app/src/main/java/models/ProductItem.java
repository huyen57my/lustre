package models;

public class ProductItem {
    private int quantity;
    private Product product;

    public ProductItem() {}

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}