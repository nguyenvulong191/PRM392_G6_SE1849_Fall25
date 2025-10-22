package com.example.hotel_booking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.UserRepository;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UserRepository repo = new UserRepository(this);

        EditText etName = findViewById(R.id.etName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirm = findViewById(R.id.etConfirm);
        Button btnRegister = findViewById(R.id.btnRegisterSubmit);
        TextView tvToLogin = findViewById(R.id.tvToLogin);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim().toLowerCase();
            String pass = etPassword.getText().toString();
            String conf = etConfirm.getText().toString();

            if (name.isEmpty()) {
                etName.setError("Nhập tên");
                return;
            }
            if (email.isEmpty() || !email.contains("@")) {
                etEmail.setError("Email không hợp lệ");
                return;
            }
            if (pass.length() < 6) {
                etPassword.setError("Mật khẩu ≥ 6 ký tự");
                return;
            }
            if (!pass.equals(conf)) {
                etConfirm.setError("Mật khẩu xác nhận không khớp");
                return;
            }

            // chạy DB trên thread nền
            AppExecutors.io().execute(() -> {
                if (repo.isEmailExists(email)) {
                    runOnUiThread(() -> etEmail.setError("Email đã tồn tại"));
                    return;
                }
                long rowId = repo.insertUser(name, email, pass);
                runOnUiThread(() -> {
                    if (rowId > 0) {
                        android.widget.Toast.makeText(this, "Đăng ký thành công!", android.widget.Toast.LENGTH_SHORT).show();
                        finish(); // quay về Login
                    } else {
                        android.widget.Toast.makeText(this, "Lỗi khi đăng ký!", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        tvToLogin.setOnClickListener(v -> finish()); // quay lại Login
    }
}
