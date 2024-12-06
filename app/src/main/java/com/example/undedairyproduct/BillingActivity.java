package com.example.undedairyproduct;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BillingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing); // Replace with your billing page XML layout file name

        // Get references to UI elements
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView productNameView = findViewById(R.id.productNameView); // Add in your layout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView selectedWeightView = findViewById(R.id.selectedWeightView); // Add in your layout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView selectedPriceView = findViewById(R.id.selectedPriceView); // Add in your layout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText customerNameInput = findViewById(R.id.customerName); // Already present in your XML
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView quantityInput = findViewById(R.id.quantityInput); // Add in your layout

        // Retrieve data from the intent
        String productName = getIntent().getStringExtra("productName");
        String selectedWeight = getIntent().getStringExtra("selectedWeight");
        String selectedPrice = getIntent().getStringExtra("selectedPrice");

        // Check if the data is received
        if (productName != null && selectedWeight != null && selectedPrice != null) {
            // Populate the data in the UI
            productNameView.setText("Product: " + productName);
            selectedWeightView.setText("Weight: " + selectedWeight);
            selectedPriceView.setText("Price: " + selectedPrice);
        } else {
            Toast.makeText(this, "Failed to load product details.", Toast.LENGTH_SHORT).show();
        }
    }
}