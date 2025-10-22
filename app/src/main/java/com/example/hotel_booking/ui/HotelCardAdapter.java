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
import java.util.List;

public class HotelCardAdapter extends RecyclerView.Adapter<HotelCardAdapter.Holder> {
    private final Context ctx; private final List<HotelCard> items;
    public interface OnClick { void onItemClick(HotelCard card); }
    private final OnClick onClick;

    public HotelCardAdapter(Context ctx, List<HotelCard> items, OnClick onClick) {
        this.ctx = ctx; this.items = items; this.onClick = onClick;
    }

    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new Holder(LayoutInflater.from(ctx).inflate(R.layout.item_hotel_card, p, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int pos) {
        HotelCard c = items.get(pos);
        h.img.setImageResource(c.imageRes);
        h.name.setText(c.name);
        h.rating.setText(String.format("%.1f ★", c.rating));
        h.time.setText(c.time);
        h.price.setText("Min - " + c.minPrice);
        h.itemView.setOnClickListener(v -> { if (onClick != null) onClick.onItemClick(c); });
    }
    @Override public int getItemCount(){ return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView img; TextView name, rating, time, price;
        Holder(@NonNull View v){ super(v);
            img=v.findViewById(R.id.img); name=v.findViewById(R.id.tvName);
            rating=v.findViewById(R.id.tvRating); time=v.findViewById(R.id.tvTime); price=v.findViewById(R.id.tvPrice);
        }
    }
}
