package com.example.hotel_booking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_booking.data.UserRepository;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etName;
    private Button btnSaveProfile;
    private CircleImageView ivProfileEdit;
    private EditText etEmailEdit;

    private MaterialToolbar toolbar;

    private UserRepository userRepository;
    private SharedPreferences prefs;
    private Uri newAvatarUri = null;
    private String email;
    private String currentName;

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
        setContentView(R.layout.activity_edit_profile);

        userRepository = new UserRepository(this);
        prefs = getSharedPreferences("hotel_auth", MODE_PRIVATE);
        email = prefs.getString("email", "guest@example.com");
        currentName = prefs.getString("full_name", "Guest");

        initViews();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadData();

        ivProfileEdit.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSaveProfile.setOnClickListener(v -> updateProfile());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarEditProfile);
        etName = findViewById(R.id.etNameEdit);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        ivProfileEdit = findViewById(R.id.ivProfileEdit);
        etEmailEdit = findViewById(R.id.etEmailEdit);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadData() {
        etName.setText(currentName);
        etEmailEdit.setText(email);
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

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            boolean nameUpdateOK = true;
            boolean avatarUpdateOK = true;

            if (isNameChanged) {
                boolean success = userRepository.updateUserName(email, newName);
                if (success) {
                    prefs.edit().putString("full_name", newName).apply();
                    nameUpdateOK = true;
                } else {
                    nameUpdateOK = false;
                }
            }

            if (isAvatarChanged) {
                try {
                    prefs.edit().putString("avatar_uri", newAvatarUri.toString()).apply();
                    avatarUpdateOK = true;
                } catch (Exception e) {
                    avatarUpdateOK = false;
                }
            }

            boolean finalNameUpdateOK = nameUpdateOK;
            boolean finalAvatarUpdateOK = avatarUpdateOK;

            runOnUiThread(() -> {
                if (finalNameUpdateOK && finalAvatarUpdateOK) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật tên thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}