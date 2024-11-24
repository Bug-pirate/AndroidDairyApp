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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            String productName = data.getStringExtra("productName");
            String productImagePath = data.getStringExtra("productImagePath");
            String[] weights = data.getStringArrayExtra("weights");
            String[] prices = data.getStringArrayExtra("prices");

            List<PriceWeightCombination> priceWeightCombinations = new ArrayList<>();
            for (int i = 0; i < weights.length; i++) {
                priceWeightCombinations.add(new PriceWeightCombination(weights[i], prices[i]));
            }

            saveProductToPreferences(productName, productImagePath, priceWeightCombinations);

            // Reload products to update the UI
            loadProductsFromPreferences();
        }
    }



    // Method to save product to SharedPreferences with weights and prices
    public void saveProductToPreferences(String productName, String productImagePath, List<PriceWeightCombination> priceWeightCombinations) {
        SharedPreferences preferences = getSharedPreferences("product_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Create a new Product object and save the product with price-weight combinations
        Product product = new Product(productName, productImagePath, priceWeightCombinations);

        // Convert the product to JSON and save
        Set<String> productJsonSet = new HashSet<>();
        productJsonSet.add(product.toJson());
        editor.putStringSet("products_key", productJsonSet);
        editor.apply();
    }


    // Method to load products from SharedPreferences
    private void loadProductsFromPreferences() {
        SharedPreferences preferences = getSharedPreferences("product_prefs", MODE_PRIVATE); // Use the same name
        Set<String> productJsonSet = preferences.getStringSet("products_key", new HashSet<>());

        LinearLayout productListLayout = findViewById(R.id.productListLayout);

        // Iterate through the saved products
        for (String productJson : productJsonSet) {
            Product product = Product.fromJson(productJson);

            // Inflate and populate the product card
            View productCard = getLayoutInflater().inflate(R.layout.product_card, productListLayout, false);

            // Set product image
            ImageView productImage = productCard.findViewById(R.id.productImage);
            String productImagePath = product.getImageUri();
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
            productNameView.setText(product.getName());

            // Populate dropdowns for weights and prices
            Spinner weightSpinner = productCard.findViewById(R.id.weightSpinner);
            Spinner priceSpinner = productCard.findViewById(R.id.priceSpinner);

            List<PriceWeightCombination> priceWeightCombinations = product.getPriceWeightCombinations();
            String[] weights = new String[priceWeightCombinations.size()];
            String[] prices = new String[priceWeightCombinations.size()];

            for (int i = 0; i < priceWeightCombinations.size(); i++) {
                weights[i] = priceWeightCombinations.get(i).getWeight();
                prices[i] = priceWeightCombinations.get(i).getPrice();
            }

            ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weights);
            ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prices);
            weightSpinner.setAdapter(weightAdapter);
            priceSpinner.setAdapter(priceAdapter);

            // Handle Add to Cart button
            Button addToCartButton = productCard.findViewById(R.id.addToCartButton);
            addToCartButton.setOnClickListener(v -> {
                Toast.makeText(this, product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
            });

            // Add the card to the layout
            productListLayout.addView(productCard);
        }
    }

}
