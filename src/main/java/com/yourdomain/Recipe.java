package com.yourdomain;

import java.util.List;
import java.util.stream.Collectors;

public class Recipe {
    private int id;
    private String title;
    private String image;
    private List<Ingredient> usedIngredients;
    private List<Ingredient> missedIngredients;
    private List<Ingredient> extendedIngredients;  // Add this line to store extended ingredients
    private int missedIngredientCount;
    private int usedIngredientCount;
    private int likes;
    private int cookTime;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Ingredient> getUsedIngredients() {
        return usedIngredients;
    }

    public void setUsedIngredients(List<Ingredient> usedIngredients) {
        this.usedIngredients = usedIngredients;
    }

    public List<Ingredient> getMissedIngredients() {
        return missedIngredients;
    }

    public void setMissedIngredients(List<Ingredient> missedIngredients) {
        this.missedIngredients = missedIngredients;
    }

    public List<Ingredient> getExtendedIngredients() {
        return extendedIngredients;
    }

    public void setExtendedIngredients(List<Ingredient> extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }

    public int getMissedIngredientCount() {
        return missedIngredientCount;
    }

    public void setMissedIngredientCount(int missedIngredientCount) {
        this.missedIngredientCount = missedIngredientCount;
    }

    public int getUsedIngredientCount() {
        return usedIngredientCount;
    }

    public void setUsedIngredientCount(int usedIngredientCount) {
        this.usedIngredientCount = usedIngredientCount;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    // Method to combine all ingredients into a single string
    public String getIngredients() {
        String used = usedIngredients.stream()
                .map(Ingredient::getName)
                .collect(Collectors.joining(", "));
        String missed = missedIngredients.stream()
                .map(Ingredient::getName)
                .collect(Collectors.joining(", "));
        String extended = extendedIngredients != null ? extendedIngredients.stream()
                .map(Ingredient::getName)
                .collect(Collectors.joining(", ")) : "";

        return used + (missed.isEmpty() ? "" : ", ") + missed + (extended.isEmpty() ? "" : ", ") + extended;
    }
}
