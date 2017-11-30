package com.example.tumao.myapplication;

/**
 * Created by tumao on 2017/11/29.
 */

public class FavObj {
    String symbol;
    double price;
    double change;
    double change_per;
    long date;
    public FavObj(String symbol,double price, double change, double change_per, long date){
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.change_per = change_per;
        this.date = date;
    }
    public Long getDate(){
        return new Long(date);
    }
    public Double getPrice(){
        return new Double(price);
    }
    public Double getChange(){
        return new Double(change);
    }
    public Double getChange_per(){
        return new Double(change_per);
    }


}
