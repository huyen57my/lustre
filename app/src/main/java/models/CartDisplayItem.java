package models;

import java.io.Serializable;
import java.util.List;

public class CartDisplayItem implements Serializable {
    private Product product;
    private int quantity;
    private String size;
    private List<String> images;

    public CartDisplayItem(Product product, int quantity, String size, List<String> images) {
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.images = images;
    }

    // getter
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public String getSize() { return size; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
