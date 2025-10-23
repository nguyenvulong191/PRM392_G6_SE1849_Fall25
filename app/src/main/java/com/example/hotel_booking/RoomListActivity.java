package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_booking.adapter.RoomAdapter;
import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.RoomRepository;
import com.example.hotel_booking.data.entity.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private RoomRepository roomRepository;
    private List<Room> roomList = new ArrayList<>();
    private ProgressBar progressBar;
    private Button btnLoadMore, btnFilter, btnBack;
    private EditText etLocation, etMinPrice, etMaxPrice;
    private Spinner spinnerRoomType, spinnerSort;

    private int currentPage = 0;
    private final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private boolean isFiltering = false;
    private String currentSortBy = "price_asc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        initViews();
        setupRecyclerView();
        setupSpinners();
        setupClickListeners();

        roomRepository = new RoomRepository(this);
        loadRooms();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        btnLoadMore = findViewById(R.id.btnLoadMore);
        btnFilter = findViewById(R.id.btnFilter);
        btnBack = findViewById(R.id.btnBack);
        etLocation = findViewById(R.id.etLocation);
        etMinPrice = findViewById(R.id.etMinPrice);
        etMaxPrice = findViewById(R.id.etMaxPrice);
        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        spinnerSort = findViewById(R.id.spinnerSort);
    }

    private void setupRecyclerView() {
        adapter = new RoomAdapter(roomList, this::onRoomClick, this::onFavoriteClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSpinners() {
        // Room type spinner
        String[] roomTypes = {"Tất cả", "Single", "Double", "Suite", "Deluxe", "Family"};
        ArrayAdapter<String> roomTypeAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, roomTypes);
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoomType.setAdapter(roomTypeAdapter);

        // Sort spinner
        String[] sortOptions = {"Giá tăng dần", "Giá giảm dần", "Đánh giá cao nhất"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);
    }

    private void setupClickListeners() {
        btnLoadMore.setOnClickListener(v -> loadMoreRooms());
        btnFilter.setOnClickListener(v -> filterRooms());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadRooms() {
        if (isLoading) return;
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        AppExecutors.io().execute(() -> {
            List<Room> newRooms = roomRepository.getRoomsPaginated(currentSortBy, PAGE_SIZE, currentPage * PAGE_SIZE);
            runOnUiThread(() -> {
                roomList.addAll(newRooms);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                btnLoadMore.setVisibility(newRooms.size() == PAGE_SIZE ? View.VISIBLE : View.GONE);
            });
        });
    }

    private void loadMoreRooms() {
        currentPage++;
        if (isFiltering) {
            filterRooms();
        } else {
            loadRooms();
        }
    }

    private void filterRooms() {
        String location = etLocation.getText().toString().trim();
        String minPriceStr = etMinPrice.getText().toString().trim();
        String maxPriceStr = etMaxPrice.getText().toString().trim();
        String roomType = spinnerRoomType.getSelectedItem().toString();
        currentSortBy = getSortBy();

        Double minPrice = null;
        Double maxPrice = null;

        try {
            if (!minPriceStr.isEmpty()) {
                minPrice = Double.parseDouble(minPriceStr);
            }
            if (!maxPriceStr.isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceStr);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (roomType.equals("Tất cả")) roomType = null;
        if (location.isEmpty()) location = null;

        if (!isFiltering) {
            currentPage = 0;
            roomList.clear();
            isFiltering = true;
        }

        // Make variables final for lambda
        final String finalLocation = location;
        final Double finalMinPrice = minPrice;
        final Double finalMaxPrice = maxPrice;
        final String finalRoomType = roomType;

        progressBar.setVisibility(View.VISIBLE);

        AppExecutors.io().execute(() -> {
            List<Room> filteredRooms = roomRepository.searchRooms(finalLocation, finalMinPrice, finalMaxPrice,
                finalRoomType, currentSortBy, PAGE_SIZE, currentPage * PAGE_SIZE);
            runOnUiThread(() -> {
                if (currentPage == 0) {
                    roomList.clear();
                }
                roomList.addAll(filteredRooms);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                btnLoadMore.setVisibility(filteredRooms.size() == PAGE_SIZE ? View.VISIBLE : View.GONE);
            });
        });
    }

    private String getSortBy() {
        int position = spinnerSort.getSelectedItemPosition();
        switch (position) {
            case 0: return "price_asc";
            case 1: return "price_desc";
            case 2: return "rating";
            default: return "price_asc";
        }
    }

    private void onRoomClick(Room room) {
        Intent intent = new Intent(this, RoomDetailActivity.class);
        intent.putExtra("room_id", room.getId());
        startActivity(intent);
    }

    private void onFavoriteClick(Room room) {
        AppExecutors.io().execute(() -> {
            roomRepository.updateFavoriteStatus(room.getId(), !room.isFavorite());
            room.setFavorite(!room.isFavorite());
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from detail
        refreshCurrentData();
    }

    private void refreshCurrentData() {
        AppExecutors.io().execute(() -> {
            List<Room> refreshedRooms = new ArrayList<>();
            for (Room room : roomList) {
                Room updated = roomRepository.getRoomById(room.getId());
                if (updated != null) {
                    refreshedRooms.add(updated);
                }
            }
            runOnUiThread(() -> {
                roomList.clear();
                roomList.addAll(refreshedRooms);
                adapter.notifyDataSetChanged();
            });
        });
    }
}
