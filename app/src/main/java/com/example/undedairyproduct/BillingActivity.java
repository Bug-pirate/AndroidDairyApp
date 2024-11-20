package com.example.undedairyproduct;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BillingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        // Home button action
        Button btnHomeFromBilling = findViewById(R.id.btnHomeFromBilling);
        btnHomeFromBilling.setOnClickListener(v -> {
            // Navigate to MainActivity
            startActivity(new Intent(BillingActivity.this, MainActivity.class));
        });

        // Billing button action - just refreshes content without any visible change (no effect)
        Button btnBillingFromBilling = findViewById(R.id.btnBillingFromBilling);
        btnBillingFromBilling.setOnClickListener(v -> {
            // No visible changes needed, refresh any internal data if necessary, but avoid UI changes
            refreshBillingContent();
        });

        // Admin button action
        Button btnAdminFromBilling = findViewById(R.id.btnAdminFromBilling);
        btnAdminFromBilling.setOnClickListener(v -> {
            // Navigate to AdminActivity
            startActivity(new Intent(BillingActivity.this, AdminActivity.class));
        });
    }

    // This function will not change the UI, but can be used to refresh internal data if needed
    private void refreshBillingContent() {
        // Add any internal refresh logic here, such as updating data or clearing fields
        // This method is now intentionally left blank to avoid any visible changes.
    }
}
