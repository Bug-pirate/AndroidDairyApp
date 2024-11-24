package com.example.undedairyproduct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminActivity extends AppCompatActivity {

    private LinearLayout layoutProductInfo, productListLayout;
    private Button btnAddProduct, btnAddMoreWeightPrice, btnSelectImage, btnHome, btnBilling;
    private EditText etProductName, etDefaultWeight, etDefaultPrice;
    private ImageView productImage;
    private TextView selectedImagePath;
    private String productImagePath = ""; // Placeholder for image path
    private ArrayList<String> weightList = new ArrayList<>();
    private ArrayList<String> priceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize UI elements
        layoutProductInfo = findViewById(R.id.layoutProductInfo);
        etProductName = findViewById(R.id.productNameInput);
        etDefaultWeight = findViewById(R.id.productWeightInput);
        etDefaultPrice = findViewById(R.id.productWeightPriceInput);
        btnAddProduct = findViewById(R.id.addProductButton);
        btnAddMoreWeightPrice = findViewById(R.id.addMoreWeightPriceButton);
        btnSelectImage = findViewById(R.id.selectImageButton);
        productImage = findViewById(R.id.selectedImageView);
        selectedImagePath = findViewById(R.id.selectedImagePath);
        productListLayout = findViewById(R.id.productListLayout);

        btnHome = findViewById(R.id.homeButton);
        btnBilling = findViewById(R.id.billingButton);

        // Add more weight and price combinations dynamically
        btnAddMoreWeightPrice.setOnClickListener(v -> addWeightPriceInput());

        // Select image for the product
        btnSelectImage.setOnClickListener(v -> selectProductImage());

        // Add product to the list
        btnAddProduct.setOnClickListener(v -> addProduct());

        // Navigate to Home
        btnHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(homeIntent);
        });

        // Navigate to Billing
        btnBilling.setOnClickListener(v -> {
            Intent billingIntent = new Intent(AdminActivity.this, BillingActivity.class);
            startActivity(billingIntent);
        });

        // Load saved products when AdminActivity is created
        loadSavedProducts();
    }

    private void addWeightPriceInput() {
        // Dynamically add a pair of weight and price fields
        LinearLayout weightPriceLayout = new LinearLayout(this);
        weightPriceLayout.setOrientation(LinearLayout.HORIZONTAL);

        EditText etNewWeight = new EditText(this);
        etNewWeight.setHint("Weight");
        etNewWeight.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        weightPriceLayout.addView(etNewWeight);

        EditText etNewPrice = new EditText(this);
        etNewPrice.setHint("Price");
        etNewPrice.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        weightPriceLayout.addView(etNewPrice);

        layoutProductInfo.addView(weightPriceLayout);
    }

    private void selectProductImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1); // Request code 1 for image selection
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri selectedImageUri = data.getData();
            productImage.setImageURI(selectedImageUri);
            selectedImagePath.setVisibility(View.GONE);
            productImagePath = selectedImageUri.toString();
        }
    }

    private void addProduct() {
        String productName = etProductName.getText().toString();

        if (productName.isEmpty()) {
            Toast.makeText(this, "Please fill in the product name.", Toast.LENGTH_SHORT).show();
            return;
        }

        weightList.clear();
        priceList.clear();

        // Add the default weight and price inputs
        String defaultWeight = etDefaultWeight.getText().toString();
        String defaultPrice = etDefaultPrice.getText().toString();

        if (!defaultWeight.isEmpty() && !defaultPrice.isEmpty()) {
            weightList.add(defaultWeight);
            priceList.add(defaultPrice);
        }

        // Add dynamically added weight and price inputs
        for (int i = 0; i < layoutProductInfo.getChildCount(); i++) {
            View child = layoutProductInfo.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout weightPriceLayout = (LinearLayout) child;
                EditText etWeight = (EditText) weightPriceLayout.getChildAt(0);
                EditText etPrice = (EditText) weightPriceLayout.getChildAt(1);

                String weight = etWeight.getText().toString();
                String price = etPrice.getText().toString();

                if (!weight.isEmpty() && !price.isEmpty()) {
                    weightList.add(weight);
                    priceList.add(price);
                }
            }
        }

        if (weightList.isEmpty() || priceList.isEmpty()) {
            Toast.makeText(this, "Please add at least one weight/price combination.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create price-weight combinations list
        List<PriceWeightCombination> priceWeightCombinations = new ArrayList<>();
        for (int i = 0; i < weightList.size(); i++) {
            priceWeightCombinations.add(new PriceWeightCombination(weightList.get(i), priceList.get(i)));
        }

        // Save the product
        saveProductToPreferences(productName, productImagePath, priceWeightCombinations);
        resetFormFields();

        // Load updated product list
        loadSavedProducts();

        Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show();
    }

    public void saveProductToPreferences(String productName, String productImagePath, List<PriceWeightCombination> priceWeightCombinations) {
        SharedPreferences preferences = getSharedPreferences("product_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Retrieve existing products
        Set<String> productJsonSet = preferences.getStringSet("products_key", new HashSet<>());

        // Create a new Product object
        Product product = new Product(productName, productImagePath, priceWeightCombinations);

        // Add the new product to the set
        productJsonSet.add(product.toJson());

        // Save the updated set back to SharedPreferences
        editor.putStringSet("products_key", productJsonSet);
        editor.apply();
    }

    private void loadSavedProducts() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> productJsonSet = sharedPreferences.getStringSet("products_key", new HashSet<>());

        // Clear the existing list before adding new products
        productListLayout.removeAllViews();

        if (productJsonSet != null) {
            for (String productJson : productJsonSet) {
                Product product = Product.fromJson(productJson);
                addProductToList(product.getName(), product.getPriceWeightCombinations(), product.getImageUri());
            }
        }
    }

    private void addProductToList(String productName, List<PriceWeightCombination> priceWeightCombinations, String imagePath) {
        // Inflate the product CardView layout
        CardView productCard = (CardView) getLayoutInflater().inflate(R.layout.product_item, productListLayout, false);

        ImageView productImageView = productCard.findViewById(R.id.productImageView);
        TextView productNameView = productCard.findViewById(R.id.productName);
        LinearLayout weightPriceContainer = productCard.findViewById(R.id.weightPriceContainer);
        Button removeButton = productCard.findViewById(R.id.removeButton);

        // Set the product image
        if (imagePath != null && !imagePath.isEmpty()) {
            productImageView.setImageURI(Uri.parse(imagePath));
        } else {
            productImageView.setImageResource(R.drawable.placeholder);
        }

        // Set the product name
        productNameView.setText(productName);

        // Add the weight and price combinations
        for (PriceWeightCombination combo : priceWeightCombinations) {
            LinearLayout weightPriceLayout = new LinearLayout(this);
            weightPriceLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView weightTextView = new TextView(this);
            weightTextView.setText(combo.getWeight());
            weightPriceLayout.addView(weightTextView);

            TextView priceTextView = new TextView(this);
            priceTextView.setText(combo.getPrice());
            weightPriceLayout.addView(priceTextView);

            weightPriceContainer.addView(weightPriceLayout);
        }

        // Set the remove button functionality
        removeButton.setOnClickListener(v -> {
            removeProductFromPreferences(productName);
            productListLayout.removeView(productCard);
        });

        // Add the product card to the layout
        productListLayout.addView(productCard);
    }

    private void removeProductFromPreferences(String productName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> productJsonSet = sharedPreferences.getStringSet("products_key", new HashSet<>());
        for (String productJson : productJsonSet) {
            Product product = Product.fromJson(productJson);
            if (product.getName().equals(productName)) {
                productJsonSet.remove(productJson);
                break;
            }
        }

        editor.putStringSet("products_key", productJsonSet);
        editor.apply();
    }

    private void resetFormFields() {
        etProductName.setText("");
        etDefaultWeight.setText("");
        etDefaultPrice.setText("");
        layoutProductInfo.removeAllViews();
        productImage.setImageResource(R.drawable.placeholder);
        selectedImagePath.setVisibility(View.VISIBLE);
    }
}
