package com.dcinspirations.bookstore.models;

public class BookModel {
    private String title,author,price,desc,key;

    public BookModel(String title, String author, String price, String desc, String key) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.desc = desc;
        this.key = key;
    }

    public BookModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
