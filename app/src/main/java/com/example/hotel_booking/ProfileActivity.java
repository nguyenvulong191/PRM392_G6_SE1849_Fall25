package com.example.hotel_booking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_booking.data.UserRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {
    // 1. Thêm biến cho EditText xác nhận
    private EditText etName, etOldPassword, etNewPassword, etConfirmNewPassword;
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

        String email = prefs.getString("email", "guest@example.com");
        String name = prefs.getString("full_name", "Guest");

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
        // 2. Khởi tạo EditText mới (giả sử ID là etConfirmNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    // 3. Cập nhật logic kiểm tra
    private void updateProfile() {
        String newName = etName.getText().toString().trim();
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        // Lấy text từ trường xác nhận
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        boolean isUpdatingName = !newName.isEmpty();
        // Kiểm tra xem người dùng có ý định đổi mật khẩu không
        // (tức là điền vào bất kỳ trường mật khẩu nào)
        boolean isAttemptingPasswordUpdate = !oldPassword.isEmpty() || !newPassword.isEmpty() || !confirmNewPassword.isEmpty();

        // Nếu không nhập gì cả
        if (!isUpdatingName && !isAttemptingPasswordUpdate) {
            Toast.makeText(this, "Vui lòng nhập thông tin cần thay đổi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật tên (nếu có)
        if (isUpdatingName) {
            updateUserName(newName);
        }

        // Xử lý cập nhật mật khẩu (nếu có)
        if (isAttemptingPasswordUpdate) {
            // Bắt lỗi thiếu trường
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "Để đổi mật khẩu, vui lòng nhập đủ 3 trường", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bắt lỗi mật khẩu mới và xác nhận không khớp
            if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(this, "Mật khẩu mới và xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // (Tùy chọn) Kiểm tra mật khẩu mới trùng mật khẩu cũ
            if (newPassword.equals(oldPassword)) {
                Toast.makeText(this, "Mật khẩu mới không được trùng mật khẩu cũ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Nếu mọi thứ OK, gọi hàm cập nhật
            updatePassword(oldPassword, newPassword);
        }
    }

    // Hàm updateUserName không đổi (đã sửa từ lần trước)
    private void updateUserName(String newName) {
        String email = prefs.getString("email", "guest@example.com");

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Hàm này trong UserRepository đã được sửa để trả về boolean
                boolean success = userRepository.updateUserName(email, newName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            prefs.edit().putString("full_name", newName).apply();
                            tvName.setText("Tên: " + newName);

                            Toast.makeText(ProfileActivity.this, "Tên đã được cập nhật", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                        } else {
                            Toast.makeText(ProfileActivity.this, "Cập nhật tên không thành công", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    // Hàm updatePassword không đổi (đã sửa từ lần trước, dùng logic check mật khẩu cũ)
    private void updatePassword(String oldPassword, String newPassword) {
        String email = prefs.getString("email", "guest@example.com");

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Hàm này trong UserRepository đã được sửa để check 3 tham số
                boolean success = userRepository.updatePassword(email, oldPassword, newPassword);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(ProfileActivity.this, "Mật khẩu đã được cập nhật", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                        } else {
                            // Lỗi này giờ có nghĩa là mật khẩu cũ sai
                            Toast.makeText(ProfileActivity.this, "Mật khẩu cũ không đúng hoặc có lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}