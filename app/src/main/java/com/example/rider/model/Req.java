package com.example.rider.model;

import java.io.Serializable;

public class Req implements Serializable {
    private int id, user_id;
    private String address, shopName, location, items;
    private Double latitude, longitude;

    public Req(int id, int user_id,  String address, String shopName, String items, String location, Double latitude, Double longitude) {
        this.id = id;
        this.user_id = user_id;
        this.address = address;
        this.shopName = shopName;
        this.location = location;
        this.latitude = latitude;
        this.items = items;
        this.longitude = longitude;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
