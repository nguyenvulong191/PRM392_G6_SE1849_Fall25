package com.example.hotel_booking.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hotel_booking.data.BookingDbHelper;
import com.example.hotel_booking.data.entity.Booking;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BookingStore {
    private final BookingDbHelper helper;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public BookingStore(Context ctx) { helper = new BookingDbHelper(ctx); }

    public List<Booking> getAllOrderByIdDesc() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Booking> list = new ArrayList<>();
        try (Cursor c = db.query(
                BookingDbHelper.TBL_BOOKING,
                null, null, null, null, null,
                BookingDbHelper.C_ID + " DESC")) {
            while (c.moveToNext()) list.add(fromCursor(c));
        }
        return list;
    }

    public List<Booking> getByUserOrderByIdDesc(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Booking> list = new ArrayList<>();
        try (Cursor c = db.query(
                BookingDbHelper.TBL_BOOKING,
                null,
                BookingDbHelper.C_USER_ID + "=?",
                new String[]{ String.valueOf(userId) },
                null, null,
                BookingDbHelper.C_ID + " DESC")) {
            while (c.moveToNext()) list.add(fromCursor(c));
        }
        return list;
    }

    public long insert(Booking b) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(BookingDbHelper.C_ROOM, b.getRoomType());
        v.put(BookingDbHelper.C_GUEST, b.getGuestName());
        v.put(BookingDbHelper.C_DATE, b.getDate());
        v.put(BookingDbHelper.C_PRICE, b.getTotalPrice());
        v.put(BookingDbHelper.C_USER_ID, b.getUserId());
        return db.insert(BookingDbHelper.TBL_BOOKING, null, v);
    }

    public void update(Booking b) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(BookingDbHelper.C_ROOM, b.getRoomType());
        v.put(BookingDbHelper.C_GUEST, b.getGuestName());
        v.put(BookingDbHelper.C_DATE, b.getDate());
        v.put(BookingDbHelper.C_PRICE, b.getTotalPrice());
        v.put(BookingDbHelper.C_USER_ID, b.getUserId());
        db.update(BookingDbHelper.TBL_BOOKING, v, BookingDbHelper.C_ID + "=?",
                new String[]{ String.valueOf(b.getId()) });
    }

    public void delete(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(BookingDbHelper.TBL_BOOKING, BookingDbHelper.C_ID + "=?",
                new String[]{ String.valueOf(id) });
    }

    public int count() {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + BookingDbHelper.TBL_BOOKING, null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    public void seedIfEmpty() {
        if (count() > 0) return;
        insert(sample("Deluxe",   "Nguyễn A", "28/10/2025 - 30/10/2025", 180.0, 1));
        insert(sample("Standard", "Trần B",   "05/11/2025 - 06/11/2025",  60.0, 1));
        insert(sample("Suite",    "Lê C",     "10/09/2025 - 12/09/2025", 360.0, 2));
    }

    private Booking sample(String roomType, String guest, String date, double price, int userId){
        Booking b = new Booking();
        b.setRoomType(roomType);
        b.setGuestName(guest);
        b.setDate(date);         // setter của bạn auto tách checkIn/checkOut
        b.setTotalPrice(price);
        b.setUserId(userId);
        return b;
    }

    private Booking fromCursor(Cursor c) {
        Booking b = new Booking();
        b.setId(c.getInt(c.getColumnIndexOrThrow(BookingDbHelper.C_ID)));
        b.setRoomType(c.getString(c.getColumnIndexOrThrow(BookingDbHelper.C_ROOM)));
        b.setGuestName(c.getString(c.getColumnIndexOrThrow(BookingDbHelper.C_GUEST)));
        b.setDate(c.getString(c.getColumnIndexOrThrow(BookingDbHelper.C_DATE)));
        b.setTotalPrice(c.getDouble(c.getColumnIndexOrThrow(BookingDbHelper.C_PRICE)));
        b.setUserId(c.getInt(c.getColumnIndexOrThrow(BookingDbHelper.C_USER_ID)));
        return b;
    }

    // ---- Tiện ích cho BookingHistory ----
    public boolean isPastByCheckIn(Booking b) {
        String in = b.getCheckInDate();
        if (in == null || in.isEmpty()) return false;
        try {
            Date d = sdf.parse(in);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            return d.before(today.getTime());
        } catch (ParseException e) {
            return false;
        }
    }
}
