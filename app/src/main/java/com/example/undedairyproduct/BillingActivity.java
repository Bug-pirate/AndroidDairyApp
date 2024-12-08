package com.example.undedairyproduct;

import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BillingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        // Get references to UI elements
        TextView dairyName = findViewById(R.id.dairyName);
        TextView dairyAddress = findViewById(R.id.dairyAddress);
        TextView currentDateText = findViewById(R.id.currentDatem);
        EditText customerNameInput = findViewById(R.id.customerName);
        TableLayout billingTable = findViewById(R.id.billingTable);

        // Set static Dairy Info
        dairyName.setText("Unde Patil Dairy");
        dairyAddress.setText("Unde Farm Shreerampur");

        // Get and display the current date in the format "dd/MM/yyyy"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = sdf.format(new Date());
        currentDateText.setText("Date: " + currentDate);

        // Retrieve cart items passed from MainActivity
        List<Map<String, String>> cartItems = (List<Map<String, String>>) getIntent().getSerializableExtra("cartItems");
        DecimalFormat df;
        df = new DecimalFormat("#.##");

        // Dynamically populate the table with cart items
        double totalAmount = 0;
       // df = null;
        if (cartItems != null) {
            for (int i = 0; i < cartItems.size(); i++) {
                Map<String, String> cartItem = cartItems.get(i);

                // Extract product details
                String productName = cartItem.get("productName");
                String productWeight = cartItem.get("productWeight");
                String productPrice = cartItem.get("productPrice");

                double price = 0;
                double weight = 0;

                try {
                    // Handle weight and price safely
                    price = Double.parseDouble(productPrice);

                    // Clean the weight string (e.g., "500g" => "500")
                    String numericWeight = productWeight.replaceAll("[^0-9.]", ""); // Remove non-numeric characters
                    weight = Double.parseDouble(numericWeight); // Parse the cleaned weight
                } catch (NumberFormatException e) {
                    Toast.makeText(BillingActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                    continue; // Skip this item if the format is invalid
                }


                // Calculate GST and total
                Double gst = (price * 0.18);// Assuming 18% GST


                double total = (price + gst);
                ;

                // Create a new table row for each cart item
                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

                // Add columns to the row
                TextView srNo = new TextView(this);
                srNo.setText(String.valueOf(i + 1)); // SrNo
                srNo.setPadding(8, 8, 8, 8);
                row.addView(srNo);

                TextView productCol = new TextView(this);
                productCol.setText(productName); // Product name
                productCol.setPadding(8, 8, 8, 8);
                row.addView(productCol);

                TextView priceCol = new TextView(this);
                priceCol.setText(String.format("%s INR", productPrice)); // Price
                priceCol.setPadding(8, 8, 8, 8);
                row.addView(priceCol);


                TextView qtyCol = new TextView(this);
                qtyCol.setText("1"); // Quantity - placeholder, it can be dynamic later
                qtyCol.setPadding(8, 8, 8, 8);
                row.addView(qtyCol);


                TextView weightCol = new TextView(this);
                weightCol.setText(String.format("%s kg", weight)); // Weight (converted to numeric value)
                weightCol.setPadding(8, 8, 8, 8);
                row.addView(weightCol);

                TextView gstCol = new TextView(this);
                gstCol.setText(String.format("%s INR", df.format(gst))); // GST
                gstCol.setPadding(8, 8, 8, 8);
                row.addView(gstCol);

                TextView totalCol = new TextView(this);
                totalCol.setText(String.format("%s INR", df.format(total))); // Total price after GST
                totalCol.setPadding(8, 8, 8, 8);
                row.addView(totalCol);

                // Add the row to the table
                billingTable.addView(row);

                totalAmount += total;
            }
        }

        // Set customer name
        customerNameInput.setHint("Enter Customer Name");

        // Display total amount at the bottom of the billing page
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView totalAmountView = findViewById(R.id.totalAmount);
        totalAmountView.setText(String.format("Total Amount: %s INR", df.format(totalAmount)));

        // Handle print button click (if needed)
        Button btnPrintPDF = findViewById(R.id.btnPrintPDF);
        double finalTotalAmount = totalAmount;
        btnPrintPDF.setOnClickListener(v -> {
            // Generate PDF and save to mobile storage
            generatePDF(customerNameInput.getText().toString(), finalTotalAmount);
        });
    }

    private void generatePDF(String customerName, double totalAmount) {
        Document document = new Document();
        try {
            // Create a unique file name using customer name
            String uniqueFileName = customerName.replaceAll("[^a-zA-Z0-9]", "_") + "_Invoice.pdf";  // Replace non-alphanumeric characters
            String filePath = Environment.getExternalStorageDirectory().getPath() + "/Download/" + uniqueFileName;  // Path to Downloads folder

            // Ensure the file exists or create it
            File file = new File(filePath);

            // Create the PDF file
            PdfWriter.getInstance(document, new FileOutputStream(file));

            document.open();

            // Add content to the document
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 23, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 18, Font.NORMAL);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);

            document.setMargins(36, 36, 50, 36);

            // Add logo (ensure you have a logo in your resources or external storage)


            // Add dairy name and address
            // Add dairy name and address with center alignment
            Paragraph dairyNameParagraph = new Paragraph("Unde Patil Dairy", titleFont);
            dairyNameParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(dairyNameParagraph);

            Paragraph dairyAddressParagraph = new Paragraph("Unde Farm Shreerampur", normalFont);
            dairyAddressParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(dairyAddressParagraph);

