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

    // giữ sẵn dữ liệu tổng để lọc nhanh
    private List<HotelCard> allCards;
    private HotelCardAdapter hotelAdp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Edge-to-edge: đẩy toolbar xuống dưới status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topAppBar), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            int minus = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics());
            int top = Math.max(0, bars.top - minus);
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // ---- Toolbar + Drawer ----
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
        if (toolbar.getOverflowIcon() != null) {
            toolbar.getOverflowIcon().setTint(android.graphics.Color.WHITE);
        }
        if (toolbar.getNavigationIcon() != null) {
            DrawableCompat.setTint(toolbar.getNavigationIcon(), android.graphics.Color.WHITE);
        }

        // Header: tên + email
        View header = navView.getHeaderView(0);
        TextView tvHeaderName = header.findViewById(R.id.tvHeaderName);
        TextView tvHeaderEmail = header.findViewById(R.id.tvHeaderEmail);

        String name = getSharedPreferences("hotel_auth", MODE_PRIVATE)
                .getString("full_name", "Guest");
        String email = getSharedPreferences("hotel_auth", MODE_PRIVATE)
                .getString("email", "guest@example.com");
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

        // Hello + first name
        String first = name != null ? name.trim().split("\\s+")[0] : "Guest";
        TextView tvHello = findViewById(R.id.tvHello);
        tvHello.setText("Hello " + first);

        // Buttons
        Button btnRooms = findViewById(R.id.btnRooms);
        Button btnFavorites = findViewById(R.id.btnFavorites);
        btnRooms.setOnClickListener(v -> startActivity(new Intent(this, RoomListActivity.class)));
        btnFavorites.setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));

        // ==== Categories (chips tròn): Hotel/Resort/Apartment/Villa/Hostel ====
        RecyclerView rvCat = findViewById(R.id.rvCategories);
        rvCat.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        CategoryAdapter catAdp = new CategoryAdapter(this, demoCategories());
        rvCat.setAdapter(catAdp);

        // ==== Hotels list (danh sách dưới): lọc theo loại ====
        RecyclerView rvHotels = findViewById(R.id.rvHotels);
        rvHotels.setLayoutManager(new LinearLayoutManager(this));

        allCards = buildAllCards(); // 5 loại, mỗi loại 3 item
        hotelAdp = new HotelCardAdapter(this, filterByType(allCards, "Hotel"), card ->
                startActivity(new Intent(this, RoomListActivity.class))
        );
        rvHotels.setAdapter(hotelAdp);

        // click chip -> lọc
        catAdp.setOnCategoryClickListener((catName, pos) -> {
            String type = normalizeType(catName); // "Hotels" -> "Hotel", ...
            hotelAdp.submit(filterByType(allCards, type));
        });

        // Back: đóng drawer trước, rồi back mặc định
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    // ==== MENU (Toolbar) ====
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

    // ==== DATA: Categories ====
    private List<Category> demoCategories() {
        List<Category> list = new ArrayList<>();
        list.add(new Category("Hotels",     R.mipmap.ic_launcher_round));
        list.add(new Category("Resorts",    R.mipmap.ic_launcher_round));
        list.add(new Category("Apartments", R.mipmap.ic_launcher_round));
        list.add(new Category("Villas",     R.mipmap.ic_launcher_round));
        list.add(new Category("Hostels",    R.mipmap.ic_launcher_round));
        return list;
    }

    // ==== DATA: All cards (mỗi type 3 item) ====
    private List<HotelCard> buildAllCards() {
        List<HotelCard> list = new ArrayList<>();

        // Hotel
        list.add(new HotelCard(R.mipmap.ic_launcher, "Hotel • Sunrise Hotel", "10:00 - 23:00", "$55", 4.9f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Hotel • Central Hotel", "00:00 - 24:00", "$50", 4.6f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Hotel • Old Quarter Inn", "10:00 - 22:00", "$45", 4.6f));

        // Resort
        list.add(new HotelCard(R.mipmap.ic_launcher, "Resort • Bay View Resort", "00:00 - 24:00", "$80", 4.7f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Resort • Coral Reef Resort", "10:00 - 22:00", "$95", 4.6f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Resort • Mountain Sky Resort", "10:00 - 23:00", "$90", 4.5f));

        // Apartment
        list.add(new HotelCard(R.mipmap.ic_launcher, "Apartment • City Center Apartment", "09:00 - 21:00", "$60", 4.5f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Apartment • Riverside Apartment", "10:00 - 22:00", "$65", 4.4f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Apartment • Garden View Apartment", "10:00 - 22:00", "$62", 4.3f));

        // Villa
        list.add(new HotelCard(R.mipmap.ic_launcher, "Villa • Sunrise Villa", "10:00 - 23:00", "$120", 4.8f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Villa • Palm Garden Villa", "10:00 - 23:00", "$130", 4.7f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Villa • Ocean Breeze Villa", "10:00 - 23:00", "$140", 4.8f));

        // Hostel
        list.add(new HotelCard(R.mipmap.ic_launcher, "Hostel • Backpackers Hostel", "10:00 - 22:00", "$20", 4.3f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Hostel • Old Quarter Hostel", "00:00 - 24:00", "$25", 4.2f));
        list.add(new HotelCard(R.mipmap.ic_launcher, "Hostel • City Budget Hostel", "09:00 - 21:00", "$18", 4.1f));

        return list;
    }

    // Lọc theo type bằng prefix trong title (Hotel • ..., Resort • ...)
    private List<HotelCard> filterByType(List<HotelCard> all, String type) {
        List<HotelCard> out = new ArrayList<>();
        String prefix = String.format(Locale.getDefault(), "%s • ", type);
        for (HotelCard c : all) {
            if (c.name != null && c.name.startsWith(prefix)) {
                out.add(c);
            }
        }
        return out;
    }

    // Chuẩn hóa tên category -> type đơn (Hotels -> Hotel, ...)
    private String normalizeType(String catName) {
        if (catName == null) return "Hotel";
        catName = catName.trim();
        if (catName.equalsIgnoreCase("Hotels"))     return "Hotel";
        if (catName.equalsIgnoreCase("Resorts"))    return "Resort";
        if (catName.equalsIgnoreCase("Apartments")) return "Apartment";
        if (catName.equalsIgnoreCase("Villas"))     return "Villa";
        if (catName.equalsIgnoreCase("Hostels"))    return "Hostel";
        return catName; // fallback
    }
}
