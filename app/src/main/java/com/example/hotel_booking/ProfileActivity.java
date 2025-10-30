package com.example.hotel_booking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnBackProfile;
    private TextView tvEmail, btnEditProfile, btnChangePassword;
    private CircleImageView ivProfile;
    private SwitchMaterial switchTheme;
    private SharedPreferences prefs;

    // === Launcher MỚI: Lắng nghe kết quả từ EditProfileActivity ===
    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Nếu EditProfileActivity báo OK, load lại data
                    loadData();

                    // Báo cho MainActivity cũng load lại (quan trọng)
                    setResult(RESULT_OK);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Tải theme TRƯỚC khi setContentView
        prefs = getSharedPreferences("hotel_auth", MODE_PRIVATE);
        applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black, getTheme()));

        initViews();
        loadData();

        // Xử lý các nút bấm
        btnBackProfile.setOnClickListener(v -> onBackPressed());

        btnEditProfile.setOnClickListener(v -> {
            // Mở EditProfileActivity và chờ kết quả
            Intent intent = new Intent(this, EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        btnChangePassword.setOnClickListener(v -> {
            // Mở ChangePasswordActivity (không cần chờ kết quả)
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        // Xử lý Nút gạt Dark Mode
        switchTheme.setChecked(isDarkMode());
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            applyTheme();
        });
    }

    private void initViews() {
        btnBackProfile = findViewById(R.id.btnBackProfile);
        ivProfile = findViewById(R.id.ivProfile);
        tvEmail = findViewById(R.id.tvEmailValue);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        switchTheme = findViewById(R.id.switchTheme);
    }

    // Hàm load data chung
    private void loadData() {
        // Tải email
        tvEmail.setText(prefs.getString("email", "guest@example.com"));

        // Tải ảnh đại diện
        String avatarUriString = prefs.getString("avatar_uri", null);
        if (avatarUriString != null) {
            try {
                ivProfile.setImageURI(Uri.parse(avatarUriString));
            } catch (Exception e) {
                ivProfile.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            ivProfile.setImageResource(R.mipmap.ic_launcher);
        }
    }

    // Hàm kiểm tra và áp dụng theme
    private boolean isDarkMode() {
        return prefs.getBoolean("dark_mode", true); // Mặc định là tối
    }

    private void applyTheme() {
        if (isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}