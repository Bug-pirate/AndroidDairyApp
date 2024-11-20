package com.example.undedairyproduct;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;  // Permission request code for storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request necessary permissions if needed
        checkPermissions();

        // Set up the buttons
        Button btnHome = findViewById(R.id.btnHome);
        Button btnBilling = findViewById(R.id.btnBilling);
        Button btnAdmin = findViewById(R.id.btnAdmin);

        // Set up the Home button to stay on the main screen (it is already here)
        btnHome.setOnClickListener(v -> {
            // No need to do anything as we're already on the home screen
        });

        // Set up the Billing button to navigate to BillingActivity
        btnBilling.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BillingActivity.class);
            startActivity(intent);
        });

        // Set up the Admin button to navigate to AdminActivity
        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivityForResult(intent, 1);  // Start AdminActivity and expect results
        });

        // Load and display the products from SharedPreferences
        loadProductsFromPreferences();
    }

    // Method to request permissions if not granted
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if permissions are granted
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Get product details from AdminActivity
            String productName = data.getStringExtra("productName");
            String productPrice = data.getStringExtra("productPrice");
            String productImagePath = data.getStringExtra("productImagePath");
            String[] weights = data.getStringArrayExtra("weights");
            String[] prices = data.getStringArrayExtra("prices");

            // Save the product to SharedPreferences
            saveProductToPreferences(productName, productPrice, productImagePath, weights, prices);

            // Inflate and populate the product card
            LinearLayout productListLayout = findViewById(R.id.productListLayout);
            View productCard = getLayoutInflater().inflate(R.layout.product_card, productListLayout, false);

            // Set product image safely
            ImageView productImage = productCard.findViewById(R.id.productImage);
            if (productImagePath != null && !productImagePath.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(productImagePath);
                    productImage.setImageURI(imageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                    productImage.setImageResource(R.drawable.placeholder); // Fallback image
                }
            } else {
                productImage.setImageResource(R.drawable.placeholder); // Fallback image
            }

            // Set product name
            TextView productNameView = productCard.findViewById(R.id.productName);
            productNameView.setText(productName);

            // Populate dropdowns for weights and prices
            Spinner weightSpinner = productCard.findViewById(R.id.weightSpinner);
            Spinner priceSpinner = productCard.findViewById(R.id.priceSpinner);
            ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weights);
            ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prices);
            weightSpinner.setAdapter(weightAdapter);
            priceSpinner.setAdapter(priceAdapter);

            // Handle Add to Cart button
            Button addToCartButton = productCard.findViewById(R.id.addToCartButton);
            addToCartButton.setOnClickListener(v -> {
                Toast.makeText(this, productName + " added to cart!", Toast.LENGTH_SHORT).show();
            });

            // Add the card to the layout
            productListLayout.addView(productCard);
        }
    }

    // Method to save product to SharedPreferences
    private void saveProductToPreferences(String productName, String productPrice, String productImagePath, String[] weights, String[] prices) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Create a unique key for each product
        String key = "product_" + productName + "_" + productPrice;

        // Convert product details to a string representation
        editor.putString(key + "_name", productName);
        editor.putString(key + "_price", productPrice);
        editor.putString(key + "_imagePath", productImagePath);

        // Store weight and price options as JSON-like strings
        editor.putString(key + "_weights", String.join(",", weights));
        editor.putString(key + "_prices", String.join(",", prices));

        editor.apply();  // Commit the changes
    }

    // Method to load products from SharedPreferences
    private void loadProductsFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        LinearLayout productListLayout = findViewById(R.id.productListLayout);

        // Iterate through the saved products in SharedPreferences
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            String value = (String) entry.getValue();

            // If the key indicates a product, process the product details
            if (key.endsWith("_name")) {
                String productName = sharedPreferences.getString(key, null);
                String productPrice = sharedPreferences.getString(key.replace("_name", "_price"), null);
                String productImagePath = sharedPreferences.getString(key.replace("_name", "_imagePath"), null);
                String[] weights = sharedPreferences.getString(key.replace("_name", "_weights"), "").split(",");
                String[] prices = sharedPreferences.getString(key.replace("_name", "_prices"), "").split(",");

                // Inflate and populate the product card (same code as in onActivityResult)
                View productCard = getLayoutInflater().inflate(R.layout.product_card, productListLayout, false);
                ImageView productImage = productCard.findViewById(R.id.productImage);

                if (productImagePath != null && !productImagePath.isEmpty()) {
                    try {
                        Uri imageUri = Uri.parse(productImagePath);
                        productImage.setImageURI(imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                        productImage.setImageResource(R.drawable.placeholder); // Fallback image
                    }
                } else {
                    productImage.setImageResource(R.drawable.placeholder); // Fallback image
                }

                TextView productNameView = productCard.findViewById(R.id.productName);
                productNameView.setText(productName);

                Spinner weightSpinner = productCard.findViewById(R.id.weightSpinner);
                Spinner priceSpinner = productCard.findViewById(R.id.priceSpinner);
                ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weights);
                ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prices);
                weightSpinner.setAdapter(weightAdapter);
                priceSpinner.setAdapter(priceAdapter);

                Button addToCartButton = productCard.findViewById(R.id.addToCartButton);
                addToCartButton.setOnClickListener(v -> {
                    Toast.makeText(this, productName + " added to cart!", Toast.LENGTH_SHORT).show();
                });

                productListLayout.addView(productCard);
            }
        }
    }
}
