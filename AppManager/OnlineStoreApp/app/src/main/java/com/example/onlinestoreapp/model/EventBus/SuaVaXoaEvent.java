package com.example.onlinestoreapp.model.EventBus;

import com.example.onlinestoreapp.model.NewProduct;

public class SuaVaXoaEvent {
    NewProduct newProduct;

    public SuaVaXoaEvent(NewProduct newProduct) {
        this.newProduct = newProduct;
    }

    public NewProduct getNewProduct() {
        return newProduct;
    }

    public void setNewProduct(NewProduct newProduct) {
        this.newProduct = newProduct;
    }
}
