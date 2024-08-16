package com.yourdomain;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GoGoodAppUI extends Application {

    private static Tab inventoryTab;
    private static Tab recipesTab;
    private final SpoonacularService spoonacularService = new SpoonacularService();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Go Good Application");

        // Initialize the screens
        LoginScreen loginScreen = new LoginScreen();
        InventoryScreen inventoryScreen = new InventoryScreen();

        // Fetch recipes from inventory screen's inventoryItems
        List<Recipe> initialRecipes = fetchInitialRecipes(inventoryScreen.getInventoryItems());
        RecipeScreen recipeScreen = new RecipeScreen(primaryStage, initialRecipes);  // Link the fetched recipes to the recipe screen

        // Initialize TabPane and Tabs
        TabPane tabPane = new TabPane();

        Tab loginTab = createTab("Login", loginScreen.createLoginPane(primaryStage));
        inventoryTab = createTab("Inventory", inventoryScreen.getInventoryPane());
        recipesTab = createTab("Recipes", recipeScreen.getRecipePane());

        inventoryTab.setDisable(true);  // Initially lock these tabs
        recipesTab.setDisable(true);

        tabPane.getTabs().addAll(loginTab, inventoryTab, recipesTab);

        // Set up the scene with the TabPane
        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab createTab(String title, Parent content) {  // Ensure this is Parent
        Tab tab = new Tab(title);
        tab.setContent(content);
        tab.setClosable(false);
        return tab;
    }

    public static void unlockTabs() {
        inventoryTab.setDisable(false);
        recipesTab.setDisable(false);
    }

    // Method to fetch initial recipes based on inventory items, prioritizing items close to expiration
    private List<Recipe> fetchInitialRecipes(ObservableList<InventoryItem> inventoryItems) {
        // Filter the inventory to include only items with expiration dates close to today
        List<InventoryItem> expiringItems = inventoryItems.stream()
                .filter(item -> item.getExpirationDate().isBefore(LocalDate.now().plusDays(3))) // Items expiring within 3 days
                .sorted(Comparator.comparing(InventoryItem::getExpirationDate)) // Sort by expiration date
                .collect(Collectors.toList());

        if (expiringItems.isEmpty()) {
            return FXCollections.observableArrayList(); // Return an empty list if no items are close to expiration
        }

        // Collect the item names to be used as ingredients
        String ingredients = expiringItems.stream()
                .map(InventoryItem::getItemName)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        // Fetch recipes based on the expiring ingredients using the Spoonacular API
        List<Recipe> recipes = spoonacularService.getRecipesByIngredientsSync(ingredients, 10); // Sync call to get recipes

        return recipes != null ? FXCollections.observableArrayList(recipes) : FXCollections.observableArrayList();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
