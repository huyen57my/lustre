package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Product;

public class ProductDataManager {

    // Mock data theo cấu trúc Firebase của bạn
    public static List<Product> getFlashSaleProducts() {
        List<Product> products = new ArrayList<>();

        // Product 1: Chân váy dài dáng suông
        Product product1 = new Product();
        product1.setId("1760051S0159");
        product1.setName("Chân váy dài dáng suông - Đen - S0159");
        product1.setDescription("hehe");
        product1.setCare("hẹ hẹ");
        product1.setColor("Đen");
        product1.setMaterial("Kaki pha");
        product1.setImg(Arrays.asList(
                "https://product.hstatic.net/1000324561/product/dsc08162_9a056532c5d84019a28147390102622e_master.jpg",
                "https://product.hstatic.net/1000324561/product/dsc08219_2fc0c4887f394871abd20a6cf824ce96_master.jpg"
        ));
        product1.setPrice(319000);
        product1.setSale(null);
        product1.setSizes(Arrays.asList("S", "M"));
        product1.setStock(10);
        products.add(product1);

        // Product 2: Áo sơ mi trắng
        Product product2 = new Product();
        product2.setId("1760051S0160");
        product2.setName("Áo sơ mi trắng basic - Trắng - S0160");
        product2.setDescription("Áo sơ mi trắng cơ bản, phù hợp mọi dịp");
        product2.setCare("Giặt máy nước lạnh");
        product2.setColor("Trắng");
        product2.setMaterial("Cotton 100%");
        product2.setImg(Arrays.asList(
                "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=400",
                "https://images.unsplash.com/photo-1564257577817-d3c4ad6a5d60?w=400"
        ));
        product2.setPrice(450000);
        product2.setSale(360000.0); // Sale 20%
        product2.setSizes(Arrays.asList("S", "M", "L"));
        product2.setStock(15);
        products.add(product2);

        // Product 3: Quần jeans xanh
        Product product3 = new Product();
        product3.setId("1760051S0161");
        product3.setName("Quần jeans skinny - Xanh đậm - S0161");
        product3.setDescription("Quần jeans form skinny thời trang");
        product3.setCare("Giặt riêng lần đầu");
        product3.setColor("Xanh đậm");
        product3.setMaterial("Denim cotton");
        product3.setImg(Arrays.asList(
                "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400",
                "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=400"
        ));
        product3.setPrice(680000);
        product3.setSale(null);
        product3.setSizes(Arrays.asList("S", "M", "L", "XL"));
        product3.setStock(8);
        product3.setFavorite(true);
        products.add(product3);

        // Product 4: Váy hoa mùa hè
        Product product4 = new Product();
        product4.setId("1760051S0162");
        product4.setName("Váy hoa dáng A - Hồng - S0162");
        product4.setDescription("Váy hoa xinh xắn cho mùa hè");
        product4.setCare("Giặt tay nhẹ nhàng");
        product4.setColor("Hồng");
        product4.setMaterial("Chiffon");
        product4.setImg(Arrays.asList(
                "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=400",
                "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=400"
        ));
        product4.setPrice(550000);
        product4.setSale(440000.0); // Sale 20%
        product4.setSizes(Arrays.asList("S", "M"));
        product4.setStock(12);
        products.add(product4);

        // Product 5: Áo khoác denim
        Product product5 = new Product();
        product5.setId("1760051S0163");
        product5.setName("Áo khoác denim basic - Xanh - S0163");
        product5.setDescription("Áo khoác denim cổ điển");
        product5.setCare("Giặt máy nước lạnh");
        product5.setColor("Xanh");
        product5.setMaterial("Denim 100%");
        product5.setImg(Arrays.asList(
                "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400",
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400"
        ));
        product5.setPrice(780000);
        product5.setSale(null);
        product5.setSizes(Arrays.asList("S", "M", "L"));
        product5.setStock(6);
        products.add(product5);

        // Product 6: Áo thun basic
        Product product6 = new Product();
        product6.setId("1760051S0164");
        product6.setName("Áo thun basic - Đen - S0164");
        product6.setDescription("Áo thun basic đa năng");
        product6.setCare("Giặt máy bình thường");
        product6.setColor("Đen");
        product6.setMaterial("Cotton 95%, Spandex 5%");
        product6.setImg(Arrays.asList(
                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400",
                "https://images.unsplash.com/photo-1556821840-3a9fbc86339e?w=400"
        ));
        product6.setPrice(250000);
        product6.setSale(200000.0); // Sale 20%
        product6.setSizes(Arrays.asList("S", "M", "L", "XL"));
        product6.setStock(20);
        products.add(product6);

        return products;
    }

    // Method để get products by category
    public static List<Product> getProductsByCategory(String category) {
        List<Product> allProducts = getFlashSaleProducts();
        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    // Method để get products by color
    public static List<Product> getProductsByColor(String color) {
        List<Product> allProducts = getFlashSaleProducts();
        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getColor().equalsIgnoreCase(color)) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    // Method để get sale products
    public static List<Product> getSaleProducts() {
        List<Product> allProducts = getFlashSaleProducts();
        List<Product> saleProducts = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.isOnSale()) {
                saleProducts.add(product);
            }
        }

        return saleProducts;
    }

    // Method để search products
    public static List<Product> searchProducts(String keyword) {
        List<Product> allProducts = getFlashSaleProducts();
        List<Product> searchResults = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(lowerKeyword) ||
                    product.getColor().toLowerCase().contains(lowerKeyword) ||
                    product.getMaterial().toLowerCase().contains(lowerKeyword) ||
                    product.getDescription().toLowerCase().contains(lowerKeyword)) {
                searchResults.add(product);
            }
        }

        return searchResults;
    }

    // Interface cho Firebase callback
    public interface ProductDataCallback {
        void onSuccess(List<Product> products);
        void onError(String error);
    }

    // Method template cho Firebase - sẵn sàng để implement
    public static void fetchProductsFromFirebase(ProductDataCallback callback) {
        // TODO: Implement Firebase fetching
        // FirebaseFirestore db = FirebaseFirestore.getInstance();
        // db.collection("products")
        //   .get()
        //   .addOnCompleteListener(task -> {
        //       if (task.isSuccessful()) {
        //           List<Product> products = new ArrayList<>();
        //           for (QueryDocumentSnapshot document : task.getResult()) {
        //               Product product = document.toObject(Product.class);
        //               products.add(product);
        //           }
        //           callback.onSuccess(products);
        //       } else {
        //           callback.onError(task.getException().getMessage());
        //       }
        //   });

        // Hiện tại return mock data
        callback.onSuccess(getFlashSaleProducts());
    }

    // Method để fetch single product by ID
    public static void fetchProductById(String productId, ProductDataCallback callback) {
        // TODO: Implement Firebase single product fetch
        // Hiện tại tìm trong mock data
        List<Product> allProducts = getFlashSaleProducts();
        for (Product product : allProducts) {
            if (product.getId().equals(productId)) {
                List<Product> result = new ArrayList<>();
                result.add(product);
                callback.onSuccess(result);
                return;
            }
        }
        callback.onError("Product not found");
    }
}