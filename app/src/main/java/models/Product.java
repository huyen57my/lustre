package models;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String id;
    private String name;
    private String description;
    private String care;
    private String color;
    private String material;
    private List<String> img;
    private double price;
    private Double sale; // Nullable - có thể null
    private List<String> sizes;
    private int stock;
    private boolean isFavorite;
    public Product() {
        this.isFavorite = false;
    }

    // Constructor đầy đủ
    public Product(String id, String name, String description, String care, String color,
                   String material, List<String> img, double price, Double sale,
                   List<String> sizes, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.care = care;
        this.color = color;
        this.material = material;
        this.img = img;
        this.price = price;
        this.sale = sale;
        this.sizes = sizes;
        this.stock = stock;
        this.isFavorite = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCare() {
        return care;
    }

    public void setCare(String care) {
        this.care = care;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public List<String> getImg() {
        return img;
    }

    public void setImg(List<String> img) {
        this.img = img;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Double getSale() {
        return sale;
    }

    public void setSale(Double sale) {
        this.sale = sale;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // Helper methods
    public String getMainImageUrl() {
        return (img != null && !img.isEmpty()) ? img.get(0) : "";
    }

    public String getFormattedPrice() {
        return String.format("%.0f VND", price);
    }

    public String getFormattedSalePrice() {
        if (sale != null && sale > 0) {
            return String.format("%.0f VND", sale);
        }
        return null;
    }

    public boolean isOnSale() {
        return sale != null && sale > 0 && sale < price;
    }

    public int getDiscountPercentage() {
        if (isOnSale()) {
            return (int) (((price - sale) / price) * 100);
        }
        return 0;
    }

    public String getSizesString() {
        if (sizes != null && !sizes.isEmpty()) {
            return String.join(", ", sizes);
        }
        return "N/A";
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public String getStockStatus() {
        if (stock > 10) {
            return "Còn hàng";
        } else if (stock > 0) {
            return "Sắp hết hàng";
        } else {
            return "Hết hàng";
        }
    }

    public List<String> getImageUrl() {
        return this.img;
    }

    public String getCategory() {
        String lowerName = name.toLowerCase();
        if (lowerName.contains("váy")) {
            return "dress";
        } else if (lowerName.contains("áo")) {
            return "shirt";
        } else if (lowerName.contains("quần")) {
            return "pant";
        }
        return "other";
    }

    @Exclude
    private DocumentSnapshot documentSnapshot;

    @Exclude
    public DocumentSnapshot getDocumentSnapshot() {
        return documentSnapshot;
    }

    @Exclude
    public void setDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        this.documentSnapshot = documentSnapshot;
    }

}