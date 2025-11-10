package com.example.hotel_booking;

import static android.content.Intent.getIntent;

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
import com.google.android.material.appbar.MaterialToolbar;

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
    private String roomImage;


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
        roomImage = getIntent().getStringExtra("room_image");

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
        MaterialToolbar toolbar = findViewById(R.id.topAppBarBooking);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, RoomDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
            addons.append("Bữa sáng, ");
        }
        if (cbPickup.isChecked()) {
            totalPrice += 20;
            addons.append("Đưa đón sân bay, ");
        }
        if (cbSpa.isChecked()) {
            totalPrice += 30;
            addons.append("Spa, ");
        }
        if (cbDinner.isChecked()) {
            totalPrice += 25;
            addons.append("Bữa tối, ");
        }

        String addonsText = addons.length() > 0
                ? addons.substring(0, addons.length() - 2)
                : "Không có";

        int uid = getSharedPreferences("hotel_auth", MODE_PRIVATE).getInt("user_id", 0);

        Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);
        intent.putExtra("room_type", roomType);
        intent.putExtra("check_in_date", checkIn);
        intent.putExtra("check_out_date", checkOut);
        intent.putExtra("addons", addonsText);
        intent.putExtra("note", noteText.isEmpty() ? "Không có ghi chú" : noteText);
        intent.putExtra("total_price", totalPrice);
        intent.putExtra("user_id", uid > 0 ? uid : 1);
        intent.putExtra("room_image", roomImage);
        startActivity(intent);
    }

}
