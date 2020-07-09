package com.example.milkapplication;

public class Delivery {
    private String address, password, milk;
    private int number;
    private boolean mon, tue, wed, thu, fri;

    public Delivery(String address, String password, String milk, int number, boolean mon, boolean tue, boolean wed, boolean thu, boolean fri) {
        this.address = address;
        this.password = password;
        this.milk = milk;
        this.number = number;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMilk() {
        return milk;
    }

    public void setMilk(String milk) {
        this.milk = milk;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
