package com.example.fruitninja;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {

    private EditText etNewShoppingItem;
    private Button btnAddShoppingItem;
    private RecyclerView recyclerViewShoppingList;
    private ShoppingListAdapter adapter;
    private ArrayList<ShoppingItem> shoppingList;
    private DatabaseReference dbRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        etNewShoppingItem = findViewById(R.id.etNewShoppingItem);
        btnAddShoppingItem = findViewById(R.id.btnAddShoppingItem);
        recyclerViewShoppingList = findViewById(R.id.recyclerViewShoppingList);

        // Initialize Firebase Auth and Database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("shoppingList");
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initialize RecyclerView
        shoppingList = new ArrayList<>();
        adapter = new ShoppingListAdapter(shoppingList);
        recyclerViewShoppingList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewShoppingList.setAdapter(adapter);

        // Load existing items from Firebase
        loadShoppingListFromDatabase();

        // Add new item to Firebase
        btnAddShoppingItem.setOnClickListener(v -> {
            String itemName = etNewShoppingItem.getText().toString().trim();
            if (!itemName.isEmpty()) {
                addShoppingItemToDatabase(itemName);
                etNewShoppingItem.setText("");  // Clear input field
            } else {
                Toast.makeText(this, "Enter an item name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadShoppingListFromDatabase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shoppingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ShoppingItem item = dataSnapshot.getValue(ShoppingItem.class);
                    shoppingList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShoppingListActivity.this, "Failed to load shopping list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addShoppingItemToDatabase(String itemName) {
        String itemId = dbRef.push().getKey();
        ShoppingItem newItem = new ShoppingItem(itemName, 1);  // Default quantity of 1
        if (itemId != null) {
            dbRef.child(itemId).setValue(newItem).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    shoppingList.add(newItem);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Item added to shopping list", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
