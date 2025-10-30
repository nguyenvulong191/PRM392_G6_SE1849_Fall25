package com.example.hotel_booking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_booking.data.UserRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etOldPassword, etNewPassword, etConfirmNewPassword;
    private Button btnSavePassword;
    private ImageButton btnBackPass;

    private UserRepository userRepository;
    private SharedPreferences prefs;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đặt màu status bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black, getTheme()));
        setContentView(R.layout.activity_change_password);

        userRepository = new UserRepository(this);
        prefs = getSharedPreferences("hotel_auth", MODE_PRIVATE);
        email = prefs.getString("email", "guest@example.com");

        initViews();

        btnBackPass.setOnClickListener(v -> finish());
        btnSavePassword.setOnClickListener(v -> updatePassword());
    }

    private void initViews() {
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
        btnBackPass = findViewById(R.id.btnBackPass);
    }

    private void updatePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ 3 trường", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải ≥ 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "Mật khẩu mới và xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.equals(oldPassword)) {
            Toast.makeText(this, "Mật khẩu mới không được trùng mật khẩu cũ", Toast.LENGTH_SHORT).show();
            return;
        }
        // --- Kết thúc kiểm tra ---

        // Chạy cập nhật trên luồng nền
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Hàm updatePassword trong Repository đã kiểm tra mật khẩu cũ
            boolean success = userRepository.updatePassword(email, oldPassword, newPassword);

            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Mật khẩu đã được cập nhật", Toast.LENGTH_SHORT).show();
                    // Đặt cờ báo thành công cho ProfileActivity (nếu cần)
                    setResult(RESULT_OK);
                    finish(); // Đóng Activity sau khi thành công
                } else {
                    // Lỗi này có nghĩa là mật khẩu cũ sai
                    Toast.makeText(this, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}