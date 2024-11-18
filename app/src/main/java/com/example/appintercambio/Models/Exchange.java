package com.example.appintercambio.Models;

public class Exchange
{
    private String theme, location, date, time;
    private int id, min_price, max_price;

    public Exchange() {
    }

    public Exchange(int id, String theme, String location, String date, String time, int min_price, int max_price) {
        this.id = id;
        this.theme = theme;
        this.location = location;
        this.date = date;
        this.time = time;
        this.min_price = min_price;
        this.max_price = max_price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMin_price() {
        return min_price;
    }

    public void setMin_price(int min_price) {
        this.min_price = min_price;
    }

    public int getMax_price() {
        return max_price;
    }

    public void setMax_price(int max_price) {
        this.max_price = max_price;
    }

    @Override
    public String toString() {
        return "Exchange{" +
                "theme='" + theme + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", id=" + id +
                ", min_price=" + min_price +
                ", max_price=" + max_price +
                '}';
    }
}
