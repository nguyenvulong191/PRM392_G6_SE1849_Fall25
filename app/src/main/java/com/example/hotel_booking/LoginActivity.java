package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.UserRepository;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvToRegister = findViewById(R.id.tvToRegister);
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        UserRepository repo = new UserRepository(this);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim().toLowerCase();
            String pass = etPassword.getText().toString();

            if (email.isEmpty()) {
                etEmail.setError("Nhập email");
                return;
            }
            if (!email.contains("@")) {
                etEmail.setError("Email không hợp lệ");
                return;
            }
            if (pass.length() < 6) {
                etPassword.setError("Mật khẩu ≥ 6 ký tự");
                return;
            }

            AppExecutors.io().execute(() -> {
                boolean ok = repo.checkLogin(email, pass);
                runOnUiThread(() -> {
                    if (ok) {
                        Intent i = new Intent(this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        android.widget.Toast.makeText(this, "Sai email hoặc mật khẩu", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        tvToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }
}
