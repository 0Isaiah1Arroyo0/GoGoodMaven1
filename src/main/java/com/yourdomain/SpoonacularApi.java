package com.yourdomain;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

import java.util.List;

public interface SpoonacularApi {

    @GET("recipes/findByIngredients")
    Call<List<Recipe>> getRecipesByIngredients(
            @Query("ingredients") String ingredients,
            @Query("number") int number,
            @Query("apiKey") String apiKey
    );

    @GET("food/ingredients/substitutes")
    Call<IngredientSubstituteResponse> getIngredientSubstitutes(
            @Query("ingredientName") String ingredientName,
            @Query("apiKey") String apiKey
    );

    @GET("recipes/complexSearch")
    Call<RecipeSearchResponse> searchRecipes(
            @Query("query") String query,
            @Query("apiKey") String apiKey
    );

    @GET("recipes/{id}/information")
    Call<Recipe> getRecipeInformation(
            @Path("id") int recipeId,
            @Query("apiKey") String apiKey
    );

    @GET("recipes/{id}/nutritionWidget.json")
    Call<NutritionResponse> getRecipeNutrition(
            @Path("id") int recipeId,
            @Query("apiKey") String apiKey
    );

    @GET("recipes/{id}/card")
    Call<RecipeCardResponse> createRecipeCard(
            @Path("id") int recipeId,
            @Query("mask") String mask,
            @Query("backgroundImage") String backgroundImage,
            @Query("backgroundColor") String backgroundColor,
            @Query("fontColor") String fontColor,
            @Query("apiKey") String apiKey
    );

    @GET
    Call<Recipe> extractRecipe(
            @Url String url,
            @Query("apiKey") String apiKey
    );
}
