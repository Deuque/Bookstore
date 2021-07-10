package com.dcinspirations.bookstore;

public class FutureDate {
    private int futureDate, futureMonth, futureYear;
    FutureDate(int futureDate, int futureMonth, int futureYear){
        this.futureDate = futureDate;
        this.futureMonth = futureMonth;
        this.futureYear = futureYear;
    }

    public int getFutureDate(){
        return futureDate;
    }

    public int getFutureMonth(){
        return futureMonth;
    }

    public int getFutureYear(){
        return futureYear;
    }
}
