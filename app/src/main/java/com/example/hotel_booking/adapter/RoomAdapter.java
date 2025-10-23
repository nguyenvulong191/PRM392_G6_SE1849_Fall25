package com.example.hotel_booking.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_booking.R;
import com.example.hotel_booking.data.entity.Room;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private List<Room> rooms;
    private OnRoomClickListener onRoomClickListener;
    private OnFavoriteClickListener onFavoriteClickListener;

    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Room room);
    }

    public RoomAdapter(List<Room> rooms, OnRoomClickListener onRoomClickListener,
                      OnFavoriteClickListener onFavoriteClickListener) {
        this.rooms = rooms;
        this.onRoomClickListener = onRoomClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.bind(room);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void updateData(List<Room> newRooms) {
        this.rooms = newRooms;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvPrice, tvLocation, tvRating, tvRoomType;
        private ImageView ivRoom, ivFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            ivRoom = itemView.findViewById(R.id.ivRoom);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
        }

        public void bind(Room room) {
            tvName.setText(room.getName());
            tvPrice.setText(String.format("$%.0f/đêm", room.getPrice()));
            tvLocation.setText(room.getLocation());
            tvRating.setText(String.format("⭐ %d/5", room.getRating()));
            tvRoomType.setText(room.getRoomType());

            // Set favorite icon
            ivFavorite.setImageResource(room.isFavorite() ?
                android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (onRoomClickListener != null) {
                    onRoomClickListener.onRoomClick(room);
                }
            });

            ivFavorite.setOnClickListener(v -> {
                if (onFavoriteClickListener != null) {
                    onFavoriteClickListener.onFavoriteClick(room);
                }
            });
        }
    }
}
