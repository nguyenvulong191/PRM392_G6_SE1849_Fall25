package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.database.AppDatabase;
import com.example.hotel_booking.data.entity.Booking;

public class PaymentMethodFragment extends Fragment {

    private RadioButton rbVNPay, rbCash;
    private CardView cardVNPay, cardCash;
    private Button btnConfirmPayment;
    private TextView tvBookingSummary;
    private String selectedMethod = "VNPay";

    private String roomType, checkInDate, checkOutDate, addons, note, roomImage;
    private double totalPrice;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_method, container, false);

        initViews(view);
        loadBookingData();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        rbVNPay = view.findViewById(R.id.rbVNPay);
        rbCash = view.findViewById(R.id.rbCash);

        cardVNPay = view.findViewById(R.id.cardVNPay);
        cardCash = view.findViewById(R.id.cardCash);

        btnConfirmPayment = view.findViewById(R.id.btnConfirmPayment);
        tvBookingSummary = view.findViewById(R.id.tvBookingSummary);

        rbVNPay.setChecked(true);
        selectedMethod = "VNPay";
    }

    private void loadBookingData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            roomType = bundle.getString("room_type", "");
            checkInDate = bundle.getString("check_in_date", "");
            checkOutDate = bundle.getString("check_out_date", "");
            addons = bundle.getString("addons", "");
            note = bundle.getString("note", "");
            totalPrice = bundle.getDouble("total_price", 0.0);
            userId = bundle.getInt("user_id", 0);
            roomImage = bundle.getString("room_image", "");

            String summary = String.format(
                "Phòng: %s\nNgày: %s → %s\nDịch vụ: %s\n\nTổng tiền: $%.2f",
                roomType, checkInDate, checkOutDate, addons, totalPrice
            );
            tvBookingSummary.setText(summary);
        }
    }

    private void setupListeners() {
        cardVNPay.setOnClickListener(v -> selectPaymentMethod(rbVNPay, "VNPay"));
        cardCash.setOnClickListener(v -> selectPaymentMethod(rbCash, "Tiền mặt"));

        rbVNPay.setOnClickListener(v -> selectPaymentMethod(rbVNPay, "VNPay"));
        rbCash.setOnClickListener(v -> selectPaymentMethod(rbCash, "Tiền mặt"));

        btnConfirmPayment.setOnClickListener(v -> confirmPayment());
    }

    private void selectPaymentMethod(RadioButton selectedRadio, String method) {
        rbVNPay.setChecked(false);
        rbCash.setChecked(false);

        selectedRadio.setChecked(true);
        selectedMethod = method;
    }

    private void confirmPayment() {
        if (selectedMethod.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        // PHÂN LUỒNG LOGIC THANH TOÁN
        if (selectedMethod.equals("VNPay")) {
            // YÊU CẦU MỚI: Nếu là VNPay, chuyển sang màn hình VNPay

            // 1. Tạo Intent để mở Activity mới (chúng ta sẽ tạo nó ở Bước 2)
            Intent intent = new Intent(getActivity(), VnPayPaymentActivity.class);

            // 2. Gửi tất cả thông tin đặt phòng sang Activity mới
            //    để màn hình đó biết cần thanh toán bao nhiêu và lưu gì
            intent.putExtra("room_type", roomType);
            intent.putExtra("check_in_date", checkInDate);
            intent.putExtra("check_out_date", checkOutDate);
            intent.putExtra("addons", addons);
            intent.putExtra("note", note);
            intent.putExtra("total_price", totalPrice);
            intent.putExtra("user_id", userId);
            intent.putExtra("room_image", roomImage);

            // 3. Bắt đầu Activity mới
            startActivity(intent);

            // Lưu ý: Chúng ta KHÔNG gọi saveBookingToDatabase() ở đây nữa
            // Màn hình VnPayPaymentActivity sẽ tự xử lý việc đó

        } else if (selectedMethod.equals("Tiền mặt")) {
            // LOGIC CŨ: Nếu là Tiền mặt, lưu và chuyển đến Lịch sử
            saveBookingToDatabase();
        }
    }

    private void saveBookingToDatabase() {
        String guestName = requireActivity()
                .getSharedPreferences("hotel_auth", requireContext().MODE_PRIVATE)
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
            AppDatabase.getInstance(requireContext()).insertBooking(booking);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Thanh toán thành công bằng " + selectedMethod + "!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), BookingHistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            });
        });
    }
}

