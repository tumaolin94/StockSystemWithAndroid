package com.example.tumao.myapplication;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tumao on 2017/11/30.
 */
class TableObj{
    String itemTitle;
    String itemValue;
    int arrow = 0;
    public TableObj(String itemTitle,String itemValue){
        this.itemTitle = itemTitle;
        this.itemValue = itemValue;
    }
}
public class TableAdapter extends BaseAdapter{
    List<TableObj> list;
    String[] values;
    public TableAdapter(String[] values,String[] itemTitles){
        list = new ArrayList<>();
        for(int i=0;i<values.length;i++){
            list.add(new TableObj(itemTitles[i],values[i]));
        }
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
            view = inflater.inflate(R.layout.innerlist,viewGroup,false);
        }
        DecimalFormat df = new DecimalFormat("0.00");
        final TableObj tableObj = list.get(i);
        TextView itemTitle = view.findViewById(R.id.itemTitle);
        TextView itemValue = view.findViewById(R.id.itemValue);
//        ImageView arrow = view.findViewById(R.id.arrow);
        itemTitle.setText(tableObj.itemTitle);
        itemValue.setText(tableObj.itemValue);
        if(i==2){

            if(tableObj.itemValue.contains("-")){
                Drawable drawable=view.getResources().getDrawable(R.drawable.down);
                drawable.setBounds(0, 0, 70, 80);
                itemValue.setCompoundDrawables(null,null,drawable,null);
            }else{
                Drawable drawable=view.getResources().getDrawable(R.drawable.up);
                drawable.setBounds(0, 0, 70, 80);
                itemValue.setCompoundDrawables(null,null,drawable,null);
            }
//            arrow.setVisibility(View.VISIBLE);
        }
//        symbol.setText(favObj.symbol);
//        price.setText(String.valueOf(favObj.price));
//        change.setText(String.valueOf(df.format(favObj.change)+"("+df.format(favObj.change_per)+"%)"));
//        if(favObj.change>0){
//            change.setTextColor(Color.GREEN);
//        }else{
//            change.setTextColor(Color.RED);
//        }
//        map.put("symbol", sortList.get(i).symbol);
//        map.put("price", sortList.get(i).price);
//        map.put("change", df.format(sortList.get(i).change)+"("+df.format(sortList.get(i).change_per)+"%)");
        return view;
    }
}
