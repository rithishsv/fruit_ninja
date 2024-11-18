package com.example.fruitninja;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);  // Ensure this layout file exists with correct IDs

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Link UI elements
        emailEditText = findViewById(R.id.editTextTextEmailAddress);  // Replace with your actual ID for email field
        passwordEditText = findViewById(R.id.editTextTextPassword);   // Replace with your actual ID for password field
        registerButton = findViewById(R.id.button_login);                 // Replace with your actual ID for login button
        loginButton = findViewById(R.id.button_register);           // Replace with your actual ID for register button

        // Set up click listener for login button
        loginButton.setOnClickListener(view -> loginUser());

        // Set up click listener for register button to go to Home page
        registerButton.setOnClickListener(view -> {
            // Go to the Home screen
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            finish();  // Optional: closes LoginActivity so it's removed from the back stack
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if email or password fields are empty
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful, update UI and navigate to the main screen
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // Login failed, show an error message
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is logged in, navigate to the Home screen
            startActivity(new Intent(LoginActivity.this, Home.class));
            finish();
        }
    }
}
