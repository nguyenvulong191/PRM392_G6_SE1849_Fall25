package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotel_booking.model.Category;
import com.example.hotel_booking.model.HotelCard;
import com.example.hotel_booking.ui.CategoryAdapter;
import com.example.hotel_booking.ui.HotelCardAdapter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String name = getSharedPreferences("hotel_auth", MODE_PRIVATE)
                .getString("full_name", "Guest");

        String first = name != null ? name.trim().split("\\s+")[0] : "Guest";

        // Add navigation buttons
        Button btnRooms = findViewById(R.id.btnRooms);
        Button btnFavorites = findViewById(R.id.btnFavorites);

        btnRooms.setOnClickListener(v -> {
            Intent intent = new Intent(this, RoomListActivity.class);
            startActivity(intent);
        });

        btnFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
        });
        RecyclerView rvCat = findViewById(R.id.rvCategories);
        rvCat.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvCat.setAdapter(new CategoryAdapter(this, demoCategories()));

        // Hotels (vertical)
        RecyclerView rvHotels = findViewById(R.id.rvHotels);
        rvHotels.setLayoutManager(new LinearLayoutManager(this));
        rvHotels.setAdapter(new HotelCardAdapter(this, demoHotels(), card -> {
        }));
            // Navigate to room list when hotel card is clicked
            Intent intent = new Intent(this, RoomListActivity.class);
            startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_rooms) {
            startActivity(new Intent(this, RoomListActivity.class));
            return true;
        } else if (id == R.id.action_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private List<Category> demoCategories() {
        List<Category> list = new ArrayList<>();
        list.add(new Category("Hotels", R.mipmap.ic_launcher_round));
        list.add(new Category("Resorts", R.mipmap.ic_launcher_round));
        list.add(new Category("Hostels", R.mipmap.ic_launcher_round));
        list.add(new Category("Villas", R.mipmap.ic_launcher_round));
        list.add(new Category("Apartments", R.mipmap.ic_launcher_round));
        return list;
    }

    private List<HotelCard> demoHotels() {
        List<HotelCard> list = new ArrayList<>();
        list.add(new HotelCard(R.mipmap.ic_launcher, "Sunrise Hotel", "10:00 - 23:00", "$55", 4.9f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Bay View Resort", "00:00 - 24:00", "$80", 4.7f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Old Quarter Inn", "10:00 - 22:00", "$45", 4.6f));
        return list;
    }
}
