package com.example.undedairyproduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product with a name, image URI, and multiple price-weight combinations.
 */
public class Product {
    private String name; // Name of the product
    private String imageUri; // Image URI or resource path for the product
    private List<PriceWeightCombination> priceWeightCombinations; // List of price-weight combinations

    /**
     * Constructor for creating a Product instance.
     *
     * @param name                   the name of the product
     * @param imageUri               the image URI
     * @param priceWeightCombinations the list of price-weight combinations
     */
    public Product(String name, String imageUri, List<PriceWeightCombination> priceWeightCombinations) {
        this.name = name;
        this.imageUri = imageUri;
        this.priceWeightCombinations = priceWeightCombinations;
    }

    /**
     * Adds a price-weight combination to the product.
     *
     * @param weight the weight (e.g., "1kg")
     * @param price  the price corresponding to the weight (e.g., "100")
     */
    public void addPriceWeightCombination(String weight, String price) {
        if (this.priceWeightCombinations == null) {
            this.priceWeightCombinations = new ArrayList<>();
        }
        this.priceWeightCombinations.add(new PriceWeightCombination(weight, price));
    }

    // Getter and setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public List<PriceWeightCombination> getPriceWeightCombinations() {
        return priceWeightCombinations;
    }

    public void setPriceWeightCombinations(List<PriceWeightCombination> priceWeightCombinations) {
        this.priceWeightCombinations = priceWeightCombinations;
    }

    /**
     * Converts the Product object into a JSON string.
     *
     * @return JSON representation of the Product
     */
    public String toJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("imageUri", imageUri);

            JSONArray jsonArray = new JSONArray();
            for (PriceWeightCombination combination : priceWeightCombinations) {
                jsonArray.put(new JSONObject(combination.toJson()));
            }
            jsonObject.put("priceWeightCombinations", jsonArray);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}"; // Return empty JSON object in case of error
        }
    }

    /**
     * Creates a Product object from a JSON string.
     *
     * @param json the JSON string representing a Product
     * @return a new Product object or null if parsing fails
     */
    public static Product fromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String name = jsonObject.getString("name");
            String imageUri = jsonObject.getString("imageUri");

            List<PriceWeightCombination> combinations = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("priceWeightCombinations");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject combinationJson = jsonArray.getJSONObject(i);
                PriceWeightCombination combination = PriceWeightCombination.fromJson(combinationJson.toString());
                combinations.add(combination);
            }

            return new Product(name, imageUri, combinations);
        } catch (JSONException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", priceWeightCombinations=" + priceWeightCombinations +
                '}';
    }
}
