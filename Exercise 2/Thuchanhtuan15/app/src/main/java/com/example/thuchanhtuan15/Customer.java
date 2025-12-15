package com.example.thuchanhtuan15;


import java.io.Serializable;

public class Customer implements Serializable {
    private int id;
    private String phone;
    private int points;
    private String createdDate;
    private String lastUpdated;

    public Customer() {}

    public Customer(String phone, int points) {
        this.phone = phone;
        this.points = points;
    }

    public Customer(int id, String phone, int points, String createdDate, String lastUpdated) {
        this.id = id;
        this.phone = phone;
        this.points = points;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}