package com.example.thuchanhtuan15;


public class Transaction {
    private int id;
    private String phone;
    private int points;
    private String note;
    private String type; // INPUT or USE
    private String date;

    public Transaction() {}

    public Transaction(String phone, int points, String note, String type, String date) {
        this.phone = phone;
        this.points = points;
        this.note = note;
        this.type = type;
        this.date = date;
    }

    public Transaction(int id, String phone, int points, String note, String type, String date) {
        this.id = id;
        this.phone = phone;
        this.points = points;
        this.note = note;
        this.type = type;
        this.date = date;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}