package com.example.undedairyproduct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class ProductViewModel extends ViewModel {
    private MutableLiveData<List<Product>> products;

    public LiveData<List<Product>> getProducts() {
        if (products == null) {
            products = new MutableLiveData<>(new ArrayList<>());
        }
        return products;
    }

    public void addProduct(Product product) {
        if (products.getValue() != null) {
            List<Product> currentList = products.getValue();
            currentList.add(product);
            products.setValue(currentList);
        }
    }
}