// Add date and customer name with center alignment
            Paragraph dateParagraph = new Paragraph("Date: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()), normalFont);
            dateParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(dateParagraph);

            Paragraph customerNameParagraph = new Paragraph("Customer Name: " + customerName, normalFont);
            customerNameParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(customerNameParagraph);
            document.add(new Paragraph("\n"));

            // Add a horizontal line
            document.add(new Paragraph("---------------------------------------------------------------------------------------------------------------------------------"));

            // Add table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);  // Make the table width 100% of the page
            table.setSpacingBefore(10f);  // Space before the table
            table.setSpacingAfter(10f);   // Space after the table

            // Add table headers with background color
            table.addCell(createCell("SrNo", headerFont, true));
            table.addCell(createCell("Product", headerFont, true));
            table.addCell(createCell("Price", headerFont, true));
            table.addCell(createCell("Quantity", headerFont, true));
            table.addCell(createCell("Weight", headerFont, true));
            table.addCell(createCell("GST", headerFont, true));
            table.addCell(createCell("Total", headerFont, true));

            // Populate table with cart items (use the same cart items passed to this activity)
            List<Map<String, String>> cartItems = (List<Map<String, String>>) getIntent().getSerializableExtra("cartItems");

            if (cartItems != null) {
                for (int i = 0; i < cartItems.size(); i++) {
                    Map<String, String> cartItem = cartItems.get(i);
                    String productName = cartItem.get("productName");
                    String productWeight = cartItem.get("productWeight");
                    String productPrice = cartItem.get("productPrice");

                    double price = Double.parseDouble(productPrice);
                    double weight = Double.parseDouble(productWeight.replaceAll("[^0-9.]", ""));  // Remove non-numeric characters
                    double gst = price * 0.18;  // Assuming 18% GST
                    double total = price + gst;

                    table.addCell(String.valueOf(i + 1));  // SrNo
                    table.addCell(productName);            // Product
                    table.addCell(String.format("%s INR", productPrice)); // Price
                    table.addCell("1");                    // Quantity placeholder, can be dynamic
                    table.addCell(String.format("%s kg", weight));  // Weight
                    table.addCell(String.format("%s INR", gst));    // GST
                    table.addCell(String.format("%s INR", total));  // Total
                }
            }

            // Add the total amount at the bottom of the table
            document.add(table);
            document.add(new Paragraph("\nTotal Amount: " + totalAmount + " INR", boldFont));

            // Add a horizontal line at the bottom of the page
            document.add(new Paragraph("---------------------------------------------------"));

            // Add footer with contact details
            document.add(new Paragraph("\nFor queries, contact us at:"));
            document.add(new Paragraph("Phone: +917499170096"));
            document.add(new Paragraph("Email: undepatilmilk@gmail.com "));







            // Close the document
            document.close();

            // Show success message
            Toast.makeText(this, "PDF saved to Download folder", Toast.LENGTH_SHORT).show();
        } catch (DocumentException | IOException e) {
            Log.e("PDFGenerationError", "Error generating PDF: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }

    // Utility method to create a styled cell for table headers or normal cells
    private PdfPCell createCell(String content, Font font, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Paragraph(content, font));
        if (isHeader) {
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Header background color
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        } else {
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        }
        cell.setPadding(5);
        return cell;
    }



}
