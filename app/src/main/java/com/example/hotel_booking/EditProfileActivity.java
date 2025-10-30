package com.example.hotel_booking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_booking.data.UserRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etName;
    private Button btnSaveProfile;
    private ImageButton btnBackEdit;
    private CircleImageView ivProfileEdit;

    private UserRepository userRepository;
    private SharedPreferences prefs;
    private Uri newAvatarUri = null;
    private String email;
    private String currentName;
    private EditText etEmailEdit;
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        ivProfileEdit.setImageURI(uri);
                        newAvatarUri = uri;
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi khi lấy ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black, getTheme()));
        setContentView(R.layout.activity_edit_profile);

        userRepository = new UserRepository(this);
        prefs = getSharedPreferences("hotel_auth", MODE_PRIVATE);
        email = prefs.getString("email", "guest@example.com");
        currentName = prefs.getString("full_name", "Guest");

        initViews();
        loadData();

        btnBackEdit.setOnClickListener(v -> finish());
        ivProfileEdit.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSaveProfile.setOnClickListener(v -> updateProfile());
    }

    private void initViews() {
        etName = findViewById(R.id.etNameEdit);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnBackEdit = findViewById(R.id.btnBackEdit);
        ivProfileEdit = findViewById(R.id.ivProfileEdit);
        etEmailEdit = findViewById(R.id.etEmailEdit);
    }

    private void loadData() {
        // Tải tên
        etName.setText(currentName);
        etEmailEdit.setText(email);
        // Tải ảnh
        String avatarUriString = prefs.getString("avatar_uri", null);
        if (avatarUriString != null) {
            try {
                ivProfileEdit.setImageURI(Uri.parse(avatarUriString));
            } catch (Exception e) {
                ivProfileEdit.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            ivProfileEdit.setImageResource(R.mipmap.ic_launcher);
        }
    }

    private void updateProfile() {
        String newName = etName.getText().toString().trim();
        boolean isNameChanged = !newName.isEmpty() && !newName.equals(currentName);
        boolean isAvatarChanged = (newAvatarUri != null);

        if (!isNameChanged && !isAvatarChanged) {
            Toast.makeText(this, "Không có gì thay đổi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cờ báo hiệu có thành công hay không
        final boolean[] nameUpdateSuccess = {true}; // Mặc định là true nếu không đổi tên
        final boolean[] avatarUpdateSuccess = {true}; // Mặc định là true nếu không đổi avatar

        // 1. Cập nhật Tên (nếu có)
        if (isNameChanged) {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                boolean success = userRepository.updateUserName(email, newName);
                if (success) {
                    prefs.edit().putString("full_name", newName).apply();
                    nameUpdateSuccess[0] = true;
                } else {
                    nameUpdateSuccess[0] = false;
                }
            });
        }

        // 2. Cập nhật Ảnh (nếu có)
        if (isAvatarChanged) {
            try {
                prefs.edit().putString("avatar_uri", newAvatarUri.toString()).apply();
                avatarUpdateSuccess[0] = true;
            } catch (Exception e) {
                avatarUpdateSuccess[0] = false;
            }
        }

        // 3. Hiển thị kết quả (Giả sử luồng chạy đủ nhanh, nếu không cần dùng CountDownLatch)
        if (nameUpdateSuccess[0] && avatarUpdateSuccess[0]) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Báo cho ProfileActivity biết để load lại
            finish();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
    }
}