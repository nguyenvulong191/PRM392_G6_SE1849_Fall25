package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_booking.adapter.RoomAdapter;
import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.RoomRepository;
import com.example.hotel_booking.data.entity.Room;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private RoomRepository roomRepository;
    private List<Room> favoriteRooms = new ArrayList<>();
    private LinearLayout emptyLayout;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initViews();
        setupToolbar();
        setupRecyclerView();

        roomRepository = new RoomRepository(this);
        loadFavorites();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        emptyLayout = findViewById(R.id.emptyLayout);
        toolbar = findViewById(R.id.topAppBar);
    }

    private void setupToolbar() {
        // Apply window insets for status bar
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            int minus = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics());
            int top = Math.max(0, bars.top - minus);
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Phòng yêu thích");
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new RoomAdapter(favoriteRooms, this::onRoomClick, this::onFavoriteClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
                    emptyLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyLayout.setVisibility(View.GONE);
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
                    emptyLayout.setVisibility(View.VISIBLE);
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
