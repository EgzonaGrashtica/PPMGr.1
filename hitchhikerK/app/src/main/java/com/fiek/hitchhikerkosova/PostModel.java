package com.fiek.hitchhikerkosova;

public class PostModel {
    String id;
    String from;
    String to;
    String departureTime;
    String date;
    double price;
    int freeSeats;
    String phoneNumber;
    String extraInfo;

    public PostModel(String from, String to, String departureTime, String date, double price, int freeSeats, String phoneNumber, String extraInfo) {
        this.from = from;
        this.to = to;
        this.departureTime = departureTime;
        this.date = date;
        this.price = price;
        this.freeSeats = freeSeats;
        this.phoneNumber = phoneNumber;
        this.extraInfo = extraInfo;
    }

    public PostModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(int freeSeats) {
        this.freeSeats = freeSeats;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}
