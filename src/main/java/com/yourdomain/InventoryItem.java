package com.yourdomain;

import javafx.beans.property.*;

import java.time.LocalDate;

public class InventoryItem {
    private final StringProperty itemName;
    private final ObjectProperty<LocalDate> expirationDate;
    private final IntegerProperty quantity;

    public InventoryItem(String itemName, LocalDate expirationDate, int quantity) {
        this.itemName = new SimpleStringProperty(itemName);
        this.expirationDate = new SimpleObjectProperty<>(expirationDate);
        this.quantity = new SimpleIntegerProperty(quantity);
    }

    public String getItemName() {
        return itemName.get();
    }

    public StringProperty itemNameProperty() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName.set(itemName);
    }

    public LocalDate getExpirationDate() {
        return expirationDate.get();
    }

    public ObjectProperty<LocalDate> expirationDateProperty() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate.set(expirationDate);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }
}