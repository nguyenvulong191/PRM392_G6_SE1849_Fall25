package com.example.hotel_booking;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.database.AppDatabase;
import com.example.hotel_booking.data.entity.Booking;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    private EditText etCheckIn, etCheckOut, etGuests;
    private CheckBox cbBreakfast, cbPickup;
    private TextView tvRoomInfo, tvReview;
    private Button btnConfirmBooking, btnBackHome;

    private String roomName, roomType;
    private double roomPrice;
    private int capacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        initViews();
        getRoomData();
        setupListeners();
        setupDatePickers();
    }

    private void initViews() {
        etCheckIn = findViewById(R.id.etCheckIn);
        etCheckOut = findViewById(R.id.etCheckOut);
        etGuests = findViewById(R.id.etGuests);
        cbBreakfast = findViewById(R.id.cbBreakfast);
        cbPickup = findViewById(R.id.cbPickup);
        tvRoomInfo = findViewById(R.id.tvRoomInfo);
        tvReview = findViewById(R.id.tvReview);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnBackHome = findViewById(R.id.btnBackHome);
    }

    private void getRoomData() {
        roomName = getIntent().getStringExtra("room_name");
        roomPrice = getIntent().getDoubleExtra("room_price", 0);
        roomType = getIntent().getStringExtra("room_type");
        capacity = getIntent().getIntExtra("room_capacity", 0);

        tvRoomInfo.setText(String.format(
                "Phòng: %s (%s)\nGiá: $%.2f/đêm\nSức chứa: %d người",
                roomName, roomType, roomPrice, capacity
        ));
    }

    private void setupListeners() {
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
        btnBackHome.setOnClickListener(v -> finish());
    }

    private void setupDatePickers() {
        etCheckIn.setOnClickListener(v -> showDatePickerDialog(etCheckIn));
        etCheckOut.setOnClickListener(v -> showDatePickerDialog(etCheckOut));
    }

    private void showDatePickerDialog(EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d/%02d/%d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    targetEditText.setText(date);
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void confirmBooking() {
        String checkIn = etCheckIn.getText().toString().trim();
        String checkOut = etCheckOut.getText().toString().trim();
        String guestsStr = etGuests.getText().toString().trim();
        int guests = guestsStr.isEmpty() ? 1 : Integer.parseInt(guestsStr);

        // Validate tối thiểu
        if (checkIn.isEmpty()) {
            etCheckIn.setError("Chọn ngày nhận phòng");
            return;
        }
        if (checkOut.isEmpty()) {
            etCheckOut.setError("Chọn ngày trả phòng");
            return;
        }

        double totalPrice = roomPrice;
        StringBuilder addons = new StringBuilder();

        if (cbBreakfast.isChecked()) {
            totalPrice += 10;
            addons.append("- Bữa sáng\n");
        }
        if (cbPickup.isChecked()) {
            totalPrice += 20;
            addons.append("- Đưa đón sân bay\n");
        }

        String reviewText = String.format(
                "✅ Đặt phòng thành công!\n\nPhòng: %s\nLoại: %s\nNgày: %s → %s\nSố khách: %d\nDịch vụ thêm:\n%sTổng giá: $%.2f",
                roomName, roomType, checkIn, checkOut, guests,
                addons.length() == 0 ? "(Không chọn)\n" : addons.toString(),
                totalPrice
        );

        tvReview.setText(reviewText);
        tvReview.setVisibility(View.VISIBLE);
        btnBackHome.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Đặt phòng thành công!", Toast.LENGTH_SHORT).show();

        int uid = getSharedPreferences("hotel_auth", MODE_PRIVATE).getInt("user_id", 0);
        String guestDisplayName = getSharedPreferences("hotel_auth", MODE_PRIVATE)
                .getString("full_name", roomName);

        Booking b = new Booking();
        b.setRoomType(roomType);
        b.setGuestName(guestDisplayName);
        b.setCheckInDate(checkIn);
        b.setCheckOutDate(checkOut);
        b.setTotalPrice(totalPrice);
        b.setUserId(uid);

        AppExecutors.io().execute(() ->
                AppDatabase.getInstance(getApplicationContext()).insertBooking(b)
        );
    }
}
