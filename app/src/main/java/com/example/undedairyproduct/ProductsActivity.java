package com.example.undedairyproduct;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private RecyclerView productsRecyclerView;
    private ArrayList<Product> productList;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        // Initialize the RecyclerView
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the product list
        productList = new ArrayList<>();
        loadProducts();

        // Set up the ProductAdapter
        productAdapter = new ProductAdapter(this, productList);
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        // Create PriceWeightCombination objects
        List<PriceWeightCombination> gheeCombinations = new ArrayList<>();
        gheeCombinations.add(new PriceWeightCombination("1kg", "450"));

        List<PriceWeightCombination> dahiCombinations = new ArrayList<>();
        dahiCombinations.add(new PriceWeightCombination("500gm", "50"));

        List<PriceWeightCombination> basundiCombinations = new ArrayList<>();
        basundiCombinations.add(new PriceWeightCombination("500gm", "120"));

        List<PriceWeightCombination> mangoLassiCombinations = new ArrayList<>();
        mangoLassiCombinations.add(new PriceWeightCombination("300ml", "60"));

        List<PriceWeightCombination> fruitsLassiCombinations = new ArrayList<>();
        fruitsLassiCombinations.add(new PriceWeightCombination("300ml", "70"));

        // Create and add products to the list
        productList.add(new Product("Ghee", "android.resource://com.example.undedairyproduct/drawable/ghee", gheeCombinations));
        productList.add(new Product("Dahi", "android.resource://com.example.undedairyproduct/drawable/dahi", dahiCombinations));
        productList.add(new Product("Basundi", "android.resource://com.example.undedairyproduct/drawable/basundi", basundiCombinations));
        productList.add(new Product("Mango Lassi", "android.resource://com.example.undedairyproduct/drawable/mango_lassi", mangoLassiCombinations));
        productList.add(new Product("Fruits Lassi", "android.resource://com.example.undedairyproduct/drawable/fruits_lassi", fruitsLassiCombinations));
    }
}
