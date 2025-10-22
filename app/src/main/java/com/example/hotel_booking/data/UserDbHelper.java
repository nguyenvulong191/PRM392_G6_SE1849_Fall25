package com.example.hotel_booking.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "hotel.db";
    public static final int DB_VERSION = 1;

    public static final String T_USERS = "users";
    public static final String C_ID = "id";
    public static final String C_NAME = "name";
    public static final String C_EMAIL = "email";
    public static final String C_PASSWORD = "password";

    private static final String SQL_CREATE_USERS =
            "CREATE TABLE " + T_USERS + " (" +
                    C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    C_NAME + " TEXT NOT NULL, " +
                    C_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    C_PASSWORD + " TEXT NOT NULL" +
                    ");";

    public UserDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_USERS);
        onCreate(db);
    }
}
