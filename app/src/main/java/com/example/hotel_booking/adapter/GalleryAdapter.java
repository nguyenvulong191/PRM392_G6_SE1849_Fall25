package com.example.hotel_booking.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_booking.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private List<String> imageUrls;

    public GalleryAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_gallery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        holder.bind(imageUrl);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivGallery;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGallery = itemView.findViewById(R.id.ivGallery);
        }

        public void bind(String imageUrl) {
            // For demo purposes, using placeholder
            ivGallery.setImageResource(R.drawable.bg_welcome);
        }
    }
}
