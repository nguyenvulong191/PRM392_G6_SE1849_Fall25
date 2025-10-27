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
        // Check if data already exists - DON'T clear existing data!
        if (roomDao.getRoomCount() > 0) {
            return; // Data exists, don't reinitialize
        }

        // Only initialize sample data if database is empty
        List<Room> sampleRooms = Arrays.asList(
                new Room("Deluxe Ocean View", "Phòng cao cấp view biển tuyệt đẹp", 150,
                        "hotel_1", "Vũng Tàu", "WiFi miễn phí, Điều hòa, Tivi, Minibar",
                        5, "Deluxe", 2, "hotel_1,hotel_2,hotel_3"),

                new Room("Standard Double Room", "Phòng đôi tiêu chuẩn thoải mái", 80,
                        "hotel_2", "Hà Nội", "WiFi miễn phí, Điều hòa, Tivi",
                        4, "Double", 2, "hotel_1,hotel_2,hotel_3"),

                new Room("Executive Suite", "Suite cao cấp dành cho doanh nhân", 250,
                        "hotel_3", "TP.HCM", "WiFi miễn phí, Điều hòa, Tivi, Minibar, Bàn làm việc",
                        5, "Suite", 4, "hotel_1,hotel_2,hotel_3"),

                new Room("Single Room", "Phòng đơn tiện nghi", 60,
                        "resort_1", "Đà Nẵng", "WiFi miễn phí, Điều hòa, Tivi",
                        3, "Single", 1, "resort_1,resort_2,resort_3"),

                new Room("Family Room", "Phòng gia đình rộng rãi", 120,
                        "resort_2", "Nha Trang", "WiFi miễn phí, Điều hòa, Tivi, Minibar",
                        4, "Family", 4, "resort_1,resort_2,resort_3"),

                new Room("Luxury Penthouse", "Căn penthouse sang trọng", 400,
                        "resort_3", "TP.HCM", "WiFi miễn phí, Điều hòa, Tivi, Minibar, Bàn làm việc, Jacuzzi",
                        5, "Suite", 6, "resort_1,resort_2,resort_3"),

                new Room("Budget Single", "Phòng đơn giá rẻ", 35,
                        "villa_1", "Hà Nội", "WiFi miễn phí, Điều hòa",
                        2, "Single", 1, "villa_1,villa_2,villa_3"),

                new Room("Garden View Double", "Phòng đôi view vườn", 90,
                        "villa_2", "Đà Lạt", "WiFi miễn phí, Điều hòa, Tivi, Ban công",
                        4, "Double", 2, "villa_1,villa_2,villa_3"),

                new Room("Beach Villa", "Villa bên bãi biển riêng tư", 350,
                        "villa_3", "Phú Quốc", "WiFi miễn phí, Điều hòa, Tivi, Minibar, Bếp, Hồ bơi riêng",
                        5, "Villa", 8, "villa_1,villa_2,villa_3"),

                new Room("Hostel Bed", "Giường trong ký túc xá", 25,
                        "hostel_1", "Hà Nội", "WiFi miễn phí, Tủ khóa cá nhân",
                        3, "Hostel", 1, "hostel_1,hostel_2,hostel_3"),

                new Room("Cozy Apartment", "Căn hộ ấm cúng tiện nghi", 110,
                        "apartment_1", "TP.HCM", "WiFi miễn phí, Điều hòa, Tivi, Bếp, Máy giặt",
                        4, "Apartment", 3, "apartment_1,apartment_2,apartment_3"),

                new Room("Modern Apartment", "Căn hộ hiện đại view thành phố", 130,
                        "apartment_2", "Hà Nội", "WiFi miễn phí, Điều hòa, Tivi, Bếp, Máy giặt, Ban công",
                        5, "Apartment", 4, "apartment_1,apartment_2,apartment_3"),

                new Room("Studio Apartment", "Căn hộ studio đầy đủ tiện nghi", 95,
                        "apartment_3", "Đà Nẵng", "WiFi miễn phí, Điều hòa, Tivi, Bếp nhỏ",
                        4, "Studio", 2, "apartment_1,apartment_2,apartment_3")
        );

        roomDao.insertRooms(sampleRooms);
    }
}

