package com.example.hotel_booking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.hotel_booking.data.UserRepository;

public class ProfileActivity extends AppCompatActivity {
    private EditText etName, etOldPassword, etNewPassword;
    private Button btnUpdate;
    private UserRepository userRepository;
    private SharedPreferences prefs;
    private TextView tvName, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        userRepository = new UserRepository(this);
        prefs = getSharedPreferences("hotel_auth", MODE_PRIVATE);

        // Lấy thông tin từ SharedPreferences (email và tên)
        String email = prefs.getString("email", "guest@example.com");
        String name = prefs.getString("full_name", "Guest");

        // Hiển thị thông tin hiện tại
        tvName.setText("Tên: " + name);
        tvEmail.setText("Email: " + email);

        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmailValue);
        etName = findViewById(R.id.etName);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void updateProfile() {
        String newName = etName.getText().toString().trim();
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (newName.isEmpty() && oldPassword.isEmpty() && newPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thông tin cần thay đổi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newName.isEmpty()) {
            updateUserName(newName);
        }

        if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
            updatePassword(oldPassword, newPassword);
        }
    }

    private void updateUserName(String newName) {
        String email = prefs.getString("email", "guest@example.com");

        // Kiểm tra và gọi repository để cập nhật tên
        if (userRepository.updateUserName(email, newName)) {
            prefs.edit().putString("full_name", newName).apply(); // Cập nhật tên trong SharedPreferences

            Toast.makeText(this, "Tên đã được cập nhật", Toast.LENGTH_SHORT).show();
            tvName.setText("Tên: " + newName);  // Cập nhật giao diện
        } else {
            Toast.makeText(this, "Cập nhật tên không thành công", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePassword(String oldPassword, String newPassword) {
        String email = prefs.getString("email", "guest@example.com");

        // Kiểm tra mật khẩu cũ và cập nhật mật khẩu mới
        if (userRepository.checkLogin(email, oldPassword)) {
            if (userRepository.updatePassword(email, newPassword)) {
                Toast.makeText(this, "Mật khẩu đã được cập nhật", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật mật khẩu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
        }
    }

}
