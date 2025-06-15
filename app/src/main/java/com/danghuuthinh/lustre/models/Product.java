package com.danghuuthinh.lustre.models;

public class Product {

    private int Id;
    private String Name;
    private String Size;
    private double Price;
    private String ImageLink;

    public Product(int id, String name, String size, double price, String imageLink) {
        Id = id;
        Name = name;
        Size = size;
        Price = price;
        ImageLink = imageLink;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }
}
