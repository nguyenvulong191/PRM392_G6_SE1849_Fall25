package com.example.hotel_booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_booking.common.AppExecutors;
import com.example.hotel_booking.data.RoomRepository;
import com.example.hotel_booking.model.Category;
import com.example.hotel_booking.model.HotelCard;
import com.example.hotel_booking.ui.CategoryAdapter;
import com.example.hotel_booking.ui.HotelCardAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private List<HotelCard> allCards = new ArrayList<>();
    private HotelCardAdapter hotelAdp;
    private RoomRepository roomRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topAppBar), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            int minus = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics());
            int top = Math.max(0, bars.top - minus);
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        NavigationView navView = findViewById(R.id.navView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Home");
        toggle.getDrawerArrowDrawable().setColor(android.graphics.Color.WHITE);
        if (toolbar.getOverflowIcon() != null)
            toolbar.getOverflowIcon().setTint(android.graphics.Color.WHITE);
        if (toolbar.getNavigationIcon() != null)
            DrawableCompat.setTint(toolbar.getNavigationIcon(), android.graphics.Color.WHITE);

        View header = navView.getHeaderView(0);
        TextView tvHeaderName = header.findViewById(R.id.tvHeaderName);
        TextView tvHeaderEmail = header.findViewById(R.id.tvHeaderEmail);

        String name = getSharedPreferences("hotel_auth", MODE_PRIVATE).getString("full_name", "Guest");
        String email = getSharedPreferences("hotel_auth", MODE_PRIVATE).getString("email", "guest@example.com");
        tvHeaderName.setText(name);
        tvHeaderEmail.setText(email);

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                item.setChecked(true);
            } else if (id == R.id.nav_user) {
                Toast.makeText(this, "Daily Meal", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_fav) {
                startActivity(new Intent(this, FavoritesActivity.class));
            } else if (id == R.id.nav_cart) {
                Toast.makeText(this, "My Cart", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                getSharedPreferences("hotel_auth", MODE_PRIVATE).edit().clear().apply();
                Intent i = new Intent(this, WelcomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        String first = name != null ? name.trim().split("\\s+")[0] : "Guest";
        TextView tvHello = findViewById(R.id.tvHello);
        tvHello.setText("Hello " + first);

        Button btnRooms = findViewById(R.id.btnRooms);
        Button btnFavorites = findViewById(R.id.btnFavorites);
        btnRooms.setOnClickListener(v -> startActivity(new Intent(this, RoomListActivity.class)));
        btnFavorites.setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));
        Button btnBookingHistory = findViewById(R.id.btnBookingHistory);
        btnBookingHistory.setOnClickListener(v ->
                startActivity(new Intent(this, BookingHistoryActivity.class))
        );
        RecyclerView rvCat = findViewById(R.id.rvCategories);
        rvCat.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        CategoryAdapter catAdp = new CategoryAdapter(this, demoCategories());
        rvCat.setAdapter(catAdp);

        RecyclerView rvHotels = findViewById(R.id.rvHotels);
        rvHotels.setLayoutManager(new LinearLayoutManager(this));
        hotelAdp = new HotelCardAdapter(this, new ArrayList<>(), card -> {
            Intent intent = new Intent(this, RoomDetailActivity.class);
            intent.putExtra("room_id", card.roomId);
            startActivity(intent);
        });
        rvHotels.setAdapter(hotelAdp);

        roomRepository = new RoomRepository(this);

        loadCardsFromDbAndShow("Single");

        catAdp.setOnCategoryClickListener((catName, pos) -> {
            loadCardsFromDbAndShow(catName);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
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
        list.add(new Category("Single",  R.mipmap.ic_launcher_round));
        list.add(new Category("Double",  R.mipmap.ic_launcher_round));
        list.add(new Category("Suite",   R.mipmap.ic_launcher_round));
        list.add(new Category("Deluxe",  R.mipmap.ic_launcher_round));
        list.add(new Category("Family",  R.mipmap.ic_launcher_round));
        return list;
    }
    private void loadCardsFromDbAndShow(String typeToShow) {
        AppExecutors.io().execute(() -> {
            List<com.example.hotel_booking.data.entity.Room> rooms =
                    roomRepository.getRoomsPaginated("price_asc", 100, 0);

            List<HotelCard> cards = new ArrayList<>();
            for (com.example.hotel_booking.data.entity.Room r : rooms) {
                String title = r.getRoomType() + " • " + r.getName();
                String hours = "00:00 - 24:00";
                String price = "$" + (int) r.getPrice();
                float rating  = (float) r.getRating();

                int imgRes = 0;
                String imageKey = r.getImageUrl();
                if (imageKey != null && !imageKey.isEmpty()) {
                    imgRes = getResources().getIdentifier(imageKey, "drawable", getPackageName());
                }
                if (imgRes == 0) imgRes = R.mipmap.ic_launcher_round;

                cards.add(new HotelCard(r.getId(), imgRes, title, hours, price, rating));
            }

            List<HotelCard> filtered;
            if (typeToShow == null || typeToShow.trim().isEmpty()) {
                filtered = cards; // không lọc
            } else {
                String prefix = typeToShow + " • ";
                filtered = new ArrayList<>();
                for (HotelCard c : cards) {
                    if (c.name != null && c.name.startsWith(prefix)) filtered.add(c);
                }
                if (filtered.isEmpty()) filtered = cards;
            }

            List<HotelCard> finalFiltered = filtered;
            runOnUiThread(() -> {
                allCards = cards;
                hotelAdp.submit(finalFiltered);
            });
        });
    }


    private List<HotelCard> filterByType(List<HotelCard> all, String type) {
        if (type == null || type.trim().isEmpty()) return all;
        List<HotelCard> out = new ArrayList<>();
        String prefix = String.format(Locale.getDefault(), "%s • ", type);
        for (HotelCard c : all) {
            if (c.name != null && c.name.startsWith(prefix)) out.add(c);
        }
        return out;
    }
    private String normalizeType(String catName) {
        if (catName == null) return "Hotel";
        catName = catName.trim();
        if (catName.equalsIgnoreCase("Hotels")) return "Hotel";
        if (catName.equalsIgnoreCase("Resorts")) return "Resort";
        if (catName.equalsIgnoreCase("Apartments")) return "Apartment";
        if (catName.equalsIgnoreCase("Villas")) return "Villa";
        if (catName.equalsIgnoreCase("Hostels")) return "Hostel";
        return catName;
    }
}
