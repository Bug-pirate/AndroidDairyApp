package com.example.undedairyproduct;

import android.net.Uri;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String name;
    private int price;
    private Uri imageUri;
    private List<String> weights;
    private List<String> prices;

    // Constructor
    public Product(String name, int price, Uri imageUri, List<String> weights, List<String> prices) {
        this.name = name;
        this.price = price;
        this.imageUri = imageUri;
        this.weights = weights;
        this.prices = prices;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public List<String> getWeights() {
        return weights;
    }

    public void setWeights(List<String> weights) {
        this.weights = weights;
    }

    public List<String> getPrices() {
        return prices;
    }

    public void setPrices(List<String> prices) {
        this.prices = prices;
    }
}
