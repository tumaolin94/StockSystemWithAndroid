package com.example.tumao.myapplication;

import android.content.Context;
import android.widget.ArrayAdapter;
/**
 * Created by tumao on 2017/11/21.
 */

class SingleArrayAdapter extends ArrayAdapter<String> {
    String[] objects;
    public SingleArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.objects = objects;
    }



//    @Override
//    public int getCount() {
//        return Math.min(this.objects.length,5);
//    }
}
