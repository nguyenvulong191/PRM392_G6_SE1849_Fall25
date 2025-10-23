package com.example.hotel_booking.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hotel_booking.data.RoomDbHelper;
import com.example.hotel_booking.data.entity.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomDao {
    private RoomDbHelper dbHelper;

    public RoomDao(Context context) {
        dbHelper = new RoomDbHelper(context);
    }

    public List<Room> getRoomsPaginated(String sortBy, int limit, int offset) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Room> rooms = new ArrayList<>();

        String orderBy = getOrderByClause(sortBy);
        String query = "SELECT * FROM " + RoomDbHelper.TABLE_ROOMS +
                      " ORDER BY " + orderBy +
                      " LIMIT " + limit + " OFFSET " + offset;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                rooms.add(cursorToRoom(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return rooms;
    }

    public List<Room> searchRooms(String location, Double minPrice, Double maxPrice,
                                 String roomType, String sortBy, int limit, int offset) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Room> rooms = new ArrayList<>();

        StringBuilder query = new StringBuilder("SELECT * FROM " + RoomDbHelper.TABLE_ROOMS + " WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (location != null && !location.isEmpty()) {
            query.append(" AND location LIKE ?");
            args.add("%" + location + "%");
        }

        if (minPrice != null) {
            query.append(" AND price >= ?");
            args.add(String.valueOf(minPrice));
        }

        if (maxPrice != null) {
            query.append(" AND price <= ?");
            args.add(String.valueOf(maxPrice));
        }

        if (roomType != null && !roomType.isEmpty()) {
            query.append(" AND roomType = ?");
            args.add(roomType);
        }

        query.append(" ORDER BY ").append(getOrderByClause(sortBy));
        query.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);

        Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                rooms.add(cursorToRoom(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return rooms;
    }

    public List<Room> getFavoriteRooms() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Room> rooms = new ArrayList<>();

        Cursor cursor = db.query(RoomDbHelper.TABLE_ROOMS, null,
                RoomDbHelper.COLUMN_IS_FAVORITE + "=?", new String[]{"1"},
                null, null, RoomDbHelper.COLUMN_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                rooms.add(cursorToRoom(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return rooms;
    }

    public Room getRoomById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Room room = null;

        Cursor cursor = db.query(RoomDbHelper.TABLE_ROOMS, null,
                RoomDbHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor.moveToFirst()) {
            room = cursorToRoom(cursor);
        }

        cursor.close();
        db.close();
        return room;
    }

    public long insertRoom(Room room) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = roomToContentValues(room);
        long id = db.insert(RoomDbHelper.TABLE_ROOMS, null, values);
        db.close();
        return id;
    }

    public void insertRooms(List<Room> rooms) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Room room : rooms) {
                ContentValues values = roomToContentValues(room);
                db.insert(RoomDbHelper.TABLE_ROOMS, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void updateFavoriteStatus(int id, boolean isFavorite) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RoomDbHelper.COLUMN_IS_FAVORITE, isFavorite ? 1 : 0);

        db.update(RoomDbHelper.TABLE_ROOMS, values,
                RoomDbHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int getRoomCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + RoomDbHelper.TABLE_ROOMS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    private String getOrderByClause(String sortBy) {
        switch (sortBy) {
            case "price_asc":
                return RoomDbHelper.COLUMN_PRICE + " ASC";
            case "price_desc":
                return RoomDbHelper.COLUMN_PRICE + " DESC";
            case "rating":
                return RoomDbHelper.COLUMN_RATING + " DESC";
            default:
                return RoomDbHelper.COLUMN_NAME + " ASC";
        }
    }

    private Room cursorToRoom(Cursor cursor) {
        Room room = new Room();
        room.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_ID)));
        room.setName(cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_NAME)));
        room.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_DESCRIPTION)));
        room.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_PRICE)));
        room.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_IMAGE_URL)));
        room.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_LOCATION)));
        room.setAmenities(cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_AMENITIES)));
        room.setRating(cursor.getInt(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_RATING)));
        room.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_IS_FAVORITE)) == 1);
        room.setRoomType(cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_ROOM_TYPE)));
        room.setCapacity(cursor.getInt(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_CAPACITY)));
        room.setGallery(cursor.getString(cursor.getColumnIndexOrThrow(RoomDbHelper.COLUMN_GALLERY)));
        return room;
    }

    private ContentValues roomToContentValues(Room room) {
        ContentValues values = new ContentValues();
        values.put(RoomDbHelper.COLUMN_NAME, room.getName());
        values.put(RoomDbHelper.COLUMN_DESCRIPTION, room.getDescription());
        values.put(RoomDbHelper.COLUMN_PRICE, room.getPrice());
        values.put(RoomDbHelper.COLUMN_IMAGE_URL, room.getImageUrl());
        values.put(RoomDbHelper.COLUMN_LOCATION, room.getLocation());
        values.put(RoomDbHelper.COLUMN_AMENITIES, room.getAmenities());
        values.put(RoomDbHelper.COLUMN_RATING, room.getRating());
        values.put(RoomDbHelper.COLUMN_IS_FAVORITE, room.isFavorite() ? 1 : 0);
        values.put(RoomDbHelper.COLUMN_ROOM_TYPE, room.getRoomType());
        values.put(RoomDbHelper.COLUMN_CAPACITY, room.getCapacity());
        values.put(RoomDbHelper.COLUMN_GALLERY, room.getGallery());
        return values;
    }
}
