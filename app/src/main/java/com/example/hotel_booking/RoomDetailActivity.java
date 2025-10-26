package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_booking.adapter.GalleryAdapter;
import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.RoomRepository;
import com.example.hotel_booking.data.entity.Room;

import java.util.Arrays;
import java.util.List;

public class RoomDetailActivity extends AppCompatActivity {
    private TextView tvName, tvDescription, tvPrice, tvLocation, tvRating, tvAmenities, tvCapacity, tvRoomType;
    private ImageView ivFavorite;
    private RecyclerView recyclerGallery;
    private Button btnBack, btnBook;
    private RoomRepository roomRepository;
    private Room currentRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        initViews();
        setupClickListeners();
        roomRepository = new RoomRepository(this);

        int roomId = getIntent().getIntExtra("room_id", -1);
        if (roomId != -1) {
            loadRoomDetail(roomId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy phòng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        tvLocation = findViewById(R.id.tvLocation);
        tvRating = findViewById(R.id.tvRating);
        tvAmenities = findViewById(R.id.tvAmenities);
        tvCapacity = findViewById(R.id.tvCapacity);
        tvRoomType = findViewById(R.id.tvRoomType);
        ivFavorite = findViewById(R.id.ivFavorite);
        recyclerGallery = findViewById(R.id.recyclerGallery);
        btnBack = findViewById(R.id.btnBack);
        btnBook = findViewById(R.id.btnBook);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadRoomDetail(int roomId) {
        AppExecutors.io().execute(() -> {
            currentRoom = roomRepository.getRoomById(roomId);
            runOnUiThread(() -> {
                if (currentRoom != null) {
                    displayRoomDetail();
                    setupGallery();
                } else {
                    Toast.makeText(this, "Không tìm thấy thông tin phòng", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void displayRoomDetail() {
        tvName.setText(currentRoom.getName());
        tvDescription.setText(currentRoom.getDescription());
        tvPrice.setText(String.format("$%.0f/đêm", currentRoom.getPrice()));
        tvLocation.setText("📍 " + currentRoom.getLocation());
        tvRating.setText(String.format("⭐ %d/5", currentRoom.getRating()));
        tvCapacity.setText(String.format("👥 %d người", currentRoom.getCapacity()));
        tvRoomType.setText("🏨 " + currentRoom.getRoomType());

        // Display amenities in a formatted way
        String amenities = currentRoom.getAmenities();
        if (amenities != null && !amenities.isEmpty()) {
            String[] amenityList = amenities.split(",");
            StringBuilder formattedAmenities = new StringBuilder();
            for (String amenity : amenityList) {
                formattedAmenities.append("✓ ").append(amenity.trim()).append("\n");
            }
            tvAmenities.setText(formattedAmenities.toString());
        }

        // Set favorite icon
        updateFavoriteIcon();

        // Set favorite click listener
        ivFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void setupGallery() {
        // Parse gallery images from JSON string
        String gallery = currentRoom.getGallery();
        List<String> galleryImages;

        if (gallery != null && !gallery.isEmpty()) {
            galleryImages = Arrays.asList(gallery.split(","));
        } else {
            // Fallback to main image
            galleryImages = Arrays.asList(currentRoom.getImageUrl());
        }

        GalleryAdapter galleryAdapter = new GalleryAdapter(galleryImages);
        recyclerGallery.setLayoutManager(new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false));
        recyclerGallery.setAdapter(galleryAdapter);
    }

    private void updateFavoriteIcon() {
        ivFavorite.setImageResource(currentRoom.isFavorite() ?
            android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
    }

    private void toggleFavorite() {
        AppExecutors.io().execute(() -> {
            roomRepository.updateFavoriteStatus(currentRoom.getId(), !currentRoom.isFavorite());
            currentRoom.setFavorite(!currentRoom.isFavorite());
            runOnUiThread(() -> {
                updateFavoriteIcon();
                Toast.makeText(this,
                    currentRoom.isFavorite() ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích",
                    Toast.LENGTH_SHORT).show();
            });
        });
    }

    public void bookRoom(View view) {
        Intent intent = new Intent(RoomDetailActivity.this, BookingActivity.class);
        intent.putExtra("room_id", currentRoom.getId());
        intent.putExtra("room_name", currentRoom.getName());
        intent.putExtra("room_price", currentRoom.getPrice());
        intent.putExtra("room_type", currentRoom.getRoomType());
        intent.putExtra("room_capacity", currentRoom.getCapacity());
        startActivity(intent);
    }
}
