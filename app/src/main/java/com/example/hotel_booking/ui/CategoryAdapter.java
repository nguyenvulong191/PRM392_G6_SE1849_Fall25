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
import com.example.hotel_booking.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(String name, int position);
    }

    private final Context ctx;
    private final List<Category> items;
    private int selected = 0;
    private OnCategoryClickListener listener;

    public CategoryAdapter(Context ctx, List<Category> items) {
        this.ctx = ctx;
        this.items = items;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener l) {
        this.listener = l;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_category, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        Category c = items.get(pos);

        h.icon.setImageResource(c.iconRes);
        h.text.setText(c.name);

        // dùng state_selected để đổi màu/tint trong item_category.xml
        h.itemView.setSelected(selected == pos);
        h.icon.setSelected(selected == pos);
        h.text.setSelected(selected == pos);

        h.itemView.setOnClickListener(v -> {
            if (selected != pos) {
                int old = selected;
                selected = pos;
                notifyItemChanged(old);
                notifyItemChanged(pos);
            }
            if (listener != null) listener.onCategoryClick(c.name, pos);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView text;
        Holder(@NonNull View v) {
            super(v);
            icon = v.findViewById(R.id.imgIcon);
            text = v.findViewById(R.id.tvCat);
        }
    }
}
