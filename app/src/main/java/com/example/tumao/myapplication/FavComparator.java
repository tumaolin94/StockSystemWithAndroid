package com.example.tumao.myapplication;

import java.util.Comparator;

/**
 * Created by tumao on 2017/11/29.
 */

public class FavComparator implements Comparator<FavObj> {
    String sortby;
    String order;
    public FavComparator(String sortby, String order){
        this.sortby = sortby;
        this.order = order;
    }
    @Override
    public int compare(FavObj obj1, FavObj obj2){
        int res = 0;
        switch (sortby){
            case "Default":{
                res = obj1.getDate().compareTo(obj2.getDate());
                break;
            }
            case "Symbol":{
                res = obj1.symbol.compareTo(obj2.symbol);
                break;
            }
            case "Price":{
                res = obj1.getPrice().compareTo(obj2.getPrice());
                break;
            }
            case "Change":{
                res = obj1.getChange().compareTo(obj2.getChange());
                break;
            }
            case "Change(%)":{
                res = obj1.getChange_per().compareTo(obj2.getChange_per());
                break;
            }
        }
        return order.equals("Descending")?-1*res: res;
    }
}
