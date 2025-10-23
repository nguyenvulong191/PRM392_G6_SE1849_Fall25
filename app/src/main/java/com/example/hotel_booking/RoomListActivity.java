package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_booking.adapter.RoomAdapter;
import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.RoomRepository;
import com.example.hotel_booking.data.entity.Room;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private RoomRepository roomRepository;
    private List<Room> roomList = new ArrayList<>();
    private ProgressBar progressBar;
    private LinearLayout layoutProgress, layoutEmptyState, layoutFilterOptions;
    private MaterialButton btnLoadMore, btnFilter, btnClearFilter, btnResetSearch;
    private ImageButton btnBack, btnToggleFilter, btnGridToggle;
    private FloatingActionButton fabQuickFilter;
    private TextInputEditText etLocation, etMinPrice, etMaxPrice;
    private ChipGroup chipGroupRoomType, chipGroupSort;
    private TextView tvResultsCount;

    private int currentPage = 0;
    private final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private boolean isFiltering = false;
    private boolean isFilterExpanded = false;
    private String currentSortBy = "price_asc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        initViews();
        setupRecyclerView();
        setupChipGroups();
        setupClickListeners();

        roomRepository = new RoomRepository(this);

        // Load all rooms initially (show everything first)
        loadAllRooms();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        layoutProgress = findViewById(R.id.layoutProgress);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        layoutFilterOptions = findViewById(R.id.layoutFilterOptions);

        btnLoadMore = findViewById(R.id.btnLoadMore);
        btnFilter = findViewById(R.id.btnFilter);
        btnClearFilter = findViewById(R.id.btnClearFilter);
        btnResetSearch = findViewById(R.id.btnResetSearch);
        btnBack = findViewById(R.id.btnBack);
        btnToggleFilter = findViewById(R.id.btnToggleFilter);
        btnGridToggle = findViewById(R.id.btnGridToggle);
        fabQuickFilter = findViewById(R.id.fabQuickFilter);

        etLocation = findViewById(R.id.etLocation);
        etMinPrice = findViewById(R.id.etMinPrice);
        etMaxPrice = findViewById(R.id.etMaxPrice);

        chipGroupRoomType = findViewById(R.id.chipGroupRoomType);
        chipGroupSort = findViewById(R.id.chipGroupSort);
        tvResultsCount = findViewById(R.id.tvResultsCount);
    }

    private void setupRecyclerView() {
        adapter = new RoomAdapter(roomList, this::onRoomClick, this::onFavoriteClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupChipGroups() {
        // Set default selections
        chipGroupRoomType.check(R.id.chipAll);
        chipGroupSort.check(R.id.chipPriceAsc);

        // Setup listeners for automatic filtering when chips are selected
        chipGroupRoomType.setOnCheckedChangeListener((group, checkedId) -> {
            if (isFiltering) {
                filterRooms();
            }
        });

        chipGroupSort.setOnCheckedChangeListener((group, checkedId) -> {
            currentSortBy = getSortByFromChip(checkedId);
            if (isFiltering) {
                filterRooms();
            } else {
                // If not filtering, just re-sort current results
                loadAllRooms();
            }
        });
    }

    private void setupClickListeners() {
        btnLoadMore.setOnClickListener(v -> loadMoreRooms());
        btnFilter.setOnClickListener(v -> filterRooms());
        btnClearFilter.setOnClickListener(v -> clearFilters());
        btnResetSearch.setOnClickListener(v -> clearFilters());
        btnBack.setOnClickListener(v -> finish());

        btnToggleFilter.setOnClickListener(v -> toggleFilterOptions());
        fabQuickFilter.setOnClickListener(v -> toggleFilterOptions());

        // Auto-search when typing in location
        etLocation.setOnEditorActionListener((v, actionId, event) -> {
            filterRooms();
            return true;
        });
    }

    private void toggleFilterOptions() {
        isFilterExpanded = !isFilterExpanded;
        layoutFilterOptions.setVisibility(isFilterExpanded ? View.VISIBLE : View.GONE);

        // Rotate the expand icon
        btnToggleFilter.animate()
            .rotation(isFilterExpanded ? 180f : 0f)
            .setDuration(200)
            .start();
    }

    private void loadAllRooms() {
        if (isLoading) return;
        isLoading = true;
        currentPage = 0;
        isFiltering = false;

        showProgressBar(true);

        AppExecutors.io().execute(() -> {
            List<Room> allRooms = roomRepository.getRoomsPaginated(currentSortBy, PAGE_SIZE, 0);
            runOnUiThread(() -> {
                roomList.clear();
                roomList.addAll(allRooms);
                adapter.notifyDataSetChanged();
                updateResultsCount();
                showProgressBar(false);
                isLoading = false;
                btnLoadMore.setVisibility(allRooms.size() == PAGE_SIZE ? View.VISIBLE : View.GONE);
                showEmptyState(roomList.isEmpty());
            });
        });
    }

    private void loadRooms() {
        if (isLoading) return;
        isLoading = true;
        showProgressBar(true);

        AppExecutors.io().execute(() -> {
            List<Room> newRooms = roomRepository.getRoomsPaginated(currentSortBy, PAGE_SIZE, currentPage * PAGE_SIZE);
            runOnUiThread(() -> {
                roomList.addAll(newRooms);
                adapter.notifyDataSetChanged();
                updateResultsCount();
                showProgressBar(false);
                isLoading = false;
                btnLoadMore.setVisibility(newRooms.size() == PAGE_SIZE ? View.VISIBLE : View.GONE);
                showEmptyState(roomList.isEmpty());
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
        String roomType = getSelectedRoomType();
        currentSortBy = getSortByFromChip(chipGroupSort.getCheckedChipId());

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

        if (!isFiltering) {
            currentPage = 0;
            roomList.clear();
            isFiltering = true;
        }

        // Make variables final for lambda
        final String finalLocation = location.isEmpty() ? null : location;
        final Double finalMinPrice = minPrice;
        final Double finalMaxPrice = maxPrice;
        final String finalRoomType = roomType;

        showProgressBar(true);

        AppExecutors.io().execute(() -> {
            List<Room> filteredRooms = roomRepository.searchRooms(finalLocation, finalMinPrice, finalMaxPrice,
                finalRoomType, currentSortBy, PAGE_SIZE, currentPage * PAGE_SIZE);
            runOnUiThread(() -> {
                if (currentPage == 0) {
                    roomList.clear();
                }
                roomList.addAll(filteredRooms);
                adapter.notifyDataSetChanged();
                updateResultsCount();
                showProgressBar(false);
                btnLoadMore.setVisibility(filteredRooms.size() == PAGE_SIZE ? View.VISIBLE : View.GONE);
                showEmptyState(roomList.isEmpty());
            });
        });
    }

    private void clearFilters() {
        etLocation.setText("");
        etMinPrice.setText("");
        etMaxPrice.setText("");
        chipGroupRoomType.check(R.id.chipAll);
        chipGroupSort.check(R.id.chipPriceAsc);
        currentSortBy = "price_asc";

        // Collapse filter options
        isFilterExpanded = false;
        layoutFilterOptions.setVisibility(View.GONE);
        btnToggleFilter.animate().rotation(0f).setDuration(200).start();

        // Load all rooms again
        loadAllRooms();
    }

    private String getSelectedRoomType() {
        int checkedId = chipGroupRoomType.getCheckedChipId();
        if (checkedId == R.id.chipAll || checkedId == View.NO_ID) return null;
        if (checkedId == R.id.chipSingle) return "Single";
        if (checkedId == R.id.chipDouble) return "Double";
        if (checkedId == R.id.chipSuite) return "Suite";
        if (checkedId == R.id.chipDeluxe) return "Deluxe";
        if (checkedId == R.id.chipFamily) return "Family";
        return null;
    }

    private String getSortByFromChip(int chipId) {
        if (chipId == R.id.chipPriceAsc) return "price_asc";
        if (chipId == R.id.chipPriceDesc) return "price_desc";
        if (chipId == R.id.chipRating) return "rating";
        return "price_asc";
    }

    private void updateResultsCount() {
        String countText = roomList.size() == 1 ?
            roomList.size() + " phòng được tìm thấy" :
            roomList.size() + " phòng được tìm thấy";
        tvResultsCount.setText(countText);
    }

    private void showProgressBar(boolean show) {
        layoutProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        layoutEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
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
                updateResultsCount();
            });
        });
    }
}
