package com.example.fruitninja;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    private ListView lvItems;
    private Button btnAddNewItem;
    private EditText etNewItem;
    private ArrayList<InventoryItem> itemList;
    private InventoryAdapter adapter;
    private DatabaseReference dbUncheckedItemsRef, dbShoppingListRef;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        lvItems = findViewById(R.id.lvUncheckedItems);
        btnAddNewItem = findViewById(R.id.btnAddNewItem);
        etNewItem = findViewById(R.id.etNewItem);

        // Initialize Firebase Authentication and get the current user ID
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
            dbUncheckedItemsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("uncheckedItems");
            dbShoppingListRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("shoppingList");
        } else {
            // Redirect to login if the user is not authenticated
            finish();
        }

        // Initialize item list and custom adapter
        itemList = new ArrayList<>();
        adapter = new InventoryAdapter(this, itemList, userId);
        lvItems.setAdapter(adapter);

        // Load items from Firebase
        loadItemsFromDatabase();

        // Add new item to Firebase
        btnAddNewItem.setOnClickListener(v -> {
            String newItemName = etNewItem.getText().toString().trim();
            if (!newItemName.isEmpty()) {
                addItemToFirebase(newItemName);
                etNewItem.setText("");  // Clear input field
            } else {
                Toast.makeText(this, "Enter an item name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadItemsFromDatabase() {
        itemList.clear();

        // Load unchecked items
        dbUncheckedItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    InventoryItem item = dataSnapshot.getValue(InventoryItem.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(InventoryActivity.this, "Failed to load unchecked items.", Toast.LENGTH_SHORT).show();
            }
        });

        // Load shopping list items
        dbShoppingListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    InventoryItem item = dataSnapshot.getValue(InventoryItem.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(InventoryActivity.this, "Failed to load shopping list items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItemToFirebase(String itemName) {
        String itemId = dbUncheckedItemsRef.push().getKey();
        InventoryItem newItem = new InventoryItem(itemId, itemName, 1);

        if (itemId != null) {
            dbUncheckedItemsRef.child(itemId).setValue(newItem).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    itemList.add(newItem);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
                    Log.d("FirebaseData", "Item added: " + newItem.toString());  // Log added item
                } else {
                    Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseData", "Failed to add item");
                }
            });
        } else {
            Log.e("FirebaseData", "Item ID is null");
        }
    }


}
