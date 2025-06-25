package models;
import java.io.Serializable;
import java.util.List;

public class OrderItem implements Serializable {

    private Product product;
    private int quantity;
    private String size;
    private List<String> images;

    public OrderItem() {
    }

    public OrderItem(Product product, int quantity, String size, List<String> images) {
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.images = images;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
