package com.example.hotel_booking.model;
public class HotelCard {
    public final int imageRes; public final String name; public final String time; public final String minPrice; public final float rating;
    public HotelCard(int imageRes, String name, String time, String minPrice, float rating) {
        this.imageRes = imageRes; this.name = name; this.time = time; this.minPrice = minPrice; this.rating = rating;
    }
}
