package com.example.undedairyproduct;

import android.net.Uri;
import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private int price;
    private Uri imageUri;

    public Product(String name, int price, Uri imageUri) {
        this.name = name;
        this.price = price;
        this.imageUri = imageUri;
    }

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
}
