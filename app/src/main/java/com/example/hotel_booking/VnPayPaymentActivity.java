package com.example.hotel_booking;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.database.AppDatabase;
import com.example.hotel_booking.data.entity.Booking;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class VnPayPaymentActivity extends AppCompatActivity {

    private TextView tvTotalAmount;
    private Button btnConfirmPaid;
    private ImageButton btnBackVnPay;
    private Toolbar toolbarVnPay;

    private TextInputLayout tilAccountNumber, tilPaymentContent;
    private TextInputEditText etAccountNumber, etPaymentContent;

    // Các biến để lưu thông tin booking
    private String roomType, checkInDate, checkOutDate, addons, note, roomImage;
    private double totalPrice;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay_payment);

        // --- 1. LẤY DỮ LIỆU TỪ INTENT ---
        Intent intent = getIntent();
        roomType = intent.getStringExtra("room_type");
        checkInDate = intent.getStringExtra("check_in_date");
        checkOutDate = intent.getStringExtra("check_out_date");
        addons = intent.getStringExtra("addons");
        note = intent.getStringExtra("note");
        totalPrice = intent.getDoubleExtra("total_price", 0.0);
        userId = intent.getIntExtra("user_id", 0);
        roomImage = intent.getStringExtra("room_image");

        // --- 2. LIÊN KẾT VIEWS ---
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnConfirmPaid = findViewById(R.id.btnConfirmPaid);
        toolbarVnPay = findViewById(R.id.toolbarVnPay);
        btnBackVnPay = findViewById(R.id.btnBackVnPay);

        tilAccountNumber = findViewById(R.id.tilAccountNumber);
        etAccountNumber = findViewById(R.id.etAccountNumber);
        tilPaymentContent = findViewById(R.id.tilPaymentContent);
        etPaymentContent = findViewById(R.id.etPaymentContent);

        // --- 3. CÀI ĐẶT TOOLBAR VÀ NÚT BACK ---
        setSupportActionBar(toolbarVnPay);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Ẩn tiêu đề mặc định
        }
        btnBackVnPay.setOnClickListener(v -> {
            finish(); // Đóng Activity khi nhấn back
        });

        // --- 4. HIỂN THỊ DỮ LIỆU ---
        tvTotalAmount.setText(String.format("$%.2f", totalPrice));
        // Bạn có thể tạo mã đơn hàng ngẫu nhiên ở đây
        String orderId = "HD" + System.currentTimeMillis() % 10000;
        etPaymentContent.setText("THANHTOAN " + orderId);

        // --- 5. THIẾT LẬP LISTENERS ---

        // Nút xác nhận đã thanh toán
        btnConfirmPaid.setOnClickListener(v -> {
            saveBookingToDatabase();
        });

        // Nút copy STK
        tilAccountNumber.setEndIconOnClickListener(v -> {
            copyToClipboard(etAccountNumber.getText().toString());
            Toast.makeText(this, "Đã sao chép Số tài khoản!", Toast.LENGTH_SHORT).show();
        });

        // Nút copy Nội dung
        tilPaymentContent.setEndIconOnClickListener(v -> {
            copyToClipboard(etPaymentContent.getText().toString());
            Toast.makeText(this, "Đã sao chép Nội dung!", Toast.LENGTH_SHORT).show();
        });
    }

    // Phương thức sao chép vào clipboard
    private void copyToClipboard(String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Payment Info", textToCopy);
            clipboard.setPrimaryClip(clip);
        }
    }

    // Phương thức lưu vào database (giống hệt code cũ của bạn)
    // Sửa lại phương thức này
    private void saveBookingToDatabase() {
        String guestName = getSharedPreferences("hotel_auth", MODE_PRIVATE)
                .getString("full_name", "Khách");

        Booking booking = new Booking();
        booking.setRoomType(roomType);
        booking.setGuestName(guestName);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setTotalPrice(totalPrice);
        booking.setUserId(userId > 0 ? userId : 1);
        booking.setAddons(addons);
        booking.setNote(note);
        booking.setRoomImage(roomImage);

        AppExecutors.io().execute(() -> {
            AppDatabase.getInstance(this).insertBooking(booking);

            runOnUiThread(() -> {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                // === THAY ĐỔI BẮT ĐẦU TỪ ĐÂY ===

                // 1. Tạo Intent để mở màn hình "Thành công" MỚI
                Intent intent = new Intent(this, BookingSuccessActivity.class);

                // 2. Gửi TẤT CẢ thông tin đặt phòng sang màn hình thành công
                //    để hiển thị lại cho người dùng
                intent.putExtra("guest_name", guestName);
                intent.putExtra("room_type", roomType);
                intent.putExtra("check_in_date", checkInDate);
                intent.putExtra("check_out_date", checkOutDate);
                intent.putExtra("addons", addons);
                intent.putExtra("total_price", totalPrice);

                // 3. Xóa các màn hình cũ (Booking, Payment) khỏi stack
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // 4. Đóng màn hình thanh toán này lại
                finish();

                // === KẾT THÚC THAY ĐỔI ===
            });
        });
    }
}