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

public class AdminActivity extends AppCompatActivity {

    private LinearLayout layoutProductInfo, productListLayout;
    private Button btnAddProduct, btnAddMoreWeightPrice, btnSelectImage, btnHome, btnBilling;
    private EditText etProductName, etProductPrice, etWeight, etWeightPrice;
    private ImageView productImage;
    private TextView selectedImagePath;
    private String productImagePath = ""; // Placeholder for image path
    private ArrayList<String> weightList = new ArrayList<>();  // List to hold weights
    private ArrayList<String> priceList = new ArrayList<>();   // List to hold prices

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize UI elements
        layoutProductInfo = findViewById(R.id.layoutProductInfo);
        etProductName = findViewById(R.id.productNameInput);
        etProductPrice = findViewById(R.id.productPriceInput);
        etWeight = findViewById(R.id.productWeightInput);
        etWeightPrice = findViewById(R.id.productWeightPriceInput);
        btnAddProduct = findViewById(R.id.addProductButton);
        btnAddMoreWeightPrice = findViewById(R.id.addMoreWeightPriceButton);
        btnSelectImage = findViewById(R.id.selectImageButton);
        productImage = findViewById(R.id.selectedImageView);
        selectedImagePath = findViewById(R.id.selectedImagePath);
        productListLayout = findViewById(R.id.productListLayout);

        // Buttons for navigation
        btnHome = findViewById(R.id.homeButton);
        btnBilling = findViewById(R.id.billingButton);

        // Button to add weight and price input fields dynamically
        btnAddMoreWeightPrice.setOnClickListener(v -> addWeightPriceInput());

        // Button to select image for the product
        btnSelectImage.setOnClickListener(v -> selectProductImage());

        // Button to add the product to the list
        btnAddProduct.setOnClickListener(v -> {
            // Validate the inputs before adding the product
            if (etProductName.getText().toString().isEmpty() || etProductPrice.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill in product name and price.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Collect weight and price data from the input fields
            weightList.clear();  // Clear any previous entries
            priceList.clear();

            // Add weight and price pairs from the dynamically added fields
            for (int i = 0; i < layoutProductInfo.getChildCount(); i++) {
                View child = layoutProductInfo.getChildAt(i);
                if (child instanceof LinearLayout) {
                    EditText weightEditText = (EditText) child.findViewById(R.id.productWeightInput);
                    EditText priceEditText = (EditText) child.findViewById(R.id.productWeightPriceInput);
                    weightList.add(weightEditText.getText().toString());
                    priceList.add(priceEditText.getText().toString());
                }
            }

            // Create an Intent to pass product data back to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("productName", etProductName.getText().toString());
            resultIntent.putExtra("productPrice", etProductPrice.getText().toString());
            resultIntent.putExtra("productImagePath", productImagePath);
            resultIntent.putExtra("weights", weightList.toArray(new String[0]));
            resultIntent.putExtra("prices", priceList.toArray(new String[0]));
            setResult(RESULT_OK, resultIntent);

            // Optionally reset the form fields
            resetFormFields();

            // Save product to SharedPreferences
            saveProductToPreferences();

            Toast.makeText(this, "Product Added Successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close the AdminActivity and send data back
        });

        // Button to navigate to Home (MainActivity)
        btnHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(homeIntent);
        });

        // Button to navigate to Billing Activity
        btnBilling.setOnClickListener(v -> {
            Intent billingIntent = new Intent(AdminActivity.this, BillingActivity.class);
            startActivity(billingIntent);
        });
    }

    private void addWeightPriceInput() {
        // Dynamically add weight-price pair input fields to layout
        LinearLayout weightPriceLayout = new LinearLayout(this);
        weightPriceLayout.setOrientation(LinearLayout.HORIZONTAL);

        EditText etNewWeight = new EditText(this);
        etNewWeight.setHint("Weight");
        weightPriceLayout.addView(etNewWeight);

        EditText etNewPrice = new EditText(this);
        etNewPrice.setHint("Price");
        weightPriceLayout.addView(etNewPrice);

        layoutProductInfo.addView(weightPriceLayout);
    }

    private void selectProductImage() {
        // Intent to open image picker
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);  // 1 is the request code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri selectedImageUri = data.getData();  // Get the selected image URI

            // Set the selected image in the ImageView
            productImage.setImageURI(selectedImageUri);

            // Hide the TextView (No image selected message)
            selectedImagePath.setVisibility(View.GONE);

            // Optionally, save the URI if needed for future use
            productImagePath = selectedImageUri.toString();  // Store the image URI if needed
        }
    }

    private void saveProductToPreferences() {
        // Save product details (name, price, image) and weight/price pairs in SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String productName = etProductName.getText().toString();
        String productPrice = etProductPrice.getText().toString();

        // Save the basic product details
        editor.putString("product_name", productName);
        editor.putString("product_price", productPrice);
        editor.putString("product_image", productImagePath);

        // Save weight and price pairs
        for (int i = 0; i < weightList.size(); i++) {
            String weight = weightList.get(i);
            String price = priceList.get(i);
            editor.putString("weight_" + i, weight);
            editor.putString("price_" + i, price);
        }

        editor.apply();  // Commit the changes to SharedPreferences
    }

    private void addProductToList(String productName, String productPrice) {
        // Inflate the product item layout
        CardView productLayout = (CardView) getLayoutInflater().inflate(R.layout.product_item, productListLayout, false);

        // Set product image (either the selected image or a placeholder)
        ImageView productImageView = productLayout.findViewById(R.id.productImageView);
        if (!productImagePath.isEmpty()) {
            productImageView.setImageURI(Uri.parse(productImagePath));  // Use the selected image
        } else {
            productImageView.setImageResource(R.drawable.ghee); // Placeholder image
        }

        // Set product name
        TextView productNameTextView = productLayout.findViewById(R.id.productName);
        productNameTextView.setText(productName);

        // Display weight/price pairs for the product
        for (int i = 0; i < weightList.size(); i++) {
            LinearLayout weightPriceLayout = new LinearLayout(this);
            weightPriceLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView weightText = new TextView(this);
            weightText.setText(weightList.get(i));

            TextView priceText = new TextView(this);
            priceText.setText(priceList.get(i));

            weightPriceLayout.addView(weightText);
            weightPriceLayout.addView(priceText);

            productLayout.addView(weightPriceLayout);
        }

        // Remove button functionality
        Button removeButton = productLayout.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(v -> {
            // Call remove method to remove the product from SharedPreferences
            removeProductFromPreferences(productName);
            productListLayout.removeView(productLayout);
        });

        productListLayout.addView(productLayout);  // Add product layout to the product list
    }

    private void removeProductFromPreferences(String productName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("product_name");  // Remove all product details
        editor.remove("product_price");
        editor.remove("product_image");

        // Remove all weight/price pairs
        int i = 0;
        while (sharedPreferences.contains("weight_" + i)) {
            editor.remove("weight_" + i);
            editor.remove("price_" + i);
            i++;
        }

        editor.apply();  // Commit the changes to SharedPreferences
    }

    private void resetFormFields() {
        etProductName.setText("");
        etProductPrice.setText("");
        etWeight.setText("");
        etWeightPrice.setText("");
        productImage.setImageResource(R.drawable.ghee);  // Reset the image
        productImagePath = "";  // Reset image path
        layoutProductInfo.removeAllViews();  // Remove all dynamically added weight/price fields
    }
}