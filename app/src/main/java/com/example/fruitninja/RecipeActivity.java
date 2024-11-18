package com.example.fruitninja;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private Button btnShowInventory, btnGetRecipes;
    private RecyclerView rvIngredients, rvRecipes;
    private DatabaseReference dbRef;
    private IngredientAdapter ingredientAdapter;
    private RecipeAdapter recipeAdapter;
    private List<String> ingredientsList = new ArrayList<>();
    private List<JSONObject> recipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        btnShowInventory = findViewById(R.id.btnShowInventory);
        btnGetRecipes = findViewById(R.id.btnGetRecipes);

        // Set up RecyclerViews
        setupRecyclerView();

        // Initialize Firebase Auth and database reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("uncheckedItems");
        } else {
            Toast.makeText(this, "Please log in to access inventory.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up button listeners
        btnShowInventory.setOnClickListener(v -> loadInventoryItems());

        btnGetRecipes.setOnClickListener(v -> {
            if (ingredientAdapter == null || ingredientAdapter.getSelectedIngredients().isEmpty()) {
                Toast.makeText(this, "Please select ingredients first.", Toast.LENGTH_SHORT).show();
            } else {
                fetchRecipesFromSelectedItems();
            }
        });
    }

    private void setupRecyclerView() {
        rvIngredients = findViewById(R.id.rvIngredients);
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));

        rvRecipes = findViewById(R.id.rvRecipes);
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));

        ingredientAdapter = new IngredientAdapter(ingredientsList);
        rvIngredients.setAdapter(ingredientAdapter);
    }

    private void loadInventoryItems() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ingredientsList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    InventoryItem inventoryItem = item.getValue(InventoryItem.class);
                    if (inventoryItem != null && inventoryItem.getName() != null) {
                        ingredientsList.add(inventoryItem.getName());
                    }
                }

                Log.d("RecipeActivity", "Loaded ingredients: " + ingredientsList.toString());
                ingredientAdapter.notifyDataSetChanged();

                Toast.makeText(RecipeActivity.this, "Items loaded successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipeActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
                Log.e("RecipeActivity", "DatabaseError: " + error.getMessage());
            }
        });
    }

    private void fetchRecipesFromSelectedItems() {
        List<String> selectedIngredients = ingredientAdapter.getSelectedIngredients();

        if (selectedIngredients.isEmpty()) {
            Toast.makeText(this, "Please select at least one ingredient", Toast.LENGTH_SHORT).show();
            return;
        }

        String ingredients = String.join(",", selectedIngredients);

        new Thread(() -> {
            String recipesJson = searchRecipes(ingredients);
            runOnUiThread(() -> displayRecipes(recipesJson));
        }).start();
    }

    private String searchRecipes(String ingredients) {
        String apiKey = "e108496e0ad848c180c7b6ace1a95676";
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

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to fetch recipes.";
        }
    }

    private void displayRecipes(String jsonResponse) {
        recipes.clear();
        try {
            JSONArray recipeArray = new JSONArray(jsonResponse);
            for (int i = 0; i < recipeArray.length(); i++) {
                JSONObject recipe = recipeArray.getJSONObject(i);
                recipes.add(recipe);
            }

            recipeAdapter = new RecipeAdapter(this, recipes);
            rvRecipes.setAdapter(recipeAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing recipe data.", Toast.LENGTH_SHORT).show();
        }
    }
}
