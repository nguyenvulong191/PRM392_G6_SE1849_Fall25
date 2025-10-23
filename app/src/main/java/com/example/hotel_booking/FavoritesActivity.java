package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_booking.adapter.RoomAdapter;
import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.RoomRepository;
import com.example.hotel_booking.data.entity.Room;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private RoomRepository roomRepository;
    private List<Room> favoriteRooms = new ArrayList<>();
    private Button btnBack;
    private TextView tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initViews();
        setupRecyclerView();
        setupClickListeners();

        roomRepository = new RoomRepository(this);
        loadFavorites();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        btnBack = findViewById(R.id.btnBack);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
    }

    private void setupRecyclerView() {
        adapter = new RoomAdapter(favoriteRooms, this::onRoomClick, this::onFavoriteClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadFavorites() {
        AppExecutors.io().execute(() -> {
            List<Room> favorites = roomRepository.getFavoriteRooms();
            runOnUiThread(() -> {
                favoriteRooms.clear();
                favoriteRooms.addAll(favorites);
                adapter.notifyDataSetChanged();

                // Show/hide empty message
                if (favoriteRooms.isEmpty()) {
                    tvEmptyMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmptyMessage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void onRoomClick(Room room) {
        Intent intent = new Intent(this, RoomDetailActivity.class);
        intent.putExtra("room_id", room.getId());
        startActivity(intent);
    }

    private void onFavoriteClick(Room room) {
        AppExecutors.io().execute(() -> {
            roomRepository.updateFavoriteStatus(room.getId(), false);
            runOnUiThread(() -> {
                favoriteRooms.remove(room);
                adapter.notifyDataSetChanged();

                // Update empty message visibility
                if (favoriteRooms.isEmpty()) {
                    tvEmptyMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh favorites when returning from detail
        loadFavorites();
    }
}
