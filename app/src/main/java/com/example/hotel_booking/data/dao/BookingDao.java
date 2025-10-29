package com.example.hotel_booking.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.hotel_booking.data.entity.Booking;

import java.util.List;

@Dao
public interface BookingDao {

    @Insert
    long insert(Booking booking);

    @Query("SELECT * FROM booking ORDER BY id DESC")
    List<Booking> getAll();

    @Query("SELECT * FROM booking WHERE userId = :uid ORDER BY id DESC")
    List<Booking> getByUser(int uid);

    @Query("DELETE FROM booking WHERE id = :id")
    void deleteById(int id);
    @Query("SELECT * FROM booking WHERE id = :id LIMIT 1")
    Booking getBookingById(int id);
}
