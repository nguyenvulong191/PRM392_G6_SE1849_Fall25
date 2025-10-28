package com.example.hotel_booking.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "booking")
public class Booking {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "room")
    private String roomType;

    @ColumnInfo(name = "guest")
    private String guestName;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "price")
    private double totalPrice;

    @ColumnInfo(name = "userId")
    private int userId;

    @ColumnInfo(name = "addons")
    private String addons; // 🆕 dịch vụ thêm (ví dụ: Bữa sáng, Đưa đón sân bay)
    @ColumnInfo(name = "note")
    private String note;
    @Ignore private String checkInDate;
    @Ignore private String checkOutDate;

    public Booking() {}

    // ==== Getters / Setters ====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getDate() { return date; }
    public void setDate(String date) {
        this.date = date;
        if (date != null && date.contains(" - ")) {
            String[] parts = date.split(" - ");
            this.checkInDate  = parts[0];
            this.checkOutDate = parts.length > 1 ? parts[1] : "";
        } else {
            this.checkInDate = date;
            this.checkOutDate = "";
        }
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getAddons() { return addons; }
    public void setAddons(String addons) { this.addons = addons; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
        rebuildDate();
    }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
        rebuildDate();
    }


    private void rebuildDate() {
        if (checkInDate == null && checkOutDate == null) return;
        String in  = checkInDate  == null ? "" : checkInDate;
        String out = checkOutDate == null ? "" : checkOutDate;
        this.date = out.isEmpty() ? in : (in + " - " + out);
    }
}
