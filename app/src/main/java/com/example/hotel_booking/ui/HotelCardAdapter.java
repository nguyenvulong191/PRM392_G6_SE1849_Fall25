package com.example.hotel_booking.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_booking.R;
import com.example.hotel_booking.model.HotelCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HotelCardAdapter extends RecyclerView.Adapter<HotelCardAdapter.Holder> {

    public interface OnClick { void onItemClick(HotelCard card); }

    private final Context ctx;
    private final OnClick onClick;
    private final List<HotelCard> items = new ArrayList<>(); // list nội bộ

    public HotelCardAdapter(Context ctx, List<HotelCard> init, OnClick onClick) {
        this.ctx = ctx;
        this.onClick = onClick;
        if (init != null) items.addAll(init);
    }

    /** Thay toàn bộ data (dùng khi lọc theo loại) */
    public void submit(List<HotelCard> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_hotel_card, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        final HotelCard c = items.get(position);

        h.img.setImageResource(c.imageRes);
        h.name.setText(c.name);
        h.rating.setText(String.format(Locale.getDefault(), "%.1f ★", c.rating));
        h.time.setText(c.time);
        h.price.setText(String.format(Locale.getDefault(), "Min - %s", c.minPrice));

        h.itemView.setOnClickListener(v -> {
            if (onClick != null) onClick.onItemClick(c);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, rating, time, price;
        Holder(@NonNull View v) {
            super(v);
            img    = v.findViewById(R.id.img);
            name   = v.findViewById(R.id.tvName);
            rating = v.findViewById(R.id.tvRating);
            time   = v.findViewById(R.id.tvTime);
            price  = v.findViewById(R.id.tvPrice);
        }
    }
}
