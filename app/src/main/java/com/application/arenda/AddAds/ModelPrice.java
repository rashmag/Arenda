package com.application.arenda.AddAds;

public class ModelPrice {
    private String price;
    private String time;

    public ModelPrice() {
    }

    public ModelPrice(String price, String time) {
        this.price = price;
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
