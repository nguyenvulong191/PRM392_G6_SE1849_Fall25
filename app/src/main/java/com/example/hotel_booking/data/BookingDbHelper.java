package com.example.hotel_booking.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookingDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "hotel_sqlite.db";
    private static final int DB_VERSION = 1;

    // BẢNG/CỘT TRÙNG KHỚP VỚI ENTITY CỦA BẠN
    public static final String TBL_BOOKING = "booking"; // <-- đúng với @Entity(tableName="booking")
    public static final String C_ID       = "id";       // INTEGER PRIMARY KEY AUTOINCREMENT
    public static final String C_ROOM     = "room";     // TEXT (roomType)
    public static final String C_GUEST    = "guest";    // TEXT (guestName)
    public static final String C_DATE     = "date";     // TEXT "dd/MM/yyyy - dd/MM/yyyy"
    public static final String C_PRICE    = "price";    // REAL (totalPrice)
    public static final String C_USER_ID  = "userId";   // INTEGER

    public BookingDbHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TBL_BOOKING + " (" +
                        C_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        C_ROOM    + " TEXT, " +
                        C_GUEST   + " TEXT, " +
                        C_DATE    + " TEXT, " +
                        C_PRICE   + " REAL, " +
                        C_USER_ID + " INTEGER" +
                        ");"
        );
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_BOOKING);
        onCreate(db);
    }
}
