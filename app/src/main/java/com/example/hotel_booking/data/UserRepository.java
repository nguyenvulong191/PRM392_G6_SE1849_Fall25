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

    // Kiểm tra xem email đã tồn tại trong DB hay chưa
    public boolean isEmailExists(String email) {
        return userDao.emailExists(email) > 0;
    }

    // Thêm người dùng mới
    public long insertUser(String name, String email, String password) {
        User u = new User(name, email, password);
        return userDao.insert(u);
    }

    // Kiểm tra login: đúng email và mật khẩu
    public boolean checkLogin(String email, String password) {
        return userDao.login(email, password) != null;
    }

    // Lấy tên người dùng qua email
    public String getNameByEmail(String email) {
        return userDao.getNameByEmail(email);
    }

    // Login và lấy toàn bộ đối tượng User nếu email và mật khẩu đúng
    public User loginAndGet(String email, String password) {
        return userDao.login(email, password);
    }

    // Cập nhật tên người dùng
    public boolean updateUserName(String email, String newName) {
        User user = userDao.getUserByEmail(email);
        if (user != null) {
            user.setName(newName);
            userDao.update(user);
        }
        return false;
    }

    // Cập nhật mật khẩu người dùng
    public boolean updatePassword(String email, String newPassword) {
        User user = userDao.getUserByEmail(email);
        if (user != null) {
            user.setPassword(newPassword);
            userDao.update(user);
        }
        return false;
    }
}
