package com.example.tumao.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tumao on 2017/11/26.
 */

public class CurrentView  extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_current, container, false);
        ListView listview = (ListView)rootView.findViewById(R.id.listView);
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<10;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemTitle", "Level "+i);
            map.put("itemValue", "Finished in 1 Min 54 Secs, 70 Moves! ");
            listItem.add(map);
        }
        //生成适配器的Item和动态数组对应的元素
        SimpleAdapter listItemAdapter = new SimpleAdapter(this.getContext(),listItem,//数据源
                R.layout.innerlist,//ListItem的XML实现
                //动态数组与ImageItem对应的子项
                new String[] {"itemTitle", "itemValue"},
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] {R.id.itemTitle,R.id.itemValue}
        );

        //添加并且显示
        listview.setAdapter(listItemAdapter);
        return rootView;
    }


}

