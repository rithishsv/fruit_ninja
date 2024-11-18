package com.example.fruitninja;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseHelper extends AppCompatActivity {

    private ListView lvUncheckedItems;
    private Button btnAddNewItem;
    private EditText etNewItem;
    private ArrayList<String> uncheckedItemList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        lvUncheckedItems = findViewById(R.id.lvUncheckedItems);
        btnAddNewItem = findViewById(R.id.btnAddNewItem);
        etNewItem = findViewById(R.id.etNewItem);

        // Initialize Firebase Database reference
        dbRef = FirebaseDatabase.getInstance().getReference("uncheckedItems");

        // Initialize item list and adapter
        uncheckedItemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, uncheckedItemList);
        lvUncheckedItems.setAdapter(adapter);
        lvUncheckedItems.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Load unchecked items from Firebase
        loadUncheckedItems();

        // Add new item to the Firebase database and list
        btnAddNewItem.setOnClickListener(v -> {
            String newItem = etNewItem.getText().toString().trim();
            if (!newItem.isEmpty()) {
                addItemToFirebase(newItem);
                etNewItem.setText("");  // Clear the input field
            } else {
                Toast.makeText(DatabaseHelper.this, "Enter an item", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle item check-off
        lvUncheckedItems.setOnItemClickListener((parent, view, position, id) -> {
            String item = uncheckedItemList.get(position);
            moveItemToChecked(item);
            uncheckedItemList.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(DatabaseHelper.this, "Item checked and moved to database", Toast.LENGTH_SHORT).show();
        });
    }

    // Load items from Firebase
    private void loadUncheckedItems() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                uncheckedItemList.clear();  // Clear the list before loading new data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String item = dataSnapshot.getValue(String.class);
                    uncheckedItemList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(DatabaseHelper.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Add new item to Firebase
    private void addItemToFirebase(String item) {
        String itemId = dbRef.push().getKey();  // Generate unique key
        dbRef.child(itemId).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                uncheckedItemList.add(item);
                adapter.notifyDataSetChanged();
                Toast.makeText(DatabaseHelper.this, "Item added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DatabaseHelper.this, "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Move item to checked items list in Firebase and remove from unchecked items
    private void moveItemToChecked(String item) {
        DatabaseReference checkedItemsRef = FirebaseDatabase.getInstance().getReference("checkedItems");
        String itemId = dbRef.push().getKey();  // Generate unique key

        // Add item to checked items and remove from unchecked items
        checkedItemsRef.child(itemId).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove item from the "uncheckedItems" list in Firebase
                dbRef.orderByValue().equalTo(item).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            childSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(DatabaseHelper.this, "Failed to move item", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(DatabaseHelper.this, "Failed to move item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
