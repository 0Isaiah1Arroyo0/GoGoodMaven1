package com.yourdomain;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GoGoodApp extends Application {

    private final List<InventoryItem> inventory = new ArrayList<>();
    private final SpoonacularService spoonacularService = new SpoonacularService();
    private TextArea recipeOutput;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Go Good App");

        // Inventory management UI elements
        VBox inventoryUI = new VBox();
        inventoryUI.getChildren().add(new Label("Inventory:"));

        // Sample items to the invetory to verify it works
        inventory.add(new InventoryItem("apples", java.time.LocalDate.now().plusDays(2), 5));
        inventory.add(new InventoryItem("flour", java.time.LocalDate.now().plusWeeks(4), 1));
        inventory.add(new InventoryItem("sugar", java.time.LocalDate.now().plusWeeks(8), 2));

        // Display inventory
        for (InventoryItem item : inventory) {
            inventoryUI.getChildren().add(new Label(item.getItemName() + " - " + item.getQuantity() + " - Exp: " + item.getExpirationDate()));
        }

        // Button to fetch recipes based on inventory
        Button fetchRecipesButton = new Button("Get Recipes");
        fetchRecipesButton.setOnAction(e -> fetchRecipes());

        // Text area to display recipes
        recipeOutput = new TextArea();
        recipeOutput.setEditable(false);

        VBox root = new VBox();
        root.getChildren().addAll(inventoryUI, fetchRecipesButton, recipeOutput);

        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    @SuppressWarnings("unchecked")
    private void fetchRecipes() {
        // Dynamically build the ingredients list from the inventory
        StringBuilder ingredientsBuilder = new StringBuilder();
        for (InventoryItem item : inventory) {
            if (ingredientsBuilder.length() > 0) {
                ingredientsBuilder.append(",");
            }
            ingredientsBuilder.append(item.getItemName());
        }
        String ingredients = ingredientsBuilder.toString();

        recipeOutput.setText("Fetching recipes...");

        // Correctly call getRecipesByIngredientsAsync with the ingredients string
        spoonacularService.getRecipesByIngredientsAsync(ingredients, 10, new SpoonacularService.SpoonacularCallback() {
            @Override
            public void onSuccess(Object result) {
                Platform.runLater(() -> {
                    recipeOutput.clear();
                    List<Recipe> recipes = (List<Recipe>) result;
                    for (Recipe recipe : recipes) {
                        recipeOutput.appendText(recipe.getTitle() + "\n");
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> recipeOutput.setText("Error fetching recipes: " + throwable.getMessage()));
            }
        });
    }
}
