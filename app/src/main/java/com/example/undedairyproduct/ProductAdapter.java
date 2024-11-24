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

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private ArrayList<Product> productList;

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());

        // Set the price and weight for the first price-weight combination (example)
        if (!product.getPriceWeightCombinations().isEmpty()) {
            PriceWeightCombination firstCombination = product.getPriceWeightCombinations().get(0);
            holder.productPrice.setText(String.valueOf(firstCombination.getPrice()));
        }

        // Set up the Image using Glide
        Glide.with(context)
                .load(product.getImageUri())
                .into(holder.productImage);

        // Set up the spinner with weights (assuming the first price-weight combination for now)
        ArrayList<String> weights = new ArrayList<>();
        for (PriceWeightCombination combination : product.getPriceWeightCombinations()) {
            weights.add(combination.getWeight());
        }
        // Setup your spinner with the weights, if applicable
        // You can use an ArrayAdapter to bind the weights to the spinner
        holder.weightSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, weights));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, productPrice;
        public ImageView productImage;
        public Spinner weightSpinner;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productNameDisplay);
            productPrice = itemView.findViewById(R.id.productPriceDisplay);
            productImage = itemView.findViewById(R.id.productImage);
            weightSpinner = itemView.findViewById(R.id.weightSpinner);  // Assuming you have a spinner for weights
        }
    }
}
