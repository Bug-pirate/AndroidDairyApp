package com.example.undedairyproduct;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminActivity extends AppCompatActivity {

    private EditText etProductName, etProductWeight, etProductWeightPrice;
    private ImageView productImage;
    private Button btnAddProduct, btnSelectImage, btnAddMoreWeightPrice;
    private Uri productImageUri;

    private LinearLayout layoutProductInfo, productListLayout;
    private FirebaseFirestore firestore;

    private final ArrayList<View> weightPriceFields = new ArrayList<>(); // List to track dynamic fields

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Button btnHome = findViewById(R.id.homeButton);
        Button btnBilling = findViewById(R.id.billingButton);
        Button btnAdmin = findViewById(R.id.adminButton);

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, MainActivity.class));
        });

        btnBilling.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, BillingActivity.class);
            startActivity(intent);
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminActivity.class);
            startActivityForResult(intent, 1); // Start AdminActivity
        });

        // Initialize UI Components
        etProductName = findViewById(R.id.productNameInput);
        etProductWeight = findViewById(R.id.productWeightInput);
        etProductWeightPrice = findViewById(R.id.productWeightPriceInput);
        btnAddProduct = findViewById(R.id.addProductButton);
        btnSelectImage = findViewById(R.id.selectImageButton);
        productImage = findViewById(R.id.selectedImageView);
        layoutProductInfo = findViewById(R.id.layoutProductInfo);
        productListLayout = findViewById(R.id.productListLayout);
        btnAddMoreWeightPrice = findViewById(R.id.addMoreWeightPriceButton);

        firestore = FirebaseFirestore.getInstance();

        // Button listeners
        btnSelectImage.setOnClickListener(v -> selectImageFromGallery());
        btnAddProduct.setOnClickListener(v -> uploadProductData());
        btnAddMoreWeightPrice.setOnClickListener(v -> addWeightPriceInput());

        loadProductsFromFirestore();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            productImageUri = data.getData();
            productImage.setImageURI(productImageUri); // Display selected image
        } else {
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void addWeightPriceInput() {
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
        weightPriceFields.add(weightPriceLayout);
    }

    private void uploadProductData() {
        String productName = etProductName.getText().toString().trim();

        if (productName.isEmpty() || productImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect weight-price pairs
        ArrayList<Map<String, Object>> weightPriceList = new ArrayList<>();

        // Include the first weight-price pair (if filled)
        if (!etProductWeight.getText().toString().trim().isEmpty() &&
                !etProductWeightPrice.getText().toString().trim().isEmpty()) {
            Map<String, Object> initialWeightPrice = new HashMap<>();
            initialWeightPrice.put("weight", etProductWeight.getText().toString().trim());
            initialWeightPrice.put("price", etProductWeightPrice.getText().toString().trim());
            weightPriceList.add(initialWeightPrice);
        }

        // Include all additional weight-price pairs from dynamically added fields
        for (View field : weightPriceFields) {
            LinearLayout weightPriceLayout = (LinearLayout) field;
            EditText etWeight = (EditText) weightPriceLayout.getChildAt(0);
            EditText etPrice = (EditText) weightPriceLayout.getChildAt(1);

            String weight = etWeight.getText().toString().trim();
            String price = etPrice.getText().toString().trim();

            if (!weight.isEmpty() && !price.isEmpty()) {
                Map<String, Object> weightPrice = new HashMap<>();
                weightPrice.put("weight", weight);
                weightPrice.put("price", price);
                weightPriceList.add(weightPrice);
            }
        }

        if (weightPriceList.isEmpty()) {
            Toast.makeText(this, "Please provide at least one weight-price pair", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed to upload image and then save product data
        uploadImageToCloudinary(productImageUri, productName, weightPriceList);
    }

    private void uploadImageToCloudinary(Uri imageUri, String productName, ArrayList<Map<String, Object>> weightPriceList) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri)
                    .compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageData = baos.toByteArray();

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "product_image.jpg",
                            RequestBody.create(MediaType.parse("image/jpeg"), imageData))
                    .addFormDataPart("upload_preset", "DiaryApp") // Replace with your preset
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/dh64ix3na/image/upload") // Replace with your Cloudinary URL
                    .post(requestBody)
                    .build();

            new Thread(() -> {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String imageUrl = jsonObject.getString("secure_url");
                        saveProductDataToFirestore(productName, imageUrl, weightPriceList);
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Cloudinary upload failed", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        } catch (IOException e) {
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProductDataToFirestore(String productName, String imageUrl, ArrayList<Map<String, Object>> weightPriceList) {
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", productName);
        productData.put("imageUrl", imageUrl);
        productData.put("weightsPrices", weightPriceList); // Add weight-price list to Firestore

        firestore.collection("Products").add(productData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    resetForm();
                    loadProductsFromFirestore();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadProductsFromFirestore() {
        productListLayout.removeAllViews(); // Clear existing views

        firestore.collection("Products")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot) {
                        String name = document.getString("name");
                        String imageUrl = document.getString("imageUrl");
                        String documentId = document.getId(); // Get the document ID

                        addProductView(name, imageUrl, documentId);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addProductView(String name, String imageUrl, String documentId) {
        View productView = LayoutInflater.from(this).inflate(R.layout.product_item, productListLayout, false);

        TextView productName = productView.findViewById(R.id.productName);
        ImageView productImage = productView.findViewById(R.id.productImageView);
        Button removeButton = productView.findViewById(R.id.removeButton);

        productName.setText(name);

        Glide.with(this)
                .load(imageUrl)
                .into(productImage);

        // Set remove button listener
        removeButton.setOnClickListener(v -> {
            firestore.collection("Products").document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Product removed successfully", Toast.LENGTH_SHORT).show();
                        productListLayout.removeView(productView); // Remove the view from the layout
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to remove product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        productListLayout.addView(productView);
    }

    private void resetForm() {
        etProductName.setText("");
        etProductWeight.setText("");
        etProductWeightPrice.setText("");
        productImage.setImageURI(null);
        weightPriceFields.clear();
        layoutProductInfo.removeAllViews();
    }
}
