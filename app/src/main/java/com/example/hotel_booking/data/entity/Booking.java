package com.example.hotel_booking.data.entity;

import androidx.room.Entity;


@Entity(tableName = "bookings") // 👈 phải có để Room biết đây là bảng
public class Booking {
    private int id;
    private String roomType;
    private String guestName;
    private String checkInDate;
    private String checkOutDate;
    private double totalPrice;

    // Constructor
    public Booking() {}


    // Getter / Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

}

