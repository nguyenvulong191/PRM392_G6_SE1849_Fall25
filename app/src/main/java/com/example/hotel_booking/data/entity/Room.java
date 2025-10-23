package com.example.hotel_booking.data.entity;

public class Room {
    public int id;
    public String name;
    public String description;
    public double price;
    public String imageUrl;
    public String location;
    public String amenities;
    public int rating;
    public boolean isFavorite;
    public String roomType;
    public int capacity;
    public String gallery;

    // Constructors
    public Room() {}

    public Room(String name, String description, double price, String imageUrl,
                String location, String amenities, int rating, String roomType, int capacity, String gallery) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.location = location;
        this.amenities = amenities;
        this.rating = rating;
        this.roomType = roomType;
        this.capacity = capacity;
        this.gallery = gallery;
        this.isFavorite = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getGallery() { return gallery; }
    public void setGallery(String gallery) { this.gallery = gallery; }
}
