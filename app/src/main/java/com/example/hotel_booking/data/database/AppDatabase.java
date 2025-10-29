package com.example.hotel_booking.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.hotel_booking.data.dao.BookingDao;
import com.example.hotel_booking.data.dao.UserDao;
import com.example.hotel_booking.data.entity.Booking;
import com.example.hotel_booking.data.entity.User;

import java.util.List;

@Database(
        entities = {Booking.class, User.class},
        version = 5,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // DAOs
    public abstract BookingDao bookingDao();

    public abstract UserDao userDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE booking ADD COLUMN userId INTEGER NOT NULL DEFAULT 0");

            db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "email TEXT NOT NULL, " +
                    "password TEXT NOT NULL)");
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_email ON users(email)");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "hotel_booking.db"
                            )
                            .fallbackToDestructiveMigration() // 🔥 thêm dòng này
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    public void insertBooking(Booking booking) {
        // Nếu cột date trống → tự tạo từ check-in / check-out
        if (booking.getDate() == null || booking.getDate().isEmpty()) {
            String in = booking.getCheckInDate() == null ? "" : booking.getCheckInDate();
            String out = booking.getCheckOutDate() == null ? "" : booking.getCheckOutDate();
            booking.setDate(out.isEmpty() ? in : (in + " - " + out));
        }
        bookingDao().insert(booking);
    }

}
