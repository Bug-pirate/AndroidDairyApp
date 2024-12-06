package com.example.undedairyproduct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.product_card, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set the product name and price
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf(product.getPrice()));

        // Load the product image using Glide
        Glide.with(context)
                .load(product.getImageUri())
                .into(holder.productImage);

        // Set up weight spinner if weights are available
        if (product.getWeights() != null && !product.getWeights().isEmpty()) {
            ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, product.getWeights());
            weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.weightSpinner.setAdapter(weightAdapter);
        }

        // Set up price spinner if prices are available
        if (product.getPrices() != null && !product.getPrices().isEmpty()) {
            ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, product.getPrices());
            priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.priceSpinner.setAdapter(priceAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, productPrice;
        public ImageView productImage;
        public Spinner weightSpinner, priceSpinner;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productImage = itemView.findViewById(R.id.productImage);
            weightSpinner = itemView.findViewById(R.id.weightSpinner);
            priceSpinner = itemView.findViewById(R.id.priceSpinner);
        }
    }
}
