package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        boolean loggedIn = getSharedPreferences("hotel_auth", MODE_PRIVATE).getBoolean("logged_in", false);
        if (loggedIn) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnSignIn = findViewById(R.id.btnSignIn);

        findViewById(R.id.btnSignIn).setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        findViewById(R.id.btnRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

    }
}
