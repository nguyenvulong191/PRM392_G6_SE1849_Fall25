package com.example.hotel_booking;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_booking.data.entity.Booking;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private final Context context;
    private final List<Booking> list;

    public BookingHistoryAdapter(Context context, List<Booking> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder h, int pos) {
        Booking b = list.get(pos);

        // Thông tin rút gọn
        h.tvRoomName.setText("🏨 " + b.getRoomType());
        h.tvDateRange.setText("📅 " + b.getCheckInDate() + " → " + b.getCheckOutDate());
        h.tvTotalPrice.setText(String.format("💵 $%.2f", b.getTotalPrice()));

        // Click mở chi tiết
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingDetailActivity.class);
            intent.putExtra("booking_id", b.getId());
            intent.putExtra("room_image", b.getRoomImage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvDateRange, tvTotalPrice;

        BookingViewHolder(@NonNull View v) {
            super(v);
            tvRoomName = v.findViewById(R.id.tvRoomName);
            tvDateRange = v.findViewById(R.id.tvDateRange);
            tvTotalPrice = v.findViewById(R.id.tvTotalPrice);
        }
    }
}
