package com.example.tumao.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tumao.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tumao on 2017/11/30.
 * An customized Adapter class for Stock detail table
 */
class TableObj{
    private String itemTitle;
    private String itemValue;
    public TableObj(String itemTitle,String itemValue){
        this.itemTitle = itemTitle;
        this.itemValue = itemValue;
    }
    public String getItemTitle(){
        return this.itemTitle;
    }

    public String getItemValue(){
        return this.itemValue;
    }
}
public class TableAdapter extends BaseAdapter{
    private List<TableObj> list;
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
        final TableObj tableObj = list.get(i);
        TextView itemTitle = view.findViewById(R.id.itemTitle);
        TextView itemValue = view.findViewById(R.id.itemValue);
        itemTitle.setText(tableObj.getItemTitle());
        itemValue.setText(tableObj.getItemValue());
        if(i==2){

            if(tableObj.getItemValue().contains("-")){
                Drawable drawable=view.getResources().getDrawable(R.drawable.down);
                drawable.setBounds(0, 0, 70, 80);
                itemValue.setCompoundDrawables(null,null,drawable,null);
            }else{
                Drawable drawable=view.getResources().getDrawable(R.drawable.up);
                drawable.setBounds(0, 0, 70, 80);
                itemValue.setCompoundDrawables(null,null,drawable,null);
            }
        }
        return view;
    }
}
