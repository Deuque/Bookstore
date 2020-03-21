package com.dcinspirations.bookstore.models;

public class DeliveryModel {
    private String Items,CDetails,Status,Publisher,DLocation,Dkey,Price;

    public DeliveryModel(String items, String CDetails, String status, String publisher, String DLocation) {
        Items = items;
        this.CDetails = CDetails;
        Status = status;
        Publisher = publisher;
        this.DLocation = DLocation;
    }

    public DeliveryModel() {
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDkey() {
        return Dkey;
    }

    public void setDkey(String dkey) {
        Dkey = dkey;
    }

    public String getItems() {
        return Items;
    }

    public void setItems(String items) {
        Items = items;
    }

    public String getCDetails() {
        return CDetails;
    }

    public void setCDetails(String CDetails) {
        this.CDetails = CDetails;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    public String getDLocation() {
        return DLocation;
    }

    public void setDLocation(String DLocation) {
        this.DLocation = DLocation;
    }

    public String getDLocaion() {
        return DLocation;
    }

}
