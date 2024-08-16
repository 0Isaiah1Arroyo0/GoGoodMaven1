package com.yourdomain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class InventoryManagement {

    private final ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList();
    private final TableView<InventoryItem> inventoryTable = new TableView<>(inventoryItems);

    public void InventoryScreen() {
        setupInventoryTable();
    }

    private void setupInventoryTable() {
        // TODO: Set up table columns and data binding
    }

    public void addItem(InventoryItem item) {
        inventoryItems.add(item);
    }

    public void removeItem(InventoryItem item) {
        inventoryItems.remove(item);
    }

    public void updateItem(InventoryItem oldItem, InventoryItem newItem) {
        int index = inventoryItems.indexOf(oldItem);
        if (index >= 0) {
            inventoryItems.set(index, newItem);
        }
    }
}
