package com.example.hotel_booking;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BookingActivity extends AppCompatActivity {

    private EditText etCheckIn, etCheckOut;
    private CheckBox cbBreakfast, cbPickup, cbSpa, cbDinner;
    private EditText etNote;
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
        cbBreakfast = findViewById(R.id.cbBreakfast);
        cbPickup = findViewById(R.id.cbPickup);
        cbSpa = findViewById(R.id.cbSpa);
        cbDinner = findViewById(R.id.cbDinner);
        etNote = findViewById(R.id.etNote);
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
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

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

        // Không cho chọn ngày trong quá khứ
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void confirmBooking() {
        String checkIn = etCheckIn.getText().toString().trim();
        String checkOut = etCheckOut.getText().toString().trim();

        if (checkIn.isEmpty()) {
            etCheckIn.setError("Chọn ngày nhận phòng");
            return;
        }
        if (checkOut.isEmpty()) {
            etCheckOut.setError("Chọn ngày trả phòng");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        long diffDays = 1;
        try {
            Date inDate = sdf.parse(checkIn);
            Date outDate = sdf.parse(checkOut);
            if (inDate != null && outDate != null && !outDate.before(inDate)) {
                long diff = outDate.getTime() - inDate.getTime();
                diffDays = Math.max(1, TimeUnit.MILLISECONDS.toDays(diff));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String noteText = etNote.getText().toString().trim();

        double totalPrice = roomPrice * diffDays;
        StringBuilder addons = new StringBuilder();

        if (cbBreakfast.isChecked()) {
            totalPrice += 10;
            addons.append("- Bữa sáng\n");
        }
        if (cbPickup.isChecked()) {
            totalPrice += 20;
            addons.append("- Đưa đón sân bay\n");
        }
        if (cbSpa.isChecked()) {
            totalPrice += 30;
            addons.append("- Dịch vụ spa\n");
        }
        if (cbDinner.isChecked()) {
            totalPrice += 25;
            addons.append("- Bữa tối sang trọng\n");
        }


        String reviewText = String.format(
                "✅ Đặt phòng thành công!\n\nPhòng: %s\nLoại: %s\nNgày: %s → %s\nDịch vụ thêm:\n%s📝 Ghi chú: %s\nTổng giá: $%.2f",
                roomName, roomType, checkIn, checkOut,
                addons.length() == 0 ? "(Không chọn)\n" : addons.toString(),
                noteText.isEmpty() ? "(Không có ghi chú)" : noteText,
                totalPrice
        );

        tvReview.setText(reviewText);
        tvReview.setVisibility(View.VISIBLE);
        btnBackHome.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Đặt phòng thành công!", Toast.LENGTH_SHORT).show();

        // Lưu booking vào database
        int uid = getSharedPreferences("hotel_auth", MODE_PRIVATE).getInt("user_id", 0);
        String guestDisplayName = getSharedPreferences("hotel_auth", MODE_PRIVATE)
                .getString("full_name", "Khách");

        Booking b = new Booking();
        b.setRoomType(roomType);
        b.setGuestName(guestDisplayName);
        b.setCheckInDate(checkIn);
        b.setCheckOutDate(checkOut);
        b.setTotalPrice(totalPrice);
        b.setUserId(uid > 0 ? uid : 1); // đảm bảo luôn có user id
        b.setAddons(addons.length() == 0 ? "(Không chọn)" : addons.toString());
        b.setNote(noteText.isEmpty() ? "(Không có ghi chú)" : noteText);
        AppExecutors.io().execute(() ->
                AppDatabase.getInstance(getApplicationContext())
                        .insertBooking(b)
        );
    }

}
