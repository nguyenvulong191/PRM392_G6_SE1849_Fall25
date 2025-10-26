package com.example.hotel_booking.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import com.example.hotel_booking.data.entity.Booking;

import java.util.ArrayList;
import java.util.List;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hotel_booking.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_BOOKING = "booking";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ROOM = "room";
    private static final String COLUMN_GUEST = "guest";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_PRICE = "price";

    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOKING_TABLE = "CREATE TABLE " + TABLE_BOOKING + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ROOM + " TEXT, "
                + COLUMN_GUEST + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_PRICE + " REAL)";
        db.execSQL(CREATE_BOOKING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        onCreate(db);
    }

    public void insertBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM, booking.getRoomType());
        values.put(COLUMN_GUEST, booking.getGuestName());
        values.put(COLUMN_DATE, booking.getCheckInDate() + " - " + booking.getCheckOutDate());
        values.put(COLUMN_PRICE, booking.getTotalPrice());
        db.insert(TABLE_BOOKING, null, values);
        db.close();
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKING, null);

        if (cursor.moveToFirst()) {
            do {
                Booking booking = new Booking();
                booking.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                booking.setRoomType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROOM)));
                booking.setGuestName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GUEST)));

                // 🧩 Phân tách chuỗi "checkIn - checkOut" thành 2 phần riêng biệt
                String dateRange = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                if (dateRange != null && dateRange.contains(" - ")) {
                    String[] parts = dateRange.split(" - ");
                    booking.setCheckInDate(parts[0]);
                    booking.setCheckOutDate(parts.length > 1 ? parts[1] : "");
                } else {
                    booking.setCheckInDate(dateRange);
                    booking.setCheckOutDate("");
                }

                booking.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                list.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public void deleteBooking(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKING, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

}
