package com.example.fruitninja;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingViewHolder> {

    private final ArrayList<ShoppingItem> shoppingList;

    public ShoppingListAdapter(ArrayList<ShoppingItem> shoppingList) {
        this.shoppingList = shoppingList;
    }

    @NonNull
    @Override
    public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false);
        return new ShoppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingViewHolder holder, int position) {
        ShoppingItem item = shoppingList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText("Quantity: " + item.getQuantity());
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public static class ShoppingViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity;

        public ShoppingViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
        }
    }
}
