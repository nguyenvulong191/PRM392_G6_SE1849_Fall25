package com.example.hotel_booking.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hotel_booking.data.entity.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(User user);

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int emailExists(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User getById(int id);

    @Query("SELECT name FROM users WHERE email = :email LIMIT 1")
    String getNameByEmail(String email);

    @Update
    int update(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);
}
