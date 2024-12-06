package com.example.undedairyproduct;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a combination of weight and price for a product.
 */
public class PriceWeightCombination {
    private String weight;  // Example: "3kg", "300gm"
    private String price;   // Example: "300", "304"

    /**
     * Constructor to initialize weight and price.
     *
     * @param weight the weight of the product (e.g., "3kg")
     * @param price the price corresponding to the weight (e.g., "300")
     */
    public PriceWeightCombination(String weight, String price) {
        this.weight = weight;
        this.price = price;
    }

    // Getter and setter for weight
    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    // Getter and setter for price
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * Converts the PriceWeightCombination object into a JSON string.
     *
     * @return a JSON representation of the object
     */
    public String toJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("weight", weight);
            jsonObject.put("price", price);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}"; // Return an empty JSON object in case of error
        }
    }

    /**
     * Creates a PriceWeightCombination object from a JSON string.
     *
     * @param json the JSON string representing a PriceWeightCombination
     * @return a new PriceWeightCombination object or null if parsing fails
     */
    public static PriceWeightCombination fromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String weight = jsonObject.getString("weight");
            String price = jsonObject.getString("price");
            return new PriceWeightCombination(weight, price);
        } catch (JSONException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }

    @Override
    public String toString() {
        return "PriceWeightCombination{" +
                "weight='" + weight + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
