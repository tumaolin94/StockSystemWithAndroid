package com.example.tumao.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
/**
 * Created by tumao on 2017/11/21.
 * Customized adapter for autocomplete list
 */

public class SingleArrayAdapter extends ArrayAdapter<String> {
    String[] objects;
    public SingleArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.objects = objects;
    }
}
