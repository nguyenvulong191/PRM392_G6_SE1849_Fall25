package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BookingSuccessActivity extends AppCompatActivity {

    private TextView tvBookingSummary;
    private Button btnGoToHistory;
    private Toolbar toolbarSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_success);

        // 1. Liên kết Views
        tvBookingSummary = findViewById(R.id.tvBookingSummary);
        btnGoToHistory = findViewById(R.id.btnGoToHistory);
        toolbarSuccess = findViewById(R.id.toolbarSuccess);

        // 2. Cài đặt Toolbar
        setSupportActionBar(toolbarSuccess);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 3. Lấy dữ liệu từ Intent (đã gửi từ VnPayPaymentActivity)
        Intent intent = getIntent();
        String guestName = intent.getStringExtra("guest_name");
        String roomType = intent.getStringExtra("room_type");
        String checkInDate = intent.getStringExtra("check_in_date");
        String checkOutDate = intent.getStringExtra("check_out_date");
        String addons = intent.getStringExtra("addons");
        double totalPrice = intent.getDoubleExtra("total_price", 0.0);

        // 4. Hiển thị thông tin tóm tắt
        String summary = String.format(
                "Khách: %s\nPhòng: %s\nNhận phòng: %s\nTrả phòng: %s\nDịch vụ: %s\n\nTổng tiền: $%.2f",
                guestName,
                roomType,
                checkInDate,
                checkOutDate,
                (addons == null || addons.isEmpty()) ? "Không" : addons,
                totalPrice
        );
        tvBookingSummary.setText(summary);

        // 5. Cài đặt Listener cho nút
        btnGoToHistory.setOnClickListener(v -> {
            // Mở trang Lịch sử
            Intent historyIntent = new Intent(this, BookingHistoryActivity.class);
            startActivity(historyIntent);
            finish(); // Đóng màn hình thành công này lại
        });
    }

    // Ghi đè nút Back của điện thoại
    @Override
    public void onBackPressed() {
        // Khi ở màn hình thành công, nhấn Back cũng sẽ đi đến trang Lịch sử
        // để tránh người dùng quay lại trang thanh toán
        btnGoToHistory.performClick();
    }
}