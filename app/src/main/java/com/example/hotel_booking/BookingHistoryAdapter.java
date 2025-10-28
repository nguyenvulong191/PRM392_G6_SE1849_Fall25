package com.example.hotel_booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_booking.data.entity.Booking;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private final List<Booking> list;

    public BookingHistoryAdapter(List<Booking> list) {
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

        h.tvRoom.setText("🏨 Phòng: " + b.getRoomType());
        h.tvGuest.setText("👤 Khách: " + b.getGuestName());
        h.tvDate.setText("📅 " + b.getDate());
        h.tvAddons.setText("🧾 Dịch vụ thêm:\n" + b.getAddons());
        h.tvNote.setText("📝 Ghi chú: " + b.getNote());
        h.tvPrice.setText(String.format("💵 Tổng tiền: $%.2f", b.getTotalPrice()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoom, tvGuest, tvDate, tvPrice, tvAddons, tvNote;

        BookingViewHolder(@NonNull View v) {
            super(v);
            tvRoom = v.findViewById(R.id.tvRoom);
            tvGuest = v.findViewById(R.id.tvGuest);
            tvDate = v.findViewById(R.id.tvDate);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvAddons = v.findViewById(R.id.tvAddons);
            tvNote = itemView.findViewById(R.id.tvNote);

        }
    }
}
