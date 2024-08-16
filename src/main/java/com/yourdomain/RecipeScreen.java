package com.yourdomain;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class RecipeScreen {
    private final ObservableList<Recipe> recipes = FXCollections.observableArrayList();
    private final TableView<Recipe> recipeTable = new TableView<>(recipes);
    private final SpoonacularService spoonacularService = new SpoonacularService();
    private final Stage primaryStage;
    private final ProgressIndicator progressIndicator = new ProgressIndicator();
    private final Label statusBar = new Label("Ready");

    public RecipeScreen(Stage primaryStage, List<Recipe> recipes) {
        this.primaryStage = primaryStage;
        this.recipes.setAll(recipes);  // Set the initial recipes in the TableView
        setupRecipeTable();
        setupUI();
        progressIndicator.setVisible(false); // Hide the progress indicator initially
    }

    private void setupRecipeTable() {
        TableColumn<Recipe, String> nameColumn = new TableColumn<>("Recipe Name");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        nameColumn.setSortable(true);

        TableColumn<Recipe, String> ingredientsColumn = new TableColumn<>("Used Ingredients");
        ingredientsColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getUsedIngredients().stream()
                        .map(Ingredient::getName)
                        .reduce((a, b) -> a + ", " + b).orElse("None")
        ));
        ingredientsColumn.setSortable(true);

        TableColumn<Recipe, String> missedIngredientsColumn = new TableColumn<>("Missed Ingredients");
        missedIngredientsColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMissedIngredients().stream()
                        .map(Ingredient::getName)
                        .reduce((a, b) -> a + ", " + b).orElse("None")
        ));
        missedIngredientsColumn.setSortable(true);

        recipeTable.getColumns().addAll(nameColumn, ingredientsColumn, missedIngredientsColumn);
        recipeTable.setPlaceholder(new Label("No recipes found. Enter ingredients and click 'Fetch Recipes' to get started."));
    }

    private void setupUI() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(15, 20, 10, 20));

        HBox searchBox = createSearchBox();
        HBox ingredientBox = createIngredientBox();
        HBox buttonBox = createButtonBox();

        BorderPane root = new BorderPane();
        root.setTop(layout);
        root.setCenter(recipeTable);
        root.setBottom(statusBar);

        layout.getChildren().addAll(searchBox, ingredientBox, buttonBox, progressIndicator);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createSearchBox() {
        TextField searchField = new TextField();
        searchField.setPromptText("Search Recipes");

        searchField.setOnAction(e -> {
            String searchTerm = searchField.getText();
            if (searchTerm != null && !searchTerm.isEmpty()) {
                searchRecipesByName(searchTerm);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Search Term", "Please enter a recipe name to search.");
            }
        });

        HBox searchBox = new HBox(10);
        searchBox.getChildren().addAll(searchField);
        return searchBox;
    }

    private HBox createIngredientBox() {
        TextField ingredientsField = new TextField();
        ingredientsField.setPromptText("Enter ingredients, separated by commas");

        Button fetchRecipesButton = new Button("Fetch Recipes");
        fetchRecipesButton.setOnAction(e -> {
            String ingredients = ingredientsField.getText();
            if (ingredients != null && !ingredients.isEmpty()) {
                statusBar.setText("Fetching recipes...");
                progressIndicator.setVisible(true); // Show progress indicator
                fetchRecipesByIngredients(ingredients);
                ingredientsField.clear();
            } else {
                showAlert(Alert.AlertType.WARNING, "No Ingredients", "Please enter some ingredients to generate recipes.");
            }
        });

        HBox ingredientBox = new HBox(10);
        ingredientBox.getChildren().addAll(ingredientsField, fetchRecipesButton);
        return ingredientBox;
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(15);

        Button createCardButton = new Button("Create Recipe Card");
        createCardButton.setOnAction(e -> {
            Recipe selectedRecipe = recipeTable.getSelectionModel().getSelectedItem();
            if (selectedRecipe != null) {
                progressIndicator.setVisible(true); // Show progress indicator
                createRecipeCard(selectedRecipe.getId());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Recipe Selected", "Please select a recipe to create a card.");
            }
        });

        Button visualizeNutritionButton = new Button("Visualize Nutrition");
        visualizeNutritionButton.setOnAction(e -> {
            Recipe selectedRecipe = recipeTable.getSelectionModel().getSelectedItem();
            if (selectedRecipe != null) {
                progressIndicator.setVisible(true); // Show progress indicator
                visualizeNutrition(selectedRecipe.getId());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Recipe Selected", "Please select a recipe to visualize nutrition.");
            }
        });

        Button visualizeIngredientsButton = new Button("Visualize Ingredients");
        visualizeIngredientsButton.setOnAction(e -> {
            Recipe selectedRecipe = recipeTable.getSelectionModel().getSelectedItem();
            if (selectedRecipe != null) {
                progressIndicator.setVisible(true); // Show progress indicator
                visualizeIngredients(selectedRecipe.getId());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Recipe Selected", "Please select a recipe to visualize ingredients.");
            }
        });

        Button extractRecipeButton = new Button("Extract Recipe");
        extractRecipeButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Extract Recipe");
            dialog.setHeaderText("Enter Recipe URL");
            dialog.setContentText("URL:");
            dialog.showAndWait().ifPresent(url -> {
                progressIndicator.setVisible(true); // Show progress indicator
                extractRecipeFromUrl(url);
            });
        });

        Button getSubstitutesButton = new Button("Get Ingredient Substitutes");
        getSubstitutesButton.setOnAction(e -> {
            Recipe selectedRecipe = recipeTable.getSelectionModel().getSelectedItem();
            if (selectedRecipe != null) {
                progressIndicator.setVisible(true); // Show progress indicator
                getIngredientSubstitutes(selectedRecipe.getId());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Recipe Selected", "Please select a recipe to get ingredient substitutes.");
            }
        });

        buttonBox.getChildren().addAll(createCardButton, visualizeNutritionButton, visualizeIngredientsButton, extractRecipeButton, getSubstitutesButton);
        return buttonBox;
    }

    private void searchRecipesByName(String recipeName) {
        spoonacularService.searchRecipesByName(recipeName, new SpoonacularService.SpoonacularCallback() {
            @Override
            public void onSuccess(Object result) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    List<Recipe> fetchedRecipes = (List<Recipe>) result;
                    if (fetchedRecipes != null && !fetchedRecipes.isEmpty()) {
                        recipes.setAll(fetchedRecipes);
                        statusBar.setText("Recipes fetched successfully.");
                    } else {
                        showAlert(Alert.AlertType.INFORMATION, "No Recipes Found", "No recipes found with the given name.");
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    statusBar.setText("Error fetching recipes.");
                    showAlert(Alert.AlertType.ERROR, "Error Fetching Recipes", "Failed to fetch recipes: " + throwable.getMessage());
                });
            }
        });
    }

    private void fetchRecipesByIngredients(String ingredients) {
        spoonacularService.getRecipesByIngredientsAsync(ingredients, 10, new SpoonacularService.SpoonacularCallback() {
            @Override
            public void onSuccess(Object result) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    List<Recipe> fetchedRecipes = (List<Recipe>) result;
                    if (fetchedRecipes != null && !fetchedRecipes.isEmpty()) {
                        recipes.setAll(fetchedRecipes);
                        statusBar.setText("Recipes fetched successfully.");
                    } else {
                        showAlert(Alert.AlertType.INFORMATION, "No Recipes Found", "No recipes found with the given ingredients.");
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    statusBar.setText("Error fetching recipes.");
                    showAlert(Alert.AlertType.ERROR, "Error Fetching Recipes", "Failed to fetch recipes: " + throwable.getMessage());
                });
            }
        });
    }

    private void createRecipeCard(int recipeId) {
        spoonacularService.createRecipeCard(recipeId, new SpoonacularService.SpoonacularCallback() {
            @Override
            public void onSuccess(Object result) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    String cardUrl = (String) result;
                    showHyperlinkAlert("Recipe Card Created", "Recipe card created successfully.", cardUrl);
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    statusBar.setText("Error creating recipe card.");
                    showAlert(Alert.AlertType.ERROR, "Error Creating Recipe Card", "Failed to create recipe card: " + throwable.getMessage());
                });
            }
        });
    }

    private void visualizeNutrition(int recipeId) {
        spoonacularService.getRecipeNutrition(recipeId, new SpoonacularService.SpoonacularCallback() {
            @Override
            public void onSuccess(Object result) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    String nutritionInfoUrl = result.toString();
                    showHyperlinkAlert("Nutrition Information", "Nutrition information for the recipe.", nutritionInfoUrl);
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    statusBar.setText("Error fetching nutrition information.");
                    showAlert(Alert.AlertType.ERROR, "Error Fetching Nutrition", "Failed to fetch nutrition information: " + throwable.getMessage());
                });
            }
        });
    }

    private void visualizeIngredients(int recipeId) {
        spoonacularService.getRecipeIngredients(recipeId, new SpoonacularService.SpoonacularCallback() {
            @Override
            public void onSuccess(Object result) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    String ingredientsInfoUrl = result.toString();
                    showHyperlinkAlert("Ingredients Information", "Ingredients information for the recipe.", ingredientsInfoUrl);
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    statusBar.setText("Error fetching ingredients information.");
                    showAlert(Alert.AlertType.ERROR, "Error Fetching Ingredients", "Failed to fetch ingredients information: " + throwable.getMessage());
                });
            }
        });
    }

    private void extractRecipeFromUrl(String url) {
        spoonacularService.extractRecipeFromUrl(url, new SpoonacularService.SpoonacularCallback() {
            @Override
            public void onSuccess(Object result) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    Recipe extractedRecipe = (Recipe) result;
                    showAlert(Alert.AlertType.INFORMATION, "Recipe Extracted", "Recipe extracted successfully: " + extractedRecipe.getTitle());
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    statusBar.setText("Error extracting recipe.");
                    showAlert(Alert.AlertType.ERROR, "Error Extracting Recipe", "Failed to extract recipe: " + throwable.getMessage());
                });
            }
        });
    }

    private void getIngredientSubstitutes(int recipeId) {
        spoonacularService.getIngredientSubstitutes(String.valueOf(recipeId), new SpoonacularService.SpoonacularCallback() {
            @Override
            public void onSuccess(Object result) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    String substitutesInfoUrl = result.toString();
                    showHyperlinkAlert("Ingredient Substitutes", "Substitutes for the ingredients.", substitutesInfoUrl);
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    statusBar.setText("Error fetching ingredient substitutes.");
                    showAlert(Alert.AlertType.ERROR, "Error Fetching Substitutes", "Failed to fetch ingredient substitutes: " + throwable.getMessage());
                });
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showHyperlinkAlert(String title, String header, String url) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);

        Hyperlink link = new Hyperlink(url);
        link.setOnAction(event -> openUrl(url));

        VBox content = new VBox(link);
        content.setPadding(new Insets(10));
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    private void openUrl(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to open URL: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Opening URLs is not supported on this system.");
        }
    }

    public Parent getRecipePane() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(15, 20, 10, 20));

        HBox searchBox = createSearchBox();
        HBox ingredientBox = createIngredientBox();
        HBox buttonBox = createButtonBox();

        BorderPane root = new BorderPane();
        root.setTop(layout);
        root.setCenter(recipeTable);
        root.setBottom(statusBar);

        layout.getChildren().addAll(searchBox, ingredientBox, buttonBox, progressIndicator);

        return root;  // Return the BorderPane that contains the UI components
    }
}
