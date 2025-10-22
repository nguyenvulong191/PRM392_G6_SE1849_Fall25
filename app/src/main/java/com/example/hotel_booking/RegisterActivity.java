package com.example.hotel_booking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etName = findViewById(R.id.etName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirm = findViewById(R.id.etConfirm);
        Button btnRegister = findViewById(R.id.btnRegisterSubmit);
        TextView tvToLogin = findViewById(R.id.tvToLogin);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass  = etPassword.getText().toString();
            String conf  = etConfirm.getText().toString();

            if (name.isEmpty()) { etName.setError("Nhập tên"); return; }
            if (email.isEmpty() || !email.contains("@")) { etEmail.setError("Email không hợp lệ"); return; }
            if (pass.length() < 6) { etPassword.setError("Mật khẩu ≥ 6 ký tự"); return; }
            if (!pass.equals(conf)) { etConfirm.setError("Mật khẩu xác nhận không khớp"); return; }

            // TODO: Xử lý gọi API/DB thật ở bước sau
            Toast.makeText(this, "Form OK (chưa xử lý backend)", Toast.LENGTH_SHORT).show();
        });

        tvToLogin.setOnClickListener(v -> finish()); // quay lại Login
    }
}
