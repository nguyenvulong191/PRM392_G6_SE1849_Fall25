package com.example.hotel_booking.data;

import android.content.Context;

import com.example.hotel_booking.data.dao.RoomDao;
import com.example.hotel_booking.data.entity.Room;

import java.util.Arrays;
import java.util.List;

public class RoomRepository {
    private RoomDao roomDao;

    public RoomRepository(Context context) {
        roomDao = new RoomDao(context);
        // Initialize sample data if database is empty
        initializeSampleData();
    }

    public List<Room> getRoomsPaginated(String sortBy, int limit, int offset) {
        return roomDao.getRoomsPaginated(sortBy, limit, offset);
    }

    public List<Room> searchRooms(String location, Double minPrice, Double maxPrice,
                                 String roomType, String sortBy, int limit, int offset) {
        return roomDao.searchRooms(location, minPrice, maxPrice,
            roomType, sortBy, limit, offset);
    }

    public List<Room> getFavoriteRooms() {
        return roomDao.getFavoriteRooms();
    }

    public Room getRoomById(int id) {
        return roomDao.getRoomById(id);
    }

    public void updateFavoriteStatus(int id, boolean isFavorite) {
        roomDao.updateFavoriteStatus(id, isFavorite);
    }

    public void insertRoom(Room room) {
        roomDao.insertRoom(room);
    }

    private void initializeSampleData() {
        // Check if data already exists
        if (roomDao.getRoomCount() > 0) {
            return;
        }

        // Sample data
        List<Room> sampleRooms = Arrays.asList(
            new Room("Deluxe Ocean View", "Phòng cao cấp view biển tuyệt đẹp", 150,
                "room1.jpg", "Vũng Tàu", "WiFi miễn phí, Điều hòa, Tivi, Minibar",
                5, "Deluxe", 2, "room1_1.jpg,room1_2.jpg,room1_3.jpg"),

            new Room("Standard Double Room", "Phòng đôi tiêu chuẩn thoải mái", 80,
                "room2.jpg", "Hà Nội", "WiFi miễn phí, Điều hòa, Tivi",
                4, "Double", 2, "room2_1.jpg,room2_2.jpg"),

            new Room("Executive Suite", "Suite cao cấp dành cho doanh nhân", 250,
                "room3.jpg", "TP.HCM", "WiFi miễn phí, Điều hòa, Tivi, Minibar, Bàn làm việc",
                5, "Suite", 4, "room3_1.jpg,room3_2.jpg,room3_3.jpg,room3_4.jpg"),

            new Room("Single Room", "Phòng đơn tiện nghi", 60,
                "room4.jpg", "Đà Nẵng", "WiFi miễn phí, Điều hòa, Tivi",
                3, "Single", 1, "room4_1.jpg,room4_2.jpg"),

            new Room("Family Room", "Phòng gia đình rộng rãi", 120,
                "room5.jpg", "Nha Trang", "WiFi miễn phí, Điều hòa, Tivi, Minibar",
                4, "Family", 4, "room5_1.jpg,room5_2.jpg,room5_3.jpg"),

            new Room("Luxury Penthouse", "Căn penthouse sang trọng", 400,
                "room6.jpg", "TP.HCM", "WiFi miễn phí, Điều hòa, Tivi, Minibar, Bàn làm việc, Jacuzzi",
                5, "Suite", 6, "room6_1.jpg,room6_2.jpg,room6_3.jpg,room6_4.jpg"),

            new Room("Budget Single", "Phòng đơn giá rẻ", 35,
                "room7.jpg", "Hà Nội", "WiFi miễn phí, Điều hòa",
                2, "Single", 1, "room7_1.jpg,room7_2.jpg"),

            new Room("Garden View Double", "Phòng đôi view vườn", 90,
                "room8.jpg", "Đà Lạt", "WiFi miễn phí, Điều hòa, Tivi, Ban công",
                4, "Double", 2, "room8_1.jpg,room8_2.jpg,room8_3.jpg"),

            new Room("Beach Villa", "Villa bên bãi biển riêng tư", 350,
                "room9.jpg", "Phú Quốc", "WiFi miễn phí, Điều hòa, Tivi, Minibar, Bếp, Hồ bơi riêng",
                5, "Villa", 8, "room9_1.jpg,room9_2.jpg,room9_3.jpg,room9_4.jpg"),

            new Room("Hostel Bed", "Giường trong ký túc xá", 25,
                "room10.jpg", "Hà Nội", "WiFi miễn phí, Tủ khóa cá nhân",
                3, "Hostel", 1, "room10_1.jpg,room10_2.jpg")
        );

        roomDao.insertRooms(sampleRooms);
    }
}

