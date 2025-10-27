package com.example.hotel_booking.data;

import android.content.Context;

import com.example.hotel_booking.data.database.AppDatabase;
import com.example.hotel_booking.data.dao.UserDao;
import com.example.hotel_booking.data.entity.User;

public class UserRepository {

    private final UserDao userDao;

    public UserRepository(Context ctx) {
        this.userDao = AppDatabase.getInstance(ctx.getApplicationContext()).userDao();
    }

    public boolean isEmailExists(String email) {
        return userDao.emailExists(email) > 0;
    }

    public long insertUser(String name, String email, String password) {
        User u = new User(name, email, password);
        return userDao.insert(u);
    }

    public boolean checkLogin(String email, String password) {
        return userDao.login(email, password) != null;
    }

    public String getNameByEmail(String email) {
        return userDao.getNameByEmail(email);
    }
    public com.example.hotel_booking.data.entity.User loginAndGet(String email, String password) {
        return userDao.login(email, password);
    }
}
