package com.example.szonyegwebshop;

public class Szonyeg {
    private String id;
    private String name;
    private String description;
    private String price;
    private float rating;
    private int image;
    private int cartedCount;

    public Szonyeg() {}

    public Szonyeg(String name, String description, String price, float rating, int image, int cartedCount) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.image = image;
        this.cartedCount = cartedCount;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getPrice() {
        return price;
    }
    public float getRating() {
        return rating;
    }
    public int getImage() {
        return image;
    }
    public String _getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getCartedCount() {
        return cartedCount;
    }
}
