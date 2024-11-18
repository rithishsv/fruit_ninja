package com.example.fruitninja;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeDisplayActivity extends AppCompatActivity {

    private RecyclerView rvRecipes;
    private RecipeAdapter recipeAdapter;
    private List<JSONObject> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_display);

        rvRecipes = findViewById(R.id.rvRecipes);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        recipes = new ArrayList<>();

        // Retrieve ingredients from intent
        List<String> selectedIngredients = getIntent().getStringArrayListExtra("selectedIngredients");

        if (selectedIngredients != null && !selectedIngredients.isEmpty()) {
            String ingredients = String.join(",", selectedIngredients);
            fetchRecipesFromAPI(ingredients);
        } else {
            Toast.makeText(this, "No ingredients selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchRecipesFromAPI(String ingredients) {
        new Thread(() -> {
            String apiKey = "YOUR_SPOONACULAR_API_KEY"; // Replace with your Spoonacular API Key
            String apiUrl = "https://api.spoonacular.com/recipes/findByIngredients?ingredients=" + ingredients + "&number=5&apiKey=" + apiKey;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                displayRecipes(response.toString());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch recipes.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void displayRecipes(String jsonResponse) {
        try {
            JSONArray recipeArray = new JSONArray(jsonResponse);
            recipes.clear();
            for (int i = 0; i < recipeArray.length(); i++) {
                recipes.add(recipeArray.getJSONObject(i));
            }

            runOnUiThread(() -> {
                recipeAdapter = new RecipeAdapter(this, recipes);

                rvRecipes.setAdapter(recipeAdapter);
            });

        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Error parsing recipe data.", Toast.LENGTH_SHORT).show());
        }
    }
}
