package com.dcinspirations.bookstore.models;

public class UserModel {
    private String Email,Name,Address,MNumber;

    public UserModel(String email, String name, String address, String MNumber) {
        Email = email;
        Name = name;
        Address = address;
        this.MNumber = MNumber;
    }

    public UserModel() {
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getMNumber() {
        return MNumber;
    }

    public void setMNumber(String MNumber) {
        this.MNumber = MNumber;
    }
}
