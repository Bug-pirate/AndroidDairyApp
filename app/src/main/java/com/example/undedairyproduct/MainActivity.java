package com.example.undedairyproduct;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1; // Permission request code for storage
    private List<Product> allProducts = new ArrayList<>();  // List to hold all products
    private LinearLayout productListLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request necessary permissions if needed
        checkPermissions();

        // Set up buttons
        Button btnHome = findViewById(R.id.btnHome);
        Button btnBilling = findViewById(R.id.btnBilling);
        Button btnAdmin = findViewById(R.id.btnAdmin);

        btnHome.setOnClickListener(v -> {
            // No action needed, already on Home
        });

        btnBilling.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BillingActivity.class);
            startActivity(intent);
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivityForResult(intent, 1); // Start AdminActivity
        });

        // Initialize product list layout
        productListLayout = findViewById(R.id.productListLayout);

        // Set up SearchView for filtering products
        SearchView searchView = findViewById(R.id.searchView);


        // Clear focus from SearchView initially
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);  // Filter when search is submitted
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);  // Filter while typing
                return false;
            }
        });

        // Load products from Firebase
        loadProductsFromFirebase();
    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission denied, cannot access gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProductsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("Products");

        productListLayout.removeAllViews(); // Clear previous entries

        productsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Parse product data
                                String productName = document.getString("name");
                                String productImagePath = document.getString("imageUrl");

                                // Parse weights and prices from Firestore
                                List<Map<String, String>> weightsAndPrices = (List<Map<String, String>>) document.get("weightsPrices");
                                List<String> weights = new ArrayList<>();
                                List<String> prices = new ArrayList<>();

                                // Fill weights and prices lists
                                if (weightsAndPrices != null) {
                                    for (Map<String, String> entry : weightsAndPrices) {
                                        String weight = entry.get("weight");
                                        String price = entry.get("price");
                                        if (weight != null && price != null) {
                                            weights.add(weight);
                                            prices.add(price);
                                        }
                                    }
                                }

                                // Convert lists to arrays for spinners
                                String[] weightsArray = weights.toArray(new String[0]);
                                String[] pricesArray = prices.toArray(new String[0]);

                                // Add product to the list
                                Product product = new Product(productName, productImagePath, weightsArray, pricesArray);
                                allProducts.add(product);  // Store all products for filtering

                                // Add product card to layout
                                addProductCard(productName, productImagePath, weightsArray, pricesArray);

                            } catch (Exception e) {
                                Log.e("Firebase", "Error loading product: ", e);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Failed to load products: " + task.getException(), Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error: ", task.getException());
                    }
                });
    }

    private void addProductCard(String productName, String productImagePath, String[] weights, String[] prices) {
        LinearLayout productListLayout = findViewById(R.id.productListLayout);
        View productCard = getLayoutInflater().inflate(R.layout.product_card, productListLayout, false);

        ImageView productImage = productCard.findViewById(R.id.productImage);

        // Load image from Firebase Storage using Glide
        if (productImagePath != null && !productImagePath.isEmpty()) {
            Glide.with(this)
                    .load(productImagePath)  // URL from Firebase Storage
                    .placeholder(R.drawable.placeholder)  // Placeholder image
                    .into(productImage);
        } else {
            productImage.setImageResource(R.drawable.placeholder);  // Fallback to placeholder image
        }

        TextView productNameView = productCard.findViewById(R.id.productName);
        productNameView.setText(productName);

        // Set up weight spinner
        Spinner weightSpinner = productCard.findViewById(R.id.weightSpinner);
        ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weights);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightSpinner.setAdapter(weightAdapter);

        // Set up price spinner
        Spinner priceSpinner = productCard.findViewById(R.id.priceSpinner);
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prices);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(priceAdapter);

        // Handle price update based on weight selection
        weightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPrice = prices[position];
                priceSpinner.setSelection(position);  // Update price spinner selection
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                priceSpinner.setSelection(0);  // Default to first price
            }
        });

        // Add "Add to Cart" button functionality
        Button addToCartButton = productCard.findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(v -> {
            // Intent to go to BillingActivity with product details
            Intent intent = new Intent(MainActivity.this, BillingActivity.class);
            intent.putExtra("productName", productName);
            intent.putExtra("productImagePath", productImagePath);
            intent.putExtra("productWeights", weights);
            intent.putExtra("productPrices", prices);
            startActivity(intent);
        });

        productListLayout.addView(productCard);
    }

    private void filterProducts(String query) {
        productListLayout.removeAllViews(); // Clear current displayed products

        // Filter the products based on the query
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                addProductCard(product.getName(), product.getImagePath(), product.getWeights(), product.getPrices());
            }
        }
    }

    // Product model class
    public static class Product {
        private String name;
        private String imagePath;
        private String[] weights;
        private String[] prices;

        public Product(String name, String imagePath, String[] weights, String[] prices) {
            this.name = name;
            this.imagePath = imagePath;
            this.weights = weights;
            this.prices = prices;
        }

        public String getName() {
            return name;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String[] getWeights() {
            return weights;
        }

        public String[] getPrices() {
            return prices;
        }
    }
}
