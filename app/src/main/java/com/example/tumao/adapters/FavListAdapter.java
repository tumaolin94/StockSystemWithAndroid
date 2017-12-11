package com.example.tumao.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tumao.myapplication.FavObj;
import com.example.tumao.myapplication.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by tumao on 2017/11/30.
 * customized adapter for favorite list
 */

public class FavListAdapter extends BaseAdapter{
    List<FavObj> list;
    public FavListAdapter(List<FavObj> list){
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.favorite,viewGroup,false);
        }
        DecimalFormat df = new DecimalFormat("0.00");
        final FavObj favObj = list.get(i);
        TextView price = view.findViewById(R.id.price);
        TextView symbol = view.findViewById(R.id.symbol);
        TextView change = view.findViewById(R.id.change);
        symbol.setText(favObj.getSymbol());
        price.setText(String.valueOf(favObj.getPrice()));
        change.setText(String.valueOf(df.format(favObj.getChange())+"("+df.format(favObj.getChange_per())+"%)"));
        if(favObj.getChange()>0){
            change.setTextColor(Color.GREEN);
        }else{
            change.setTextColor(Color.RED);
        }
        return view;
    }
}
