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
import com.example.hotel_booking.data.entity.User;

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
                User u = repo.loginAndGet(email, pass);

                runOnUiThread(() -> {
                    if (u != null) {
                        getSharedPreferences("hotel_auth", MODE_PRIVATE)
                                .edit()
                                .putBoolean("logged_in", true)
                                .putInt("user_id", u.getId())     // QUAN TRỌNG
                                .putString("email", email)
                                .putString("full_name", u.getName())
                                .apply();

                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        tvToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }
}
