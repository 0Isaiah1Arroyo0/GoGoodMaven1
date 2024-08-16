package com.yourdomain;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class InventoryScreen extends BorderPane {

    private final ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList();
    private final TableView<InventoryItem> inventoryTable = new TableView<>(inventoryItems);
    private final SpoonacularService spoonacularService = new SpoonacularService();
    private final ProgressIndicator progressIndicator = new ProgressIndicator();
    private final Label statusBar = new Label("Ready");

    private static final int PAGE_SIZE = 100;
    private int currentPage = 0;
    private Pagination pagination = new Pagination();

    public InventoryScreen() {
        setPadding(new Insets(10));
        setupInventoryTable();

        HBox actionButtons = createButtonBox();
        HBox searchBox = createSearchBox();

        ScrollPane scrollPane = new ScrollPane(inventoryTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        setTop(new VBox(actionButtons, searchBox));
        setCenter(scrollPane);
        setBottom(pagination);

        configureInventoryTable(); // Method to configure the row styling

        setupPagination();
    }

    private void setupInventoryTable() {
        TableColumn<InventoryItem, String> nameColumn = new TableColumn<>("Item Name");
        nameColumn.setCellValueFactory(data -> data.getValue().itemNameProperty());
        nameColumn.setSortable(true);
        nameColumn.setPrefWidth(150);

        TableColumn<InventoryItem, LocalDate> dateColumn = new TableColumn<>("Expiration Date");
        dateColumn.setCellValueFactory(data -> data.getValue().expirationDateProperty());
        dateColumn.setSortable(true);
        dateColumn.setPrefWidth(150);

        TableColumn<InventoryItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        quantityColumn.setSortable(true);
        quantityColumn.setPrefWidth(100);

        inventoryTable.getColumns().addAll(nameColumn, dateColumn, quantityColumn);
    }

    private void configureInventoryTable() {
        inventoryTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        inventoryTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(InventoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    LocalDate today = LocalDate.now();
                    LocalDate expirationDate = item.getExpirationDate();

                    if (expirationDate.isBefore(today)) {
                        setStyle("-fx-background-color: rgba(0,0,0,0.33); -fx-text-fill: #ff0000;");
                    } else if (expirationDate.isEqual(today)) {
                        setStyle("-fx-background-color: #FF6347; -fx-text-fill: #ffffff;");
                    } else if (expirationDate.isBefore(today.plusDays(3))) {
                        setStyle("-fx-background-color: #FFD700;");
                    } else {
                        setStyle("-fx-background-color: #90EE90;");
                    }
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void setupPagination() {
        pagination.setPageCount((int) Math.ceil((double) inventoryItems.size() / PAGE_SIZE));
        pagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {
        int startIndex = pageIndex * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, inventoryItems.size());

        ObservableList<InventoryItem> currentItems = FXCollections.observableArrayList(inventoryItems.subList(startIndex, endIndex));
        inventoryTable.setItems(currentItems);

        return new VBox(inventoryTable);
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));

        Button addItemButton = createButton("Add Item", "Add a new item to the inventory");
        addItemButton.setOnAction(e -> showAddItemDialog());

        Button removeItemButton = createButton("Remove Item", "Remove selected items from inventory");
        removeItemButton.setOnAction(e -> removeSelectedItem());

        Button batchUpdateButton = createButton("Batch Update", "Update expiration dates for multiple items");
        batchUpdateButton.setOnAction(e -> showBatchUpdateDialog());

        Button editItemButton = createButton("Edit Item", "Edit the selected item");
        editItemButton.setOnAction(e -> showEditItemDialog());

        Button exportButton = createButton("Export to CSV", "Export the inventory to a CSV file");
        exportButton.setOnAction(e -> exportToCSV());

        Button importButton = createButton("Import from CSV", "Import inventory from a CSV file");
        importButton.setOnAction(e -> importFromCSV());

        buttonBox.getChildren().addAll(addItemButton, removeItemButton, batchUpdateButton, editItemButton, exportButton, importButton);

        return buttonBox;
    }

    private HBox createSearchBox() {
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10, 0, 10, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Item Name or Expiration Date");
        searchField.setStyle("-fx-border-color: #5598e7; -fx-border-width: 2px;");
        searchField.setPrefWidth(250);

        Button searchButton = createButton("Search", "Search inventory");
        searchButton.setOnAction(e -> filterInventory(searchField.getText()));

        searchBox.getChildren().addAll(searchField, searchButton);

        return searchBox;
    }

    private Button createButton(String text, String tooltipText) {
        Button button = new Button(text);
        button.setTooltip(new Tooltip(tooltipText));
        button.setStyle("-fx-background-color: #d3d3d3; -fx-text-fill: black; -fx-font-size: 14px; -fx-background-radius: 5px;");
        button.setPrefWidth(120);

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #c0c0c0; -fx-text-fill: black; -fx-font-size: 14px; -fx-background-radius: 5px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #d3d3d3; -fx-text-fill: black; -fx-font-size: 14px; -fx-background-radius: 5px;"));

        return button;
    }

    private void showAddItemDialog() {
        Dialog<InventoryItem> dialog = new Dialog<>();
        dialog.setTitle("Add New Inventory Item");

        Label nameLabel = new Label("Item Name:");
        TextField nameField = new TextField();

        Label dateLabel = new Label("Expiration Date:");
        DatePicker datePicker = new DatePicker();

        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField();

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(dateLabel, 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(quantityLabel, 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String itemName = nameField.getText();
                LocalDate expirationDate = datePicker.getValue();
                int quantity;

                try {
                    quantity = Integer.parseInt(quantityField.getText());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Please enter a valid integer for quantity.");
                    return null;
                }

                InventoryItem newItem = new InventoryItem(itemName, expirationDate, quantity);

                if (expirationDate.isBefore(LocalDate.now())) {
                    showAlert(Alert.AlertType.ERROR, "Item Expired", "The item you are adding is already expired.");
                } else if (expirationDate.isBefore(LocalDate.now().plusDays(3))) {
                    showAlert(Alert.AlertType.WARNING, "Expiring Soon", "This item is expiring soon! Consider generating recipes to avoid waste.");
                }

                return newItem;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(item -> {
            inventoryItems.add(item);
            setupPagination();
        });
    }

    private void removeSelectedItem() {
        ObservableList<InventoryItem> selectedItems = inventoryTable.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            inventoryItems.removeAll(selectedItems);
            setupPagination();
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select item(s) to remove.");
        }
    }

    private void showBatchUpdateDialog() {
        ObservableList<InventoryItem> selectedItems = inventoryTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select item(s) for batch update.");
            return;
        }

        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle("Batch Update Expiration Dates");

        Label dateLabel = new Label("New Expiration Date:");
        DatePicker datePicker = new DatePicker();

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(dateLabel, 0, 0);
        grid.add(datePicker, 1, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return datePicker.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newDate -> {
            for (InventoryItem item : selectedItems) {
                LocalDate oldDate = item.getExpirationDate();
                item.setExpirationDate(newDate);

                if (newDate.isBefore(LocalDate.now().plusDays(3))) {
                    showAlert(Alert.AlertType.WARNING, "Expiring Soon", "The expiration date has been updated to a date that is soon. Consider generating recipes to avoid waste.");
                }
            }
            inventoryTable.refresh();
        });
    }

    private void showEditItemDialog() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to edit.");
            return;
        }

        Dialog<InventoryItem> dialog = new Dialog<>();
        dialog.setTitle("Edit Inventory Item");

        Label nameLabel = new Label("Item Name:");
        TextField nameField = new TextField(selectedItem.getItemName());

        Label dateLabel = new Label("Expiration Date:");
        DatePicker datePicker = new DatePicker(selectedItem.getExpirationDate());

        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField(String.valueOf(selectedItem.getQuantity()));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(dateLabel, 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(quantityLabel, 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                selectedItem.setItemName(nameField.getText());
                selectedItem.setExpirationDate(datePicker.getValue());
                selectedItem.setQuantity(Integer.parseInt(quantityField.getText()));

                if (datePicker.getValue().isBefore(LocalDate.now().plusDays(3))) {
                    showAlert(Alert.AlertType.WARNING, "Expiring Soon", "This item is expiring soon after the edit. Consider generating recipes to avoid waste.");
                }

                return selectedItem;
            }
            return null;
        });

        dialog.showAndWait();
        inventoryTable.refresh();
    }

    private void filterInventory(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            inventoryTable.setItems(inventoryItems);
            return;
        }

        ObservableList<InventoryItem> filteredList = FXCollections.observableArrayList();
        for (InventoryItem item : inventoryItems) {
            if (item.getItemName().toLowerCase().contains(searchText.toLowerCase()) ||
                    item.getExpirationDate().toString().contains(searchText)) {
                filteredList.add(item);
            }
        }
        inventoryTable.setItems(filteredList);
    }

    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Inventory to CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.append("Item Name,Expiration Date,Quantity\n");
                for (InventoryItem item : inventoryItems) {
                    writer.append(String.format("%s,%s,%d\n", item.getItemName(), item.getExpirationDate(), item.getQuantity()));
                }
                showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Inventory exported to CSV successfully.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Export Failed", "An error occurred while exporting inventory.");
            }
        }
    }

    private void importFromCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Inventory CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(",");
                    if (fields.length == 3) {
                        String itemName = fields[0];
                        LocalDate expirationDate = LocalDate.parse(fields[1]);
                        int quantity = Integer.parseInt(fields[2]);

                        InventoryItem newItem = new InventoryItem(itemName, expirationDate, quantity);
                        inventoryItems.add(newItem);
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Import Successful", "Inventory imported from CSV successfully.");
                setupPagination();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Import Failed", "An error occurred while importing inventory.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getInventoryPane() {
        return this;
    }

    public ObservableList<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }
}
