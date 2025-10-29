package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_booking.data.database.AppDatabase;
import com.example.hotel_booking.data.entity.Booking;

import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private ProgressBar progress;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        rvHistory = findViewById(R.id.rvHistory);
        progress = findViewById(R.id.progress);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnBack = findViewById(R.id.btnBackHome);
        btnBack.setOnClickListener(v -> finish());

        loadBookingHistory();
    }

    private void loadBookingHistory() {
        progress.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        int uid = getSharedPreferences("hotel_auth", MODE_PRIVATE).getInt("user_id", 0);

        new Thread(() -> {
            List<Booking> list;
            if (uid > 0) {
                list = AppDatabase.getInstance(getApplicationContext())
                        .bookingDao()
                        .getByUser(uid);
            } else {
                list = AppDatabase.getInstance(getApplicationContext())
                        .bookingDao()
                        .getAll();
            }

            runOnUiThread(() -> {
                progress.setVisibility(View.GONE);
                if (list == null || list.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvHistory.setAdapter(new BookingHistoryAdapter(BookingHistoryActivity.this, list));
                }
            });
        }).start();
    }
}
