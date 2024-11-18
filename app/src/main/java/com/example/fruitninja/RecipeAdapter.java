package com.example.fruitninja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final Context context;
    private final List<JSONObject> recipes;

    public RecipeAdapter(Context context, List<JSONObject> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject recipe = recipes.get(position);

        try {
            // Assuming JSON has a "title" field for recipe name
            holder.recipeName.setText(recipe.getString("title"));

            // Example: Display number of used and missed ingredients if available
            holder.usedIngredients.setText("Used ingredients: " + recipe.getJSONArray("usedIngredients").length());
            holder.missedIngredients.setText("Missed ingredients: " + recipe.getJSONArray("missedIngredients").length());

        } catch (JSONException e) {
            e.printStackTrace();
            holder.recipeName.setText("Error loading recipe");
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName;
        TextView usedIngredients;
        TextView missedIngredients;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            usedIngredients = itemView.findViewById(R.id.usedIngredients);
            missedIngredients = itemView.findViewById(R.id.missedIngredients);
        }
    }
}
