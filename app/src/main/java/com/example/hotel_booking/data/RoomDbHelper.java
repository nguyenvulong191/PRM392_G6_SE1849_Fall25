package com.example.hotel_booking.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RoomDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "hotel_rooms.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_ROOMS = "rooms";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE_URL = "imageUrl";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_AMENITIES = "amenities";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_IS_FAVORITE = "isFavorite";
    public static final String COLUMN_ROOM_TYPE = "roomType";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_GALLERY = "gallery";

    private static final String SQL_CREATE_ROOMS_TABLE =
            "CREATE TABLE " + TABLE_ROOMS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_PRICE + " REAL NOT NULL, " +
                    COLUMN_IMAGE_URL + " TEXT, " +
                    COLUMN_LOCATION + " TEXT, " +
                    COLUMN_AMENITIES + " TEXT, " +
                    COLUMN_RATING + " INTEGER, " +
                    COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0, " +
                    COLUMN_ROOM_TYPE + " TEXT, " +
                    COLUMN_CAPACITY + " INTEGER, " +
                    COLUMN_GALLERY + " TEXT" +
                    ");";

    public RoomDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ROOMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        onCreate(db);
    }
}
