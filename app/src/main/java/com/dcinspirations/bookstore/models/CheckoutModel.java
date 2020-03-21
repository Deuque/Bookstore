package com.dcinspirations.bookstore.models;

public class CheckoutModel {
    private BookModel bookModel;
    private String Quantity;

    public CheckoutModel(BookModel bookModel, String quantity) {
        this.bookModel = bookModel;
        Quantity = quantity;
    }

    public BookModel getBookModel() {
        return bookModel;
    }

    public void setBookModel(BookModel bookModel) {
        this.bookModel = bookModel;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }
}
