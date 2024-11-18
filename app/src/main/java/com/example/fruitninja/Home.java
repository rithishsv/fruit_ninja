package com.example.fruitninja;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {

    private ImageView profileImage;
    private TextView homeTitle;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase Auth and get current user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize UI components
        profileImage = findViewById(R.id.profileImage);
        homeTitle = findViewById(R.id.homeTitle);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set welcome message with user's name
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            homeTitle.setText("Welcome, " + (userName != null ? userName : "User") + "!");
        } else {
            homeTitle.setText("Welcome, User!");
        }

        // Set up bottom navigation actions
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_inventory:
                    Toast.makeText(Home.this, "Inventory selected", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Home.this, InventoryActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_shopping:
                    Toast.makeText(Home.this, "Shopping List selected", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Home.this, ShoppingListActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_recipe:
                    Toast.makeText(Home.this, "Recipe selected", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Home.this, RecipeActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_location:
                    Toast.makeText(Home.this, "Location selected", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Home.this, LocationActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        });
    }
}
