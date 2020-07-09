package com.example.milkapplication;

public class Delivery {
    private String address, password, milk;
    private int number;

    public Delivery(String address, String password, String milk, int number) {
        this.address = address;
        this.password = password;
        this.milk = milk;
        this.number = number;
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
