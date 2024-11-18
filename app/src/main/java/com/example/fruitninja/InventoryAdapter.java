package com.example.fruitninja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class InventoryAdapter extends ArrayAdapter<InventoryItem> {

    private DatabaseReference dbRef;

    public InventoryAdapter(Context context, ArrayList<InventoryItem> items, String userId) {
        super(context, 0, items);
        dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_inventory, parent, false);
        }

        InventoryItem item = getItem(position);

        TextView itemName = convertView.findViewById(R.id.itemName);
        TextView itemQuantity = convertView.findViewById(R.id.itemQuantity);
        Button btnIncreaseQuantity = convertView.findViewById(R.id.btnIncreaseQuantity);
        Button btnDecreaseQuantity = convertView.findViewById(R.id.btnDecreaseQuantity);

        if (item != null) {
            itemName.setText(item.getName());
            itemQuantity.setText(String.valueOf(item.getQuantity()));

            // Increase Quantity
            btnIncreaseQuantity.setOnClickListener(v -> {
                item.setQuantity(item.getQuantity() + 1);
                updateItemInFirebase(item);
                itemQuantity.setText(String.valueOf(item.getQuantity()));
            });

            // Decrease Quantity
            btnDecreaseQuantity.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    updateItemInFirebase(item);
                    itemQuantity.setText(String.valueOf(item.getQuantity()));
                } else {
                    // Remove item from Firebase if quantity is less than 1
                    removeItemFromFirebase(item);
                    remove(item); // Remove item from the list adapter
                    notifyDataSetChanged();
                    Toast.makeText(getContext(), "Item removed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return convertView;
    }

    private void updateItemInFirebase(InventoryItem item) {
        // Update item quantity in Firebase
        dbRef.child("uncheckedItems").child(item.getId()).setValue(item);
    }

    private void removeItemFromFirebase(InventoryItem item) {
        // Remove item from Firebase
        dbRef.child("uncheckedItems").child(item.getId()).removeValue();
    }
}
