package com.example.hotel_booking.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;   // <-- THÊM DÒNG NÀY

@Entity(
        tableName = "users",
        indices = {@Index(value = {"email"}, unique = true)}
)
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String email;
    private String password;

    public User() {}

    @Ignore
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
