package com.example.undedairyproduct;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {

    private RecyclerView productsRecyclerView;
    private ArrayList<Product> productList;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        loadProducts();

        productAdapter = new ProductAdapter(this, productList);
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        productList.add(new Product("Ghee", 450, Uri.parse("android.resource://com.example.undedairyproduct/drawable/ghee")));
        productList.add(new Product("Dahi", 50, Uri.parse("android.resource://com.example.undedairyproduct/drawable/dahi")));
        productList.add(new Product("Basundi", 120, Uri.parse("android.resource://com.example.undedairyproduct/drawable/basundi")));
        productList.add(new Product("Mango Lassi", 60, Uri.parse("android.resource://com.example.undedairyproduct/drawable/mango_lassi")));
        productList.add(new Product("Fruits Lassi", 70, Uri.parse("android.resource://com.example.undedairyproduct/drawable/fruits_lassi")));
    }
}
