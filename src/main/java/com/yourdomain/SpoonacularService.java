package com.yourdomain;

import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SpoonacularService {
    private static final String BASE_URL = "https://api.spoonacular.com/";
    private static final String API_KEY = "92eea0947b9e49919ee05b1dd2045793"; // Replace with your actual API key (future forkers)
    private final SpoonacularApi spoonacularApi;

    public SpoonacularService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        spoonacularApi = retrofit.create(SpoonacularApi.class);
    }

    public void getRecipesByIngredientsAsync(String ingredients, int number, SpoonacularCallback callback) {
        Call<List<Recipe>> call = spoonacularApi.getRecipesByIngredients(ingredients, number, API_KEY);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<List<Recipe>> call, @NotNull Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        String errorBody = Objects.requireNonNull(response.errorBody()).string();
                        callback.onError(new Exception("Failed to fetch recipes: " + errorBody));
                    } catch (IOException e) {
                        callback.onError(new Exception("Failed to fetch recipes: Unable to parse error body"));
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Recipe>> call, @NotNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    public List<Recipe> getRecipesByIngredientsSync(String ingredients, int number) {
        // Create a call to the Spoonacular API
        Call<List<Recipe>> call = spoonacularApi.getRecipesByIngredients(ingredients, number, API_KEY);

        try {
            // Execute the call synchronously
            Response<List<Recipe>> response = call.execute();

            if (response.isSuccessful()) {
                // Return the list of recipes if the call is successful
                return response.body();
            } else {
                // Log an error message if the call was not successful
                System.err.println("Failed to fetch recipes: " + Objects.requireNonNull(response.errorBody()).string());
            }
        } catch (IOException e) {
            // Handle any IO exceptions that occur during the API call
            e.printStackTrace();
        }

        // Return null or an empty list if the call fails
        return null;
    }

    public void searchRecipesByName(String recipeName, SpoonacularCallback callback) {
        Call<RecipeSearchResponse> call = spoonacularApi.searchRecipes(recipeName, API_KEY);

        call.enqueue(new Callback<RecipeSearchResponse>() {
            @Override
            public void onResponse(Call<RecipeSearchResponse> call, Response<RecipeSearchResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().getResults());
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        callback.onError(new Exception("Failed to search recipes: " + errorBody));
                    } catch (IOException e) {
                        callback.onError(new Exception("Failed to search recipes: Unable to parse error body"));
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void createRecipeCard(int recipeId, SpoonacularCallback callback) {
        String mask = "ellipseMask";
        String backgroundImage = "background1";
        String backgroundColor = "ffffff";
        String fontColor = "333333";

        Call<RecipeCardResponse> call = spoonacularApi.createRecipeCard(recipeId, mask, backgroundImage, backgroundColor, fontColor, API_KEY);

        call.enqueue(new Callback<RecipeCardResponse>() {
            @Override
            public void onResponse(Call<RecipeCardResponse> call, Response<RecipeCardResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().getUrl());
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        callback.onError(new Exception("Failed to create recipe card: " + errorBody));
                    } catch (IOException e) {
                        callback.onError(new Exception("Failed to create recipe card: Unable to parse error body"));
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeCardResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void getRecipeNutrition(int recipeId, SpoonacularCallback callback) {
        Call<NutritionResponse> call = spoonacularApi.getRecipeNutrition(recipeId, API_KEY);

        call.enqueue(new Callback<NutritionResponse>() {
            @Override
            public void onResponse(Call<NutritionResponse> call, Response<NutritionResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        callback.onError(new Exception("Failed to get recipe nutrition: " + errorBody));
                    } catch (IOException e) {
                        callback.onError(new Exception("Failed to get recipe nutrition: Unable to parse error body"));
                    }
                }
            }

            @Override
            public void onFailure(Call<NutritionResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void getRecipeIngredients(int recipeId, SpoonacularCallback callback) {
        Call<Recipe> call = spoonacularApi.getRecipeInformation(recipeId, API_KEY);

        call.enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().getExtendedIngredients());
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        callback.onError(new Exception("Failed to get recipe ingredients: " + errorBody));
                    } catch (IOException e) {
                        callback.onError(new Exception("Failed to get recipe ingredients: Unable to parse error body"));
                    }
                }
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void extractRecipeFromUrl(String url, SpoonacularCallback callback) {
        Call<Recipe> call = spoonacularApi.extractRecipe(url, API_KEY);

        call.enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        callback.onError(new Exception("Failed to extract recipe: " + errorBody));
                    } catch (IOException e) {
                        callback.onError(new Exception("Failed to extract recipe: Unable to parse error body"));
                    }
                }
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void getIngredientSubstitutes(String ingredientName, SpoonacularCallback callback) {
        Call<IngredientSubstituteResponse> call = spoonacularApi.getIngredientSubstitutes(ingredientName, API_KEY);

        call.enqueue(new Callback<IngredientSubstituteResponse>() {
            @Override
            public void onResponse(Call<IngredientSubstituteResponse> call, Response<IngredientSubstituteResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        callback.onError(new Exception("Failed to get ingredient substitutes: " + errorBody));
                    } catch (IOException e) {
                        callback.onError(new Exception("Failed to get ingredient substitutes: Unable to parse error body"));
                    }
                }
            }

            @Override
            public void onFailure(Call<IngredientSubstituteResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface SpoonacularCallback {
        void onSuccess(Object result);
        void onError(Throwable throwable);
    }
}
