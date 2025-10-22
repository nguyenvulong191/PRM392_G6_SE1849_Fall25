package com.example.hotel_booking.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserRepository {

    private final UserDbHelper helper;

    public UserRepository(Context ctx) {
        helper = new UserDbHelper(ctx.getApplicationContext());
    }

    /** true nếu email đã tồn tại */
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(
                    UserDbHelper.T_USERS,
                    new String[]{UserDbHelper.C_ID},
                    UserDbHelper.C_EMAIL + "=?",
                    new String[]{email},
                    null, null, "1");
            return c.moveToFirst();
        } finally {
            if (c != null) c.close();
            // db không cần close vì do helper quản lý
        }
    }

    /** insert user mới; trả về rowId (>0) nếu thành công, -1 nếu lỗi (ví dụ trùng email) */
    public long insertUser(String name, String email, String password) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(UserDbHelper.C_NAME, name);
        cv.put(UserDbHelper.C_EMAIL, email);
        cv.put(UserDbHelper.C_PASSWORD, password);
        return db.insert(UserDbHelper.T_USERS, null, cv);
    }

    /** true nếu có user với email + password khớp */
    public boolean checkLogin(String email, String password) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(
                    UserDbHelper.T_USERS,
                    new String[]{UserDbHelper.C_ID},
                    UserDbHelper.C_EMAIL + "=? AND " + UserDbHelper.C_PASSWORD + "=?",
                    new String[]{email, password},
                    null, null, "1");
            return c.moveToFirst();
        } finally {
            if (c != null) c.close();
        }
    }
}
