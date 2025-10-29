package com.example.hotel_booking;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.database.AppDatabase;
import com.example.hotel_booking.data.entity.Booking;

public class BookingDetailActivity extends AppCompatActivity {

    private TextView tvBookingDetail;
    private ImageView imgRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        tvBookingDetail = findViewById(R.id.tvBookingDetail);
        imgRoom = findViewById(R.id.imgRoom);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        int bookingId = getIntent().getIntExtra("booking_id", -1);
        if (bookingId != -1) {
            loadBookingDetail(bookingId);
        }
    }

    private void loadBookingDetail(int id) {
        AppExecutors.io().execute(() -> {
            Booking booking = AppDatabase.getInstance(getApplicationContext())
                    .bookingDao()
                    .getBookingById(id);

            runOnUiThread(() -> {
                if (booking != null) {
                    // ✅ Load ảnh phòng
                    if (booking.getRoomImage() != null && !booking.getRoomImage().isEmpty()) {
                        Glide.with(this)
                                .load(booking.getRoomImage())
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_delete)
                                .into(imgRoom);
                    }

                    // Hiển thị chi tiết
                    String detail = String.format(
                            "🏨 Phòng: %s\nLoại: %s\nKhách: %s\nNgày nhận: %s\nNgày trả: %s\n\nDịch vụ thêm:\n%s\nGhi chú: %s\n\n💰 Tổng giá: $%.2f",
                            booking.getRoomType(),
                            booking.getRoomType(),
                            booking.getGuestName(),
                            booking.getCheckInDate(),
                            booking.getCheckOutDate(),
                            booking.getAddons(),
                            booking.getNote(),
                            booking.getTotalPrice()
                    );
                    tvBookingDetail.setText(detail);
                } else {
                    tvBookingDetail.setText("Không tìm thấy thông tin đặt phòng.");
                }
            });
        });
    }
}
