package com.example.hotel_booking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.appbar.MaterialToolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvEmail, btnEditProfile, btnChangePassword;
    private CircleImageView ivProfile;
    private SwitchMaterial switchTheme;
    private SharedPreferences prefs;

    // === Launcher MỚI (Giữ nguyên) ===
    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadData();
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

        initViews();

        // === THÊM MỚI: SETUP TOOLBAR ===
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadData();

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        switchTheme.setChecked(isDarkMode());
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            applyTheme();
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarProfile);
        ivProfile = findViewById(R.id.ivProfile);
        tvEmail = findViewById(R.id.tvEmailValue);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        switchTheme = findViewById(R.id.switchTheme);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Hoặc finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        tvEmail.setText(prefs.getString("email", "guest@example.com"));

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